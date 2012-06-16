package info.jiangpeng.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import info.jiangpeng.R;
import info.jiangpeng.helper.RequestParams;
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

public class MainActivity extends Activity {
    public static String accessToken;
    public static String accessTokenSecret;

    private static String requestToken;
    private static String requestTokenSecret;

    public static final String USER_INFO_URL = "http://api.douban.com/people/%40me?alt=json";
    public static final String CALLBACK_URL = "vtbapp-doudou:///";


    public UserBooksFragment userBooksFragment;
    private Fragment searchFragment;
    private FragmentTransaction ft;
    private ContactsFragment contactsFragment;
    private User user = new NullUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);

        ft = getFragmentManager().beginTransaction();

        searchFragment = getFragmentManager().findFragmentById(R.id.search_fragment);
        userBooksFragment = (UserBooksFragment) getFragmentManager().findFragmentById(R.id.my_books_fragment);
        contactsFragment = (ContactsFragment) getFragmentManager().findFragmentById(R.id.contacts_fragment);
        ft.hide(searchFragment);
        ft.hide(userBooksFragment);
        ft.hide(contactsFragment);
        ft.commit();




    }

    @Override
    public void onResume() {
        super.onResume();
        ft = getFragmentManager().beginTransaction();
        ft.hide(searchFragment);
        ft.hide(contactsFragment);
        ft.hide(userBooksFragment);
        ft.commit();

        getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        try {
            updateUserInfo();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateUserInfo() throws OAuthExpectationFailedException, OAuthMessageSignerException, OAuthCommunicationException, OAuthNotAuthorizedException, IOException, JSONException {
        new UpdateUserIntoTask().execute();
    }


    private class UpdateUserIntoTask extends AsyncTask<String, Integer, User> {

        @Override
        protected User doInBackground(String... strings) {
            try {
                user = retrieveUserInfo();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return user;
        }

        @Override
        protected void onPostExecute(User user) {
            RequestParams requestParams = new RequestParams();
            requestParams.setUserId(user.getId());
            requestParams.setUserName(user.getName());
            requestParams.setAccessToken(accessToken);
            requestParams.setAccessTokenSecret(accessTokenSecret);
            userBooksFragment.setRequestParams(requestParams);
            contactsFragment.setRequestParams(requestParams);
//            contactsFragment.searchContacts();
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.menu_more).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_search:
                showSearchTab();
                return true;
//            case R.id.menu_more:
//                bookListFragment.executeSearchByKeyWord(query);
//                searchBar.showProgressBar();
//                return true;
            case R.id.menu_my_books:
                try {
                    if (user.isSignedIn()) {
                        showMyBooksTab();
                        RequestParams requestParams = new RequestParams();
                        requestParams.setAccessToken(accessToken);
                        requestParams.setAccessTokenSecret(accessTokenSecret);
                        requestParams.setUserId(user.getId());
                        requestParams.setUserName(user.getName());
                        userBooksFragment.setRequestParams(requestParams);
                    } else {
                        retrieveRequestToken();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return true;
            case R.id.menu_contacts:
                try {
                    if (user.isSignedIn()) {
                        showContactsTab();
                        contactsFragment.searchContacts();
                    } else {
                        retrieveRequestToken();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return true;

            default:
                return true;
        }

    }

    private void showContactsTab() {
        getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        ft = getFragmentManager().beginTransaction();
        ft.hide(searchFragment);
        ft.hide(userBooksFragment);
        ft.show(contactsFragment);
        ft.commit();
    }

    private void showSearchTab() {
        getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);

        ft = getFragmentManager().beginTransaction();
        ft.show(searchFragment);
        ft.hide(userBooksFragment);
        ft.hide(contactsFragment);
        ft.commit();
    }

    public void showMyBooksTab() {
        ft = getFragmentManager().beginTransaction();
        getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        ft.hide(searchFragment);
        ft.show(userBooksFragment);
        ft.hide(contactsFragment);
        ft.commit();
    }

    private void retrieveRequestToken() throws OAuthMessageSignerException, OAuthNotAuthorizedException, OAuthExpectationFailedException, OAuthCommunicationException {
        new RetrieveRequestTokenTask().execute();
    }


    private class RetrieveRequestTokenTask extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... strings) {
            DefaultOAuthConsumer consumer = OAuthFactory.createConsumer();
            try {
                String url = OAuthFactory.createProvider().retrieveRequestToken(consumer, CALLBACK_URL);
                requestToken = consumer.getToken();
                requestTokenSecret = consumer.getTokenSecret();
                return url;
            } catch (Exception e) {
                e.printStackTrace();
            }

            return "";
        }

        @Override
        protected void onPostExecute(String url) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(browserIntent);
        }
    }

}
