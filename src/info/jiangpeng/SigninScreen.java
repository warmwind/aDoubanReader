package info.jiangpeng;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.basic.DefaultOAuthConsumer;
import oauth.signpost.basic.UrlStringRequestAdapter;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.exception.OAuthNotAuthorizedException;
import oauth.signpost.http.HttpRequest;
import oauth.signpost.signature.HmacSha1MessageSigner;
import oauth.signpost.signature.QueryStringSigningStrategy;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;

public class SigninScreen extends Activity {
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
                OAuthConsumer consumer = new DefaultOAuthConsumer(CONSUMER_KEY,
                        CONSUMER_SECRET);
                String ACCESS_TOKEN = "";
                String TOKEN_SECRET= "";
                consumer.setTokenWithSecret(ACCESS_TOKEN, TOKEN_SECRET);
                consumer.setMessageSigner(new HmacSha1MessageSigner());
                consumer.setSigningStrategy(new QueryStringSigningStrategy());


                UrlStringRequestAdapter stringRequestAdapter = new UrlStringRequestAdapter("http://www.douban.com/service/auth/request_token");
                HttpRequest sign = null;
                try {
                    sign = consumer.sign(stringRequestAdapter);
                String url = sign.getRequestUrl();
                System.out.println("------------request = " + url);

                HttpGet request = new HttpGet(url);
                    String s = EntityUtils.toString(new DefaultHttpClient().execute(request).getEntity());
                    System.out.println("------------s = " + s);
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        });


    }

    private static String sha1(String s, String keyString) {
        try {
            SecretKeySpec key = new SecretKeySpec((keyString).getBytes("UTF-8"), "HMACSHA1");
            Mac mac = Mac.getInstance("HMACSHA1");
            mac.init(key);

            byte[] bytes = mac.doFinal(s.getBytes("UTF-8"));
            return new String(Base64.encodeBase64(bytes));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) throws OAuthExpectationFailedException, OAuthMessageSignerException, OAuthCommunicationException, IOException, OAuthNotAuthorizedException {

    }
}
