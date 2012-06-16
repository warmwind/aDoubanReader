package info.jiangpeng.activity;

import android.app.ListActivity;
import android.os.Bundle;
import info.jiangpeng.R;
import info.jiangpeng.adapter.BookAdapter;
import info.jiangpeng.adapter.BookDetailAdapter;
import info.jiangpeng.task.SearchDetailsTask;

public class BookDetailsActivity extends ListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.book_details_container);

        BookAdapter bookAdapter = new BookDetailAdapter(this, R.layout.book_detail, R.id.book_author);
        this.setListAdapter(bookAdapter);
        String detailsUrl = getIntent().getStringExtra("BOOK_DETAILS_URL");
        new SearchDetailsTask(bookAdapter).execute(detailsUrl);
    }

}
