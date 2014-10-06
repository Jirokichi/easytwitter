package com.example.twitter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.example.easytweetclient.R;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import twitter4j.Paging;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

// Delegate

public class MyTwitterAPI extends AsyncTask implements OnCancelListener {

	/*
	 * ツイッターAPIの使い方のまとめ 1. Twitter developerのサイトで、ConsumerKey and
	 * ConsumerSecretを取得 2. 取得した二つをもとに、Pinコードを発行するためのURLを取得 3.
	 * 利用者にＰＩＮコードを入力してもらい、そのコードをもとにアクセストークンを作成して保存 4. アクセストークンでツイッターの機能を利用
	 */

	public enum APIType {
		GET_URL_FOR_PINCODE, GET_AUTHTOKEN, TWEET, GET_USER_INFO, GET_HOME_TIME_LINE, GET_USER_TIME_LINE, CANCEL
	};

	private static final String TAG = "MyTwitterAPI";
	private Context mContext = null;
	private Twitter mTwitter = null;
	private TwitterListener mTwitterListener = null;

	// ダイアログの表示がまだ実装されていない
	private APIType mAPIType = null;
	private ProgressDialog mDialog = null;

	// PINコードを取得するためのコンストラクタ
	public MyTwitterAPI(APIType apitype, String consumerKey, String consumerSecret, Context context) {
		Log.d(TAG, "MyTwitterAPI - " + apitype);
		mTwitter = new TwitterFactory().getInstance();
		mTwitter.setOAuthConsumer(consumerKey, consumerSecret);
		this.mContext = context;
		this.mAPIType = apitype;
	}

	public void changeAPIType(APIType apitype) {
		this.mAPIType = apitype;
	}

	public void setTwitterListenr(TwitterListener twitterListener) {
		Log.d(TAG, "setTwitterListenr");
		this.mTwitterListener = twitterListener;
	}

	@Override
	protected void onPreExecute() {
		Log.d(TAG, "onPreExecute - ");
		mDialog = new ProgressDialog(mContext);
		mDialog.setTitle("Please wait");
		mDialog.setMessage("Loading data...");
		mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		mDialog.setCancelable(true);
		mDialog.setOnCancelListener(this);
		mDialog.setMax(100);
		mDialog.setProgress(0);
		mDialog.show();
	}

	@Override
	protected Object doInBackground(Object... params) {
		// TODO Auto-generated method stub
		Log.d(TAG, "doInBackground - " + params);

		Object object = null;
		AccessToken accessToken = null;
		int numStartTimeLine = 1;
		int numTimeLine = 1;
		
		try {
			switch (mAPIType) {
			case GET_URL_FOR_PINCODE:
				object = this.getRequestToken();
				break;
			case GET_AUTHTOKEN:
				// params[0] should be pin code
				object = getAccessToken((RequestToken) params[0], (String) params[1]);
				break;
			case TWEET:
				String message = (String) params[0];
				accessToken = (AccessToken) params[1];
				object = updateStatus(message, accessToken);
				break;
			case GET_USER_INFO:
				accessToken = (AccessToken) params[0];
				object = getAccountSettings(accessToken);
				break;
			case GET_USER_TIME_LINE:
				accessToken = (AccessToken) params[0];
				numStartTimeLine = (Integer) params[1];
				numTimeLine = (Integer) params[2];
				object = getUserTimeLine(accessToken, numStartTimeLine, numTimeLine);
				
			case GET_HOME_TIME_LINE:
				accessToken = (AccessToken) params[0];
				numStartTimeLine = (Integer) params[1];
				numTimeLine = (Integer) params[2];
				object = getHomeTimeLine(accessToken, numStartTimeLine, numTimeLine);
				
			default:
				break;
			}
		} catch (TwitterException te) {
			object = te;
		}
		return object;
	}

	@Override
	protected void onPostExecute(Object result) {
		Log.d(TAG, "onPostExecute - " + result);
		if (mDialog == null || mTwitterListener == null) {
			return;
		}
		
		if (result != null) {
			switch (mAPIType) {
			case GET_URL_FOR_PINCODE:
			case GET_AUTHTOKEN:
			case TWEET:
			case GET_USER_INFO:
			case GET_USER_TIME_LINE:
			case GET_HOME_TIME_LINE:
				mTwitterListener.onReturnAPI(mAPIType, result);
				break;
			default:
				break;
			}
		}

		if (mDialog != null) {
			mDialog.dismiss();
			mDialog = null;
		}
	}

	@Override
	protected void onCancelled() {
		// This method is called after onCancel for Dialod is called.
		Log.d(TAG, "onCancelled in AsyncTask");
		this.mTwitterListener.onReturnAPI(null, null);
	}

