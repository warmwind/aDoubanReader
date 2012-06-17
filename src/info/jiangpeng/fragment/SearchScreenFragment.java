package info.jiangpeng.fragment;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.*;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import info.jiangpeng.R;
import info.jiangpeng.SearchBar;
import info.jiangpeng.fragment.BookListFragment;
import info.jiangpeng.task.SearchTask;

public class SearchScreenFragment extends Fragment {

    private BookListFragment bookListFragment;
    private SearchBar searchBar;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bookListFragment = (BookListFragment) getFragmentManager().findFragmentById(R.id.main_book_list);
        searchBar = (SearchBar) getView().findViewById(R.id.search_bar);
        searchBar.initComponent(bookListFragment);

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.search, container, false);
    }
}
