package com.example.util;

import java.util.EventListener;

public interface TweetDialogListener extends EventListener {
	 /**
     * Tweet確認画面で
     * okボタンが押されたイベントを通知する
     */
    public void onPositiveTweetClick(String tweetMessage);

    /**
     * 画面に関わらず、
     * cancelボタンが押されたイベントを通知する
     */
    public void onNegativeClick();
}
