package info.jiangpeng;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class BookDetailsWeb extends Activity {

    public static final String BOOK_DETAILS_WEB_URL = "BOOK_DETAILS_WEB_URL";
    private WebView bookDetailsWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.book_details_web);

        bookDetailsWebView = (WebView) findViewById(R.id.book_details_webview);
        bookDetailsWebView.getSettings().setJavaScriptEnabled(true);

        Intent intent = getIntent();
        String url = intent.getStringExtra(BOOK_DETAILS_WEB_URL);
        bookDetailsWebView.loadUrl(url);
        bookDetailsWebView.setWebViewClient(new BookDetailsWebClient());
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && bookDetailsWebView.canGoBack()) {
            bookDetailsWebView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private class BookDetailsWebClient extends WebViewClient{
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }
}
