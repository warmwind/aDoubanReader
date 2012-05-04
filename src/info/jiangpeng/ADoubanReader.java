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
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.*;
import org.apache.http.client.ClientProtocolException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

public class aDoubanReader extends ListActivity {

    private SearchResultAdapter bookArrayAdapter;

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

        Intent intent = getIntent();
        String rawString = intent.getStringExtra(SearchActivity.RAW_SEARCH_RESULT);
        if (rawString != null) {
            parseBookList(rawString);
            bookArrayAdapter.notifyDataSetChanged();
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

    private void parseBookList(String rawString) {
        try {
            JSONArray entryArray = new JSONObject(rawString).getJSONArray("entry");

            int resultNumber = entryArray.length();
            for (int i = 0; i < resultNumber; i++) {
                new BookParserTask().execute(entryArray.getJSONObject(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private Book parseBook(JSONObject jsonBook) throws JSONException, IOException {
        Book book = new Book();

        book.setTitle(jsonBook.getJSONObject("title").getString("$t"));
        book.setBookUrlInWeb(jsonBook.getJSONArray("link").getJSONObject(1).getString("@href"));

        String imageUrl = jsonBook.getJSONArray("link").getJSONObject(2).getString("@href");
        book.setImageDrawable(new BitmapDrawable(BitmapFactory.decodeStream(new URL(imageUrl).openStream())));

        book.setAuthor(jsonBook.getJSONArray("author").getJSONObject(0).getJSONObject("name").getString("$t"));
        book.setAverageRate(jsonBook.getJSONObject("gd:rating").getString("@average"));
        return book;
    }

    private class BookParserTask extends AsyncTask<JSONObject, Integer, Book> {

        @Override
        protected Book doInBackground(JSONObject... jsonObjects) {
            try {
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
            if (!book.isEmpty()) {
                bookArrayAdapter.add(book);
                bookArrayAdapter.notifyDataSetChanged();
            }
        }
    }

}
