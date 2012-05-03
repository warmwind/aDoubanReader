package info.jiangpeng;

public class Book {
    private String title;
    private String imageUrl;
    private String author;
    private String averageRate;
    private String bookUrlInWeb;

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public String toString() {
        return "Book{" +
                "title='" + title + '\'' +
                '}';
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
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
}
