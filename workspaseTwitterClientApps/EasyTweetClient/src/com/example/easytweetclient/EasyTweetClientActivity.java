package com.example.easytweetclient;

import java.util.Locale;

import com.example.setting.MyPreference;
import com.example.util.UtilKey;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class EasyTweetClientActivity extends FragmentActivity implements ActionBar.TabListener {

	private static final String TAG = "EasyTweetClientActivity";

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
	 * will keep every loaded fragment in memory. If this becomes too memory
	 * intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "onCreate start");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_easy_tweet_client);

		// Set up the action bar.
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		// When swiping between different sections, select the corresponding
		// tab. We can also use ActionBar.Tab#select() to do this if we have
		// a reference to the Tab.
		mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				actionBar.setSelectedNavigationItem(position);
			}
		});

		// For each of the sections in the app, add a tab to the action bar.
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			// Create a tab with text corresponding to the page title defined by
			// the adapter. Also specify this Activity object, which implements
			// the TabListener interface, as the callback (listener) for when
			// this tab is selected.
			actionBar.addTab(actionBar.newTab().setText(mSectionsPagerAdapter.getPageTitle(i)).setTabListener(this));
		}
		Log.d(TAG, "onCreate end");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		Log.d(TAG, "onCreateOptionsMenu start");
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.easy_tweet_client, menu);
		Log.d(TAG, "onCreateOptionsMenu end");
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.d(TAG, "onOptionsItemSelected start");
		switch (item.getItemId()) {
		case R.id.action_settings:
			Log.d(TAG, "Select action_settings");
			Intent intent = new Intent(this, MyPreference.class);
			startActivity(intent);
			this.saveCurrentStatus();
			return true;
		default:
			Log.d(TAG, "Select Default");

		}
		Log.d(TAG, "onOptionsItemSelected end");
		return false;
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
		Log.d(TAG, "onTabSelected start");
		// When the given tab is selected, switch to the corresponding page in
		// the ViewPager.
		mViewPager.setCurrentItem(tab.getPosition());
		Log.d(TAG, "onTabSelected end");
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
		Log.d(TAG, "onTabUnselected");
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
		Log.d(TAG, "onTabReselected");
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
			Log.d(TAG, "SectionsPagerAdapter");
		}

		@Override
		public Fragment getItem(int position) {
			Log.d(TAG, "getItem(" + position + ") start");
			// 数値->enum
			UtilKey.FRAGMENT_NUMBER[] values = UtilKey.FRAGMENT_NUMBER.values();
			UtilKey.FRAGMENT_NUMBER align = values[position];
			Fragment fragment = new HomeFragment();
			switch (align) {
			case HOME:
				fragment = new HomeFragment();
				break;
			case HISTORY:
				fragment = new HistoryFragment();
				break;
			// case SETUP:
			// fragment = new ETCPreferenceFragment();
			// break;
			}
			Log.d(TAG, "getItem(" + position + ") end");
			return fragment;
		}

		@Override
		public int getCount() {
			int total = 2;
			return total;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Log.d(TAG, "getPageTitle(" + position + ") start");
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return getString(R.string.title_section_home).toUpperCase(l);
			case 1:
				return getString(R.string.title_section_history).toUpperCase(l);
			}
			Log.d(TAG, "getPageTitle(" + position + ") end");
			return null;
		}
	}

	/*
	 * 現在の状況を保存するためのメソッッド onPauseや設定画面への遷移時に実行される
	 */
	private void saveCurrentStatus() {

	}

}
