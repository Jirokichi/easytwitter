package com.example.util;

import com.example.easytweetclient.R;
import com.example.easytweetclient.R.string;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;

// 参考サイト:http://d.hatena.ne.jp/sakura_bird1/20130207/1360193574
public class MyFragmentDialog extends DialogFragment {
	protected static final String TAG = "MyFragmentDialog";
	private TweetDialogListener listener = null;

	// ダイアログタイプ用パラメー保存キー
	private static String bundle_key_dialog_type = "DIALOG_TYPE";

	// ツイート用パラメー保存キー
	private static String bundle_key_title = "TITLE";
	private static String bundle_key_tweet = "TWEET";
	private static String bundle_key_message = "MESSAGE";

	// ダイアログタイプ
	public enum TYPE {
		TWEET_DIALOG, JUST_CONFIRMATION_DIALOG
	};

	// Tweet用
	public static MyFragmentDialog newInstanceForTweet(String title, String tweetMessage, String diaogMessage) {
		MyFragmentDialog frag = new MyFragmentDialog();
		Bundle bundle = new Bundle();
		bundle.putString(bundle_key_dialog_type, TYPE.TWEET_DIALOG.name());
		bundle.putString(bundle_key_title, title);
		bundle.putString(bundle_key_tweet, tweetMessage);
		bundle.putString(bundle_key_message, diaogMessage);
		frag.setArguments(bundle);
		return frag;
	}

	// キャンセルのみ用
	public static MyFragmentDialog newInstanceForJustCancel(String title, String diaogMessage) {
		MyFragmentDialog frag = new MyFragmentDialog();
		Bundle bundle = new Bundle();
		bundle.putString(bundle_key_dialog_type, TYPE.JUST_CONFIRMATION_DIALOG.name());
		bundle.putString(bundle_key_title, title);
		bundle.putString(bundle_key_message, diaogMessage);
		frag.setArguments(bundle);
		return frag;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		TYPE dialogType = TYPE.valueOf(getArguments().getString(bundle_key_dialog_type));
		switch (dialogType) {
		case TWEET_DIALOG:
			return createTweetDialog(savedInstanceState);
		case JUST_CONFIRMATION_DIALOG:
			return createCancelDialog(savedInstanceState);
		default:
			return null;
		}
	}

	private Dialog createTweetDialog(Bundle savedInstanceState) {
		String title = getArguments().getString(bundle_key_title);
		final String tweetMessage = getArguments().getString(bundle_key_tweet);
		String dialogMessage = getArguments().getString(bundle_key_message);

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(title);
		builder.setMessage(dialogMessage);
		builder.setPositiveButton(getString(R.string.Yes), new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				Log.d(TAG, "onClick:" + tweetMessage);
				listener.onPositiveTweetClick(tweetMessage);
				dismiss();
			}
		});
		builder.setNegativeButton(getString(R.string.No), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				listener.onNegativeClick();
				dismiss();
			}
		});
		return builder.create();
	}
	
	private Dialog createCancelDialog(Bundle savedInstanceState) {
		String title = getArguments().getString(bundle_key_title);
		String dialogMessage = getArguments().getString(bundle_key_message);

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(title);
		builder.setMessage(dialogMessage);
		builder.setPositiveButton(getString(R.string.Yes), new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				listener.onNegativeClick();
				dismiss();
			}
		});
		return builder.create();
	}

	/**
	 * リスナーを追加する
	 * 
	 * @param listener
	 */
	public void setDialogListener(TweetDialogListener listener) {
		this.listener = listener;
	}

	/**
	 * リスナーを削除する
	 */
	public void removeDialogListener() {
		this.listener = null;
	}
}