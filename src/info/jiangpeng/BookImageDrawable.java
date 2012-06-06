package info.jiangpeng;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;

import java.io.Serializable;

public class BookImageDrawable extends BitmapDrawable implements Serializable {
    public BookImageDrawable(Bitmap bitmap) {
        super(bitmap);
    }
}
