package info.jiangpeng.helper;

import info.jiangpeng.model.Book;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class MyBookParser{

    public Book parse(JSONObject jsonBookObject) throws JSONException, IOException {
        Book book = new Book();
        JSONObject jsonBook = jsonBookObject.getJSONObject("db:subject");
        book.setTitle(jsonBook.getJSONObject("title").getString("$t"));
        JSONArray jsonAttribute = jsonBook.getJSONArray("db:attribute");
        book.setPublisher(jsonAttribute.getJSONObject(4).getString("$t"));
        book.setStatus(jsonBookObject.getJSONObject("db:status").getString("$t"));
        return book;
    }
}
