package com.hb.qx;

import in.srain.cube.request.FailData;
import in.srain.cube.request.JsonData;
import in.srain.cube.request.RequestJsonHandler;

import com.baidu.mobads.SplashAd;
import com.baidu.mobads.SplashAdListener;
import com.hb.tool.Commonutil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class NewADActivity extends Activity {

	private HbApplication application;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_newad);
		showAd();
	}

	public void request() {
		RequestUtil.reverseget(URL.UP_URL, new RequestJsonHandler() {
			@Override
			public void onRequestFinish(final JsonData data) {
				try {
					if (data != null) {
						int vsercode = data.optInt("vsercode");
						System.out.println(application.vsercode);
						if (vsercode > application.vsercode) {
							View view = getLayoutInflater().inflate(
									R.layout.dialog_update, null);
							AlertDialogContainer container = new AlertDialogContainer(
									NewADActivity.this, view);
							container.setNoText("否");
							container.setOkText("是");
							container.setTitle("更新通知");
							TextView textView = (TextView) view
									.findViewById(R.id.msg);
							textView.setText(Html.fromHtml(data
									.optString("msg")));
							container.setCallBack(new AlertDialogCallBack() {
								@Override
								public boolean ok() {
									Intent intent = new Intent(
											NewADActivity.this,
											UpdateService.class);
									intent.putExtra("dowurl",
											data.optString("dowurl"));
									startService(intent);
									if (data.optString("compulsory").equals(
											"no")) {
										jump();
									}
									return false;
								}

								@Override
								public boolean no() {
									if (data.optString("compulsory").equals(
											"no")) {
										jump();
										return true;
									} else {
										return false;
									}
								}
							});
						} else {
							jump();
						}
					}
				} catch (Exception e) {

				}

			}
			@Override
			public void onRequestFail(FailData failData) {
				jump();
			}

		});
	}

	public void request_new() {
		RequestUtil.reverseget(URL.NEW_URL, new RequestJsonHandler() {
			@Override
			public void onRequestFinish(final JsonData data) {
				JsonData listJsonData = data.optJson("data").optJson("list");
				System.out.println(listJsonData.toString());
				Commonutil.writeData("new_text.txt", listJsonData.toString(),
						getApplicationContext());
			}

			@Override
			public void onRequestFail(FailData failData) {

			}
		});
	}

	public void request_lock_type() {
		RequestUtil.reverseget(URL.LOCK_URL, new RequestJsonHandler() {
			@Override
			public void onRequestFinish(final JsonData data) {
				if (data != null) {
					String lock = data.optString("lock_type");
					application.editor.putString("lock_type", lock);
					application.editor.commit();
				}
			}

			@Override
			public void onRequestFail(FailData failData) {

			}
		});
	}

	public void showAd() {
		// adUnitContainer
		RelativeLayout adsParent = (RelativeLayout) this
				.findViewById(R.id.adsRl_new);
		// the observer of AD
		SplashAdListener listener = new SplashAdListener() {
			@Override
			public void onAdDismissed() {
				Log.i("RSplashActivity", "onAdDismissed");
				
				System.out.println("--------------dismiss-----------------");
				
				jump(); // 跳转至您的应用主界面
			}

			@Override
			public void onAdFailed(String arg0) {
				Log.i("RSplashActivity", "onAdFailed");
				request();
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

	/**
	 * 当设置开屏可点击时，需要等待跳转页面关闭后，再切换至您的主窗口。故此时需要增加canJumpImmediately判断。
	 * 另外，点击开屏还需要在onResume中调用jumpWhenCanClick接口。
	 */
	public boolean canJumpImmediately = false;

	private void jumpWhenCanClick() {
		Log.d("test", "this.hasWindowFocus():" + this.hasWindowFocus());
		if (canJumpImmediately) {
			request();
			// this.startActivity(new Intent(SplashActivity.this,
			// MainActivity.class));
			// this.finish();
		} else {
			canJumpImmediately = true;
		}

	}

	@Override
	protected void onPause() {
		super.onPause();
		canJumpImmediately = false;
	}

	/**
	 * 不可点击的开屏，使用该jump方法，而不是用jumpWhenCanClick
	 */
	private void jump() {
		this.startActivity(new Intent(NewADActivity.this, MainActivity.class));
		this.finish();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (canJumpImmediately) {
			jumpWhenCanClick();
		}
		canJumpImmediately = true;
	}

}
