package info.jiangpeng.task;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import info.jiangpeng.BookListFragment;
import info.jiangpeng.R;
import info.jiangpeng.activity.BookDetailsActivity;
import info.jiangpeng.helper.CommonBookParser;
import info.jiangpeng.model.Book;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class SearchDetailsTask extends AsyncTask<Book, Integer, Book> {


    private Activity activity;

    public SearchDetailsTask(Activity activity) {

        this.activity = activity;
    }

    @Override
    protected Book doInBackground(Book... books) {

        try {
            Book book = books[0];
            HttpGet request = new HttpGet(book.getBookDetailsUrl() + "?alt=json&apikey=0d5f0a33b677be10281d1e9b23673a30");
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
    protected void onPostExecute(Book book) {
        Intent intent = new Intent(activity, BookDetailsActivity.class);
        intent.putExtra("BOOK", book);
        activity.startActivity(intent);

    }
}
