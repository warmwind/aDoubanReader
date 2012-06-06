package info.jiangpeng;

import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import info.jiangpeng.activity.BookDetailsActivity;
import info.jiangpeng.activity.MainSearchActivity;
import info.jiangpeng.adapter.BookListAdapter;
import info.jiangpeng.helper.CommonBookParser;
import info.jiangpeng.model.Book;
import info.jiangpeng.helper.RequestParams;
import info.jiangpeng.task.SearchDetailsTask;
import info.jiangpeng.task.SearchMyBookTask;
import info.jiangpeng.task.SearchTask;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
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
        try {
            Book book = getBook(position);
            HttpGet request = new HttpGet(book.getBookDetailsUrl() + "?alt=json&apikey=0d5f0a33b677be10281d1e9b23673a30");
            String rawJson = EntityUtils.toString(new DefaultHttpClient().execute(request).getEntity());
            new SearchDetailsTask(getActivity()).execute(book);
//            new CommonBookParser().parse(new JSONObject(rawJson));
        } catch (IOException e) {
            e.printStackTrace();
        }

//        new SearchDetailsTask(getActivity()).execute(book);
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
            RequestParams params = new RequestParams(intent);
            String tag = getTag();
            if (tag != null) {
                searchMyOwn(params, tag.toLowerCase());
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


    private void searchMyOwn(RequestParams params, String tag) {
        bookArrayAdapter.clear();
        new SearchMyBookTask(this).execute(params.getUserId(), params.getAccessToken(), params.getAccessTokenSecret(), tag);
    }

    private void notifyListingViewAndProgressBar() {
        bookArrayAdapter.notifyDataSetChanged();
        for (DataChangeListener listener : listeners) {
            listener.update();
        }
    }
}
