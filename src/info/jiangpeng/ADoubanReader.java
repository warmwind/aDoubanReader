package info.jiangpeng;

import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

public class aDoubanReader extends ListActivity {

    private SearchResultAdapter bookArrayAdapter;
    private int currentStatus;
    private int bookListSize;
    private ProgressBar progressBar;
    private String query;
    private boolean canLoadMore;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);

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
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_more:
                executeSearch();
                return true;
            case R.id.menu_signin:
                startActivity(new Intent(getApplicationContext(), SigninScreen.class));
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

}
