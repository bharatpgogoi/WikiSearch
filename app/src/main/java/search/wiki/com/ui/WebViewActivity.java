package search.wiki.com.ui;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import search.wiki.com.wikisearch.R;

public class WebViewActivity extends BaseActivity {

    private WebView mWebView;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        mWebView = findViewById(R.id.search_webview);
        mProgressBar = findViewById(R.id.progress_bar_webview);
        Bundle extras = getIntent().getExtras();
        if(extras != null){
            String url = extras.getString("url");
            loadWebView(url);
        }
    }


    private void loadWebView(String url) {
        mWebView.setVisibility(View.VISIBLE);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setWebViewClient(new WebViewController());
        mWebView.loadUrl(url);
        mProgressBar.setVisibility(View.VISIBLE);
    }


    private class WebViewController extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url){
            if(mProgressBar.isShown()){
                mProgressBar.setVisibility(View.INVISIBLE);
            }

        }


    }

}
