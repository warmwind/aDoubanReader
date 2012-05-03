package info.jiangpeng;

import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;
import org.apache.http.client.ClientProtocolException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;

public class aDoubanReader extends ListActivity {

    private SearchResultAdapter bookArrayAdapter;

    /**
     * Called when the activity is first created.
     */
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
        System.out.println("------------rawString = " + rawString);
        if (rawString != null) {
            parseBookList(rawString);

            bookArrayAdapter.notifyDataSetChanged();
        }


    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Book book = bookArrayAdapter.getItem(position);
        Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(book.getBookUrlInWeb()));
        startActivity(myIntent);
    }

    private void parseBookList(String rawString) {
        try {

            JSONObject jsonObject = new JSONObject(rawString);
            JSONArray entryArray = jsonObject.getJSONArray("entry");

            int resultNumber = entryArray.length();
            for (int i = 0; i < resultNumber; i++) {
                JSONObject jsonBook = entryArray.getJSONObject(i);
                Book book = new Book();
                book.setTitle(jsonBook.getJSONObject("title").getString("$t"));
                book.setBookUrlInWeb(jsonBook.getJSONArray("link").getJSONObject(1).getString("@href"));
                book.setImageUrl(jsonBook.getJSONArray("link").getJSONObject(2).getString("@href"));
                book.setAuthor(jsonBook.getJSONArray("author").getJSONObject(0).getJSONObject("name").getString("$t"));
                book.setAverageRate(jsonBook.getJSONObject("gd:rating").getString("@average"));

                bookArrayAdapter.add(book);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
