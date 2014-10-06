package com.example.easytweetclient;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import twitter4j.AccountSettings;
import twitter4j.ResponseList;
import twitter4j.TwitterException;
import twitter4j.auth.AccessToken;

import com.example.twitter.MyTwitterAPI;
import com.example.twitter.MyTwitterAPI.APIType;
import com.example.twitter.TweetData;
import com.example.twitter.TweetListAdapter;
import com.example.twitter.TwitterListener;
import com.example.util.Util;
import com.fortysevendeg.swipelistview.BaseSwipeListViewListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshSwipeListView;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class HistoryFragment extends Fragment implements TwitterListener {
	/**
	 * The fragment argument representing the section number for this fragment.
	 */
	private static final String TAG = "HistoryFragment";
	Handler mHandler = new Handler();
	PullToRefreshSwipeListView rlv;
	MyTwitterAPI mTwitterAPI = null;

	ResponseList<twitter4j.Status> mUserstatus = null;

	// List<TweetData> mTweetData = new ArrayList<TweetData>();
	TweetListAdapter tla = null;

	final int firstDataNum = 5;

	public HistoryFragment() {
		Log.d(TAG, "HistoryFragment");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_history, container, false);

		// PullTorefreshSwipeListViewの取得
		rlv = (PullToRefreshSwipeListView) rootView.findViewById(R.id.refresh_list_view);

		// PullTorefreshListViewへの処理は取得したListVIewに行う
		rlv.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh(PullToRefreshBase refreshView) {
				// 更新処理
				Log.d(TAG, "onRefresh");
				new Thread(new Runnable() {
					public void run() {
						mHandler.post(new Runnable() {
							public void run() {
								reLoadListData();
							}
						});
					}
				}).start();
			}
		});
		// SwipeListViewへの処理はgetrefreshableView()で取得して行う
		rlv.getRefreshableView().setSwipeListViewListener(new BaseSwipeListViewListener() {
			@Override
			public void onClickFrontView(int position) {
				// アイテム押下時の処理
				Log.d(TAG, "onClickFrontView");
			};
		});
		return rootView;
	}

	void getListDataFromDataBase() {

	}

	void reLoadListData() {
		//
		if (isReloading()) {
			didLoadListDate();
		} else {
			Log.d(TAG, "reLoadListData:");
			this.mTwitterAPI = new MyTwitterAPI(MyTwitterAPI.APIType.GET_HOME_TIME_LINE, getActivity().getString(R.string.consumerKey), getActivity().getString(R.string.consumerSecret), getActivity());
			mTwitterAPI.setTwitterListenr(this);

			AccessToken accessTokenSet = Util.getAccessToken(this);
			mTwitterAPI.execute(accessTokenSet, 1, 10);
		}
	}

	boolean isReloading() {
		boolean isreload = false;
		Log.d(TAG, "isReloading:未実装");
		return isreload;
	}

	// reloadListDataの処理終了時
	private void didLoadListDate() {
		rlv.onRefreshComplete();
		mTwitterAPI = null;
	}

	@Override
	public void onReturnAPI(APIType type, Object object) {
		// TODO Auto-generated method stub
		Log.d(TAG, "onReturnAPI(" + type + ") - " + object);
		if (object == null || type == null) {
			didLoadListDate();
			return;
		}

		if (object instanceof TwitterException) {
			Toast.makeText(getActivity(), "Error", Toast.LENGTH_LONG).show();
			didLoadListDate();
			return;
		}

		// Userタイムラインの取得
		if (type.equals(APIType.GET_USER_TIME_LINE) || type.equals(APIType.GET_HOME_TIME_LINE)) {
			// To get the downloaded tweet data
			mUserstatus = (ResponseList<twitter4j.Status>) object;

			// データの作成
			// リソースに準備した画像ファイルからBitmapを作成しておく
			Bitmap image;
			image = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);

			if (tla != null) {
				tla.clear();
			}
			List<TweetData> tweetData = new ArrayList<TweetData>();
			for(twitter4j.Status status:mUserstatus){
				Log.d(TAG, "Status: " + status);
				TweetData item = new TweetData();
				item.setImagaData(image);
				item.mUserName = status.getUser().getScreenName();
				item.mTweetMessage = status.getText();
				tweetData.add(item);
			}

			tla = new TweetListAdapter(this.getActivity(), 0, tweetData);
			rlv.getRefreshableView().setAdapter(tla);
		}

		didLoadListDate();
	}
}
