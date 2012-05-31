package info.jiangpeng;

import android.app.SearchManager;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;

public class SearchBar extends FrameLayout implements DataChangeListener {
    private ProgressBar progressBar;
    private int currentStatus;
    public static final int PROGRESS_BAR_MAX = 1000;
    private MainActivity mainActivity;

    public SearchBar(Context context) {
        super(context);
        initUI();
    }

    public SearchBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        initUI();
    }

    private void initUI() {
        FrameLayout frameLayout = new FrameLayout(getContext());
        LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        vi.inflate(R.layout.search_bar, frameLayout, true);

        this.addView(frameLayout);

        progressBar = (ProgressBar) findViewById(R.id.search_progress_bar);
    }

    @Override
    public void update() {
        currentStatus += PROGRESS_BAR_MAX / 10;
        progressBar.setProgress(currentStatus);

        if (currentStatus >= PROGRESS_BAR_MAX) {
            progressBar.setProgress(PROGRESS_BAR_MAX);
            progressBar.setVisibility(View.GONE);
            currentStatus = 0;
        }

    }
    public void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    public void initComponent(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        initSearchBar();
    }

    private void initSearchBar() {
        SearchManager searchManager = (SearchManager) mainActivity.getSystemService(Context.SEARCH_SERVICE);

        SearchView searchView = (SearchView) findViewById(R.id.search);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(mainActivity.getComponentName()));
    }


}
