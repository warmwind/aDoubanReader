package info.jiangpeng;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import info.jiangpeng.helper.MyBookParser;
import info.jiangpeng.helper.task.SearchTask;
import info.jiangpeng.model.Book;
import info.jiangpeng.sign.OAuthFactory;
import oauth.signpost.basic.DefaultOAuthConsumer;
import oauth.signpost.basic.UrlStringRequestAdapter;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;


public class BookListScreen extends LinearLayout {

    private BookListAdapter bookArrayAdapter;
    private final ArrayList<DataChangeListener> listeners;
    private MainActivity mainActivity;

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

    public void initComponent(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        bookArrayAdapter = new BookListAdapter(mainActivity, R.layout.book_item, R.id.book_title);
        ListView listView = mainActivity.getListView();
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


    public int getBookCount(){
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

    public void searchMyOwn(String userId){
        try {

            bookArrayAdapter.clear();

            DefaultOAuthConsumer consumer = OAuthFactory.createConsumer();
            consumer.setTokenWithSecret(mainActivity.accessToken, mainActivity.accessTokenSceret);

            String requestUrl = consumer.sign(new UrlStringRequestAdapter("http://api.douban.com/people/" + userId + "/collection?cat=book&alt=json")).getRequestUrl();
            String s1 = EntityUtils.toString(new DefaultHttpClient().execute(new HttpGet(requestUrl)).getEntity());


            JSONObject jsonObject = new JSONObject(s1);
            JSONArray entry = jsonObject.getJSONArray("entry");
            int length = entry.length();
            for (int i = 0; i < length; i++) {
                add(new MyBookParser().parse(entry.getJSONObject(i)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void notifyListingViewAndProgressBar() {
        bookArrayAdapter.notifyDataSetChanged();
        for (DataChangeListener listener : listeners) {
            listener.update();
        }
    }
}
