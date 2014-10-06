package com.example.dummy;

import com.example.easytweetclient.R;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

public class MyFragmentActivity extends FragmentActivity {
	private static final String TAG = "MyFragmentActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "onCreate start");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dummy);
	}
}
