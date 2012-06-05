package info.jiangpeng.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import info.jiangpeng.adapter.ContactsAdapter;
import info.jiangpeng.HeaderScreen;
import info.jiangpeng.R;
import info.jiangpeng.model.RequestParams;
import info.jiangpeng.sign.CustomOAuthConsumer;
import info.jiangpeng.sign.OAuthFactory;
import info.jiangpeng.task.UserParseTask;
import org.json.JSONArray;
import org.json.JSONObject;

public class ContactsActivity extends Activity {
    private HeaderScreen headerScreen;
    private GridView contactsGridView;
    private ContactsAdapter contactsAdapter;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.contacts);
        Intent intent = getIntent();

        final RequestParams requestParams = new RequestParams(intent);

        headerScreen = (HeaderScreen) findViewById(R.id.header);
        contactsGridView = (GridView)findViewById(R.id.contact_grid);
        contactsAdapter = new ContactsAdapter(this);
        contactsGridView.setAdapter(contactsAdapter);

        contactsGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), UserBooksActivity.class);

                requestParams.setUserId(String.valueOf(id));
                requestParams.setUserName(contactsAdapter.getItem(position).getName());
                intent.putExtra("REQUEST_PARAMS", requestParams);
                startActivity(intent);
            }
        });

        CustomOAuthConsumer consumerSignedIn = OAuthFactory.createConsumer(requestParams.getAccessToken(), requestParams.getAccessTokenSecret());
        try {
            String rawJsonString = consumerSignedIn.executeAfterSignIn("http://api.douban.com/people/" + requestParams.getUserId() + "/contacts?alt=json");
            JSONObject jsonObject = new JSONObject(rawJsonString);
            JSONArray entry = jsonObject.getJSONArray("entry");
            int length = entry.length();
            for (int i = 0; i < length; i++) {
                new UserParseTask(contactsAdapter).execute(entry.get(i).toString());
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
