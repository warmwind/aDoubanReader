package info.jiangpeng.task;

import android.net.Uri;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;
import info.jiangpeng.fragment.BookListFragment;
import info.jiangpeng.helper.CommonBookParser;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SearchTask extends AsyncTask<String, Integer, String> {

    private BookListFragment bookListFragment;
    private ProgressBar progressBar;

    public SearchTask(BookListFragment bookListFragment, ProgressBar progressBar) {
        this.bookListFragment = bookListFragment;
        this.progressBar = progressBar;
    }

    @Override
    protected String doInBackground(String... strings) {
        try {
            progressBar.setProgress(500);
            return searchBookList(strings[0]);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    protected void onPostExecute(String s) {
        progressBar.setProgress(1000);
        hideProgressBar();
        parseBookList(s);

    }

    private String searchBookList(String keyWord) throws Exception {
        Uri uri = new Uri.Builder().scheme("http").authority("api.douban.com").path("book/subjects").
                appendQueryParameter("alt", "json").
                appendQueryParameter("apikey", "0d5f0a33b677be10281d1e9b23673a30").
                appendQueryParameter("max-results", "20").
                appendQueryParameter("start-index", String.valueOf(bookListFragment.getBookCount())).
                appendQueryParameter("q", keyWord).build();

        HttpGet request = new HttpGet(uri.toString());

        return EntityUtils.toString(new DefaultHttpClient().execute(request).getEntity());
    }

    private void parseBookList(String rawString) {
        try {
            System.out.println("------------rawString = " + rawString);
            JSONArray entryArray = new JSONObject(rawString).getJSONArray("entry");
            progressBar.setVisibility(View.VISIBLE);
            int bookListSize = entryArray.length();
            for (int i = 0; i < bookListSize; i++) {
                new BookParseTask(bookListFragment, new CommonBookParser()).execute(entryArray.getJSONObject(i));
            }
        } catch (JSONException e) {
            hideProgressBar();
            e.printStackTrace();
        }
    }

    private void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
    }

}
