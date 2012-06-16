package info.jiangpeng;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import info.jiangpeng.task.SearchTask;

public class SearchScreenFragment extends Fragment {

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final EditText searchText = (EditText) getView().findViewById(R.id.search_text);

        final BookListFragment bookListFragment = (BookListFragment) getFragmentManager().findFragmentById(R.id.main_book_list);

        final ImageView searchImage = (ImageView) getView().findViewById(R.id.search_icon);
        searchImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String keyWord = searchText.getText().toString();
                if (!keyWord.trim().equals("")) {
                    new SearchTask(bookListFragment).execute(keyWord);
                    InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getActivity().getWindow().getCurrentFocus().getWindowToken(), 0);
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.search, container, false);
    }


}
