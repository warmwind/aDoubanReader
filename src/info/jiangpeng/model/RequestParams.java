package info.jiangpeng.model;

import android.content.Intent;

import java.io.Serializable;

public class RequestParams implements Serializable {
    private String userName;
    private String userId;
    private String accessToken;
    private String accessTokenSecret;

    public RequestParams() {
    }

    public RequestParams(Intent intent) {
        Serializable requestParams = intent.getSerializableExtra("REQUEST_PARAMS");
        if (requestParams != null) {
            RequestParams params = (RequestParams) requestParams;
            userId = params.getUserId();
            accessToken = params.getAccessToken();
            accessTokenSecret = params.getAccessTokenSecret();
        } else {
            userId = intent.getStringExtra("USER_ID");
            accessToken = intent.getStringExtra("ACCESS_TOKEN");
            accessTokenSecret = intent.getStringExtra("ACCESS_TOKEN_SECRET");
        }
    }


    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getAccessTokenSecret() {
        return accessTokenSecret;
    }

    public void setAccessTokenSecret(String accessTokenSecret) {
        this.accessTokenSecret = accessTokenSecret;
    }
}
