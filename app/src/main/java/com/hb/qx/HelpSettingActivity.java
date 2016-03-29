package com.hb.qx;

import com.tencent.stat.StatService;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class HelpSettingActivity extends Activity {
	private LinearLayout backlayout;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_help);
		backlayout = (LinearLayout) findViewById(R.id.more_layout);
		backlayout.setOnClickListener(backlayou);
		handleMIUIStatusBar();
	
	}
	public OnClickListener backlayou = new OnClickListener() {
		@Override
		public void onClick(View v) {
			finish();
		}
	};
	
	@Override
	protected void onPause() {
		super.onPause();
		// 如果本Activity是继承基类BaseActivity的，可注释掉此行。
		com.baidu.mobstat.StatService.onPause(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		// 如果本Activity是继承基类BaseActivity的，可注释掉此行。
		com.baidu.mobstat.StatService.onResume(this);
		
	}
	
	/**
	 * 适配MIUI沉浸状态栏
	 */
	private void handleMIUIStatusBar() {
		try {
			SystemBarTintManager tintManager = new SystemBarTintManager(this);
			tintManager = new SystemBarTintManager(this);
			tintManager.setStatusBarTintEnabled(true);
			tintManager.setNavigationBarTintEnabled(true);

		} catch (Exception e) {

		}
	}
	@Override
	protected void onDestroy() {
		getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		super.onDestroy();
	}
	
}
