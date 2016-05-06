package com.hb.qx;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.WindowManager;
import android.widget.TextView;

public class MoreActivity extends Activity {
	private TextView v_code;
	private String code_name;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_more);
		v_code = (TextView) findViewById(R.id.v_code);
		code_name = HbApplication.instance.vsername;
		v_code.setText(code_name);
	}

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

	@Override
	protected void onDestroy() {
		getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		super.onDestroy();
	}
}
