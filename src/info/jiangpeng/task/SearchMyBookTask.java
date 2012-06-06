package info.jiangpeng.task;

import android.os.AsyncTask;
import info.jiangpeng.BookListFragment;
import info.jiangpeng.helper.UserBookParser;
import info.jiangpeng.sign.OAuthFactory;
import oauth.signpost.basic.DefaultOAuthConsumer;
import oauth.signpost.basic.UrlStringRequestAdapter;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

public class SearchMyBookTask extends AsyncTask<String, Integer, String> {

    private BookListFragment bookListFragment;

    public SearchMyBookTask(BookListFragment bookListFragment) {
        this.bookListFragment = bookListFragment;
    }

    @Override
    protected String doInBackground(String... strings) {
        String userId = strings[0];
        String accessToken = strings[1];
        String accessTokenSecret = strings[2];
        String status = strings[3];

        DefaultOAuthConsumer consumer = OAuthFactory.createConsumer();
        consumer.setTokenWithSecret(accessToken, accessTokenSecret);

        try {
            String requestUrl = consumer.sign(new UrlStringRequestAdapter("http://api.douban.com/people/" + userId + "/collection?cat=book&alt=json&status="+ status)).getRequestUrl();
            return EntityUtils.toString(new DefaultHttpClient().execute(new HttpGet(requestUrl)).getEntity());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";

    }

    @Override
    protected void onPostExecute(String s) {
        try {
            JSONObject jsonObject = new JSONObject(s);
            JSONArray entry = jsonObject.getJSONArray("entry");
            int length = entry.length();
            for (int i = 0; i < length; i++) {
                new BookParseTask(bookListFragment, new UserBookParser()).execute(entry.getJSONObject(i));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
