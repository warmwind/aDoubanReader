package info.jiangpeng.activity;

import android.app.ActionBar;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import info.jiangpeng.*;
import info.jiangpeng.helper.RequestParams;

public class UserBooksActivity extends ListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.user_book_list);

        Intent intent = getIntent();
        RequestParams requestParams = (RequestParams)intent.getSerializableExtra("REQUEST_PARAMS");
        String userName = null;
        if (requestParams != null){
            userName = requestParams.getUserName();
        }
        if (userName == null){
            userName = getString(R.string.me);
        }
        ActionBar actionBar = createTabs(userName);


        if (savedInstanceState != null) {
            actionBar.setSelectedNavigationItem(savedInstanceState.getInt("tab", 0));
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("tab", getActionBar().getSelectedNavigationIndex());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                Intent intent = new Intent(this, MainSearchActivity.class);
                startActivity(intent);
                return true;
        }
        return true;
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
        menu.findItem(R.id.menu_more).setVisible(false);
        menu.findItem(R.id.menu_my_books).setVisible(true);
        menu.findItem(R.id.menu_contacts).setVisible(true);
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch (item.getItemId()) {
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


    private ActionBar createTabs(String userName) {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(userName + "的书单");

        actionBar.setIcon(R.drawable.douban);

        actionBar.addTab(createTab(actionBar, R.string.wish, ReadingStatus.WISH));
        actionBar.addTab(createTab(actionBar, R.string.reading, ReadingStatus.READING));
        actionBar.addTab(createTab(actionBar, R.string.read, ReadingStatus.READ));

        return actionBar;
    }

    private ActionBar.Tab createTab(ActionBar actionBar, int tabTextId, ReadingStatus readingStatus) {
        return actionBar.newTab()
                .setText(tabTextId)
                .setTag(readingStatus)
                .setTabListener(new UserBookTabListener(this, ReadingStatus.WISH.toString(), BookListFragment.class));
    }

    private void startActivityWithRequestParams(Class activityClass) {
        Intent intent = getIntent();
        intent.setClass(this, activityClass);
        startActivity(intent);
    }

}