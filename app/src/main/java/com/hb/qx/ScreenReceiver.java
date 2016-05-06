package com.hb.qx;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;

public class ScreenReceiver extends BroadcastReceiver {
	private static boolean isOFF;
	private static int b = 0;

	@SuppressWarnings("unused")
	@Override
	public void onReceive(Context paramContext, Intent paramIntent) {
		String action = paramIntent.getAction();

		// 休眠
		if (action.equals("android.intent.action.SCREEN_OFF")) {
			startOFFIntent(paramContext);
			isOFF = false;
			return;
		}

		if (action.equals("android.intent.action.SCREEN_ON")) {
			isOFF = true;
			return;
		}
	}

	public void startOFFIntent(Context paramContext) {
		try {
			try {
				TelephonyManager telephony = (TelephonyManager) paramContext.getSystemService(Context.TELEPHONY_SERVICE);
				int state = telephony.getCallState();
				String lock = HbApplication.instance.sp.getString("lock", "");
				if (lock.equals("yes") && state != 1 && state != 2) {
					Intent localIntent = new Intent(paramContext, LockMainActvity.class);
					localIntent.putExtra("fromReceiver_abcd", true);
					localIntent.addFlags(335544320);
					paramContext.startActivity(localIntent);
				}
				return;
			} catch (Exception localException) {

			}
		} finally {
		}
	}

	public static void startCount() {
		b = 1 + b;
	}

	public static boolean getisOFF() {
		return isOFF;
	}
}
