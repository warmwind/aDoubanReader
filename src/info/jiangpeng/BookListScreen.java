package info.jiangpeng;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import info.jiangpeng.task.SearchMyBookTask;
import info.jiangpeng.task.SearchTask;
import info.jiangpeng.model.Book;

import java.util.ArrayList;


public class BookListScreen extends LinearLayout {

    private BookListAdapter bookArrayAdapter;
    private final ArrayList<DataChangeListener> listeners;

    public BookListScreen(Context context, AttributeSet attrs) {
        super(context, attrs);
        initUI();
        listeners = new ArrayList<DataChangeListener>();
    }

    private void initUI() {
        LinearLayout linearLayout = new LinearLayout(getContext());
        LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        vi.inflate(R.layout.book_list, linearLayout, true);

        this.addView(linearLayout);
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

    public void addDataChangeListener(DataChangeListener listener) {
        listeners.add(listener);
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
