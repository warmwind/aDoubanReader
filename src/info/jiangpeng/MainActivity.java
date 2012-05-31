package info.jiangpeng;

import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import info.jiangpeng.helper.MyBookParser;
import info.jiangpeng.helper.UserParser;
import info.jiangpeng.helper.task.SearchTask;
import info.jiangpeng.model.Book;
import info.jiangpeng.model.NullUser;
import info.jiangpeng.model.User;
import info.jiangpeng.sign.CustomOAuthConsumer;
import info.jiangpeng.sign.OAuthFactory;
import oauth.signpost.basic.DefaultOAuthConsumer;
import oauth.signpost.basic.DefaultOAuthProvider;
import oauth.signpost.basic.UrlStringRequestAdapter;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.exception.OAuthNotAuthorizedException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

public class MainActivity extends ListActivity {

    private static String accessToken;
    private static String accessTokenSceret;
    private static String requestToken;
    private static String requestTokenSceret;
    private static User user = new NullUser();

    private String query;
    private boolean canLoadMore;

    private DefaultOAuthProvider authProvider;
    private TextView signIn;
    private BookListScreen bookListScreen;
    private SearchBar searchBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);

        signIn = (TextView) findViewById(R.id.user);
        signIn.setText(user.getName());

        authProvider = new DefaultOAuthProvider("http://www.douban.com/service/auth/request_token", "http://www.douban.com/service/auth/access_token", "http://www.douban.com/service/auth/authorize");

        signIn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_UP:
                        if (user.isSignedIn()) {
                            user = new NullUser();
                            signIn.setText(user.getName());
                        } else {
                            try {
                                retrieveRequestToken();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        return true;
                    default:
                        return true;
                }
            }
        });

        searchBar = (SearchBar)findViewById(R.id.search_bar);
        bookListScreen = (BookListScreen) findViewById(R.id.book_list);
        searchBar.initComponent(this);
        bookListScreen.initComponent(this, getListView());
        bookListScreen.addDataChangeListener(searchBar);


        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            query = intent.getStringExtra(SearchManager.QUERY);

            new SearchTask(bookListScreen).execute(query);
            searchBar.showProgressBar();
        }

        this.getListView().setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                int lastInScreen = firstVisibleItem + visibleItemCount;
                if (lastInScreen == totalItemCount && canLoadMore) {
                    executeSearch();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            Uri uri = this.getIntent().getData();
            if (uri != null) {

                DefaultOAuthConsumer consumer = OAuthFactory.createConsumer();
                retrieveAccessToken(consumer);

                CustomOAuthConsumer consumerSignedIn = OAuthFactory.createConsumer(consumer.getToken(), consumer.getTokenSecret());
                user = new UserParser().parse(consumerSignedIn.executeAfterSignIn("http://api.douban.com/people/%40me?alt=json"));
                signIn.setText(user.getName());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void retrieveRequestToken() throws OAuthMessageSignerException, OAuthNotAuthorizedException, OAuthExpectationFailedException, OAuthCommunicationException {
        DefaultOAuthConsumer consumer = OAuthFactory.createConsumer();
        String url1 = authProvider.retrieveRequestToken(consumer, "vtbapp-doudou:///");

        requestToken = consumer.getToken();
        requestTokenSceret = consumer.getTokenSecret();
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url1));
        startActivity(browserIntent);
    }

    private void retrieveAccessToken(DefaultOAuthConsumer consumer) throws OAuthMessageSignerException, OAuthNotAuthorizedException, OAuthExpectationFailedException, OAuthCommunicationException {
        consumer.setTokenWithSecret(requestToken, requestTokenSceret);
        authProvider.retrieveAccessToken(consumer, null);
        accessToken = consumer.getToken();
        accessTokenSceret = consumer.getTokenSecret();
    }

    private void executeSearch() {
        new SearchTask(bookListScreen).execute(query);
        searchBar.showProgressBar();
        canLoadMore = false;
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Book book = bookListScreen.getBook(position);
        Intent myIntent = new Intent(this, BookDetailsWeb.class);
        myIntent.putExtra(BookDetailsWeb.BOOK_DETAILS_WEB_URL, book.getBookUrlInWeb());

        startActivity(myIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.menu_my_books).setVisible(user.isSignedIn());
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_more:
                executeSearch();
                return true;
            case R.id.menu_my_books:
                try {
                    bookListScreen.clear();

                    DefaultOAuthConsumer consumer = OAuthFactory.createConsumer();
                    consumer.setTokenWithSecret(accessToken, accessTokenSceret);

                    String requestUrl = consumer.sign(new UrlStringRequestAdapter("http://api.douban.com/people/" + user.getId() + "/collection?cat=book&alt=json")).getRequestUrl();
                    String s1 = EntityUtils.toString(new DefaultHttpClient().execute(new HttpGet(requestUrl)).getEntity());


                    JSONObject jsonObject = new JSONObject(s1);
                    JSONArray entry = jsonObject.getJSONArray("entry");
                    int length = entry.length();
                    for (int i = 0; i < length; i++) {
                        bookListScreen.add(new MyBookParser().parse(entry.getJSONObject(i)));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return true;
            default:
                return false;
        }
    }
}
