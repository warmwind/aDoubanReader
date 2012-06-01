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

    public static DefaultOAuthProvider createProvider(){
        String requestTokenEndpointUrl = "http://www.douban.com/service/auth/request_token";
        String accessTokenEndpointUrl = "http://www.douban.com/service/auth/access_token";
        String authorizationWebsiteUrl = "http://www.douban.com/service/auth/authorize";
        return new DefaultOAuthProvider(requestTokenEndpointUrl, accessTokenEndpointUrl, authorizationWebsiteUrl);
    }
}
