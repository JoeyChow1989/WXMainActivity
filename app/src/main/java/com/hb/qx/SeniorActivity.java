package com.hb.qx;

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
import android.text.Html;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityManager;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.List;

import in.srain.cube.request.FailData;
import in.srain.cube.request.JsonData;
import in.srain.cube.request.RequestJsonHandler;

public class SeniorActivity extends Activity
{
    private ToggleButton mTB_open;
    private ToggleButton mOpenLock, mChatpage, mSreenLight, mVoise;
    private LinearLayout ly_check, ly_nokick;
    private ImageView mBack;
    private TextView v_code;
    private String code_name;

    private SharedPreferences sp;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.senior_activity);
        init();
        initEvent();
    }

    private void initEvent()
    {
        // TODO Auto-generated method stub
        mBack.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // TODO Auto-generated method stub
                SeniorActivity.this.finish();
            }
        });

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

        mOpenLock.setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View v)
            {

                System.out.println("---------------------share-------------" + sp.getInt("share", 0));

                if (sp.getInt("share", 0) == 1)
                {
                    Toast.makeText(SeniorActivity.this, "已开启", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(SeniorActivity.this, LockMainActvity.class);
                    startActivity(intent);
                } else
                {
                    share();
                }
            }
        });

        mChatpage.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                // TODO Auto-generated method stub
                if (sp.getInt("share", 0) == 0)
                {
                    share();
                } else
                {
                    if (isChecked)
                    {
                        editor.putInt("chatpage", 1);
                        Toast.makeText(SeniorActivity.this, "已开启", Toast.LENGTH_SHORT).show();
                    } else
                    {
                        editor.putInt("chatpage", 0);
                        Toast.makeText(SeniorActivity.this, "已关闭", Toast.LENGTH_SHORT).show();
                    }
                    editor.commit();
                }


            }
        });

        mSreenLight.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b)
            {

                if (sp.getInt("share", 0) == 0)
                {
                    share();
                } else
                {
                    if (b)
                    {
                        editor.putInt("sreen", 1);
                        Toast.makeText(SeniorActivity.this, "已开启", Toast.LENGTH_SHORT).show();
                    } else
                    {
                        editor.putInt("sreen", 0);
                        Toast.makeText(SeniorActivity.this, "已关闭", Toast.LENGTH_SHORT).show();
                    }
                    editor.commit();
                }


            }
        });

        mVoise.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b)
            {
                if (sp.getInt("share", 0) == 0)
                {
                    share();
                } else
                {
                    if (b)
                    {
                        editor.putInt("voise", 1);
                        Toast.makeText(SeniorActivity.this, "已开启", Toast.LENGTH_SHORT).show();
                    } else
                    {
                        editor.putInt("voise", 0);
                        Toast.makeText(SeniorActivity.this, "已关闭", Toast.LENGTH_SHORT).show();
                    }
                    editor.commit();
                }
            }
        });

        ly_nokick.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(SeniorActivity.this, NoKickActivity.class);
                startActivity(intent);
            }
        });
    }

    private void share()
    {
        YqhyDialog1 yyDialog = new YqhyDialog1(SeniorActivity.this, 0);
        try
        {
            yyDialog.showAtLocation(SeniorActivity.this.getWindow()
                    .getDecorView(), Gravity.CENTER, 0, 0);
        } catch (Exception e)
        {
            if (yyDialog != null)
            {
                if (yyDialog.isShowing())
                {
                    yyDialog.dismiss();
                }
            }
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

    private void init()
    {
        // TODO Auto-generated method stub
        sp = getSharedPreferences("chatpage", MODE_PRIVATE);
        editor = sp.edit();

        mTB_open = (ToggleButton) findViewById(R.id.accessibility_open_tbutton);
        mBack = (ImageView) findViewById(R.id.img_senior_back);
        mOpenLock = (ToggleButton) findViewById(R.id.ly_open_lock);
        mChatpage = (ToggleButton) findViewById(R.id.accessibility_chatpage_tbutton);
        mSreenLight = (ToggleButton) findViewById(R.id.accessibility_sceen_tbutton);
        mVoise = (ToggleButton) findViewById(R.id.accessibility_voicenotice_tbutton);
        ly_nokick = (LinearLayout) findViewById(R.id.id_nokick);
        ly_check = (LinearLayout) findViewById(R.id.id_check);

        v_code = (TextView) findViewById(R.id.id_v_code);
        code_name = HbApplication.instance.vsername;
        v_code.setText("版本：v"+code_name);

        ly_check = (LinearLayout) findViewById(R.id.id_check);
        ly_check.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                request();
            }
        });


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

        if (sp.getInt("voise", 0) == 1)
        {
            mVoise.setChecked(true);
        } else if (sp.getInt("voise", 0) == 0)
        {
            mVoise.setChecked(false);
        }

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
                                SeniorActivity.this, view);
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
                                Intent intent = new Intent(SeniorActivity.this,
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

    @SuppressLint("NewApi")
    private void updateServiceStatus()
    {
        try
        {
            boolean serviceEnabled = false;
            AccessibilityManager accessibilityManager = (AccessibilityManager) getSystemService(Context.ACCESSIBILITY_SERVICE);
            System.out.println("accessibilityManager============================" + accessibilityManager);
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
                mTB_open.setChecked(true);
                getWindow().addFlags(
                        WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            } else
            {
                mTB_open.setChecked(false);
                getWindow().clearFlags(
                        WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
