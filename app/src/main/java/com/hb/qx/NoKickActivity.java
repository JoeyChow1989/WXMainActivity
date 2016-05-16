package com.hb.qx;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
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
    private InterstitialAd interAd;
    private LinearLayout ganxieyu;
    private EditText tv_GanXieyu;

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
                } else
                {
                    editor.putInt("huifu", 0);
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

        mChehui.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b)
            {
                if (b)
                {
                    editor.putInt("chexiao", 1);
                } else
                {
                    editor.putInt("chexiao", 0);
                }
                editor.commit();
            }
        });

        mHuifuqiangshu.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b)
            {
                if (b)
                {
                    editor.putInt("qianshu", 1);
                } else
                {
                    editor.putInt("qianshu", 0);
                }
                editor.commit();
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

        mGanxieyu.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b)
            {
                if (b)
                {
                    editor.putInt("ganxie", 1);
                    ganxieyu.setVisibility(View.VISIBLE);

                } else
                {
                    editor.putInt("ganxie", 0);
                    ganxieyu.setVisibility(View.GONE);
                }
                editor.commit();
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
        ganxieyu = (LinearLayout) findViewById(R.id.ganxieyu);
        tv_GanXieyu = (EditText) findViewById(R.id.tv_ganxieyu);


        System.out.println("huifu------------------" + sp.getInt("huifu", 0));

        if (sp.getInt("huifu", 0) == 1)
        {
            mAutoHuiFu.setChecked(true);

        } else if (sp.getInt("huifu", 0) == 0)
        {
            mAutoHuiFu.setChecked(false);
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

        if (sp.getInt("qianshu", 0) == 1)
        {
            mHuifuqiangshu.setChecked(true);

        } else if (sp.getInt("qianshu", 0) == 0)
        {
            mHuifuqiangshu.setChecked(false);
        }

        if (sp.getInt("ganxie", 0) == 1)
        {
            mGanxieyu.setChecked(true);
            ganxieyu.setVisibility(View.VISIBLE);
            tv_GanXieyu.setText(sp.getString("ganxieyu", "谢谢"));

        } else if (sp.getInt("ganxie", 0) == 0)
        {
            mGanxieyu.setChecked(false);
            ganxieyu.setVisibility(View.GONE);
        }

        if (sp.getInt("chexiao", 0) == 1)
        {
            mChehui.setChecked(true);
        } else if (sp.getInt("chexiao", 0) == 0)
        {
            mChehui.setChecked(false);
        }

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

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        editor.putString("ganxieyu", tv_GanXieyu.getText().toString().trim());
        editor.commit();
    }
}
