package info.jiangpeng;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

public class SearchActivity extends Activity {

    public static final String RAW_SEARCH_RESULT = "RAW_SEARCH_RESULT";
    public static final int SEARCH = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);

            Intent showInMain = new Intent(this, aDoubanReader.class);
            try {
                String value = searchBookList(query);
                showInMain.putExtra(RAW_SEARCH_RESULT, value);
            } catch (IOException e) {
                e.printStackTrace();
            }
            startActivity(showInMain);
            finish();
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

}
