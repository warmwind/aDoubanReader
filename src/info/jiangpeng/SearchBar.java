package info.jiangpeng;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

public class SearchBar extends FrameLayout{
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
    }
}
