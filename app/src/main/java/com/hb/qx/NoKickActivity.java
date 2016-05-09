package com.hb.qx;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class NoKickActivity extends Activity
{

    private ImageView nokick_view_back;
    private ToggleButton mAutoHuiFu, mBuzidong, mDianzan, mChehui, mHuifuqiangshu, mHuifuHongbaoren, mGanxieyu;
    private SeekBar mSeekBar_yanshi, mSeekBar_huifu;
    private TextView tv_yanshi, tv_huifu;
    private LinearLayout ly_huifu;

    SharedPreferences sp;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_kick);
        initViews();

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

        mDianzan.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Toast.makeText(NoKickActivity.this, "敬请期待", Toast.LENGTH_SHORT).show();
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

        mHuifuHongbaoren.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Toast.makeText(NoKickActivity.this, "敬请期待", Toast.LENGTH_SHORT).show();
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
        mDianzan = (ToggleButton) findViewById(R.id.accessibility_kick_dianzan);
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

        System.out.println("YANSHI============================" + MainActivity.YANSHI);

        mSeekBar_yanshi.setProgress(MainActivity.YANSHI);
        tv_yanshi.setText(String.valueOf(MainActivity.YANSHI));
    }
}
