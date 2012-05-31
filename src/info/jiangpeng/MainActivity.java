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

    public static String accessToken;
    public static String accessTokenSceret;
    public static String requestToken;
    public static String requestTokenSceret;


    private String query;

    private DefaultOAuthProvider authProvider;
    private BookListScreen bookListScreen;
    private SearchBar searchBar;
    private HeaderScreen headerScreen;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);

        authProvider = new DefaultOAuthProvider("http://www.douban.com/service/auth/request_token", "http://www.douban.com/service/auth/access_token", "http://www.douban.com/service/auth/authorize");
        headerScreen = (HeaderScreen) findViewById(R.id.header);

        searchBar = (SearchBar) findViewById(R.id.search_bar);
        bookListScreen = (BookListScreen) findViewById(R.id.book_list);

        initComponent();

        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            query = intent.getStringExtra(SearchManager.QUERY);

            new SearchTask(bookListScreen).execute(query);
            searchBar.showProgressBar();
        }

    }

    private void initComponent() {
        headerScreen.initComponent(this);
        searchBar.initComponent(this);
        bookListScreen.initComponent(this);
        bookListScreen.addDataChangeListener(searchBar);
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
                User user = new UserParser().parse(consumerSignedIn.executeAfterSignIn("http://api.douban.com/people/%40me?alt=json"));
                headerScreen.updateUser(user);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void retrieveAccessToken(DefaultOAuthConsumer consumer) throws OAuthMessageSignerException, OAuthNotAuthorizedException, OAuthExpectationFailedException, OAuthCommunicationException {
        consumer.setTokenWithSecret(requestToken, requestTokenSceret);
        authProvider.retrieveAccessToken(consumer, null);
        accessToken = consumer.getToken();
        accessTokenSceret = consumer.getTokenSecret();
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
        menu.findItem(R.id.menu_my_books).setVisible(headerScreen.isUserSignedIn());
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_more:
                bookListScreen.executeSearch(query);
                searchBar.showProgressBar();
                return true;
            case R.id.menu_my_books:
                bookListScreen.searchMyOwn(headerScreen.getUserId());
                return true;
            default:
                return false;
        }
    }
}
