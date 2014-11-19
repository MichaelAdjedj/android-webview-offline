package com.hybride1;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;


public class StartActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        
        WebView myWebView = (WebView) findViewById(R.id.webview);
        
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        
//        myWebView.loadUrl("http://production.compilsoft.com/ws-smarty-smart-demo/?smarty=html/job:20/lang:fr");
        myWebView.loadUrl("http://www.compilsoft.com/fr/compilsoft.html");
        myWebView.setWebViewClient(new WebViewClient());
        
    }

}
