package info.jiangpeng.task;

import android.os.AsyncTask;
import info.jiangpeng.BookListFragment;
import info.jiangpeng.helper.BookParser;
import info.jiangpeng.model.Book;
import org.json.JSONObject;

public class BookParseTask extends AsyncTask<JSONObject, Integer, Book> {

    private BookListFragment bookListFragment;
    private BookParser bookParser;

    public BookParseTask(BookListFragment bookListFragment, BookParser bookParser) {

        this.bookListFragment = bookListFragment;
        this.bookParser = bookParser;
    }

    @Override
    protected Book doInBackground(JSONObject... jsonObjects) {
        try {
            return bookParser.parse(jsonObjects[0]);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Book();
    }

    @Override
    protected void onPostExecute(final Book book) {
        if (!book.isEmpty()) {
            bookListFragment.add(book);
            System.out.println("------------book = " + book);
        }
    }
}
