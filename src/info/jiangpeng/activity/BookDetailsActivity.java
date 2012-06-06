package info.jiangpeng.activity;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import info.jiangpeng.BookImageDrawable;
import info.jiangpeng.R;
import info.jiangpeng.model.Book;

import java.io.IOException;
import java.net.URL;

public class BookDetailsActivity extends Activity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.book_details);

        Book book = (Book) getIntent().getSerializableExtra("BOOK");

        try {
            ((ImageView)findViewById(R.id.book_detail_image)).setImageDrawable(new BookImageDrawable(BitmapFactory.decodeStream(new URL(book.getImageUrl()).openStream())));
        } catch (IOException e) {
            e.printStackTrace();
        }
        ((TextView) findViewById(R.id.book_detail_title)).setText(book.getTitle());
        ((TextView) findViewById(R.id.book_detail_author)).setText(book.getAuthor());
        ((TextView) findViewById(R.id.book_detail_rate)).setText(book.getAverageRate());
        ((TextView) findViewById(R.id.book_detail_publisher)).setText(book.getPublisher());
        ((TextView) findViewById(R.id.book_detail_pubdate)).setText(book.getPubDate());
        ((TextView) findViewById(R.id.book_detail_status)).setText(book.getStatus());
        ((TextView) findViewById(R.id.book_detail_author_intro)).setText(book.getAuthorIntro());
        ((TextView) findViewById(R.id.book_detail_summary)).setText(book.getSummary());

    }
}
