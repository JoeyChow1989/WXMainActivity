package com.hb.qx;

import java.util.List;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mobads.InterstitialAd;
import com.baidu.mobads.InterstitialAdListener;
import com.baidu.mobads.SplashAd;
import com.baidu.mobads.SplashAdListener;
import com.hb.qx.wxapi.RightActivity;

@SuppressLint("NewApi")
public class MainActivity extends Activity
{
    private InterstitialAd interAd;
    private ImageView more;
    private TextView help;
    private GifMovieView gifImage;
    private String share_type = "";
    private RelativeLayout yes_layout;
    private LinearLayout no_layout;
    private ImageView share_view;
    private OpenDialog container;
    private TextView start_but;
    private ImageView gif_qiang_iamge;
    private long exitTime = 0;
    private LinearLayout more_layout;
    private LinearLayout help_layout;
    private HbApplication mApplication;
    private RelativeLayout relativeLayout;
    private RelativeLayout show_ad_layout;
    private RelativeLayout.LayoutParams reLayoutParams;
    private long mExitTime;

    static String qianshu;
    public static int YANSHI = 0;

    private SharedPreferences sp;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_ui);
        sp = getSharedPreferences("chatpage", MODE_PRIVATE);
        editor = sp.edit();
        editor.putInt("share",1);
        editor.putInt("share1",1);

        //editor.commit();

        gifImage = (GifMovieView) findViewById(R.id.gif_iamge);
        more = (ImageView) findViewById(R.id.more_text);
        more_layout = (LinearLayout) findViewById(R.id.more_layout);
        more_layout.setOnClickListener(more_c);
        more.setOnClickListener(more_c);
        help = (TextView) findViewById(R.id.help);
        help_layout = (LinearLayout) findViewById(R.id.help_layout);
        help_layout.setOnClickListener(help_c);
        help.setOnClickListener(help_c);

        gifImage.setMovieResource(R.drawable.qiang_progress);
        gifImage.setVisibility(View.VISIBLE);

        // 服务未启动
        no_layout = (LinearLayout) findViewById(R.id.no_commit);
        start_but = (TextView) findViewById(R.id.start_but);
        start_but.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onButtonClicked();
            }
        });
        // 服务启动
        yes_layout = (RelativeLayout) findViewById(R.id.yes_commit);
        // 分享按钮
        share_view = (ImageView) findViewById(R.id.share_id);
        share_view.setOnClickListener(share_c);
        // 加速安键
        gif_qiang_iamge = (ImageView) findViewById(R.id.gif_qiang_iamge);
        gif_qiang_iamge.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                accelerate();
            }
        });


        mApplication = HbApplication.getInstance();
        //handleMIUIStatusBar();
        updateServiceStatus();

        if (!isServierRuning())
        {
            // 开启锁
            Intent lockservice = new Intent(this, LockService.class);
            startService(lockservice);
        }
        baidu_ad();
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        System.out.println("yanshi---------------Main" + YANSHI);
    }

    public void baidu_ad()
    {
        String show_ad_type = mApplication.sp.getString("show_ad_type", "");
        if (show_ad_type.equals("yes"))
        {
            String adPlaceId = "2402145"; // 重要：请填上您的广告位ID，代码位错误会导致无法请求到广告
            interAd = new InterstitialAd(this, adPlaceId);
            interAd.setListener(new InterstitialAdListener()
            {
                @Override
                public void onAdClick(InterstitialAd arg0)
                {
                    Log.i("InterstitialAd", "onAdClick");
                }

                @Override
                public void onAdDismissed()
                {
                    Log.i("InterstitialAd", "onAdDismissed");
                }

                @Override
                public void onAdFailed(String arg0)
                {
                    Log.i("InterstitialAd", "onAdFailed");
                }

                @Override
                public void onAdPresent()
                {
                    Log.i("InterstitialAd", "onAdPresent");
                }

                @Override
                public void onAdReady()
                {
                    Log.i("InterstitialAd", "onAdReady");
                    interAd.showAd(MainActivity.this);
                }

            });

            interAd.loadAd();
            mApplication.editor.putString("show_ad_type", "no");
            mApplication.editor.commit();
        }
    }

    public void setShowProgressbar()
    {
        // 显示进度条
        no_layout.setVisibility(View.GONE);
        yes_layout.setVisibility(View.VISIBLE);

    }

    public void setHideProgressbar()
    {
        // 隐藏进度条
        yes_layout.setVisibility(View.GONE);
        no_layout.setVisibility(View.VISIBLE);

    }

    private OnClickListener more_c = new OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            Intent intent = new Intent(getApplicationContext(),
                    SeniorActivity.class);
            startActivity(intent);
        }
    };

    private OnClickListener help_c = new OnClickListener()
    {

        @Override
        public void onClick(View v)
        {
            Intent intent = new Intent(getApplicationContext(),
                    HelpSettingActivity.class);
            startActivity(intent);
        }
    };

    private OnClickListener share_c = new OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
