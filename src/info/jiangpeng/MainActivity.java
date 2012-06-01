package info.jiangpeng;

import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import info.jiangpeng.helper.task.SearchTask;
import info.jiangpeng.model.Book;

public class MainActivity extends ListActivity {

    private String query;
    private BookListScreen bookListScreen;
    private SearchBar searchBar;
    private HeaderScreen headerScreen;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);

        headerScreen = (HeaderScreen) findViewById(R.id.header);
        searchBar = (SearchBar) findViewById(R.id.search_bar);
        bookListScreen = (BookListScreen) findViewById(R.id.book_list);

        initComponent();

        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            query = intent.getStringExtra(SearchManager.QUERY);

            new SearchTask(bookListScreen).execute(query);
            searchBar.showProgressBar();
        }

    }

    private void initComponent() {
        headerScreen.initComponent(this);
        searchBar.initComponent(this);
        bookListScreen.initComponent(this);
        bookListScreen.addDataChangeListener(searchBar);
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
        super.onListItemClick(l, v, position, id);
        Book book = bookListScreen.getBook(position);
        Intent myIntent = new Intent(this, BookDetailsWeb.class);
        myIntent.putExtra(BookDetailsWeb.BOOK_DETAILS_WEB_URL, book.getBookUrlInWeb());

        startActivity(myIntent);
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
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_more:
                bookListScreen.executeSearch(query);
                searchBar.showProgressBar();
                return true;
            case R.id.menu_my_books:
                bookListScreen.searchMyOwn(headerScreen.getUserId(), headerScreen.accessToken, headerScreen.accessTokenSecret);
                return true;
            default:
                return false;
        }
    }
}