	/*
	 * RequestToken取得のためのメソッド
	 */
	private RequestToken getRequestToken() {
		Log.d(TAG, "getRequestToken");
		RequestToken requestToken = null;
		try {
			// このrequestTokenの値をもとにaccessTokenを取得する必要があり
			requestToken = mTwitter.getOAuthRequestToken();
		} catch (TwitterException e) {
			e.printStackTrace();
		}
		return requestToken;
	}

	/*
	 * AccessToken取得のためのメソッド param1 : Pin Code Description:
	 * このメソッッドはGET_URL_FOR_PINCODEでAsyncTaskを走らせたあとに続けて実行する必要がある
	 */
	private AccessToken getAccessToken(RequestToken requestToken, String pin)  throws TwitterException {
		Log.d(TAG, "getAccessToken - " + pin);
		if (pin == null || pin.length() <= 0) {
			Log.d(TAG, "pinコード不正 - " + pin);
			return null;
		}
		if (requestToken == null) {
			Log.d(TAG, "RequestToken不正 - " + requestToken);
			return null;
		}

		AccessToken accessToken = null;
		try {
			accessToken = mTwitter.getOAuthAccessToken(requestToken, pin);
		} catch (TwitterException te) {
			if (401 == te.getStatusCode()) {
				Log.d(TAG, "Unable to get the access token.");
			} else {
				te.printStackTrace();
			}
			throw te;
		}
		return accessToken;
	}

	public twitter4j.Status updateStatus(String message, AccessToken accessToken) throws TwitterException {
		Log.d(TAG, "updateStatus - " + message);
		if (message == null || message.length() == 0 || accessToken == null || accessToken.getToken() == null || accessToken.getTokenSecret() == null) {
			Log.d(TAG, "Access Tokenが設定されていません");
			return null;
		}
		mTwitter.setOAuthAccessToken(accessToken);
		twitter4j.Status status = null;
		try {
			status = mTwitter.updateStatus(message);
			Log.d(TAG, "Successfully updated the status to [" + status.getText() + "].");
		} catch (TwitterException te) {
			te.printStackTrace();
			Log.d(TAG, "Failed to get timeline: " + te.getMessage());
			throw te;
		}
		return status;
	}

	public twitter4j.AccountSettings getAccountSettings(AccessToken accessToken) throws TwitterException  {
		Log.d(TAG, "getAccountSettings - ");
		if (accessToken.getToken() == null || accessToken.getTokenSecret() == null) {
			Log.d(TAG, "Access Tokenが設定されていません");
			return null;
		}
		mTwitter.setOAuthAccessToken(accessToken);
		twitter4j.AccountSettings accountSettings = null;
		try {
			accountSettings = mTwitter.getAccountSettings();
			Log.d(TAG, "Successfully got the account info: " + accountSettings.getScreenName());
		} catch (TwitterException te) {
			te.printStackTrace();
			Log.d(TAG, "Failed to get timeline: " + te.getMessage());
			throw te;
		}
		return accountSettings;
	}
	
	ResponseList<twitter4j.Status> getUserTimeLine(AccessToken accessToken, int numStartTimeLine, int numTimeLine) throws TwitterException{
		Log.d(TAG, "getAccountSettings - ");
		if (accessToken.getToken() == null || accessToken.getTokenSecret() == null) {
			Log.d(TAG, "Access Tokenが設定されていません");
			return null;
		}
		mTwitter.setOAuthAccessToken(accessToken);

		ResponseList<twitter4j.Status> userstatus = null;
		try {
			userstatus = mTwitter.getUserTimeline(new Paging(numStartTimeLine,numTimeLine));
		} catch (TwitterException te) {
			te.printStackTrace();
			Log.d(TAG, "Failed to get timeline: " + te.getMessage());
			throw te;
		}
		
		return userstatus;
	}

	ResponseList<twitter4j.Status> getHomeTimeLine(AccessToken accessToken, int numStartTimeLine, int numTimeLine) throws TwitterException{
		Log.d(TAG, "getAccountSettings - ");
		if (accessToken.getToken() == null || accessToken.getTokenSecret() == null) {
			Log.d(TAG, "Access Tokenが設定されていません");
			return null;
		}
		mTwitter.setOAuthAccessToken(accessToken);

		ResponseList<twitter4j.Status> userstatus = null;
		try {
			userstatus = mTwitter.getHomeTimeline(new Paging(numStartTimeLine,numTimeLine));
		} catch (TwitterException te) {
			te.printStackTrace();
			Log.d(TAG, "Failed to get timeline: " + te.getMessage());
			throw te;
		}
		
		return userstatus;
	}
	
	
	@Override
	public void onCancel(DialogInterface dialog) {
		// TODO Auto-generated method stub
		// DialogのためのonCancel
		Log.d(TAG, "onCancel(DialogInterface dialog)");
		if (this.mDialog != null) {
			this.mDialog.dismiss();
			this.mDialog = null;
		}
		this.onCancelled();
	}
}
