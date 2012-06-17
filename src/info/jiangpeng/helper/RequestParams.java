package info.jiangpeng.helper;

import java.io.Serializable;

public class RequestParams implements Serializable {
    private String userName;
    private String userId;
    private String accessToken;
    private String accessTokenSecret;
    private boolean isUserChanged;

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

    public void setUserChanged(boolean userChanged) {
        this.isUserChanged = userChanged;
    }

    public boolean isUserChanged() {
        return isUserChanged;
    }
}
