package info.jiangpeng.helper;

import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import info.jiangpeng.Book;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

public class CommonBookParser{

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

    private void parseLinkJson(JSONObject rawJsonString, Book book) throws JSONException, IOException {
        JSONArray linkArray = rawJsonString.getJSONArray("link");
        for (int i = 0; i < linkArray.length(); i++) {
            JSONObject linkJson = linkArray.getJSONObject(i);
            if (linkJson.getString("@rel").equals("alternate")) {
                book.setBookUrlInWeb(linkJson.getString("@href"));
            } else if (linkJson.getString("@rel").equals("image")) {
                String imageUrl = linkJson.getString("@href");
                book.setImageDrawable(new BitmapDrawable(BitmapFactory.decodeStream(new URL(imageUrl).openStream())));
            }
        }
    }
}
