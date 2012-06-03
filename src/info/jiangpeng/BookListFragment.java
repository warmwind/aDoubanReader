package info.jiangpeng;

import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import info.jiangpeng.model.Book;
import info.jiangpeng.task.SearchMyBookTask;
import info.jiangpeng.task.SearchTask;

import java.util.ArrayList;

public class BookListFragment extends ListFragment {
    private BookListAdapter bookArrayAdapter;
    private ArrayList<DataChangeListener> listeners;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        listeners = new ArrayList<DataChangeListener>();
        bookArrayAdapter = new BookListAdapter(getActivity(), R.layout.book_item, R.id.book_title);
        setListAdapter(bookArrayAdapter);

        Intent intent = getActivity().getIntent();
        if (intent != null) {
            String user_id = intent.getStringExtra("USER_ID");
            String access_token = intent.getStringExtra("ACCESS_TOKEN");
            String access_token_secret = intent.getStringExtra("ACCESS_TOKEN_SECRET");
            String tag = getTag();
            if (tag != null) {
                searchMyOwn(user_id, access_token, access_token_secret, tag.toLowerCase());
            }
        }
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
    }


    public void addDataChangeListener(DataChangeListener listener) {
        listeners.add(listener);
    }

    public void executeSearch(String query) {
        new SearchTask(this).execute(query);
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


    private void searchMyOwn(String userId, String accessToken, String accessTokenSecret, String tag) {
        bookArrayAdapter.clear();
        new SearchMyBookTask(this).execute(userId, accessToken, accessTokenSecret, tag);
    }

    private void notifyListingViewAndProgressBar() {
        bookArrayAdapter.notifyDataSetChanged();
        for (DataChangeListener listener : listeners) {
            listener.update();
        }
    }
}
