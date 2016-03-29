package com.hb.qx;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.widget.RemoteViews;



public class UpdateService extends Service{
	private static final int TIMEOUT = 10 * 1000;// 超时
	private NotificationManager notificationManager;
	private Notification notification;

	private RemoteViews contentView;
	private static final int DOWN_OK = 1;
	private static final int DOWN_ERROR = 0;
	private int notification_id = 0;
	private File filedir;
	private File downFile;
	private PendingIntent pendingIntent;
	private Intent updateIntent;
	private String downurl;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		downurl = intent.getStringExtra("dowurl");
		// 判断文件夹是否存在
		String filepath = getSDPath() + "/wifiqx/";
		filedir = new File(filepath);
		if (!filedir.exists()) {
			filedir.mkdir();
		}
		// 判断文件是否存在
		String filename = getSDPath() + "/wifiqx/wifiqx.apk";
		downFile = new File(filename);
		if (!downFile.exists()) {
			try {
				downFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		showNotification();
		createThread();
		return super.onStartCommand(intent, flags, startId);

	}

	// 显示消息框
	public void showNotification() {
		notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		notification = new Notification();
		notification.icon = R.drawable.icon;
		// 这个参数是通知提示闪出来的值.
		notification.tickerText = "开始下载";
		// pendingIntent = PendingIntent.getActivity(this, 0, updateIntent, 0);
		// // 这里面的参数是通知栏view显示的内容
		notification.setLatestEventInfo(this, "超级抢红包助手", "下载：0%", pendingIntent);
		//
		notificationManager.notify(notification_id, notification);
		/***
		 * 在这里我们用自定的view来显示Notification
		 */
		contentView = new RemoteViews(getPackageName(), R.layout.notification_item);
		contentView.setTextViewText(R.id.notificationTitle, "正在下载");
		contentView.setTextViewText(R.id.notificationPercent, "0%");
		contentView.setProgressBar(R.id.notificationProgress, 100, 0, false);
		notification.contentView = contentView;

		// updateIntent = new Intent(this, MainActivity.class);
		// updateIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		// pendingIntent = PendingIntent.getActivity(this, 0, updateIntent, 0);
		notification.contentIntent = pendingIntent;
		notificationManager.notify(notification_id, notification);
	}

	/***
	 * 开线程下载
	 */
	public void createThread() {
		/***
		 * 更新UI
		 */
		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case DOWN_OK:
					// 下载完成，点击安装
					Uri uri = Uri.fromFile(downFile);
					Intent intent = new Intent(Intent.ACTION_VIEW);
					intent.setDataAndType(uri, "application/vnd.android.package-archive");
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(intent);
					stopSelf();
					break;
				case DOWN_ERROR:
					notification.setLatestEventInfo(UpdateService.this, "超级抢红包助手", "下载失败", pendingIntent);
					break;

				default:
					stopSelf();
					break;
				}

			}

		};

		final Message message = new Message();

		new Thread(new Runnable() {
			@Override
			public void run() {

				try {
					long downloadSize = downloadUpdateFile(downurl, downFile.toString());
					if (downloadSize > 0) {
						// 下载成功
						message.what = DOWN_OK;
						handler.sendMessage(message);
					}

				} catch (Exception e) {
					e.printStackTrace();
					message.what = DOWN_ERROR;
					handler.sendMessage(message);
				}

			}
		}).start();
	}

	/***
	 * 下载文件
	 * 
	 * @return
	 * @throws MalformedURLException
	 */
	public long downloadUpdateFile(String down_url, String file) throws Exception {
		int down_step = 5;// 提示step
		int totalSize;// 文件总大小
		int downloadCount = 0;// 已经下载好的大小
		int updateCount = 0;// 已经上传的文件大小
		InputStream inputStream;
		OutputStream outputStream;

		URL url = new URL(down_url);
		HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
		httpURLConnection.setConnectTimeout(TIMEOUT);
		httpURLConnection.setReadTimeout(TIMEOUT);
		// 获取下载文件的size
		totalSize = httpURLConnection.getContentLength();
		if (httpURLConnection.getResponseCode() == 404) {
			throw new Exception("fail!");
		}
		inputStream = httpURLConnection.getInputStream();
		outputStream = new FileOutputStream(file, false);// 文件存在则覆盖掉
		byte buffer[] = new byte[1024];
		int readsize = 0;
		while ((readsize = inputStream.read(buffer)) != -1) {
			outputStream.write(buffer, 0, readsize);
			downloadCount += readsize;// 时时获取下载到的大小
			/**
			 * 每次增张5%
			 */
			if (updateCount == 0 || (downloadCount * 100 / totalSize - down_step) >= updateCount) {
				updateCount += down_step;
				// 改变通知栏
				// notification.setLatestEventInfo(this, "正在下载...", updateCount
				// + "%" + "", pendingIntent);
				contentView.setTextViewText(R.id.notificationPercent, updateCount + "%");
				contentView.setProgressBar(R.id.notificationProgress, 100, updateCount, false);
				// show_view
				notificationManager.notify(notification_id, notification);

			}

		}
		if (httpURLConnection != null) {
			httpURLConnection.disconnect();
		}
		inputStream.close();
		outputStream.close();

		return downloadCount;

	}

	public  String getSDPath() {
		File sdDir = null;
		boolean sdCardExist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
		if (sdCardExist) {
			sdDir = Environment.getExternalStorageDirectory();// 获取跟目录
		}

		return sdDir.toString();
	}
}
