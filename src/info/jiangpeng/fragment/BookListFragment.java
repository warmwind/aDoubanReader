package info.jiangpeng.fragment;

import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import info.jiangpeng.DataChangeListener;
import info.jiangpeng.R;
import info.jiangpeng.activity.BookDetailsActivity;
import info.jiangpeng.adapter.BookListAdapter;
import info.jiangpeng.helper.RequestParams;
import info.jiangpeng.model.Book;
import info.jiangpeng.task.SearchMyBookTask;
import info.jiangpeng.task.SearchTask;

import java.util.ArrayList;

public class BookListFragment extends ListFragment {
    private BookListAdapter bookArrayAdapter;
    private ArrayList<DataChangeListener> listeners;

    //TODO: remove static
    private static RequestParams requestParams;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        listeners = new ArrayList<DataChangeListener>();
        bookArrayAdapter = new BookListAdapter(getActivity(), R.layout.book_item, R.id.book_title);
        setListAdapter(bookArrayAdapter);


    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Book book = getBook(position);
        Intent intent = new Intent(getActivity(), BookDetailsActivity.class);
        intent.putExtra("BOOK_DETAILS_URL", book.getBookDetailsUrl());
        getActivity().startActivity(intent);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.list, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        System.out.println("--------bookListFragment resumed and search again");
        executeSearchByReadingStatus();
    }

    public void addDataChangeListener(DataChangeListener listener) {
        listeners.add(listener);
    }


    public void executeSearchByReadingStatus() {
        String tag = getTag();
        System.out.println("------------search tag = " + tag);
        System.out.println("------------requestParams = " + requestParams);
        if (tag != null & requestParams != null) {
            getActivity().getActionBar().setTitle(requestParams.getUserName() + "的书单");
            searchMyOwn(requestParams, tag.toLowerCase());
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


    public void searchMyOwn(RequestParams params, String tag) {
//        if (bookArrayAdapter.getCount() == 0) {
        bookArrayAdapter.clear();
            System.out.println("------------params.getUserName() = " + params.getUserName());
            new SearchMyBookTask(this).execute(this.requestParams.getUserId(), params.getAccessToken(), params.getAccessTokenSecret(), tag.toLowerCase());
//        }
    }

    private void notifyListingViewAndProgressBar() {
        bookArrayAdapter.notifyDataSetChanged();
        for (DataChangeListener listener : listeners) {
            listener.update();
        }
    }


    public void setRequestParams(RequestParams requestParams) {
        System.out.println("------------setting requestParams = " + requestParams);
        this.requestParams = requestParams;
    }
}
