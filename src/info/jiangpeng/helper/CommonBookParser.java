package info.jiangpeng.helper;

import info.jiangpeng.model.Book;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class CommonBookParser extends BookParser{

    public Book parse(JSONObject jsonBookObject) throws JSONException, IOException {
        Book book = new Book();

        book.setTitle(jsonBookObject.getJSONObject("title").getString("$t"));
        parseLinkJson(jsonBookObject, book);

        book.setAuthor(jsonBookObject.getJSONArray("author").getJSONObject(0).getJSONObject("name").getString("$t"));
        book.setAverageRate(jsonBookObject.getJSONObject("gd:rating").getString("@average"));

        parseMetadataJson(jsonBookObject, book);

        return book;

    }

    private void parseMetadataJson(JSONObject rawJsonString, Book book) throws JSONException {
        JSONArray metadataArray = rawJsonString.getJSONArray("db:attribute");
        for (int i = 0; i < metadataArray.length(); i++) {
            JSONObject metaJson = metadataArray.getJSONObject(i);
            if (metaJson.getString("@name").equals("publisher")) {
                book.setPublisher(metaJson.getString("$t"));
            } else if (metaJson.getString("@name").equals("pubdate")) {
                book.setPubDate(metaJson.getString("$t"));
            }
        }
    }
}
