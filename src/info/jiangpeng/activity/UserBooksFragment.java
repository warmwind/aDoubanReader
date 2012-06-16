package info.jiangpeng.activity;

import android.app.ActionBar;
import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import info.jiangpeng.BookListFragment;
import info.jiangpeng.R;
import info.jiangpeng.ReadingStatus;
import info.jiangpeng.UserBookTabListener;
import info.jiangpeng.helper.RequestParams;

public class UserBooksFragment extends ListFragment {

    private ActionBar actionBar;
    private RequestParams requestParams;
    private BookListFragment wishBookListFragment;
    private BookListFragment readingBookListFragment;
    private BookListFragment readBookListFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("--------------create tabs");


//        if (savedInstanceState != null) {
//            actionBar.setSelectedNavigationItem(savedInstanceState.getInt("tab", 0));
//        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        String userName = null;
        if (requestParams != null) {
            userName = requestParams.getUserName();
        }
        if (userName == null) {
            userName = getString(R.string.me);
        }
        createTabs(userName);

        return inflater.inflate(R.layout.user_book_list, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    private ActionBar createTabs(String userName) {
        actionBar = getActivity().getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(userName + "的书单");

//        actionBar.setIcon(R.drawable.douban);
        wishBookListFragment = new BookListFragment();
        readingBookListFragment = new BookListFragment();
        readBookListFragment = new BookListFragment();

        actionBar.addTab(createTab(actionBar, R.string.wish, ReadingStatus.WISH, wishBookListFragment));
        actionBar.addTab(createTab(actionBar, R.string.reading, ReadingStatus.READING, readBookListFragment));
        actionBar.addTab(createTab(actionBar, R.string.read, ReadingStatus.READ, readingBookListFragment));


        return actionBar;
    }

    private ActionBar.Tab createTab(ActionBar actionBar, int tabTextId, ReadingStatus readingStatus, BookListFragment bookListFragment) {
        return actionBar.newTab()
                .setText(tabTextId)
                .setTag(readingStatus)
                .setTabListener(new UserBookTabListener(getActivity(), readingStatus.toString(), bookListFragment));
    }

    public void setRequestParams(RequestParams requestParams) {
        this.requestParams = requestParams;
        wishBookListFragment.setRequestParams(requestParams);
        readingBookListFragment.setRequestParams(requestParams);
        readBookListFragment.setRequestParams(requestParams);
    }

}
