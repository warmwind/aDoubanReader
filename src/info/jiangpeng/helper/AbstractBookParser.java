package info.jiangpeng.helper;

import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import info.jiangpeng.BookImageDrawable;
import info.jiangpeng.model.Book;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

public class AbstractBookParser {
    protected void parseLinkJson(JSONObject rawJsonString, Book book) throws JSONException, IOException {
        JSONArray linkArray = rawJsonString.getJSONArray("link");
        for (int i = 0; i < linkArray.length(); i++) {
            JSONObject linkJson = linkArray.getJSONObject(i);
            if (linkJson.getString("@rel").equals("alternate")) {
                book.setBookUrlInWeb(linkJson.getString("@href"));
            } else if (linkJson.getString("@rel").equals("self")) {
                book.setBookDetailsUrl(linkJson.getString("@href"));
            } else if (linkJson.getString("@rel").equals("image")) {
                String imageUrl = linkJson.getString("@href");
                book.setImageUrl(imageUrl);
                book.setImageDrawable(new BookImageDrawable(BitmapFactory.decodeStream(new URL(imageUrl).openStream())));
            }
        }
    }

    protected void parseMetadataJson(JSONObject rawJsonString, Book book) throws JSONException {
        JSONArray metadataArray = rawJsonString.getJSONArray("db:attribute");
        for (int i = 0; i < metadataArray.length(); i++) {
            JSONObject metaJson = metadataArray.getJSONObject(i);
            if (metaJson.getString("@name").equals("publisher")) {
                book.setPublisher(metaJson.getString("$t"));
            } else if (metaJson.getString("@name").equals("pubdate")) {
                book.setPubDate(metaJson.getString("$t"));
            } else if (metaJson.getString("@name").equals("author")) {
                book.setAuthor(metaJson.getString("$t"));
            } else if (metaJson.getString("@name").equals("author-intro")) {
                book.setAuthorIntro(metaJson.getString("$t"));
            }

        }
    }
}
