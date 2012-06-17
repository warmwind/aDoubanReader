package info.jiangpeng;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import info.jiangpeng.activity.MainActivity;
import info.jiangpeng.fragment.BookListFragment;
import info.jiangpeng.task.SearchTask;

public class SearchBar extends FrameLayout implements DataChangeListener {
    private ProgressBar progressBar;
    private int currentStatus;
    public static final int PROGRESS_BAR_MAX = 1000;
    private Activity activity;
    private EditText searchArea;
    private BookListFragment bookListFragment;
    private ImageView spinnerImageView;
    private AnimationDrawable spinnerAnimation;
    private Context context;

    public SearchBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initUI();
    }

    private void initUI() {
        FrameLayout frameLayout = new FrameLayout(getContext());
        LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        vi.inflate(R.layout.search_bar, frameLayout, true);

        this.addView(frameLayout);
        searchArea = (EditText) findViewById(R.id.search_text);
        searchArea.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    executeSearch();
                    return true;
                }
                return false;
            }
        });
        progressBar = (ProgressBar) findViewById(R.id.search_progress_bar);

    }

    @Override
    public void update() {
        progressBar.setVisibility(View.VISIBLE);
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

    public void initComponent(BookListFragment bookListFragment) {
        this.bookListFragment = bookListFragment;
        bookListFragment.addDataChangeListener(this);
    }

    private void executeSearch() {
        bookListFragment.resetResultList();
        String keyWord = searchArea.getText().toString();
        if (!keyWord.trim().equals("")) {
            showProgressBar();
            new SearchTask(bookListFragment, this).execute(keyWord);
            searchArea.clearFocus();
            hideKeyBoard();
        }
    }

    private void hideKeyBoard() {
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(((MainActivity) getContext()).getCurrentFocus().getWindowToken(), 0);
    }

    public void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
    }
}
