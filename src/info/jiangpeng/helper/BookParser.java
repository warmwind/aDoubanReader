package info.jiangpeng.helper;

import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import info.jiangpeng.model.Book;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

public class BookParser {
    protected void parseLinkJson(JSONObject rawJsonString, Book book) throws JSONException, IOException {
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