//            YqhyDialog yyDialog = new YqhyDialog(MainActivity.this, 0);
//            try
//            {
//                yyDialog.showAtLocation(MainActivity.this.getWindow()
//                        .getDecorView(), Gravity.CENTER, 0, 0);
//            } catch (Exception e)
//            {
//                if (yyDialog != null)
//                {
//                    if (yyDialog.isShowing())
//                    {
//                        yyDialog.dismiss();
//                    }
//                }
//            }
        }
    };

    public void showOpen()
    {
        if (container == null)
        {
            container = new OpenDialog(MainActivity.this);
        }
        if (container != null)
        {
            if (!container.isshow())
            {
                container.show();
            }
        }

    }

    private OnClickListener start_c = new OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
        }
    };

    @Override
    protected void onPause()
    {
        super.onPause();
        // 如果本Activity是继承基类BaseActivity的，可注释掉此行。
        com.baidu.mobstat.StatService.onPause(this);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK)
        {
            if ((System.currentTimeMillis() - mExitTime) > 2000)
            {
                Toast.makeText(this, "再按一次返回桌面", Toast.LENGTH_SHORT).show();
                mExitTime = System.currentTimeMillis();
                // interAd.showAd(this);
            } else
            {
                //interAd.destroy();
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        // 如果本Activity是继承基类BaseActivity的，可注释掉此行。
        com.baidu.mobstat.StatService.onResume(this);
        updateServiceStatus();
        isShare();
    }

    @Override
    protected void onDestroy()
    {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onDestroy();
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
                if (info.getId().equals(getPackageName() + "/.QQHongbaoService"))
                {
                    serviceEnabled = true;
                }
            }
            if (serviceEnabled)
            {
                setShowProgressbar();
                // Prevent screen from dimming
                getWindow().addFlags(
                        WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            } else
            {
                setHideProgressbar();
                showOpen();
                getWindow().clearFlags(
                        WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            }
        } catch (Exception e)
        {
        }
    }

    public void onButtonClicked()
    {
        Intent mAccessibleIntent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        startActivity(mAccessibleIntent);
    }

    public void isShare()
    {

        share_type = HbApplication.instance.sp.getString("get_share", "");
        if (share_type.equals(""))
        {
            gif_qiang_iamge.setImageResource(R.drawable.check_no);
        } else
        {
            gif_qiang_iamge.setImageResource(R.drawable.check_yes);
        }

    }

    public void accelerate()
    {

        if (share_type.equals(""))
        {
            if ((System.currentTimeMillis() - exitTime) > 2000)
            {
                Toast.makeText(getApplicationContext(), "点亮皇冠需要分享一次，能加速50%哦！",
                        Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else
            {
                showShare();
            }
        }

    }

    public void showShare()
    {
        YqhyDialog yyDialog = new YqhyDialog(MainActivity.this, 1);
        try
        {
            yyDialog.showAtLocation(MainActivity.this.getWindow()
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

    // 判断服务是否运行
    public boolean isServierRuning()
    {
        boolean isRunning = false;
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList = activityManager
                .getRunningServices(Integer.MAX_VALUE);
        if (serviceList == null || serviceList.size() == 0)
        {
            return false;
        }
        for (int i = 0; i < serviceList.size(); i++)
        {
            if (serviceList.get(i).service.getClassName().equals(
                    LockService.class.getName()))
            {
                isRunning = true;
                break;
            }
        }
        return isRunning;
    }
}
