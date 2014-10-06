package com.example.util;

import twitter4j.auth.AccessToken;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.example.easytweetclient.R;

public class Util {
	static public AccessToken getAccessToken(Fragment context){
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context.getActivity());
		String accessToken = prefs.getString(context.getString(R.string.key_twitter_accessToken), null);
		String accessTokenSecret = prefs.getString(context.getString(R.string.key_twitter_accessTokenSecret), null);
		return new AccessToken(accessToken, accessTokenSecret);
	}
	static public AccessToken getAccessToken(Context context){
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		String accessToken = prefs.getString(context.getString(R.string.key_twitter_accessToken), null);
		String accessTokenSecret = prefs.getString(context.getString(R.string.key_twitter_accessTokenSecret), null);
		return new AccessToken(accessToken, accessTokenSecret);
	}
}
