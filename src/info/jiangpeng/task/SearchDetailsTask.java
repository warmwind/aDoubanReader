package info.jiangpeng.task;

import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;
import android.widget.TextView;
import info.jiangpeng.BookImageDrawable;
import info.jiangpeng.R;
import info.jiangpeng.adapter.BookAdapter;
import info.jiangpeng.helper.CommonBookParser;
import info.jiangpeng.model.Book;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

public class SearchDetailsTask extends AsyncTask<String, Integer, Book> {


    private BookAdapter adapter;

    public SearchDetailsTask(BookAdapter adapter) {

        this.adapter = adapter;
    }

    @Override
    protected Book doInBackground(String... strings) {

        try {
            HttpGet request = new HttpGet(strings[0] + "?alt=json&apikey=0d5f0a33b677be10281d1e9b23673a30");
            String rawJson = EntityUtils.toString(new DefaultHttpClient().execute(request).getEntity());
            return new CommonBookParser().parse(new JSONObject(rawJson));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new Book();
    }

    @Override
    protected void onPostExecute(final Book book) {
        adapter.add(book);
    }


}
