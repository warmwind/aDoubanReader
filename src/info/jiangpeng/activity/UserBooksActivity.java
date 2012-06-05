package info.jiangpeng.activity;

import android.app.ActionBar;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import info.jiangpeng.BookListFragment;
import info.jiangpeng.UserBookTabListener;
import info.jiangpeng.R;
import info.jiangpeng.ReadingStatus;

public class UserBooksActivity extends ListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.user_book_list);
        Intent intent = getIntent();
        String userName = intent.getStringExtra("USER_NAME");
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

    private ActionBar createTabs(String userName) {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(userName + "的书单");

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
}
