package info.jiangpeng;

import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import oauth.signpost.basic.DefaultOAuthConsumer;
import oauth.signpost.basic.DefaultOAuthProvider;
import oauth.signpost.basic.UrlStringRequestAdapter;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.http.HttpRequest;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

public class aDoubanReader extends ListActivity {

    public static final String USER = "USER";
    public static final String USER_ID = "USER_ID";
    private SearchResultAdapter bookArrayAdapter;
    private int currentStatus;
    private int bookListSize;
    private ProgressBar progressBar;
    private String query;
    private boolean canLoadMore;
    public static final String ACCESS_TOKEN = "ACCESS_TOKEN";
    public static final String ACCESS_TOKEN_SECRET = "ACCESS_TOKEN_SECRET";
    public static final String REQUEST_TOKEN = "REQUEST_TOKEN";
    public static final String REQUEST_TOKEN_SECRET = "REQUEST_TOKEN_SECRET";
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

        String consumerKey = "0d5f0a33b677be10281d1e9b23673a30";
        String consumerSecret = "d66dc447cdfa7eeb";
        consumer = new DefaultOAuthConsumer(consumerKey, consumerSecret);
        authProvider = new DefaultOAuthProvider("http://www.douban.com/service/auth/request_token", "http://www.douban.com/service/auth/access_token", "http://www.douban.com/service/auth/authorize");

        signIn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_UP:
                        if (preferences.getString(USER, "").equals("")) {
                            try {

                                String url1 = authProvider.retrieveRequestToken(consumer, "vtbapp-doudou:///");
                                setRequestToken();
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

                consumer.setTokenWithSecret(preferences.getString(REQUEST_TOKEN, ""), preferences.getString(REQUEST_TOKEN_SECRET, ""));
                authProvider.retrieveAccessToken(consumer, null);
                setAccessToken(preferences);


                consumer.setTokenWithSecret(preferences.getString(ACCESS_TOKEN, ""), preferences.getString(ACCESS_TOKEN_SECRET, ""));

                HttpRequest request = consumer.sign(new UrlStringRequestAdapter("http://api.douban.com/people/%40me?alt=json"));
                String requestUrl = request.getRequestUrl();
                String s1 = EntityUtils.toString(new DefaultHttpClient().execute(new HttpGet(requestUrl)).getEntity());

                System.out.println("------------s1 = " + s1);
                JSONObject jsonObject = new JSONObject(s1);
                String userName = jsonObject.getJSONObject("title").getString("$t");
                String userId = jsonObject.getJSONObject("db:uid").getString("$t");


                SharedPreferences.Editor edit = preferences.edit();
                edit.putString(USER, userName);
                edit.putString(USER_ID, userId);
                edit.commit();
                signIn.setText(userName);

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

                    String consumerKey = "0d5f0a33b677be10281d1e9b23673a30";
                    String consumerSecret = "d66dc447cdfa7eeb";
                    consumer = new DefaultOAuthConsumer(consumerKey, consumerSecret);

                    consumer.setTokenWithSecret(preferences.getString(ACCESS_TOKEN, ""), preferences.getString(ACCESS_TOKEN_SECRET, ""));
                    String userId = preferences.getString(USER_ID, "");
                    System.out.println("------------userId = " + userId);
                    String requestUrl = consumer.sign(new UrlStringRequestAdapter("http://api.douban.com/people/"+ userId+"/collection?cat=book&alt=json")).getRequestUrl();
                    String s1 = EntityUtils.toString(new DefaultHttpClient().execute(new HttpGet(requestUrl)).getEntity());
                    System.out.println("------------s1 = " + s1);

                    JSONObject jsonObject = new JSONObject(s1);
                    JSONArray entry = jsonObject.getJSONArray("entry");
                    int length = entry.length();
                    for (int i = 0; i < length; i++) {
                        Book book = new Book();
                        JSONObject jsonBook = entry.getJSONObject(i).getJSONObject("db:subject");
                        book.setTitle(jsonBook.getJSONObject("title").getString("$t"));
                        JSONArray jsonAttribute = jsonBook.getJSONArray("db:attribute");
                        book.setPublisher(jsonAttribute.getJSONObject(4).getString("$t"));
                        book.setStatus(entry.getJSONObject(i).getJSONObject("db:status").getString("$t"));

                        bookArrayAdapter.add(book);
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

    private Book parseBook(JSONObject jsonBook) throws JSONException, IOException {
        Book book = new Book();

        book.setTitle(jsonBook.getJSONObject("title").getString("$t"));
        parseLinkJson(jsonBook, book);

        book.setAuthor(jsonBook.getJSONArray("author").getJSONObject(0).getJSONObject("name").getString("$t"));
        book.setAverageRate(jsonBook.getJSONObject("gd:rating").getString("@average"));

        parseMetadataJson(jsonBook, book);

        return book;
    }

    private void parseMetadataJson(JSONObject jsonBook, Book book) throws JSONException {
        JSONArray metadataArray = jsonBook.getJSONArray("db:attribute");
        for (int i = 0; i < metadataArray.length(); i++) {
            JSONObject metaJson = metadataArray.getJSONObject(i);
            if (metaJson.getString("@name").equals("publisher")) {
                book.setPublisher(metaJson.getString("$t"));
            } else if (metaJson.getString("@name").equals("pubdate")) {
                book.setPubDate(metaJson.getString("$t"));
            }
        }
    }

    private void parseLinkJson(JSONObject jsonBook, Book book) throws JSONException, IOException {
        JSONArray linkArray = jsonBook.getJSONArray("link");
        for (int i = 0; i < linkArray.length(); i++) {
            JSONObject linkJson = linkArray.getJSONObject(i);
            if (linkJson.getString("@rel").equals("alternate")) {
                book.setBookUrlInWeb(linkJson.getString("@href"));
            } else if (linkJson.getString("@rel").equals("image")) {
                String imageUrl = linkJson.getString("@href");
                book.setImageDrawable(new BitmapDrawable(BitmapFactory.decodeStream(new URL(imageUrl).openStream())));
            }
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
                return parseBook(jsonObjects[0]);
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

    private void setRequestToken() {
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor edit = preferences.edit();
        edit.putString(REQUEST_TOKEN, consumer.getToken());
        edit.putString(REQUEST_TOKEN_SECRET, consumer.getTokenSecret());
        edit.commit();
    }

    private void setAccessToken(SharedPreferences preferences) {
        SharedPreferences.Editor edit = preferences.edit();
        edit.putString(ACCESS_TOKEN, consumer.getToken());
        edit.putString(ACCESS_TOKEN_SECRET, consumer.getTokenSecret());
        edit.commit();
    }

}
