package info.jiangpeng;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.basic.DefaultOAuthConsumer;
import oauth.signpost.basic.DefaultOAuthProvider;
import oauth.signpost.basic.UrlStringRequestAdapter;
import oauth.signpost.http.HttpRequest;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

public class SigninScreen extends Activity {

    public static final String ACCESS_TOKEN = "ACCESS_TOKEN";
    public static final String ACCESS_TOKEN_SECRET = "ACCESS_TOKEN_SECRET";
    public static final String REQUEST_TOKEN = "REQUEST_TOKEN";
    public static final String REQUEST_TOKEN_SECRET = "REQUEST_TOKEN_SECRET";
    private DefaultOAuthProvider authProvider;
    private OAuthConsumer consumer;
    private String CONSUMER_SECRET;
    private String CONSUMER_KEY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_in);

        Button signinBt = (Button) findViewById(R.id.signin_bt);

        CONSUMER_KEY = "0d5f0a33b677be10281d1e9b23673a30";
        CONSUMER_SECRET = "d66dc447cdfa7eeb";
        consumer = new DefaultOAuthConsumer(CONSUMER_KEY,
                CONSUMER_SECRET);
        authProvider = new DefaultOAuthProvider("http://www.douban.com/service/auth/request_token", "http://www.douban.com/service/auth/access_token", "http://www.douban.com/service/auth/authorize");

        signinBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    String url1 = authProvider.retrieveRequestToken(consumer, "vtbapp-doudou:///");
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url1));

                    SharedPreferences preferences = getPreferences(MODE_PRIVATE);
                    SharedPreferences.Editor edit = preferences.edit();
                    edit.putString(REQUEST_TOKEN, consumer.getToken());
                    edit.putString(REQUEST_TOKEN_SECRET, consumer.getTokenSecret());
                    edit.commit();

                    startActivity(browserIntent);


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        Button myInfoBn = (Button) findViewById(R.id.myinfo_bt);
        myInfoBn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    SharedPreferences preferences = getPreferences(MODE_PRIVATE);
                    consumer.setTokenWithSecret(preferences.getString(ACCESS_TOKEN, ""), preferences.getString(ACCESS_TOKEN_SECRET, ""));

                    HttpRequest request = consumer.sign(new UrlStringRequestAdapter("http://api.douban.com/people/%40me"));
                    String s1 = EntityUtils.toString(new DefaultHttpClient().execute(new HttpGet(request.getRequestUrl())).getEntity());
                    System.out.println("------------s1 = " + s1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            Uri uri = this.getIntent().getData();
            if (uri != null) {
                SharedPreferences preferences = getPreferences(MODE_PRIVATE);

                consumer.setTokenWithSecret(preferences.getString(REQUEST_TOKEN, ""), preferences.getString(REQUEST_TOKEN_SECRET, ""));
                authProvider.retrieveAccessToken(consumer, null);
                SharedPreferences.Editor edit = preferences.edit();
                edit.putString(ACCESS_TOKEN, consumer.getToken());
                edit.putString(ACCESS_TOKEN_SECRET, consumer.getTokenSecret());
                edit.commit();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
