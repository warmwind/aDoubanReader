package info.jiangpeng;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import info.jiangpeng.model.NullUser;
import info.jiangpeng.model.User;
import info.jiangpeng.sign.OAuthFactory;
import oauth.signpost.basic.DefaultOAuthConsumer;
import oauth.signpost.basic.DefaultOAuthProvider;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.exception.OAuthNotAuthorizedException;

public class HeaderScreen extends RelativeLayout{
    private static User user = new NullUser();
    private TextView signIn;
    private MainActivity mainActivity;

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


    public void initComponent(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        signIn = (TextView) findViewById(R.id.user);
        signIn.setText(user.getName());

        signIn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_UP:
                        if (user.isSignedIn()) {
                            user = new NullUser();
                            signIn.setText(user.getName());
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
    private void retrieveRequestToken() throws OAuthMessageSignerException, OAuthNotAuthorizedException, OAuthExpectationFailedException, OAuthCommunicationException {
        DefaultOAuthConsumer consumer = OAuthFactory.createConsumer();
        DefaultOAuthProvider authProvider = new DefaultOAuthProvider("http://www.douban.com/service/auth/request_token", "http://www.douban.com/service/auth/access_token", "http://www.douban.com/service/auth/authorize");
        String url1 = authProvider.retrieveRequestToken(consumer, "vtbapp-doudou:///");

        mainActivity.requestToken = consumer.getToken();
        mainActivity.requestTokenSceret = consumer.getTokenSecret();
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url1));
        mainActivity.startActivity(browserIntent);
    }

    public void updateUser(User newUser) {
        user = newUser;
        signIn.setText(user.getName());
    }

    public boolean isUserSignedIn(){
        return user.isSignedIn();
    }

    public String getUserId(){
        return user.getId();
    }
}
