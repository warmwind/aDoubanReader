package info.jiangpeng;

import android.graphics.drawable.BitmapDrawable;

public class Book {
    private String title;
    private String author;
    private String averageRate;
    private String bookUrlInWeb;
    private BitmapDrawable imageDrawable;
    private String publisher;
    private String pubDate;
    private String status;

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

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public void setPubDate(String pubDate) {
        this.pubDate = pubDate;
    }

    public String getPublisher() {
        return publisher;
    }

    public String getPubDate() {
        return pubDate;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
