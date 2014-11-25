package com.hybride1.sync;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.NetworkErrorException;
import android.annotation.TargetApi;
import android.app.Service;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.Intent;
import android.content.SyncResult;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;

/**
 * @author madjedj
 *
 */
public abstract class AbstractSyncService extends Service {

	public static final String TAG="SyncService";

	// Sync adapter
	private SyncAdapter mSyncAdapter;
	// Empty authenticator
	private AbstractAccountAuthenticator mAuthenticator;

	// Dummy authenticator
	private static class AccountAuthenticator extends AbstractAccountAuthenticator {

		AccountAuthenticator(Context context) {
			super(context.getApplicationContext());
		}
		@Override
		public Bundle editProperties(AccountAuthenticatorResponse accountAuthenticatorResponse,
				String s) {
			throw new UnsupportedOperationException();
		}

		@Override
		public Bundle addAccount(AccountAuthenticatorResponse response,
				String accountType, String authTokenType,
				String[] requiredFeatures, Bundle options)
						throws NetworkErrorException {
			return null;
		}

		@Override
		public Bundle confirmCredentials(AccountAuthenticatorResponse response,
				Account account, Bundle options) throws NetworkErrorException {
			return null;
		}

		@Override
		public Bundle getAuthToken(AccountAuthenticatorResponse response,
				Account account, String authTokenType, Bundle options)
						throws NetworkErrorException {
			throw new UnsupportedOperationException();
		}

		@Override
		public String getAuthTokenLabel(String authTokenType) {
			throw new UnsupportedOperationException();
		}

		@Override
		public Bundle updateCredentials(AccountAuthenticatorResponse response,
				Account account, String authTokenType, Bundle options)
						throws NetworkErrorException {
			throw new UnsupportedOperationException();
		}

		@Override
		public Bundle hasFeatures(AccountAuthenticatorResponse response,
				Account account, String[] features)
						throws NetworkErrorException {
			throw new UnsupportedOperationException();
		}
	};

	// Wrapper sync adapter
	private class SyncAdapter extends AbstractThreadedSyncAdapter{

		/**
		 * Set up the sync adapter
		 * @param context
		 * @param autoInitialize
		 */
		public SyncAdapter(Context context, boolean autoInitialize) {
			super(context, autoInitialize);
		}

		// Delegate to outer class
		@Override
		public void onSyncCanceled()
		{
			super.onSyncCanceled();
			AbstractSyncService.this.onSyncCanceled();
		}

		// Delegate to outer class
		@TargetApi(Build.VERSION_CODES.HONEYCOMB)
		@Override
		public void onSyncCanceled(Thread thread) {
			super.onSyncCanceled(thread);
			AbstractSyncService.this.onSyncCanceled(thread);
		}

		@TargetApi(Build.VERSION_CODES.HONEYCOMB)
		public SyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
			super(context, autoInitialize, allowParallelSyncs);
		}

		// Delegate to outer class
		@Override
		public void onPerformSync(Account account, Bundle extras, String authority,
				ContentProviderClient provider, SyncResult syncResult) {
			AbstractSyncService.this.onPerformSync(getContext(),account, extras, authority, provider, syncResult);
		}

	}

	@Override
	public void onCreate() {
		mAuthenticator=new AccountAuthenticator(this);
		mSyncAdapter=(Build.VERSION.SDK_INT>=Build.VERSION_CODES.HONEYCOMB)
				? new SyncAdapter(getApplicationContext(), true,true)
				: new SyncAdapter(getApplicationContext(), true);
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		
		final String action = intent.getAction();
		
		if (action.equals("android.accounts.AccountAuthenticator")){
			return mAuthenticator.getIBinder();
		}
		else if (action.equals("android.content.SyncAdapter")){
			return mSyncAdapter.getSyncAdapterBinder();
		}
		else
			return null;
	}

	/**
	 * @see {@link android.content.AbstractThreadedSyncAdapter#onSyncCanceled()}
	 */
	protected void onSyncCanceled()
	{
	}

	/**
	 * @see {@link android.content.AbstractThreadedSyncAdapter#onSyncCanceled(Thread)}
	 */
	protected void onSyncCanceled(Thread thread)
	{
	}

	/**
	 * @see {@link android.content.AbstractThreadedSyncAdapter#onPerformSync(Account, Bundle, String, ContentProviderClient, SyncResult)}
	 */
	abstract protected void onPerformSync(Context context, Account account, Bundle extras,
			String authority, ContentProviderClient provider,
			SyncResult syncResult);

}
