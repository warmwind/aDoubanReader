package info.jiangpeng;

import android.graphics.drawable.BitmapDrawable;

public class Book {
    private String title;
    private String author;
    private String averageRate;
    private String bookUrlInWeb;
    private BitmapDrawable imageDrawable;

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getAuthor() {
        return author;
    }

    public void setAverageRate(String averageRate) {
        this.averageRate = averageRate;
    }

    public String getAverageRate() {
        return averageRate;
    }

    public void setBookUrlInWeb(String bookUrlInWeb) {
        this.bookUrlInWeb = bookUrlInWeb;
    }

    public String getBookUrlInWeb() {
        return bookUrlInWeb;
    }

    public void setImageDrawable(BitmapDrawable imageDrawable) {
        this.imageDrawable = imageDrawable;
    }

    public BitmapDrawable getImageDrawable() {
        return imageDrawable;
    }

    public boolean  isEmpty(){
        return title == null || title.isEmpty();
    }
}
