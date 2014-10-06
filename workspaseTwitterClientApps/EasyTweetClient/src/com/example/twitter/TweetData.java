package com.example.twitter;

import android.graphics.Bitmap;

public class TweetData {
    private Bitmap mImage;
    public String mUserName;
    public String mAccountName;
    public String mTweetMessage;
    public String mFrom;
    public String mTime;
 
    public void setImagaData(Bitmap image) {
    	mImage = image;
    }
 
    public Bitmap getImageData() {
        return mImage;
    }
 
    public void setTextData(String...text) {
    	if(text == null || text.length != 5){
    		return;
    	}
    	mUserName = text[0];
    	mAccountName = text[1];
    	mTweetMessage = text[2];
    	mFrom = text[3];
    	mTime = text[4];
    }
}