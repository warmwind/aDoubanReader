package info.jiangpeng.task;

import android.os.AsyncTask;
import info.jiangpeng.adapter.ContactsAdapter;
import info.jiangpeng.helper.UserParser;
import info.jiangpeng.model.NullUser;
import info.jiangpeng.model.User;
import org.json.JSONException;

import java.io.IOException;

public class UserParseTask extends AsyncTask<String, Integer, User> {

    private ContactsAdapter adapter;

    public UserParseTask(ContactsAdapter adapter) {

        this.adapter = adapter;
    }

    @Override
    protected User doInBackground(String... strings) {
        try {
            return new UserParser().parse(strings[0]);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new NullUser();
    }

    @Override
    protected void onPostExecute(User user) {
        System.out.println("------------user = " + user);
        adapter.add(user);
    }
}
