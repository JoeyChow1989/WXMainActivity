package com.hb.qx;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;

import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.tencent.stat.StatConfig;
import com.tencent.stat.StatReportStrategy;


public class HbApplication extends Application {
	public static HbApplication instance;
	public int vsercode;
	public String vsername;
	public SharedPreferences sp;
	private LocationClient mLocationClient = null;
	
	public Editor editor;
	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;
		
		
		// 禁止MTA打印日志
		StatConfig.setDebugEnable(false);
		// 根据情况，决定是否开启MTA对app未处理异常的捕获
		StatConfig.setAutoExceptionCaught(true);
		// 选择默认的上报策略
		StatConfig.setStatSendStrategy(StatReportStrategy.APP_LAUNCH);
		PackageInfo info;
		try {
			info = this.getPackageManager().getPackageInfo(this.getPackageName(), 0);
			vsercode = info.versionCode;//
			vsername = info.versionName;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		sp = getSharedPreferences("common", MODE_PRIVATE);
		editor = sp.edit();
	}

	public static synchronized HbApplication getInstance() {
		return instance;
	}
	
	public synchronized LocationClient getLocationClient() {
		if (mLocationClient == null)
			mLocationClient = new LocationClient(this,
					getLocationClientOption());
		return mLocationClient;
	}
	
	private LocationClientOption getLocationClientOption() {
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);
		option.setAddrType("all");
		option.setServiceName(this.getPackageName());
		option.setScanSpan(0);
		option.disableCache(true);
		return option;
	}

	public String getDate() {
		String temp_str = "";
		Date dt = new Date();
		// 最后的aa表示“上午”或“下午” HH表示24小时制 如果换成hh表示12小时制
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		temp_str = sdf.format(dt);
		return temp_str;
	}
}
