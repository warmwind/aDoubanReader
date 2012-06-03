package info.jiangpeng;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ListActivity;
import android.os.Bundle;

public class MyBookActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.my_book_list);

        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);

        ActionBar.Tab withTab = actionBar.newTab()
                .setText(R.string.wish)
                .setTabListener(new MyBookTabListener(this, "wish", BookListFragment.class));
        actionBar.addTab(withTab);

        ActionBar.Tab readingTab = actionBar.newTab()
                .setText(R.string.reading)
                .setTabListener(new MyBookTabListener(this, "reading", BookListFragment.class));
        actionBar.addTab(readingTab);

        ActionBar.Tab readTab = actionBar.newTab()
                .setText(R.string.read)
                .setTabListener(new MyBookTabListener(this, "read", BookListFragment.class));
        actionBar.addTab(readTab);
    }
}
