package com.hb.qx;

import android.content.Context;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;


public class AlertDialogContainer {
	private Context context;
	private TextView okText;
	private TextView noText;
	private TextView updateText;
	android.app.AlertDialog ad;
	private FrameLayout frameLayout;
	private TextView titleView;
	private AlertDialogCallBack callBack;
	private LinearLayout linLayout;
	private View alertbrView;

	public AlertDialogContainer(Context context, View view) {
		this.context = context;
		ad = new android.app.AlertDialog.Builder(context).create();
		ad.show();
		Window window = ad.getWindow();
		window.setContentView(R.layout.dialog_container);
		// 关键在下面的两行,使用window.setContentView,替换整个对话框窗口的布局
		ad.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
		ad.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
		okText = (TextView) window.findViewById(R.id.submitButton);
		noText = (TextView) window.findViewById(R.id.cancelButton);
		
		titleView = (TextView) window.findViewById(R.id.titleLabel);
		frameLayout = (FrameLayout) window.findViewById(R.id.layout_container);
		alertbrView = (View) window.findViewById(R.id.alert_br);
		frameLayout.addView(view);
		okText.setOnClickListener(okListener);
		noText.setOnClickListener(noListener);

	}

	private OnClickListener okListener = new OnClickListener(){

		@Override
		public void onClick(View v) {
			if (callBack.ok()) {
				ad.dismiss();
			}
			;
		}
	};

	private OnClickListener noListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			  if(callBack.no()){
				  ad.dismiss();
			  };
			};
	};

	public void setCallBack(AlertDialogCallBack callBack) {
		this.callBack = callBack;
	}

	public void dismiss() {
		ad.dismiss();
	}

	public void setOkText(String text) {
		okText.setText(text);
	}

	public void setNoText(String text) {
		noText.setText(text);
	}

	public TextView getUpdateTextView() {
		return updateText;
	}

	public LinearLayout getLinearLayout() {
		return linLayout;
	}

	public void setTitle(String tilte) {
		this.titleView.setText(tilte);
	}

	public View getBrView() {
		return alertbrView;
	}
}
