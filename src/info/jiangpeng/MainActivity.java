package info.jiangpeng;

import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import info.jiangpeng.helper.AccountParser;
import info.jiangpeng.helper.CommonBookParser;
import info.jiangpeng.helper.MyBookParser;
import info.jiangpeng.sign.OAuthFactory;
import oauth.signpost.basic.DefaultOAuthConsumer;
import oauth.signpost.basic.DefaultOAuthProvider;
import oauth.signpost.basic.UrlStringRequestAdapter;
import oauth.signpost.http.HttpRequest;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class MainActivity extends ListActivity {

    public static final String USER = "USER";
    public static final String USER_ID = "USER_ID";
    private SearchResultAdapter bookArrayAdapter;
    private int currentStatus;
    private int bookListSize;
    private ProgressBar progressBar;
    private String query;
    private boolean canLoadMore;
    private static  String accessToken;
    private static  String accessTokenSceret;
    private static  String requestToken;
    private static  String requestTokenSceret;

    private SharedPreferences preferences;
    private DefaultOAuthConsumer consumer;
    private DefaultOAuthProvider authProvider;
    private TextView signIn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);

        signIn = (TextView) findViewById(R.id.user);
        preferences = getPreferences(MODE_PRIVATE);
        String user = preferences.getString(USER, "");

        if (user.equals("")) {
            signIn.setText(R.string.sign_in);
        } else {
            signIn.setText(user);
        }

        consumer = OAuthFactory.createConsumer();
        authProvider = new DefaultOAuthProvider("http://www.douban.com/service/auth/request_token", "http://www.douban.com/service/auth/access_token", "http://www.douban.com/service/auth/authorize");

        signIn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_UP:
                        if (preferences.getString(USER, "").equals("")) {
                            try {

                                String url1 = authProvider.retrieveRequestToken(consumer, "vtbapp-doudou:///");

                                requestToken = consumer.getToken();
                                requestTokenSceret = consumer.getTokenSecret();
                                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url1));
                                startActivity(browserIntent);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        } else {
                            SharedPreferences.Editor edit = preferences.edit();
                            edit.putString(USER, "");
                            edit.commit();
                            signIn.setText(R.string.sign_in);
                        }
                        return true;
                    default:
                        return true;
                }
            }
        });

        initSearchBar();

        initAdapter();
        progressBar = (ProgressBar) findViewById(R.id.search_progress_bar);

        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            query = intent.getStringExtra(SearchManager.QUERY);
            new Search(bookArrayAdapter.getCount()).execute(query);
            progressBar.setVisibility(View.VISIBLE);

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
                preferences = getPreferences(MODE_PRIVATE);

                consumer.setTokenWithSecret(requestToken, requestTokenSceret);
                authProvider.retrieveAccessToken(consumer, null);
                accessToken = consumer.getToken();
                accessTokenSceret = consumer.getTokenSecret();


                consumer.setTokenWithSecret(accessToken, accessTokenSceret);

                HttpRequest request = consumer.sign(new UrlStringRequestAdapter("http://api.douban.com/people/%40me?alt=json"));
                String requestUrl = request.getRequestUrl();
                String s1 = EntityUtils.toString(new DefaultHttpClient().execute(new HttpGet(requestUrl)).getEntity());

                System.out.println("------------s1 = " + s1);

                Account account = new AccountParser().parse(s1);
                SharedPreferences.Editor edit = preferences.edit();
                edit.putString(USER, account.getName());
                edit.putString(USER_ID, account.getId());
                edit.commit();
                signIn.setText(account.getName());

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void executeSearch() {
        new Search(bookArrayAdapter.getCount()).execute(query);
        canLoadMore = false;
    }

    private void initAdapter() {
        ListView mainView = getListView();
        bookArrayAdapter = new SearchResultAdapter(this, R.layout.book_item, R.id.book_title);
        mainView.setAdapter(bookArrayAdapter);
    }

    private void initSearchBar() {
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        SearchView searchView = (SearchView) findViewById(R.id.search);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Book book = bookArrayAdapter.getItem(position);
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
        if (preferences.getString(USER, "").equals("")) {
            menu.findItem(R.id.menu_my_books).setVisible(false);
        } else {
            menu.findItem(R.id.menu_my_books).setVisible(true);
        }
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
                    bookArrayAdapter.clear();
                    preferences = getPreferences(MODE_PRIVATE);

                    consumer = OAuthFactory.createConsumer();

                    consumer.setTokenWithSecret(accessToken, accessTokenSceret);
                    String userId = preferences.getString(USER_ID, "");

                    String requestUrl = consumer.sign(new UrlStringRequestAdapter("http://api.douban.com/people/"+ userId+"/collection?cat=book&alt=json")).getRequestUrl();
                    String s1 = EntityUtils.toString(new DefaultHttpClient().execute(new HttpGet(requestUrl)).getEntity());


                    JSONObject jsonObject = new JSONObject(s1);
                    JSONArray entry = jsonObject.getJSONArray("entry");
                    int length = entry.length();
                    for (int i = 0; i < length; i++) {

                        bookArrayAdapter.add(new MyBookParser().parse(entry.getJSONObject(i)));
                    }
                    bookArrayAdapter.notifyDataSetChanged();

                } catch (Exception e) {
                    e.printStackTrace();
                }

                return true;
            default:
                return false;
        }
    }

    private void parseBookList(String rawString) {
        try {
            JSONArray entryArray = new JSONObject(rawString).getJSONArray("entry");

            bookListSize = entryArray.length();
            for (int i = 0; i < bookListSize; i++) {
                new BookParserTask().execute(entryArray.getJSONObject(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class Search extends AsyncTask<String, Integer, String> {

        private int startIndex;

        private Search(int startIndex) {
            this.startIndex = startIndex;
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                return searchBookList(strings[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "";
        }

        @Override
        protected void onPostExecute(String s) {
            parseBookList(s);
            bookArrayAdapter.notifyDataSetChanged();
        }

        private String searchBookList(String query) throws IOException {
            Uri uri = new Uri.Builder().scheme("http").authority("api.douban.com").path("book/subjects").
                    appendQueryParameter("alt", "json").
                    appendQueryParameter("apikey", "0d5f0a33b677be10281d1e9b23673a30").
                    appendQueryParameter("max-results", "20").
                    appendQueryParameter("start-index", String.valueOf(startIndex)).
                    appendQueryParameter("q", query).build();

            HttpGet request = new HttpGet(uri.toString());

            return EntityUtils.toString(new DefaultHttpClient().execute(request).getEntity());
        }
    }


    private class BookParserTask extends AsyncTask<JSONObject, Integer, Book> {

        public static final int PROGRESS_BAR_MAX = 1000;

        @Override
        protected Book doInBackground(JSONObject... jsonObjects) {
            try {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.VISIBLE);
                    }
                });
                return new CommonBookParser().parse(jsonObjects[0]);
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return new Book();
        }

        @Override
        protected void onPostExecute(final Book book) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    currentStatus = currentStatus + PROGRESS_BAR_MAX / bookListSize;
                    progressBar.setProgress(currentStatus);
                    if (!book.isEmpty()) {
                        bookArrayAdapter.add(book);
                        bookArrayAdapter.notifyDataSetChanged();
                    }

                    if (currentStatus >= PROGRESS_BAR_MAX) {
                        progressBar.setProgress(PROGRESS_BAR_MAX);
                        progressBar.setVisibility(View.GONE);
                        canLoadMore = true;
                        currentStatus = 0;
                        makeToast();
                    }
                }
            });
        }
    }

    private void makeToast() {
        View layout = getLayoutInflater().inflate(R.layout.toast, (ViewGroup) findViewById(R.id.toast));

        TextView text = (TextView) layout.findViewById(R.id.toast_message);
        text.setText(bookListSize + " book(s) loaded");
        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.CENTER, 0, 50);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }

}
