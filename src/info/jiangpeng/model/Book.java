package info.jiangpeng.model;

import android.graphics.drawable.BitmapDrawable;
import info.jiangpeng.BookImageDrawable;

import java.io.Serializable;

public class Book implements Serializable{
    private String title;
    private String author;
    private String averageRate;
    private String bookUrlInWeb;
    private BookImageDrawable imageDrawable;
    private String publisher;
    private String pubDate;
    private String status;
    private String bookDetailsUrl;
    private String summary;
    private String authorIntro;
    private String imageUrl;

    public Book() {
    }

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

    public void setImageDrawable(BookImageDrawable imageDrawable) {
        this.imageDrawable = imageDrawable;
    }

    public BookImageDrawable getImageDrawable() {
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

    public void setBookDetailsUrl(String bookDetailsUrl) {
        this.bookDetailsUrl = bookDetailsUrl;
    }

    public String getBookDetailsUrl() {
        return bookDetailsUrl;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getSummary() {
        return summary;
    }

    public void setAuthorIntro(String authorIntro) {
        this.authorIntro = authorIntro;
    }

    public String getAuthorIntro() {
        return authorIntro;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
