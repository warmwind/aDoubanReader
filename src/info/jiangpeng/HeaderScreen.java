package info.jiangpeng;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import info.jiangpeng.helper.UserParser;
import info.jiangpeng.model.NullUser;
import info.jiangpeng.model.User;
import info.jiangpeng.sign.CustomOAuthConsumer;
import info.jiangpeng.sign.OAuthFactory;
import oauth.signpost.basic.DefaultOAuthConsumer;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.exception.OAuthNotAuthorizedException;
import org.json.JSONException;

import java.io.IOException;

public class HeaderScreen extends RelativeLayout{

    public static String accessToken;
    public static String accessTokenSecret;
    private static String requestToken;
    private static String requestTokenSecret;

    private static User user = new NullUser();
    public static final String USER_INFO_URL = "http://api.douban.com/people/%40me?alt=json";
    public static final String CALLBACK_URL = "vtbapp-doudou:///";
    private TextView signInText;
    private Activity activity;
    private AlertDialog alertDialog;


    public HeaderScreen(Context context, AttributeSet attrs) {
        super(context, attrs);
        initUI();
    }

    private void initUI() {
        FrameLayout frameLayout = new FrameLayout(getContext());
        LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        vi.inflate(R.layout.header, frameLayout, true);

        this.addView(frameLayout);

    }


    public void initComponent(final Activity activity) {
        this.activity = activity;
        alertDialog = initAlertDialog(activity);

        signInText = (TextView) findViewById(R.id.user);
        signInText.setText(user.getName());

        signInText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_UP:
                        if (user.isSignedIn()) {
                            alertDialog.show();
                        } else {
                            try {
                                retrieveRequestToken();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        return true;
                    default:
                        return true;
                }
            }
        });

    }

    private AlertDialog initAlertDialog(final Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage("Are you sure you want to exit?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        user = new NullUser();
                        signInText.setText(user.getName());
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        return builder.create();
    }

    public boolean isUserSignedIn(){
        return user.isSignedIn();
    }

    public String getUserId(){
        return user.getId();
    }

    public void updateUserInfo() throws OAuthExpectationFailedException, OAuthMessageSignerException, OAuthCommunicationException, OAuthNotAuthorizedException, IOException, JSONException {
        User user = retrieveUserInfo();
        HeaderScreen.user = user;
        signInText.setText(HeaderScreen.user.getName());

    }

    private void retrieveRequestToken() throws OAuthMessageSignerException, OAuthNotAuthorizedException, OAuthExpectationFailedException, OAuthCommunicationException {
        DefaultOAuthConsumer consumer = OAuthFactory.createConsumer();
        String url = OAuthFactory.createProvider().retrieveRequestToken(consumer, CALLBACK_URL);

        requestToken = consumer.getToken();
        requestTokenSecret = consumer.getTokenSecret();
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        activity.startActivity(browserIntent);
    }

    private User retrieveUserInfo() throws OAuthMessageSignerException, OAuthNotAuthorizedException, OAuthExpectationFailedException, OAuthCommunicationException, JSONException, IOException {
        DefaultOAuthConsumer consumer = OAuthFactory.createConsumer();
        retrieveAccessToken(consumer);

        CustomOAuthConsumer consumerSignedIn = OAuthFactory.createConsumer(consumer.getToken(), consumer.getTokenSecret());
        return new UserParser().parse(consumerSignedIn.executeAfterSignIn(USER_INFO_URL));
    }

    private void retrieveAccessToken(DefaultOAuthConsumer consumer) throws OAuthMessageSignerException, OAuthNotAuthorizedException, OAuthExpectationFailedException, OAuthCommunicationException {
        consumer.setTokenWithSecret(requestToken, requestTokenSecret);
        OAuthFactory.createProvider().retrieveAccessToken(consumer, null);
        accessToken = consumer.getToken();
        accessTokenSecret = consumer.getTokenSecret();
    }

}
