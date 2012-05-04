package info.jiangpeng;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class SearchActivity extends Activity {

    public static final String RAW_SEARCH_RESULT = "RAW_SEARCH_RESULT";
    private Intent showInMain;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.search_bar);
        progressBar = (ProgressBar) findViewById(R.id.search_progress_bar);
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);

            showInMain = new Intent(this, aDoubanReader.class);
            new Search().execute(query);
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    private String searchBookList(String query) throws IOException {
        Uri uri = new Uri.Builder().scheme("http").authority("api.douban.com").path("book/subjects").
                appendQueryParameter("alt", "json").
                appendQueryParameter("apikey", "0d5f0a33b677be10281d1e9b23673a30").
                appendQueryParameter("tag", query).build();

        HttpGet request = new HttpGet(uri.toString());

        return EntityUtils.toString(new DefaultHttpClient().execute(request).getEntity());
    }

    private class Search extends AsyncTask<String, Integer, String>{

        @Override
        protected String doInBackground(String... strings) {
            try {
                new Timer().scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        publishProgress(500);
                    }
                }, 0, 50);
                return searchBookList(strings[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "";
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            progressBar.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            progressBar.setProgress(1000);
            progressBar.setVisibility(View.GONE);
            showInMain.putExtra(RAW_SEARCH_RESULT, s);
            startActivity(showInMain);
        }
    }

}
