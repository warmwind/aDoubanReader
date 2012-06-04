package info.jiangpeng.helper;

import info.jiangpeng.model.Book;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class CommonBookParser extends AbstractBookParser implements BookParser{

    @Override
    public Book parse(JSONObject jsonBookObject) throws JSONException, IOException {
        Book book = new Book();

        book.setTitle(jsonBookObject.getJSONObject("title").getString("$t"));
        parseLinkJson(jsonBookObject, book);
        book.setAverageRate(jsonBookObject.getJSONObject("gd:rating").getString("@average"));
        parseMetadataJson(jsonBookObject, book);

        return book;

    }

}
