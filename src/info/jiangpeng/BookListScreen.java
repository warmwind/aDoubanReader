package info.jiangpeng;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.ListView;
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

    public void initComponent(MainActivity mainActivity, ListView listView) {
        bookArrayAdapter = new BookListAdapter(mainActivity, R.layout.book_item, R.id.book_title);
        listView.setAdapter(bookArrayAdapter);

    }

    public int getBookCount(){
        return bookArrayAdapter.getCount();
    }

    public Book getBook(int position) {
        return bookArrayAdapter.getItem(position);
    }

    public void clear() {
        bookArrayAdapter.clear();
    }

    public void add(Book book) {
        bookArrayAdapter.add(book);
        notifyListingViewAndProgressBar();
    }

    public void addDataChangeListener(DataChangeListener listener) {
         listeners.add(listener);
    }

    private void notifyListingViewAndProgressBar() {
        bookArrayAdapter.notifyDataSetChanged();
        for (DataChangeListener listener : listeners) {
            listener.update();
        }
    }
}
