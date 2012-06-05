package info.jiangpeng.activity;

import android.app.ActionBar;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import info.jiangpeng.BookListFragment;
import info.jiangpeng.MyBookTabListener;
import info.jiangpeng.R;
import info.jiangpeng.ReadingStatus;
import info.jiangpeng.activity.MainSearchActivity;

public class MyBooksActivity extends ListActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.my_book_list);

        ActionBar actionBar = createTabs();


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

    private ActionBar createTabs() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(R.string.my_books);

        actionBar.addTab(createTab(actionBar, R.string.wish, ReadingStatus.WISH));
        actionBar.addTab(createTab(actionBar, R.string.reading, ReadingStatus.READING));
        actionBar.addTab(createTab(actionBar, R.string.read, ReadingStatus.READ));
        return actionBar;
    }

    private ActionBar.Tab createTab(ActionBar actionBar, int tabTextId, ReadingStatus readingStatus) {
        return actionBar.newTab()
                .setText(tabTextId)
                .setTag(readingStatus)
                .setTabListener(new MyBookTabListener(this, ReadingStatus.WISH.toString(), BookListFragment.class));
    }
}
