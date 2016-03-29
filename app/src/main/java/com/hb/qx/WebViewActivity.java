package com.hb.qx;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import junit.framework.Test;

import org.json.JSONObject;

import in.srain.cube.request.FailData;
import in.srain.cube.request.JsonData;
import in.srain.cube.request.RequestHandler;
import in.srain.cube.request.RequestJsonHandler;
import in.srain.cube.util.CLog;
import in.srain.cube.util.NetworkStatusManager;
import android.R.integer;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.DownloadListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.RelativeLayout.LayoutParams;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hb.ui.ProgressWebView;

public class WebViewActivity extends Activity {

	private ImageView fxView;
	private ImageView back;
	private ImageView go;
	private ImageView refresh;
	private ImageView close;
	private ProgressWebView webView;
	private String url;
	private boolean is_load = true;
	private HbApplication application;
	private RelativeLayout relative_layout;
	private ImageView backImage;
	private LinearLayout webViewLayout;
	private ImageView close_img;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.webview_activity);
		webViewLayout = (LinearLayout) findViewById(R.id.web_view_linear_layout);
		Intent intent = getIntent();
		url = intent.getStringExtra("url");
		//初始化视图
		initVeiw();
		//初始化事件
		close_img = (ImageView) findViewById(R.id.close);
		//
		close_img.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	// 切换浏览器
	private OnClickListener otherClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			Uri uri = Uri.parse(url);
			Intent intent = new Intent(Intent.ACTION_VIEW, uri);
			startActivity(intent);
		}
	};

	public void initVeiw() {
		// 动态增加webView
		webView = new ProgressWebView(this);
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		webViewLayout.addView(webView, params);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setUseWideViewPort(true);
		webView.getSettings().setLoadWithOverviewMode(true);
		webView.getSettings().setDomStorageEnabled(true);
		webView.getSettings().setDefaultTextEncodingName("UTF-8");
		webView.setDownloadListener(new MyWebViewDownLoadListener());
		webView.setWebViewClient(new MyWebViewClient());
		webView.loadUrl(url);
	}

	class MyWebViewClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			return super.shouldOverrideUrlLoading(view, url);
		}

		@Override
		public void doUpdateVisitedHistory(WebView view, String url, boolean isReload) {
			try {

				super.doUpdateVisitedHistory(view, url, isReload);
			} catch (Exception e) {
			}
		}

		@Override
		public void onPageFinished(WebView view, String url) {

			super.onPageFinished(view, url);
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {

			super.onPageStarted(view, url, favicon);
		}
	}

	private class MyWebViewDownLoadListener implements DownloadListener {

		@Override
		public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
			try {
				Uri uri = Uri.parse(url);
				Intent viewIntent = new Intent(Intent.ACTION_VIEW, uri);
				startActivity(viewIntent);
			} catch (Exception e) {

			}
		}

	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();

	}

	@Override
	protected void onStop() {
		super.onStop();

	}

	@Override
	protected void onStart() {
		super.onStart();

	}

	@Override
	protected void onDestroy() {
		try {
			if (webView != null) {
				webViewLayout.removeView(webView);
				webView.removeAllViews();
				webView.destroy();
			}
			System.gc();
		} catch (Exception e) {
		}
		super.onDestroy();
	}

}
