package info.jiangpeng.helper;

import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import info.jiangpeng.model.User;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class UserParser {
    public User parse(String rawJsonString) throws JSONException, IOException {
        User user = new User();
        JSONObject jsonObject = new JSONObject(rawJsonString);

        String userName = jsonObject.getJSONObject("title").getString("$t");
        user.setName(userName);


        String uri = jsonObject.getJSONObject("uri").getString("$t");
        String userId = uri.substring(uri.lastIndexOf("/") + 1, uri.length());
        user.setId(userId);

        String signature = jsonObject.getJSONObject("db:signature").getString("$t");
        user.setSignature(signature);

        parseImage(user, jsonObject);

        return user;
    }

    private void parseImage(User user, JSONObject jsonObject) throws JSONException, IOException {
        JSONArray linkArray = jsonObject.getJSONArray("link");
        for (int i = 0; i < linkArray.length(); i++) {
            JSONObject linkJson = linkArray.getJSONObject(i);
            if (linkJson.getString("@rel").equals("icon")) {
                String imageUrl = linkJson.getString("@href");
                user.setImageDrawable(new BitmapDrawable(BitmapFactory.decodeStream(new URL(imageUrl).openStream())));
            }
        }
    }
}
