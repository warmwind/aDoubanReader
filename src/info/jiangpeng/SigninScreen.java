package info.jiangpeng;

import android.app.Activity;
import android.content.Intent;
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
import oauth.signpost.signature.HmacSha1MessageSigner;
import oauth.signpost.signature.QueryStringSigningStrategy;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

public class SigninScreen extends Activity {

    public static final int OAUTH_CODE = 1;
    private DefaultOAuthProvider authProvider;
    private OAuthConsumer consumer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_in);

        EditText emailInput = (EditText) findViewById(R.id.email);
        EditText passwordInput = (EditText) findViewById(R.id.password);
        Button signinBt = (Button) findViewById(R.id.signin_bt);


        signinBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String CONSUMER_KEY = "0d5f0a33b677be10281d1e9b23673a30";
                String CONSUMER_SECRET = "d66dc447cdfa7eeb";
                consumer = new DefaultOAuthConsumer(CONSUMER_KEY,
                        CONSUMER_SECRET);
                consumer.setMessageSigner(new HmacSha1MessageSigner());
                consumer.setSigningStrategy(new QueryStringSigningStrategy());
                try {
                    authProvider = new DefaultOAuthProvider("http://www.douban.com/service/auth/request_token", "http://www.douban.com/service/auth/access_token", "http://www.douban.com/service/auth/authorize");
                    String url1 = authProvider.retrieveRequestToken(consumer, null);
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url1));
                    startActivity(browserIntent);
                    authProvider.retrieveAccessToken(consumer, null);
                    String accessToken = consumer.getToken();
                    HttpRequest request = consumer.sign(new UrlStringRequestAdapter("http://api.douban.com/people/%40me"));
                    String s1 = EntityUtils.toString(new DefaultHttpClient().execute(new HttpGet(request.getRequestUrl())).getEntity());
                    System.out.println("------------s1 = " + s1);


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }
}
