package info.jiangpeng.activity;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ListView;
import info.jiangpeng.R;
import info.jiangpeng.adapter.BookAdapter;
import info.jiangpeng.adapter.BookDetailAdapter;
import info.jiangpeng.task.SearchDetailsTask;

public class BookDetailsActivity extends ListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.book_details_container);
        ListView listView = getListView();

        //make the details view not clickable
        listView.setSelector(android.R.color.transparent);
        BookAdapter bookAdapter = new BookDetailAdapter(this, R.layout.book_detail, R.id.book_author);
        this.setListAdapter(bookAdapter);
        String detailsUrl = getIntent().getStringExtra("BOOK_DETAILS_URL");
        new SearchDetailsTask(bookAdapter).execute(detailsUrl);
    }

}
