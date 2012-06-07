package info.jiangpeng;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

public class BookDetailsScreen extends RelativeLayout {
    public BookDetailsScreen(Context context, AttributeSet attrs) {
        super(context, attrs);
        initUI();
    }

    private void initUI() {
        RelativeLayout layout = new RelativeLayout(getContext());
        LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        vi.inflate(R.layout.book_detail, layout, true);

        this.addView(layout);

    }

}
