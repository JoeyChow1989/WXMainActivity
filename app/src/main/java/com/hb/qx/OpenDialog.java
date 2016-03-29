package com.hb.qx;

import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

public class OpenDialog implements OnClickListener {
	private Context mcontext;
	private TextView textView;
	android.app.AlertDialog ad;

	public OpenDialog(Context context) {
		this.mcontext = context;
		ad = new android.app.AlertDialog.Builder(context).create();
		ad.show();
		Window window = ad.getWindow();
		window.setContentView(R.layout.dialog_open);
		textView = (TextView) window.findViewById(R.id.open_server);
		textView.setOnClickListener(this);
		// 关键在下面的两行,使用window.setContentView,替换整个对话框窗口的布局
		ad.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
		ad.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

	}

	@Override
	public void onClick(View v) {
		try {
			int id = v.getId();
			switch (id) {
			case R.id.open_server:
				Intent mAccessibleIntent = new Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS);
				mcontext.startActivity(mAccessibleIntent);
				ad.dismiss();
				return;
			default:
				break;
			}
		} catch (Exception e) {

		}

	}

	public void hide() {
		ad.dismiss();
	}

	public boolean isshow() {
		return ad.isShowing();
	}
	public void show(){
		ad.show();
	}

}
