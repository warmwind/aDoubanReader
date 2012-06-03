package info.jiangpeng;

import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import info.jiangpeng.model.Book;
import info.jiangpeng.task.SearchMyBookTask;
import info.jiangpeng.task.SearchTask;

import java.util.ArrayList;

public class BookListFragment extends ListFragment{
    private BookListAdapter bookArrayAdapter;
    private ArrayList<DataChangeListener> listeners;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        listeners = new ArrayList<DataChangeListener>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.book_list, container, false);
    }

    public void initComponent(MainSearchActivity mainSearchActivity) {
        bookArrayAdapter = new BookListAdapter(mainSearchActivity, R.layout.book_item, R.id.book_title);
        ListView listView = mainSearchActivity.getListView();
        listView.setAdapter(bookArrayAdapter);
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
// comment function for auto load when scroll down
//                int lastInScreen = firstVisibleItem + visibleItemCount;
//                if (lastInScreen == totalItemCount && canLoadMore) {
//                    executeSearch();
//                }
            }
        });

    }

    public void addDataChangeListener(DataChangeListener listener) {
        listeners.add(listener);
    }

    public void executeSearch(String query) {
        new SearchTask(this).execute(query);
//        canLoadMore = false;
    }


    public int getBookCount() {
        return bookArrayAdapter.getCount();
    }

    public Book getBook(int position) {
        return bookArrayAdapter.getItem(position);
    }

    public void add(Book book) {
        bookArrayAdapter.add(book);
        notifyListingViewAndProgressBar();
    }


    public void searchMyOwn(String userId, String accessToken, String accessTokenSecret) {
        bookArrayAdapter.clear();
        new SearchMyBookTask(this).execute(userId, accessToken, accessTokenSecret);
    }

    private void notifyListingViewAndProgressBar() {
        bookArrayAdapter.notifyDataSetChanged();
        for (DataChangeListener listener : listeners) {
            listener.update();
        }
    }



}
