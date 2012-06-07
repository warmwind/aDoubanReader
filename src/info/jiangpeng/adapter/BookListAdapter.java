package info.jiangpeng.adapter;

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
import info.jiangpeng.R;
import info.jiangpeng.model.Book;

import java.io.IOException;
import java.net.URL;

public class BookListAdapter extends BookAdapter {

    public BookListAdapter(Context context, int resource, int textViewResourceId) {
        super(context, resource, textViewResourceId);
    }
}
