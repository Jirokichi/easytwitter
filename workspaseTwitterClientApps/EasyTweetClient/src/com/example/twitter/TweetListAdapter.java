package com.example.twitter;

import java.util.List;

import com.example.easytweetclient.R;
import com.fortysevendeg.swipelistview.SwipeListView;
import com.handmark.pulltorefresh.library.PullToRefreshSwipeListView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class TweetListAdapter extends ArrayAdapter<TweetData> {
	private LayoutInflater layoutInflater_;

	public TweetListAdapter(Context context, int textViewResourceId, List<TweetData> objects) {
		super(context, textViewResourceId, objects);
		layoutInflater_ = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// 特定の行(position)のデータを得る
		TweetData item = (TweetData) getItem(position);

		// convertViewは使い回しされている可能性があるのでnullの時だけ新しく作る
		if (null == convertView) {
			convertView = layoutInflater_.inflate(R.layout.list_tweet, null);
		}

		//((PullToRefreshSwipeListView)parent).getRefreshableView().recycle(convertView, position);
		
		// CustomDataのデータをViewの各Widgetにセットする
		ImageView imageView;
		imageView = (ImageView) convertView.findViewById(R.id.example_row_iv_image);
		imageView.setImageBitmap(item.getImageData());

		TextView textView;
		textView = (TextView) convertView.findViewById(R.id.example_row_tv_title);
		textView.setText(item.mUserName);
		
		TextView textView2;
		textView2 = (TextView) convertView.findViewById(R.id.example_row_tv_description);
		textView2.setText(item.mTweetMessage);

		return convertView;
	}
}
