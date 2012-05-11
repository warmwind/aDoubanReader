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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
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


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        SearchView searchView = (SearchView) findViewById(R.id.search);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        ListView mainView = getListView();
        bookArrayAdapter = new SearchResultAdapter(this, R.layout.book_item, R.id.book_title);
        mainView.setAdapter(bookArrayAdapter);

        progressBar = (ProgressBar) findViewById(R.id.search_progress_bar);

        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            new Search().execute(query);
            progressBar.setVisibility(View.VISIBLE);
        }
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
    public boolean onPrepareOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refresh:
                System.out.println("-----refreshed");
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

    private String searchBookList(String query) throws IOException {
        Uri uri = new Uri.Builder().scheme("http").authority("api.douban.com").path("book/subjects").
                appendQueryParameter("alt", "json").
                appendQueryParameter("apikey", "0d5f0a33b677be10281d1e9b23673a30").
                appendQueryParameter("max-results", "20").
                appendQueryParameter("q", query).build();

        HttpGet request = new HttpGet(uri.toString());

        return EntityUtils.toString(new DefaultHttpClient().execute(request).getEntity());
    }


    private class Search extends AsyncTask<String, Integer, String> {

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
    }


    private class BookParserTask extends AsyncTask<JSONObject, Integer, Book> {

        public static final int PROGRESS_BAR_MAX = 1000;

        @Override
        protected Book doInBackground(JSONObject... jsonObjects) {
            try {
                progressBar.setVisibility(View.VISIBLE);
                return parseBook(jsonObjects[0]);
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return new Book();
        }

        @Override
        protected void onPostExecute(Book book) {
            currentStatus = currentStatus + PROGRESS_BAR_MAX / bookListSize;
            progressBar.setProgress(currentStatus);
            if (!book.isEmpty()) {
                bookArrayAdapter.add(book);
                bookArrayAdapter.notifyDataSetChanged();
            }

            if (currentStatus >= PROGRESS_BAR_MAX) {
                progressBar.setProgress(PROGRESS_BAR_MAX);
                progressBar.setVisibility(View.GONE);
            }
        }
    }

}
