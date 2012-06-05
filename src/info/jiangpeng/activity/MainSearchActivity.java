package info.jiangpeng.activity;

import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import info.jiangpeng.*;
import info.jiangpeng.helper.RequestParams;
import info.jiangpeng.task.SearchTask;

public class MainSearchActivity extends ListActivity {

    private String query;
    private SearchBar searchBar;
    private HeaderScreen headerScreen;
    private BookListFragment bookListFragment;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);

        headerScreen = (HeaderScreen) findViewById(R.id.header);
        searchBar = (SearchBar) findViewById(R.id.search_bar);
        bookListFragment = (BookListFragment) getFragmentManager().findFragmentById(R.id.main_book_list);

        initComponent();

        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            query = intent.getStringExtra(SearchManager.QUERY);

            new SearchTask(bookListFragment).execute(query);
            searchBar.showProgressBar();
        }

    }

    private void initComponent() {
        headerScreen.initComponent(this);
        searchBar.initComponent(this);
        bookListFragment.initComponent(this);
        bookListFragment.addDataChangeListener(searchBar);
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            Uri uri = this.getIntent().getData();
            if (uri != null) {
                headerScreen.updateUserInfo();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        bookListFragment.onListItemClick(l, v, position, id);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.menu_my_books).setVisible(headerScreen.isUserSignedIn());
        menu.findItem(R.id.menu_contacts).setVisible(headerScreen.isUserSignedIn());
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_more:
                bookListFragment.executeSearchByKeyWord(query);
                searchBar.showProgressBar();
                return true;
            case R.id.menu_my_books:
                startActivityWithRequestParams(UserBooksActivity.class);
                return true;
            case R.id.menu_contacts:
                startActivityWithRequestParams(ContactsActivity.class);
                return true;
            default:
                return false;
        }
    }

    private void startActivityWithRequestParams(Class activityClass) {
        Intent intent = new Intent(this, activityClass);
        RequestParams params = new RequestParams();
        params.setUserId(headerScreen.getUserId());
        params.setAccessToken(headerScreen.accessToken);
        params.setAccessTokenSecret(headerScreen.accessTokenSecret);
        intent.putExtra("REQUEST_PARAMS", params);
        startActivity(intent);
    }
}
