package info.jiangpeng.activity;

import android.app.Activity;
import android.os.Bundle;
import info.jiangpeng.R;
import info.jiangpeng.task.SearchDetailsTask;

public class BookDetailsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.book_details);

        //If remove it, scroll in details screen will be very slow
        findViewById(R.id.book_detail_image).setDrawingCacheEnabled(true);

        String detailsUrl = getIntent().getStringExtra("BOOK_DETAILS_URL");
        new SearchDetailsTask(this).execute(detailsUrl);
    }
}
