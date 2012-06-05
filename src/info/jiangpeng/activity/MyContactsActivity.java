package info.jiangpeng.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.GridView;
import info.jiangpeng.adapter.ContactsAdapter;
import info.jiangpeng.HeaderScreen;
import info.jiangpeng.R;
import info.jiangpeng.helper.UserParser;
import info.jiangpeng.model.User;
import info.jiangpeng.sign.CustomOAuthConsumer;
import info.jiangpeng.sign.OAuthFactory;
import org.json.JSONArray;
import org.json.JSONObject;

public class MyContactsActivity extends Activity {
    private HeaderScreen headerScreen;
    private GridView contactsGridView;
    private ContactsAdapter contactsAdapter;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.my_contacts);

        headerScreen = (HeaderScreen) findViewById(R.id.header);
        contactsGridView = (GridView)findViewById(R.id.my_contact_grid);
        contactsAdapter = new ContactsAdapter(this);
        contactsGridView.setAdapter(contactsAdapter);

        Intent intent = getIntent();
        String user_id = intent.getStringExtra("USER_ID");
        String access_token = intent.getStringExtra("ACCESS_TOKEN");
        String access_token_secret = intent.getStringExtra("ACCESS_TOKEN_SECRET");

        CustomOAuthConsumer consumerSignedIn = OAuthFactory.createConsumer(access_token, access_token_secret);
        try {
            String rawJsonString = consumerSignedIn.executeAfterSignIn("http://api.douban.com/people/" + user_id + "/contacts?alt=json");
            JSONObject jsonObject = new JSONObject(rawJsonString);
            JSONArray entry = jsonObject.getJSONArray("entry");
            int length = entry.length();
            for (int i = 0; i < length; i++) {
                User user = new UserParser().parse(entry.get(i).toString());
                contactsAdapter.add(user);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        initComponent();


    }

    private void initComponent() {
        headerScreen.initComponent(this);
    }


}
