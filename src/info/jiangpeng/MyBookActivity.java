package info.jiangpeng;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

public class MyBookActivity extends ListActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.my_book_list);

        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(R.string.my_books);

        ActionBar.Tab wishTab = actionBar.newTab()
                .setText(R.string.wish)
                .setTag(BookStatus.WISH)
                .setTabListener(new MyBookTabListener(this, BookListFragment.class));
        actionBar.addTab(wishTab);

        ActionBar.Tab readingTab = actionBar.newTab()
                .setText(R.string.reading)
                .setTag(BookStatus.READING)
                .setTabListener(new MyBookTabListener(this, BookListFragment.class));
        actionBar.addTab(readingTab);

        ActionBar.Tab readTab = actionBar.newTab()
                .setText(R.string.read)
                .setTag(BookStatus.READ)
                .setTabListener(new MyBookTabListener(this, BookListFragment.class));
        actionBar.addTab(readTab);
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
}