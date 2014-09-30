package com.ihateflyingbugs.vocaslide;

import android.widget.Toast;

import com.kakao.APIErrorResult;
import com.kakao.KakaoTalkHttpResponseHandler;

public abstract class MyTalkHttpResponseHandler<T> extends KakaoTalkHttpResponseHandler<T> {
	
	@Override
	protected void onHttpSessionClosedFailure(final APIErrorResult errorResult) {
	}

	@Override
	protected void onNotKakaoTalkUser(){
	}

	@Override
	protected void onFailure(final APIErrorResult errorResult) {
	}

}
