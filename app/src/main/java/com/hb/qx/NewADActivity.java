package com.hb.qx;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.baidu.mobads.SplashAd;
import com.baidu.mobads.SplashAdListener;

public class NewADActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_newad);
		showAd();
	}

	public void showAd() {
		// adUnitContainer
		LinearLayout adsParent = (LinearLayout) this
				.findViewById(R.id.adsRl_new);
		// the observer of AD
		SplashAdListener listener = new SplashAdListener() {
			@Override
			public void onAdDismissed() {
				Log.i("RSplashActivity", "onAdDismissed");
				System.out.println("--------------dismiss-----------------");
			}

			@Override
			public void onAdFailed(String arg0) {
				Log.i("RSplashActivity", "onAdFailed");
			}

			@Override
			public void onAdPresent() {
				Log.i("RSplashActivity", "onAdPresent");
			}

			@Override
			public void onAdClick() {
				Log.i("RSplashActivity", "onAdClick");
				// 设置开屏可接受点击时，该回调可用
			}
		};
		String adPlaceId = "2423494"; // 重要：请填上您的广告位ID，代码位错误会导致无法请求到广告
		new SplashAd(this, adsParent, listener, adPlaceId, true);
	}
}
