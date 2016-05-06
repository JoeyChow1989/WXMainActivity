package com.hb.qx.wxapi;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Html;
import android.view.View;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hb.qx.AlertDialogCallBack;
import com.hb.qx.AlertDialogContainer;
import com.hb.qx.HbApplication;
import com.hb.qx.MoreActivity;
import com.hb.qx.R;
import com.hb.qx.RequestUtil;
import com.hb.qx.URL;
import com.hb.qx.UpdateService;

import java.util.List;

import in.srain.cube.request.FailData;
import in.srain.cube.request.JsonData;
import in.srain.cube.request.RequestJsonHandler;

public class RightActivity extends Activity implements View.OnClickListener
{


    private final Intent mAccessibleIntent = new Intent(
            Settings.ACTION_ACCESSIBILITY_SETTINGS);

    private ImageView back;
    private LinearLayout about_layout;
    private RelativeLayout relativelayout;
    private ImageView open_img_view;
    private TextView v_code;
    private String code_name;
    private ImageView start_sp;
    private HbApplication mApplication;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mApplication = HbApplication.getInstance();
        setContentView(R.layout.activity_right);
        initViews();
    }

    private void initViews()
    {
        open_img_view = (ImageView) findViewById(R.id.open_img_view);
        v_code = (TextView) findViewById(R.id.v_code);
        code_name = HbApplication.instance.vsername;
        v_code.setText(code_name);

        back = (ImageView) findViewById(R.id.right_back);
        about_layout = (LinearLayout) findViewById(R.id.about_layout);
        relativelayout = (RelativeLayout) findViewById(R.id.right_start_layout);

        back.setOnClickListener(this);
        about_layout.setOnClickListener(this);
        relativelayout.setOnClickListener(this);

        // start_sp = (ImageView) findViewById(R.id.lock_image);
        /*
		 * start_sp.setOnClickListener(new OnClickListener() {
		 *
		 * @Override public void onClick(View v) { String lock_type =
		 * mApplication.sp.getString("lock_type", ""); if
		 * (lock_type.equals("yes")){ String lock =
		 * mApplication.sp.getString("lock", ""); if (lock.equals("yes")) {
		 * start_sp.setImageResource(R.drawable.off);
		 * mApplication.editor.putString("lock", "no");
		 * mApplication.editor.commit(); } else {
		 * start_sp.setImageResource(R.drawable.open);
		 * mApplication.editor.putString("lock", "yes");
		 * mApplication.editor.commit(); } }else{
		 * mApplication.editor.putString("lock", "");
		 * mApplication.editor.commit(); Toast.makeText(getApplicationContext(),
		 * "测试中", 0).show(); } } });
		 */
    }

    @SuppressLint("NewApi")
    private void updateServiceStatus()
    {
        try
        {
            boolean serviceEnabled = false;
            AccessibilityManager accessibilityManager = (AccessibilityManager) getSystemService(Context.ACCESSIBILITY_SERVICE);
            List<AccessibilityServiceInfo> accessibilityServices = accessibilityManager
                    .getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_GENERIC);
            for (AccessibilityServiceInfo info : accessibilityServices)
            {
                if (info.getId().equals(getPackageName() + "/.HongbaoService"))
                {
                    serviceEnabled = true;
                }
            }

            if (serviceEnabled)
            {
                open_img_view.setImageResource(R.drawable.open);
            } else
            {
                open_img_view.setImageResource(R.drawable.off);
            }

            String locktype = mApplication.sp.getString("lock", "");
            if (locktype.equals("yes"))
            {
                start_sp.setImageResource(R.drawable.open);
            } else
            {
                start_sp.setImageResource(R.drawable.off);
            }

        } catch (Exception e)
        {

        }

    }

    @Override
    protected void onPause()
    {
        super.onPause();
        com.baidu.mobstat.StatService.onPause(this);

    }

    @Override
    protected void onResume()
    {
        super.onResume();
        updateServiceStatus();
        com.baidu.mobstat.StatService.onResume(this);
    }

    @Override
    protected void onDestroy()
    {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onDestroy();
    }

    public void request()
    {
        RequestUtil.reverseget(URL.UP_URL, new RequestJsonHandler()
        {
            @Override
            public void onRequestFinish(final JsonData data)
            {
                if (data != null)
                {
                    int vsercode = data.optInt("vsercode");
                    if (vsercode > HbApplication.instance.vsercode)
                    {
                        View view = getLayoutInflater().inflate(
                                R.layout.dialog_update, null);
                        AlertDialogContainer container = new AlertDialogContainer(
                                RightActivity.this, view);
                        container.setNoText("否");
                        container.setOkText("是");
                        container.setTitle("更新通知");
                        TextView textView = (TextView) view
                                .findViewById(R.id.msg);
                        textView.setText(Html.fromHtml(data.optString("msg")));
                        container.setCallBack(new AlertDialogCallBack()
                        {
                            @Override
                            public boolean ok()
                            {
                                Intent intent = new Intent(RightActivity.this,
                                        UpdateService.class);
                                intent.putExtra("dowurl",
                                        data.optString("dowurl"));
                                startService(intent);
                                return true;
                            }

                            @Override
                            public boolean no()
                            {
                                return true;
                            }
                        });
                    } else
                    {
                        Toast.makeText(getApplicationContext(),
                                "已是最新版本，当版本" + HbApplication.instance.vsername,
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onRequestFail(FailData failData)
            {
            }

        });
    }

    public void onButtonClicked()
    {
        startActivity(mAccessibleIntent);
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId()){
            case R.id.right_back:
                finish();
                break;
            case R.id.right_start_layout:
                onButtonClicked();
                break;
            case R.id.about_layout:
                request();
                break;
        }
    }
}
