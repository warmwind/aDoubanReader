package info.jiangpeng.task;

import android.net.Uri;
import android.os.AsyncTask;
import info.jiangpeng.BookListScreen;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SearchTask extends AsyncTask<String, Integer, String> {

    private BookListScreen bookListScreen;

    public SearchTask(BookListScreen bookListScreen) {
        this.bookListScreen = bookListScreen;
    }

    @Override
    protected String doInBackground(String... strings) {
        try {
            return searchBookList(strings[0]);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    protected void onPostExecute(String s) {
        parseBookList(s);
    }

    private String searchBookList(String query) throws Exception {
        Uri uri = new Uri.Builder().scheme("http").authority("api.douban.com").path("book/subjects").
                appendQueryParameter("alt", "json").
                appendQueryParameter("apikey", "0d5f0a33b677be10281d1e9b23673a30").
                appendQueryParameter("max-results", "20").
                appendQueryParameter("start-index", String.valueOf(bookListScreen.getBookCount())).
                appendQueryParameter("q", query).build();

        HttpGet request = new HttpGet(uri.toString());

        return EntityUtils.toString(new DefaultHttpClient().execute(request).getEntity());
    }

    private void parseBookList(String rawString) {
        try {
            JSONArray entryArray = new JSONObject(rawString).getJSONArray("entry");

            int bookListSize = entryArray.length();
            for (int i = 0; i < bookListSize; i++) {
                new BookParserTask(bookListScreen).execute(entryArray.getJSONObject(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
