package com.example.setting;

import twitter4j.AccountSettings;
import twitter4j.TwitterException;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

import com.example.easytweetclient.R;
import com.example.easytweetclient.R.string;
import com.example.easytweetclient.R.xml;
import com.example.twitter.MyTwitterAPI;
import com.example.twitter.TwitterListener;
import com.example.twitter.MyTwitterAPI.APIType;
import com.example.util.Util;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

public class MyPreference extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getFragmentManager().beginTransaction().replace(android.R.id.content, new HogePreferencesFragment()).commit();

		ActionBar actionBar = getActionBar();
		actionBar.setHomeButtonEnabled(true);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public static class HogePreferencesFragment extends PreferenceFragment implements OnPreferenceChangeListener, OnPreferenceClickListener, TwitterListener {

		//private MyTwitterAPI mTwitterAPI = null;
		private static final String TAG = "HogePreferencesFragment";
		private RequestToken mRequestToken = null;
		
		
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);

			addPreferencesFromResource(R.xml.fragment_preference);

			// // キーを基に、テキストボックス設定のインスタンスを取得する
			EditTextPreference etp = (EditTextPreference) findPreference(getString(R.string.key_preference_twitter));
			etp.setOnPreferenceChangeListener(this);
			etp.setOnPreferenceClickListener(this);
			
			// UserInfo取得
			SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
			String str = sp.getString(getString(R.string.key_preference_twitter_user_info), null);
			Preference p = (Preference)findPreference(getString(R.string.key_preference_twitter_user_info));
			p.setOnPreferenceClickListener(this);
			if(str != null)
				p.setTitle(str);
			else{
				p.setTitle("未設定");
			}
		}

		@Override
		public boolean onPreferenceClick(Preference preference) {
			// TODO Auto-generated method stub
			Log.d(TAG, "onPreferenceClick - " + preference);
			if (preference.getKey().equals(getString(R.string.key_preference_twitter))) {
				// 認証キー取得のためのブラウザ起動
				Log.d(TAG, "認証キー取得のためのブラウザ起動");
				MyTwitterAPI twitterAPI = new MyTwitterAPI(MyTwitterAPI.APIType.GET_URL_FOR_PINCODE, getActivity().getString(R.string.consumerKey), getActivity().getString(R.string.consumerSecret), getActivity());
				twitterAPI.setTwitterListenr(this);
				twitterAPI.execute("Start");
			}else if (preference.getKey().equals(getString(R.string.key_preference_twitter_user_info))) {
				Log.d(TAG, "ユーザー情報取得");
				MyTwitterAPI mta = new MyTwitterAPI(MyTwitterAPI.APIType.GET_USER_INFO, getActivity().getString(R.string.consumerKey), getActivity().getString(R.string.consumerSecret), getActivity());
				mta.setTwitterListenr(this);

				AccessToken accessTokenSet = Util.getAccessToken(this.getActivity());
				Log.d(TAG, "accessTokenSet - " + accessTokenSet);
				mta.execute(accessTokenSet);
			}
			return false;
		}
		
		@Override
		public boolean onPreferenceChange(Preference preference, Object newValue) {
			// TODO Auto-generated method stub
			Log.d(TAG, "onPreferenceChange - " + newValue);

			if (newValue != null && ((String) newValue).length() > 0) {
				// OTokenの取得を実施
				MyTwitterAPI twitterAPI = new MyTwitterAPI(MyTwitterAPI.APIType.GET_AUTHTOKEN, getActivity().getString(R.string.consumerKey), getActivity().getString(R.string.consumerSecret), getActivity());
				twitterAPI.setTwitterListenr(this);
				twitterAPI.changeAPIType(APIType.GET_AUTHTOKEN);
				twitterAPI.execute(this.mRequestToken, (String)newValue);
			} else {
				return false;
			}

			return false;
		}

		@Override
		public void onReturnAPI(APIType type, Object object) {
			// TODO Auto-generated method stub
			Log.d(TAG, "onReturnAPI(" + type +") - " + object);
			if(object == null || type == null){
				return;
			}
			
			if(object instanceof TwitterException){
				Toast.makeText(getActivity(), "Error", Toast.LENGTH_LONG).show();
				return;
			}
			
			// PINコード取得のためのAPI呼び出し後
			if (type.equals(APIType.GET_URL_FOR_PINCODE)) {
				mRequestToken = (RequestToken)object;
				String url = mRequestToken.getAuthorizationURL();
				if (url != null && url.length() != 0) {
					Uri uri = Uri.parse(url);
					Intent i = new Intent(Intent.ACTION_VIEW, uri);
					startActivity(i);
				} else {
					Toast.makeText(this.getActivity(), this.getActivity().getString(R.string.mypreference_no_url_alert_message), Toast.LENGTH_LONG).show();
				}
			}
			// アクセストークン取得のためのAPI呼び出し後
			else if(type.equals(APIType.GET_AUTHTOKEN)){
				AccessToken accessToken = (AccessToken)object;
				// accessTokenをSharedPreferencesで保存
				Log.d(TAG, "Got access token.");
				Log.d(TAG, "Access token: " + accessToken.getToken());
				Log.d(TAG, "Access token secret: " + accessToken.getTokenSecret());
				SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
				SharedPreferences.Editor editor = prefs.edit();
				editor.putString(getString(R.string.key_twitter_accessToken), accessToken.getToken());
				editor.putString(getString(R.string.key_twitter_accessTokenSecret), accessToken.getTokenSecret());
				editor.apply();
			}
			// ユーザ情報取得API
			else if (type.equals(APIType.GET_USER_INFO)) {
				AccountSettings accountSettings = (AccountSettings) object;
				// accessTokenをSharedPreferencesで保存
				Log.d(TAG, "accountSettings: " + accountSettings.getScreenName());
				SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
				SharedPreferences.Editor editor = prefs.edit();
				editor.putString(getString(R.string.key_preference_twitter_user_info), accountSettings.getScreenName());
				editor.apply();
				
				// UserInfo取得
				SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
				String str = sp.getString(getString(R.string.key_preference_twitter_user_info), null);
				Preference p = (Preference)findPreference(getString(R.string.key_preference_twitter_user_info));
				p.setOnPreferenceClickListener(this);
				if(str != null)
					p.setTitle(str);
				else{
					p.setTitle("未設定");
				}
			}

		}

		
	}
}
