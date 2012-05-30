package info.jiangpeng.helper;

import info.jiangpeng.Account;
import org.json.JSONException;
import org.json.JSONObject;

public class AccountParser {
    public Account parse(String rawJsonString) throws JSONException {
        Account account = new Account();
        JSONObject jsonObject = new JSONObject(rawJsonString);
        String userName = jsonObject.getJSONObject("title").getString("$t");
        String userId = jsonObject.getJSONObject("db:uid").getString("$t");
        account.setName(userName);
        account.setId(userId);
        return account;
    }
}
