package info.jiangpeng.sign;

import oauth.signpost.basic.DefaultOAuthConsumer;
import oauth.signpost.basic.DefaultOAuthProvider;

public class OAuthFactory {

    public static DefaultOAuthConsumer createConsumer() {
        String consumerKey = "0d5f0a33b677be10281d1e9b23673a30";
        String consumerSecret = "d66dc447cdfa7eeb";
        return new DefaultOAuthConsumer(consumerKey, consumerSecret);
    }

    public static CustomOAuthConsumer createConsumer(String accessToken, String accessTokenSceret) {
        return new CustomOAuthConsumer(accessToken, accessTokenSceret);
    }
}
