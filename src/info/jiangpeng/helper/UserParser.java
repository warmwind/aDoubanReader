package info.jiangpeng.helper;

import info.jiangpeng.model.User;
import org.json.JSONException;
import org.json.JSONObject;

public class UserParser {
    public User parse(String rawJsonString) throws JSONException {
        User user = new User();
        JSONObject jsonObject = new JSONObject(rawJsonString);
        String userName = jsonObject.getJSONObject("title").getString("$t");
        String userId = jsonObject.getJSONObject("db:uid").getString("$t");
        user.setName(userName);
        user.setId(userId);
        return user;
    }
}
