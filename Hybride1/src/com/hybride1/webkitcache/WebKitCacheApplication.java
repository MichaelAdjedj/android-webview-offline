package com.hybride1.webkitcache;


import com.hybride1.StartActivity;
import com.hybride1.sync.SyncHTTPService;

import android.app.Application;

/**
 * Bootstrap the application.
 * 
 * @author madjedj
 *
 */
public class WebKitCacheApplication extends Application {

	/**
	* Initialize the application.
	*/
	@Override
	public void onCreate()
	{
	super.onCreate();
	// Register an URL for the cache
	SyncHTTPService.registerURL(this,StartActivity.URL_HOME);
	// and initialize the first synchronization
	SyncHTTPTools.initializeHTTPSyncService(this);
	}
}
