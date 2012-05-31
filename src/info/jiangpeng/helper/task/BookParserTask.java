package info.jiangpeng.helper.task;

import android.os.AsyncTask;
import info.jiangpeng.BookListScreen;
import info.jiangpeng.helper.CommonBookParser;
import info.jiangpeng.model.Book;
import org.json.JSONObject;

public class BookParserTask extends AsyncTask<JSONObject, Integer, Book> {

    private BookListScreen bookListScreen;

    public BookParserTask(BookListScreen bookListScreen) {

        this.bookListScreen = bookListScreen;
    }

    @Override
    protected Book doInBackground(JSONObject... jsonObjects) {
        try {
            return new CommonBookParser().parse(jsonObjects[0]);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Book();
    }

    @Override
    protected void onPostExecute(final Book book) {
        if (!book.isEmpty()) {
            bookListScreen.add(book);
        }
    }
}
