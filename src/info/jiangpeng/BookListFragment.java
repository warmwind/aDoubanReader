package info.jiangpeng;

import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import info.jiangpeng.activity.MainSearchActivity;
import info.jiangpeng.adapter.BookListAdapter;
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

        executeSearchByReadingStatus();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Book book = getBook(position);
        Intent myIntent = new Intent(getActivity(), BookDetailsWeb.class);
        myIntent.putExtra(BookDetailsWeb.BOOK_DETAILS_WEB_URL, book.getBookUrlInWeb());

        startActivity(myIntent);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.list, container, false);
    }

    public void initComponent(MainSearchActivity mainSearchActivity) {
        bookArrayAdapter = new BookListAdapter(mainSearchActivity, R.layout.book_item, R.id.book_title);
        ListView listView = mainSearchActivity.getListView();
        listView.setAdapter(bookArrayAdapter);
    }


    public void addDataChangeListener(DataChangeListener listener) {
        listeners.add(listener);
    }


    private void executeSearchByReadingStatus() {
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

    public void executeSearchByKeyWord(String query) {
        new SearchTask(this).execute(query);
    }


    public int getBookCount() {
        return bookArrayAdapter.getCount();
    }

    public void add(Book book) {
        bookArrayAdapter.add(book);
        notifyListingViewAndProgressBar();
    }

    private Book getBook(int position) {
        return bookArrayAdapter.getItem(position);
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
