package info.jiangpeng.task;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import info.jiangpeng.BookImageDrawable;
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
import java.net.URL;

public class SearchDetailsTask extends AsyncTask<String, Integer, Book> {


    private Activity activity;

    public SearchDetailsTask(Activity activity) {

        this.activity = activity;
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
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    ((ImageView) activity.findViewById(R.id.book_detail_image)).setImageDrawable(new BookImageDrawable(BitmapFactory.decodeStream(new URL(book.getImageUrl()).openStream())));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                ((TextView) activity.findViewById(R.id.book_detail_title)).setText(book.getTitle());
                ((TextView) activity.findViewById(R.id.book_detail_author)).setText(book.getAuthor());
                ((TextView) activity.findViewById(R.id.book_detail_rate)).setText(book.getAverageRate());
                ((TextView) activity.findViewById(R.id.book_detail_publisher)).setText(book.getPublisher());
                ((TextView) activity.findViewById(R.id.book_detail_pubdate)).setText(book.getPubDate());
                ((TextView) activity.findViewById(R.id.book_detail_status)).setText(book.getStatus());
                ((TextView) activity.findViewById(R.id.book_detail_author_intro)).setText(book.getAuthorIntro());
                ((TextView) activity.findViewById(R.id.book_detail_summary)).setText(book.getSummary());
            }
        });

    }


}
