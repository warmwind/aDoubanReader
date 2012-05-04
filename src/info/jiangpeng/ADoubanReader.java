package info.jiangpeng;

import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
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
        String rawString = intent.getStringExtra(SearchActivity.RAW_SEARCH_RESULT);
        if (rawString != null) {
            progressBar.setVisibility(View.VISIBLE);
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
            currentStatus = currentStatus + 1000 / bookListSize;
            progressBar.setProgress(currentStatus);
            if (!book.isEmpty()) {
                bookArrayAdapter.add(book);
                bookArrayAdapter.notifyDataSetChanged();
            }

            if(currentStatus >= 1000){
                progressBar.setProgress(1000);
                progressBar.setVisibility(View.GONE);
            }
        }
    }

}
