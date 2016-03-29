package com.hb.qx;

import java.util.ArrayList;
import java.util.List;



import android.accessibilityservice.AccessibilityService;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

public class WechatAccessbilityJob extends AccessibilityService {

	/** 红包消息的关键字 */
	private static final String HONGBAO_TEXT_KEY = "[微信红包]";

	private static final String TAG = "WechatAccessbilityJob";

	/** 微信的包名 */
	private static final String WECHAT_PACKAGENAME = "com.tencent.mm";

	private static final int USE_ID_MIN_VERSION = 700;

	private boolean isFirstChecked;

	private Config mConfig;
	private PackageInfo mWechatPackageInfo = null;
	private Handler mHandler = null;

	@Override
	public void onCreate() {
		super.onCreate();

		mConfig = new Config(this);

	}

	@Override
	public void onAccessibilityEvent(AccessibilityEvent event) {
		final int eventType = event.getEventType();

		// 通知栏事件
		if (eventType == AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED) {
			List<CharSequence> texts = event.getText();
			if (!texts.isEmpty()) {
				for (CharSequence t : texts) {
					String text = String.valueOf(t);
					if (text.contains(HONGBAO_TEXT_KEY)) {
						openNotify(event);
						break;
					}
				}
			}
		} else if (eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
			openHongBao(event);
		}
	}

	/** 打开通知栏消息 */
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	private void openNotify(AccessibilityEvent event) {
		if (event.getParcelableData() == null || !(event.getParcelableData() instanceof Notification)) {
			return;
		}
		// 以下是精华，将微信的通知栏消息打开
		Notification notification = (Notification) event.getParcelableData();
		PendingIntent pendingIntent = notification.contentIntent;

		isFirstChecked = true;
		try {
			pendingIntent.send();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	private void openHongBao(AccessibilityEvent event) {
		if ("com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyReceiveUI".equals(event.getClassName())) {
			// 点中了红包，下一步就是去拆红包
			handleLuckyMoneyReceive();
		} else if ("com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyDetailUI".equals(event.getClassName())) {
			// 拆完红包后看详细的纪录界面
			// nonething
		} else if ("com.tencent.mm.ui.LauncherUI".equals(event.getClassName())) {
			// 在聊天界面,去点中红包
			handleChatListHongBao();
		}
	}

	/**
	 * 点击聊天里的红包后，显示的界面
	 * */
	@SuppressLint("NewApi")
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	private void handleLuckyMoneyReceive() {
		AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
		if (nodeInfo == null) {
			return;
		}

		AccessibilityNodeInfo targetNode = null;

		List<AccessibilityNodeInfo> list = null;
		int event = mConfig.getWechatAfterOpenHongBaoEvent();
		if (event == Config.WX_AFTER_OPEN_HONGBAO) { // 拆红包
			if (getWechatVersion() < USE_ID_MIN_VERSION) {
				list = nodeInfo.findAccessibilityNodeInfosByText("拆红包");
			} else {
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
					list = nodeInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/b2c");
				}
				if (list == null || list.isEmpty()) {
					List<AccessibilityNodeInfo> l = nodeInfo.findAccessibilityNodeInfosByText("给你发了一个红包");
					if (l != null && !l.isEmpty()) {
						AccessibilityNodeInfo p = l.get(0).getParent();
						if (p != null) {
							for (int i = 0; i < p.getChildCount(); i++) {
								AccessibilityNodeInfo node = p.getChild(i);
								if ("android.widget.Button".equals(node.getClassName())) {
									targetNode = node;
									break;
								}
							}
						}
					}
				}
			}
		} else if (event == Config.WX_AFTER_OPEN_SEE) { // 看一看
			if (getWechatVersion() < USE_ID_MIN_VERSION) { // 低版本才有 看大家手气的功能
				list = nodeInfo.findAccessibilityNodeInfosByText("看看大家的手气");
			}
		}

		if (list != null && !list.isEmpty()) {
			targetNode = list.get(0);
		}

		if (targetNode != null) {
			final AccessibilityNodeInfo n = targetNode;
			long sDelayTime = mConfig.getWechatOpenDelayTime();
			if (sDelayTime != 0) {
				getHandler().postDelayed(new Runnable() {
					@Override
					public void run() {
						n.performAction(AccessibilityNodeInfo.ACTION_CLICK);
					}
				}, sDelayTime);
			} else {
				n.performAction(AccessibilityNodeInfo.ACTION_CLICK);
			}
		}
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
	private void handleChatListHongBao() {
		AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
		if (nodeInfo == null) {
			Log.w(TAG, "rootWindow为空");
			return;
		}

		List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByText("领取红包");

		if (list != null && list.isEmpty()) {
			// 从消息列表查找红包
			list = nodeInfo.findAccessibilityNodeInfosByText("[微信红包]");

			if (list == null || list.isEmpty()) {
				return;
			}

			for (AccessibilityNodeInfo n : list) {
				if (BuildConfig.DEBUG) {
					Log.i(TAG, "-->微信红包:" + n);
				}
				n.performAction(AccessibilityNodeInfo.ACTION_CLICK);
				break;
			}
		} else if (list != null) {
			// 最新的红包领起
			for (int i = list.size() - 1; i >= 0; i--) {
				AccessibilityNodeInfo parent = list.get(i).getParent();
				if (BuildConfig.DEBUG) {
					Log.i(TAG, "-->领取红包:" + parent);
				}
				if (parent != null) {
					if (isFirstChecked) {
						parent.performAction(AccessibilityNodeInfo.ACTION_CLICK);
						isFirstChecked = false;
					}
					break;
				}
			}
		}
	}

	@Override
	public void onInterrupt() {

	}

	private Handler getHandler() {
		if (mHandler == null) {
			mHandler = new Handler(Looper.getMainLooper());
		}
		return mHandler;
	}

	/** 获取微信的版本 */
	private int getWechatVersion() {
		if (mWechatPackageInfo == null) {
			return 0;
		}
		return mWechatPackageInfo.versionCode;
	}

	/** 更新微信包信息 */
	private void updatePackageInfo() {
		try {
			mWechatPackageInfo = getApplicationContext().getPackageManager().getPackageInfo(WECHAT_PACKAGENAME, 0);
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}
	}
}
