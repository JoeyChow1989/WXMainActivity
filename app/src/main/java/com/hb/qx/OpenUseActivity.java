package com.hb.qx;

import java.util.List;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityManager;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.ToggleButton;

public class OpenUseActivity extends Activity
{

    private ToggleButton mTB_open;
    private ToggleButton mOpenLock, mChatpage, mSreenLight, mVoise;
    private LinearLayout ly_fangfenghao, ly_jiasu;
    private ImageView mBack;

    private SharedPreferences sp;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_use);
        initView();
        mTB_open.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // TODO Auto-generated method stub
                Intent intent = new Intent(
                        Settings.ACTION_ACCESSIBILITY_SETTINGS);
                startActivity(intent);
            }
        });

        mBack.setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                // TODO Auto-generated method stub
                OpenUseActivity.this.finish();
            }
        });

        mOpenLock.setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                // TODO Auto-generated method stub
                Intent intent = new Intent(OpenUseActivity.this, LockMainActvity.class);
                startActivity(intent);
            }
        });

        mChatpage.setOnCheckedChangeListener(new OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                // TODO Auto-generated method stub
                if (isChecked)
                {
                    editor.putInt("chatpage", 1);
                    Toast.makeText(OpenUseActivity.this, "已开启", Toast.LENGTH_SHORT).show();
                } else
                {
                    editor.putInt("chatpage", 0);
                    Toast.makeText(OpenUseActivity.this, "已关闭", Toast.LENGTH_SHORT).show();
                }
                editor.commit();
            }
        });

        mSreenLight.setOnCheckedChangeListener(new OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b)
            {
                if (b)
                {
                    editor.putInt("sreen", 1);
                    Toast.makeText(OpenUseActivity.this, "已开启", Toast.LENGTH_SHORT).show();
                } else
                {
                    editor.putInt("sreen", 0);
                    Toast.makeText(OpenUseActivity.this, "已关闭", Toast.LENGTH_SHORT).show();
                }
                editor.commit();
            }
        });

        mVoise.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Toast.makeText(OpenUseActivity.this, "敬请期待", Toast.LENGTH_SHORT).show();
            }
        });

        ly_fangfenghao.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Toast.makeText(OpenUseActivity.this, "敬请期待", Toast.LENGTH_SHORT).show();
            }
        });


        if (isNoOption())
        {
            ly_jiasu.setOnClickListener(new OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    // Toast.makeText(OpenUseActivity.this,"敬请期待",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(
                            Settings.ACTION_USAGE_ACCESS_SETTINGS);
                    startActivity(intent);
                }
            });
        }
    }

    @Override
    protected void onStart()
    {
        // TODO Auto-generated method stub
        super.onStart();
        updateServiceStatus();
        System.out.println("cccccccccc" + sp.getInt("sreen", 0));
    }

    private void initView()
    {
        // TODO Auto-generated method stub

        sp = getSharedPreferences("chatpage", MODE_PRIVATE);
        editor = sp.edit();

        mTB_open = (ToggleButton) findViewById(R.id.accessibility_open_tbutton);
        mBack = (ImageView) findViewById(R.id.img_open_back);
        mOpenLock = (ToggleButton) findViewById(R.id.ly_open_lock);
        mChatpage = (ToggleButton) findViewById(R.id.accessibility_chatpage_tbutton);
        mSreenLight = (ToggleButton) findViewById(R.id.accessibility_sceen_tbutton);
        mVoise = (ToggleButton) findViewById(R.id.accessibility_voicenotice_tbutton);
        ly_fangfenghao = (LinearLayout) findViewById(R.id.ly_fangfenghao);
        ly_jiasu = (LinearLayout) findViewById(R.id.ly_jiasu);


        if (sp.getInt("chatpage", 0) == 1)
        {
            mChatpage.setChecked(true);
        } else if (sp.getInt("chatpage", 0) == 0)
        {
            mChatpage.setChecked(false);
        }

        if (sp.getInt("sreen", 0) == 1)
        {
            mSreenLight.setChecked(true);
        } else if (sp.getInt("sreen", 0) == 0)
        {
            mSreenLight.setChecked(false);
        }
    }

    private boolean isNoOption()
    {
        PackageManager packageManager = getApplicationContext()
                .getPackageManager();
        Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    @SuppressLint("NewApi")
    private void updateServiceStatus()
    {
        try
        {
            boolean serviceEnabled = false;
            AccessibilityManager accessibilityManager = (AccessibilityManager) getSystemService(Context.ACCESSIBILITY_SERVICE);
            System.out
                    .println("accessibilityManager============================"
                            + accessibilityManager);

            List<AccessibilityServiceInfo> accessibilityServices = accessibilityManager
                    .getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_GENERIC);
            for (AccessibilityServiceInfo info : accessibilityServices)
            {
                if (info.getId() != "")
                {
                    if (info.getId().equals(
                            getPackageName() + "/.QQHongbaoService"))
                    {
                        serviceEnabled = true;
                    }
                }
            }

            if (serviceEnabled)
            {
                // Prevent screen from dimming
                mTB_open.setButtonDrawable(R.drawable.ic_switch_on);
                getWindow().addFlags(
                        WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            } else
            {
                mTB_open.setButtonDrawable(R.drawable.ic_switch_off);
                getWindow().clearFlags(
                        WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

}
