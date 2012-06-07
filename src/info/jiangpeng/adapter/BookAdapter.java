package info.jiangpeng.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import info.jiangpeng.R;
import info.jiangpeng.model.Book;

public class BookAdapter extends ArrayAdapter<Book>{

    protected int resource;

    public BookAdapter(Context context, int resource, int textViewResourceId) {
        super(context, resource, textViewResourceId);
        this.resource = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        super.getView(position, convertView, parent);

        LinearLayout bookLayout;
        if (convertView == null) {
            bookLayout = new LinearLayout(getContext());
            String inflater = Context.LAYOUT_INFLATER_SERVICE;
            LayoutInflater vi = (LayoutInflater) getContext().getSystemService(inflater);
            vi.inflate(resource, bookLayout, true);
        } else {
            bookLayout = (LinearLayout) convertView;
        }

        fillContentToView(getItem(position), bookLayout);

        return bookLayout;
    }

    protected void fillContentToView(Book book, LinearLayout bookLayout) {
        ((ImageView) bookLayout.findViewById(R.id.book_image)).setImageDrawable(book.getImageDrawable());
        ((TextView) bookLayout.findViewById(R.id.book_title)).setText(book.getTitle());
        ((TextView) bookLayout.findViewById(R.id.book_author)).setText(book.getAuthor());
        ((TextView) bookLayout.findViewById(R.id.book_rate)).setText(book.getAverageRate());
        ((TextView) bookLayout.findViewById(R.id.book_publisher)).setText(book.getPublisher());
        ((TextView) bookLayout.findViewById(R.id.book_pubdate)).setText(book.getPubDate());
        ((TextView) bookLayout.findViewById(R.id.book_status)).setText(book.getStatus());
    }
}
