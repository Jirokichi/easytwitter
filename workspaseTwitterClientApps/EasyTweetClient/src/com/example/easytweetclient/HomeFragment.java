package com.example.easytweetclient;

import twitter4j.TwitterException;
import twitter4j.auth.AccessToken;

import com.example.twitter.MyTwitterAPI;
import com.example.twitter.MyTwitterAPI.APIType;
import com.example.twitter.TwitterListener;
import com.example.util.MyFragmentDialog;
import com.example.util.TweetDialogListener;
import com.example.util.Util;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class HomeFragment extends Fragment implements OnClickListener, TweetDialogListener, TwitterListener {
	
	private static final String TAG = "HomeFragment";
	
	private final String key_bundle_textField = "KEY_BUNDLE_TEXTFIELD";
	
	private Button mWakeUpButton = null;
	private Button mGoToBedButton = null;
	private EditText mTweetEditText = null;

	public HomeFragment() {
		Log.d(TAG, "HomeFragment");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
		String str = null;
		View rootView = inflater.inflate(R.layout.fragment_home, container, false);
		
		
		// 起床ボタン
		mWakeUpButton = (Button) rootView.findViewById(R.id.wakeUpButton);
		mWakeUpButton.setOnClickListener(this);
		str = sp.getString(getString(R.string.key_current_wakeUpButton_message), null);
		if(str == null || str.length() == 0){
			mWakeUpButton.setText(this.getString(R.string.homefragment_default_wakeUpButton_title));
		}else{
			mWakeUpButton.setText(str);
		}
		
		// 就寝ボタン
		mGoToBedButton = (Button) rootView.findViewById(R.id.goBedButton);
		mGoToBedButton.setOnClickListener(this);
		str = sp.getString(getString(R.string.key_current_goToBedButton_message), null);
		if(str == null || str.length() == 0){
			mGoToBedButton.setText(this.getString(R.string.homefragment_default_goBedButton_title));
		}else{
			mGoToBedButton.setText(str);
		}

		// ツイートボタン
		Button tweetButton = (Button) rootView.findViewById(R.id.tweetButton);
		tweetButton.setOnClickListener(this);
		tweetButton.setText(this.getString(R.string.homefragment_default_tweetButton_title));

		// ツイートメッセージ入力エディタ
		mTweetEditText = (EditText) rootView.findViewById(R.id.tweetEditText);

		return rootView;
	}
	

	@Override
	public void onClick(View v) {
		Log.d(TAG, "onClick start");
		String title = getString(R.string.dialog_title_to_confirm);
		String action = (String) ((Button) v).getText();

		switch (v.getId()) {
		// 起床ボタン
		case R.id.wakeUpButton:
			Log.d(TAG, "onWakeUpButtonClick start");
			break;
		// 就寝ボタン
		case R.id.goBedButton:
			Log.d(TAG, "onGoBedButtonClick start");
			break;
		// ツイートボタン
		case R.id.tweetButton:
			Log.d(TAG, "onTweetButtonClick start");
			SpannableStringBuilder sb = (SpannableStringBuilder) mTweetEditText.getText();
			String str = sb.toString();
			action = str;
			if(action == null || action.equals("")){
				String message = this.getString(R.string.homefragment_alertDialog_no_tweet_message, action);
				MyFragmentDialog dialog = MyFragmentDialog.newInstanceForJustCancel(title, message);
				dialog.setDialogListener(this);
				dialog.show(getActivity().getSupportFragmentManager(), "MyFragmentDialog");
				Log.d(TAG, "onClick end(tweetButton)");
				return;
			}
			break;
		}

		String message = this.getString(R.string.homefragment_alertDialog_message, action);

		MyFragmentDialog dialog = MyFragmentDialog.newInstanceForTweet(title, action, message);
		dialog.setDialogListener(this);
		dialog.show(getActivity().getSupportFragmentManager(), "MyFragmentDialog");
		// dialog.show(getSupportFragmentManager(), "dialog");

		Log.d(TAG, "onClick end");
	}

	// 自分のコードから Fragment の初期化に使うメソッド
	public static HomeFragment newInstance() {
		HomeFragment fragment = new HomeFragment();
		// 以下二行は、Fragment に初期化用の変数を渡したいときに使う
		// Bundle arguments = new Bundle();
		// fragment.setArguments(arguments);
		return fragment;
	}

	@Override
	public void onPositiveTweetClick(String tweetMessage) {
		// TODO Auto-generated method stub
		Log.d(TAG, "onPositiveTweetClick:"+tweetMessage);
		Toast.makeText(this.getActivity(), "Yes:"+tweetMessage, Toast.LENGTH_SHORT).show();
		
		MyTwitterAPI mta = new MyTwitterAPI(MyTwitterAPI.APIType.TWEET, getActivity().getString(R.string.consumerKey), getActivity().getString(R.string.consumerSecret), getActivity());
		mta.setTwitterListenr(this);
		
		AccessToken accessTokenSet = Util.getAccessToken(this);
		Log.d(TAG, "accessTokenSet - " + accessTokenSet);
		mta.execute(tweetMessage, accessTokenSet);
		
	}

	@Override
	public void onNegativeClick() {
		// TODO Auto-generated method stub
		Log.d(TAG, "onNegativeClick:");
		Toast.makeText(this.getActivity(), "No", Toast.LENGTH_SHORT).show();
	}
	
	@Override
	public void onPause(){
		// 必ず保存する必要があるデータはここに保存する。そしてonCreateViewで復元すればおk。
		super.onPause();
		Log.d(TAG, "onPause()");
		
	}
	
	@Override
	public void onStop(){
		super.onStop();
		Log.d(TAG, "onStop()");
	}
	@Override
	public void onResume(){
		super.onResume();
		Log.d(TAG, "onResume()");
		// To update the texts of buttons to the latest texts 
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
		String str = null;
		
		if(mGoToBedButton != null){
			str = sp.getString(getString(R.string.key_current_goToBedButton_message), null);
			if (str == null || str.length() == 0) {
				mGoToBedButton.setText(this.getString(R.string.homefragment_default_goBedButton_title));
			} else {
				mGoToBedButton.setText(str);
			}
		}
		if (mWakeUpButton != null) {
			str = sp.getString(getString(R.string.key_current_wakeUpButton_message), null);
			if (str == null || str.length() == 0) {
				mWakeUpButton.setText(this.getString(R.string.homefragment_default_wakeUpButton_title));
			} else {
				mWakeUpButton.setText(str);
			}
		}
		
	}
	@Override
	public void onStart(){
		super.onStop();
		Log.d(TAG, "onStart()");
	}
	
	@Override
	public void onDestroyView(){
		super.onDestroyView();
		Log.d(TAG, "onDestroyView()");
	}
	@Override
	public void onDestroy(){
		super.onDestroy();
		Log.d(TAG, "onDestroy()");
	}

	@Override
	public void onReturnAPI(APIType apiType, Object object) {
		Log.d(TAG, "onReturnAPI("+apiType +") - " + object);
		
		if(object == null || apiType == null){
			return;
		}
		
		if(object instanceof TwitterException){
			Toast.makeText(getActivity(), "Error", Toast.LENGTH_LONG).show();
			return;
		}
		
	}
}
