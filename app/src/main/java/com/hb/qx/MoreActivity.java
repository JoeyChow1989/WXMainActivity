package com.hb.qx;

import android.R.layout;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import in.srain.cube.request.FailData;
import in.srain.cube.request.JsonData;
import in.srain.cube.request.RequestJsonHandler;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import com.tencent.stat.StatService;

public class MoreActivity extends Activity {
	private final Intent mAccessibleIntent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
	private Button switchPlugin;

	private LinearLayout backlayout;
	private LinearLayout about_layout;
	private RelativeLayout relativelayout;
	private ImageView open_img_view;
	private TextView v_code;
	private String code_name;
	private ImageView start_sp;
	private HbApplication mApplication;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mApplication = HbApplication.getInstance();
		setContentView(R.layout.activity_more);
		backlayout = (LinearLayout) findViewById(R.id.more_layout);
		backlayout.setOnClickListener(backlayou);
		about_layout = (LinearLayout) findViewById(R.id.about_layout);
		about_layout.setOnClickListener(updateClick);
		relativelayout = (RelativeLayout) findViewById(R.id.start_layout);
		relativelayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onButtonClicked();
			}
		});
		start_sp = (ImageView) findViewById(R.id.lock_image);
		start_sp.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String lock_type = mApplication.sp.getString("lock_type", "");
				if (lock_type.equals("yes")){
					String lock = mApplication.sp.getString("lock", "");
					if (lock.equals("yes")) {
						start_sp.setImageResource(R.drawable.off);
						mApplication.editor.putString("lock", "no");
						mApplication.editor.commit();
					} else {
						start_sp.setImageResource(R.drawable.open);
						mApplication.editor.putString("lock", "yes");
						mApplication.editor.commit();
					}
				}else{
					mApplication.editor.putString("lock", "");
					mApplication.editor.commit();
					Toast.makeText(getApplicationContext(), "测试中", 0).show();
				}
			}
		});

		open_img_view = (ImageView) findViewById(R.id.open_img_view);
		v_code = (TextView) findViewById(R.id.v_code);
		code_name = HbApplication.instance.vsername;
		v_code.setText(code_name);
		handleMIUIStatusBar();
	}

	public OnClickListener backlayou = new OnClickListener() {
		@Override
		public void onClick(View v) {
			finish();
		}
	};
	public OnClickListener updateClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			request();
		}
	};

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

	@SuppressLint("NewApi")
	private void updateServiceStatus() {
		try {
			boolean serviceEnabled = false;
			AccessibilityManager accessibilityManager = (AccessibilityManager) getSystemService(Context.ACCESSIBILITY_SERVICE);
			List<AccessibilityServiceInfo> accessibilityServices = accessibilityManager.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_GENERIC);
			for (AccessibilityServiceInfo info : accessibilityServices) {
				if (info.getId().equals(getPackageName() + "/.HongbaoService")) {
					serviceEnabled = true;
				}
			}
			if (serviceEnabled) {
				open_img_view.setImageResource(R.drawable.open);
			} else {
				open_img_view.setImageResource(R.drawable.off);
			}

			String locktype = mApplication.sp.getString("lock", "");
			if (locktype.equals("yes")) {
				start_sp.setImageResource(R.drawable.open);
			} else {
				start_sp.setImageResource(R.drawable.off);
			}

		} catch (Exception e) {

		}

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
		updateServiceStatus();
		com.baidu.mobstat.StatService.onResume(this);
	}

	@Override
	protected void onDestroy() {
		getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		super.onDestroy();
	}

	public void request() {
		RequestUtil.reverseget(URL.UP_URL, new RequestJsonHandler() {
			@Override
			public void onRequestFinish(final JsonData data) {
				if (data != null) {
					int vsercode = data.optInt("vsercode");
					if (vsercode > HbApplication.instance.vsercode) {
						View view = getLayoutInflater().inflate(R.layout.dialog_update, null);
						AlertDialogContainer container = new AlertDialogContainer(MoreActivity.this, view);
						container.setNoText("否");
						container.setOkText("是");
						container.setTitle("更新通知");
						TextView textView = (TextView) view.findViewById(R.id.msg);
						textView.setText(Html.fromHtml(data.optString("msg")));
						container.setCallBack(new AlertDialogCallBack() {
							@Override
							public boolean ok() {
								Intent intent = new Intent(MoreActivity.this, UpdateService.class);
								intent.putExtra("dowurl", data.optString("dowurl"));
								startService(intent);
								return true;
							}

							@Override
							public boolean no() {
								return true;
							}
						});
					} else {
						Toast.makeText(getApplicationContext(), "已是最新版本，当版本" + HbApplication.instance.vsername, 0).show();
					}
				}
			}

			@Override
			public void onRequestFail(FailData failData) {

			}

		});
	}

	public void onButtonClicked() {
		startActivity(mAccessibleIntent);
	}

}
