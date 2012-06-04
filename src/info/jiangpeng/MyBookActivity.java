package info.jiangpeng;

import android.app.ActionBar;
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
                .setTag(ReadingStatus.WISH)
                .setTabListener(new MyBookTabListener(this, ReadingStatus.WISH.toString(), BookListFragment.class));
        actionBar.addTab(wishTab);

        ActionBar.Tab readingTab = actionBar.newTab()
                .setText(R.string.reading)
                .setTag(ReadingStatus.READING)
                .setTabListener(new MyBookTabListener(this, ReadingStatus.READING.toString(), BookListFragment.class));
        actionBar.addTab(readingTab);

        ActionBar.Tab readTab = actionBar.newTab()
                .setText(R.string.read)
                .setTag(ReadingStatus.READ)
                .setTabListener(new MyBookTabListener(this, ReadingStatus.READ.toString(), BookListFragment.class));
        actionBar.addTab(readTab);

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
}
