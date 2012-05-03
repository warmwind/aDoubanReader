package info.jiangpeng;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;
import java.net.URL;

public class SearchResultAdapter extends ArrayAdapter<Book> {

    private int resource;

    public SearchResultAdapter(Context context, int resource, int textViewResourceId) {
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

    private void fillContentToView(Book item, LinearLayout bookLayout) {
        ((ImageView) bookLayout.findViewById(R.id.book_image)).setImageDrawable(item.getImageDrawable());
        ((TextView) bookLayout.findViewById(R.id.book_title)).setText(item.getTitle());
        ((TextView) bookLayout.findViewById(R.id.book_author)).setText(item.getAuthor());
        ((TextView) bookLayout.findViewById(R.id.book_rate)).setText(item.getAverageRate());
    }

}
