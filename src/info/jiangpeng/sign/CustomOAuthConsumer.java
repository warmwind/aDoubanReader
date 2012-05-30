package info.jiangpeng.sign;

import oauth.signpost.basic.DefaultOAuthConsumer;
import oauth.signpost.basic.UrlStringRequestAdapter;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.http.HttpRequest;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

public class CustomOAuthConsumer {
    private String accessToken;
    private String accessTokenSceret;

    public CustomOAuthConsumer(String accessToken, String accessTokenSecret) {
        this.accessToken = accessToken;
        this.accessTokenSceret = accessTokenSecret;
    }

    public String executeAfterSignIn(String url) throws OAuthMessageSignerException, OAuthExpectationFailedException, OAuthCommunicationException, IOException {
        DefaultOAuthConsumer consumer = OAuthFactory.createConsumer();
        consumer.setTokenWithSecret(accessToken, accessTokenSceret);

        HttpRequest request = consumer.sign(new UrlStringRequestAdapter(url));
        String requestUrl = request.getRequestUrl();
        return EntityUtils.toString(new DefaultHttpClient().execute(new HttpGet(requestUrl)).getEntity());
    }

}
