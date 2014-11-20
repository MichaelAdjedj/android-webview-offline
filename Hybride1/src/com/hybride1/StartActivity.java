package com.hybride1;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;


public class StartActivity extends Activity {

	private final static String LOG_TAG = "DisplayWebsiteActivity";
	private WebView myWebView;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Activity activity = this;
        setContentView(R.layout.activity_start);
        
        final Resources resources = getResources();
        
        myWebView = (WebView) findViewById(R.id.webview);
        
        myWebView.getSettings().setLoadsImagesAutomatically(true);
        myWebView.getSettings().setJavaScriptEnabled(true);
        
        myWebView.setWebViewClient(new WebViewClient(){
        	 public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        		 String messageTemplate = resources.getString(R.string.error_loading_webiste);
        		 Log.d(LOG_TAG, String.format(messageTemplate, description));
        		 Toast.makeText(activity, String.format(messageTemplate, description), Toast.LENGTH_LONG).show();
        	 }
        });
        
        myWebView.getSettings().setDomStorageEnabled(true);
        myWebView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        myWebView.getSettings().setAppCacheEnabled(true);
        myWebView.getSettings().setAllowFileAccess(true);
        myWebView.getSettings().setAppCacheEnabled(true); 
        
        
        myWebView.loadUrl("http://production.compilsoft.com/ws-smarty-smart-demo/?smarty=html/job:20/lang:fr");
//        myWebView.loadUrl("http://www.compilsoft.com/fr/compilsoft.html");
//        myWebView.loadUrl("http://m.materiel.net");
        
        
    }

    /**
    * Override back button behavior. It behaves like browser back button
    */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    if((keyCode == KeyEvent.KEYCODE_BACK) && myWebView.canGoBack()) {
    myWebView.goBack();
    return true;
    }
    return super.onKeyDown(keyCode, event);
    }
}
