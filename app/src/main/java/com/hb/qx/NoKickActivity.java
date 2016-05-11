package com.hb.qx;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.baidu.mobads.InterstitialAd;
import com.baidu.mobads.InterstitialAdListener;

public class NoKickActivity extends Activity
{

    private ImageView nokick_view_back;
    private ToggleButton mAutoHuiFu, mBuzidong, mChehui, mHuifuqiangshu, mHuifuHongbaoren, mGanxieyu;
    private SeekBar mSeekBar_yanshi, mSeekBar_huifu;
    private TextView tv_yanshi, tv_huifu;
    private LinearLayout ly_huifu;
    private InterstitialAd interAd;

    SharedPreferences sp;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_kick);
        initViews();
        baidu_ad();

        //抢自己发出的红包
        nokick_view_back.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                NoKickActivity.this.finish();
            }
        });

        mAutoHuiFu.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b)
            {
                if (b)
                {
                    editor.putInt("huifu", 1);
                    ly_huifu.setVisibility(View.VISIBLE);
                } else
                {
                    editor.putInt("huifu", 0);
                    ly_huifu.setVisibility(View.INVISIBLE);
                }

                editor.commit();
            }
        });

        mBuzidong.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b)
            {
                if (b)
                {
                    editor.putInt("buzidong", 1);
                } else
                {
                    editor.putInt("buzidong", 0);
                }
                editor.commit();
            }
        });

        mChehui.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Toast.makeText(NoKickActivity.this, "敬请期待", Toast.LENGTH_SHORT).show();
            }
        });

        mHuifuqiangshu.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Toast.makeText(NoKickActivity.this, "敬请期待", Toast.LENGTH_SHORT).show();
            }
        });

        mHuifuHongbaoren.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b)
            {
                if (b)
                {
                    editor.putInt("aite", 1);
                } else
                {
                    editor.putInt("aite", 0);
                }
                editor.commit();
            }
        });

        mGanxieyu.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Toast.makeText(NoKickActivity.this, "敬请期待", Toast.LENGTH_SHORT).show();
            }
        });

        mSeekBar_yanshi.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b)
            {

                double max = seekBar.getMax();
                double pro = seekBar.getProgress();

                tv_yanshi.setText(String.valueOf((pro / max) * 10));
                MainActivity.YANSHI = (int) ((pro / max) * 10);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar)
            {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar)
            {

            }
        });


        mSeekBar_huifu.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b)
            {
                double max = seekBar.getMax();
                double pro = seekBar.getProgress();
                tv_huifu.setText(String.valueOf((pro / max) * 10));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar)
            {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar)
            {

            }
        });
    }

    private void initViews()
    {
        sp = getSharedPreferences("chatpage", MODE_PRIVATE);
        editor = sp.edit();
        ly_huifu = (LinearLayout) findViewById(R.id.ly_auto_huifu);
        nokick_view_back = (ImageView) findViewById(R.id.nokick_view_back);
        mAutoHuiFu = (ToggleButton) findViewById(R.id.accessibility_kick_zidonghuifu);
        mBuzidong = (ToggleButton) findViewById(R.id.accessibility_kick_buzidong);
        // mDianzan = (ToggleButton) findViewById(R.id.accessibility_kick_dianzan);
        mChehui = (ToggleButton) findViewById(R.id.accessibility_kick_zidongchehui);
        mHuifuqiangshu = (ToggleButton) findViewById(R.id.accessibility_kick_huifuqianshu);
        mHuifuHongbaoren = (ToggleButton) findViewById(R.id.accessibility_kick_huifuhongbaoren);
        mGanxieyu = (ToggleButton) findViewById(R.id.accessibility_kick_zidingyiganxieyu);
        mSeekBar_yanshi = (SeekBar) findViewById(R.id.seekbar_yanshi);
        tv_yanshi = (TextView) findViewById(R.id.nokick_tv_yanshi);
        mSeekBar_huifu = (SeekBar) findViewById(R.id.seekbar_huifu);
        tv_huifu = (TextView) findViewById(R.id.nokick_tv_huifu);


        System.out.println("huifu------------------" + sp.getInt("huifu", 0));

        if (sp.getInt("huifu", 0) == 1)
        {
            mAutoHuiFu.setChecked(true);
            ly_huifu.setVisibility(View.VISIBLE);

        } else if (sp.getInt("huifu", 0) == 0)
        {
            mAutoHuiFu.setChecked(false);
            ly_huifu.setVisibility(View.INVISIBLE);
        }

        if (sp.getInt("buzidong", 0) == 1)
        {
            mBuzidong.setChecked(true);

        } else if (sp.getInt("buzidong", 0) == 0)
        {
            mBuzidong.setChecked(false);
        }

        if (sp.getInt("aite", 0) == 1)
        {
            mHuifuHongbaoren.setChecked(true);

        } else if (sp.getInt("aite", 0) == 0)
        {
            mHuifuHongbaoren.setChecked(false);
        }

        System.out.println("YANSHI============================" + MainActivity.YANSHI);

        mSeekBar_yanshi.setProgress(MainActivity.YANSHI);
        tv_yanshi.setText(String.valueOf(MainActivity.YANSHI));
    }

    public void baidu_ad()
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
                interAd.showAd(NoKickActivity.this);
            }
        });
        interAd.loadAd();
    }
}
