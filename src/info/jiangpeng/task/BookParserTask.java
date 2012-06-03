package info.jiangpeng.task;

import android.os.AsyncTask;
import info.jiangpeng.BookListFragment;
import info.jiangpeng.helper.CommonBookParser;
import info.jiangpeng.model.Book;
import org.json.JSONObject;

public class BookParserTask extends AsyncTask<JSONObject, Integer, Book> {

    private BookListFragment bookListFragment;

    public BookParserTask(BookListFragment bookListFragment) {

        this.bookListFragment = bookListFragment;
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
            bookListFragment.add(book);
        }
    }
}
