package info.jiangpeng.adapter;

import android.content.Context;
import android.widget.LinearLayout;
import android.widget.TextView;
import info.jiangpeng.R;
import info.jiangpeng.model.Book;

public class BookDetailAdapter extends BookAdapter{

    public BookDetailAdapter(Context context, int resource, int textViewResourceId) {
        super(context, resource, textViewResourceId);
    }

    @Override
    protected void fillContentToView(Book book, LinearLayout bookLayout) {
        super.fillContentToView(book,bookLayout);
        ((TextView) bookLayout.findViewById(R.id.book_author_intro)).setText(book.getAuthorIntro());
        ((TextView) bookLayout.findViewById(R.id.book_summary)).setText(book.getSummary());

    }
}
