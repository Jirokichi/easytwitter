package com.example.twitter;

import com.example.twitter.MyTwitterAPI.APIType;

public interface TwitterListener {
	public void onReturnAPI(APIType apiType, Object object);
}
