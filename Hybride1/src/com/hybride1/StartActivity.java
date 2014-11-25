package com.hybride1;

import static com.hybride1.webkitcache.SyncHTTPTools.ACCOUNT_NAME;
import static com.hybride1.webkitcache.SyncHTTPTools.ACCOUNT_TYPE;
import static com.hybride1.webkitcache.SyncHTTPTools.CONTENT_AUTHORITY;
import android.accounts.Account;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;


/**
 * Sample activity with a WebKit with pages loaded from cache.
 * 
 * @author madjedj
 *
 */
public class StartActivity extends Activity {

	public static final String TAG="UseWebKit";
	public static final String URL_HOME=
			"http://production.compilsoft.com/ws-smarty-smart-demo/?smarty=html/job:20/lang:fr";
	private WebView myWebView;
	// The URL to present in the WebView
	private Uri mHome;
	
	// Observe the HTTP cache
	private final ContentObserver mContentObserver=new ContentObserver((new Handler())) {
		
		/**
		 * @see android.database.ContentObserver#onChange(boolean)
		 */
		@Override
		public void onChange(boolean selfChange) {
			super.onChange(selfChange);
			if (Build.VERSION.SDK_INT<Build.VERSION_CODES.JELLY_BEAN)
				onCacheUpdated(null);
		}
		/**
		 * @see android.database.ContentObserver#onChange(boolean, android.net.Uri)
		 */
		@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
		@Override
		public void onChange(boolean selfChange, Uri uri)
		{
			super.onChange(selfChange, uri);
			onCacheUpdated(uri);
		}
	};
	
	/**
	 * Called by the content observer when the HTTP cache is updated.
	 * It's time to load the URL.
	 *
	 * @param uri The Uri present in the Webkit cache.
	 */
	protected void onCacheUpdated(Uri uri)
	{
		Log.d(TAG,"onCacheUpdate("+uri+")");
		// WebView bug if the background thread update the cache in another process and the cache mod is LOAD_CACHE_ONLY
		myWebView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
		myWebView.reload();
	}
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        mHome=Uri.parse(URL_HOME);
        
        getResources();
        
        myWebView = (WebView) findViewById(R.id.webview);        
        myWebView.setWebViewClient(new WebViewClient(){
        	/**
        	* @see android.webkit.WebViewClient#onReceivedError(android.webkit.WebView, int, java.lang.String, java.lang.String)
        	*/
        	@Override
        	public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        		//super.onReceivedError(view, errorCode, description, failingUrl);
        		Log.d(TAG,"error "+errorCode+" "+description+" "+failingUrl);
        		switch (errorCode){
        		case ERROR_HOST_LOOKUP:
        		case ERROR_CONNECT:
        			Bundle b = new Bundle();
        			b.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        			b.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        			ContentResolver.requestSync(new Account(ACCOUNT_NAME, ACCOUNT_TYPE), CONTENT_AUTHORITY, b);
        			ConnectivityManager cm=((ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE));
        			NetworkInfo info=cm.getActiveNetworkInfo();
        			final boolean isConnected =	(info!=null) ? info.isConnectedOrConnecting() : false;
        			view.loadUrl(isConnected
        					? "file:///android_asset/Waiting.html"
        							: "file:///android_asset/NoNetwork.html");
        			break;
        		default:
        			view.loadUrl("file:///android_asset/Error.html");
        		}
        	}
        });
        initWebViewForHTML5Cache(this,myWebView);

        // Set to load the cache only, because the background service manage the cache.
        // I am optimistic !
        // myWebView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ONLY);
        myWebView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        myWebView.loadUrl("http://www.compilsoft.com/fr/compilsoft.html");
        //        myWebView.loadUrl("http://www.compilsoft.com/fr/compilsoft.html");
        //        myWebView.loadUrl("http://www.montpellier.fr/actualite/4092-actualites.htm");


    }

    /**
     * @see android.app.Activity#onResume()
     */
    @Override
    protected void onResume()
    {
    	super.onResume();
    	// Wait if the cache is updated
    	getContentResolver().registerContentObserver(mHome, true,mContentObserver);
    }
    /**
     * @see android.app.Activity#onPause()
     */
    @Override
    protected void onPause()
    {
    	super.onDestroy();
    	getContentResolver().unregisterContentObserver(mContentObserver);
    }
    /**
     * Initialize a WebView to be compatible with HTML5 manifest.
     *
     * @param context The context.
     * @param webView The web view.
     */
    @SuppressLint("SetJavaScriptEnabled")
    @SuppressWarnings("deprecation")
    private static void initWebViewForHTML5Cache(Context context,WebView webView)
    {
    	final WebSettings settings = webView.getSettings();
    	settings.setJavaScriptEnabled(true);
    	settings.setDomStorageEnabled(true);
    	settings.setDatabaseEnabled(true);
    	settings.setSaveFormData(true);
    	settings.setAllowFileAccess(false);
    	// Application cache enabled
    	settings.setAppCacheEnabled(true);
    	settings.setAppCachePath(context.getCacheDir().getAbsolutePath());
    	if (VERSION.SDK_INT < VERSION_CODES.JELLY_BEAN_MR2)
    		settings.setAppCacheMaxSize(1024 * 1024 * 8);
    }
}
