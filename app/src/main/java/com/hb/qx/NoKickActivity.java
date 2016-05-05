package com.hb.qx;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class NoKickActivity extends Activity
{

    private ImageView nokick_view_back;
    private ToggleButton mAutoHuiFu,mBuzidong,mDianzan,mChehui,mHuifuqiangshu,mHuifuHongbaoren,mGanxieyu;

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

//        mAutoHuiFu.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
//        {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean b)
//            {
//                if (b)
//                {
//                    editor.putInt("huifu", 1);
//                } else
//                {
//                    editor.putInt("huifu", 0);
//                }
//            }
//        });

        mAutoHuiFu.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Toast.makeText(NoKickActivity.this,"敬请期待",Toast.LENGTH_SHORT).show();
            }
        });

        mBuzidong.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Toast.makeText(NoKickActivity.this,"敬请期待",Toast.LENGTH_SHORT).show();
            }
        });

        mDianzan.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Toast.makeText(NoKickActivity.this,"敬请期待",Toast.LENGTH_SHORT).show();
            }
        });

        mChehui.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Toast.makeText(NoKickActivity.this,"敬请期待",Toast.LENGTH_SHORT).show();
            }
        });

        mHuifuqiangshu.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Toast.makeText(NoKickActivity.this,"敬请期待",Toast.LENGTH_SHORT).show();
            }
        });

        mHuifuHongbaoren.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Toast.makeText(NoKickActivity.this,"敬请期待",Toast.LENGTH_SHORT).show();
            }
        });

        mGanxieyu.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Toast.makeText(NoKickActivity.this,"敬请期待",Toast.LENGTH_SHORT).show();
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
        mDianzan = (ToggleButton) findViewById(R.id.accessibility_kick_dianzan);
        mChehui = (ToggleButton) findViewById(R.id.accessibility_kick_zidongchehui);
        mHuifuqiangshu = (ToggleButton) findViewById(R.id.accessibility_kick_huifuqianshu);
        mHuifuHongbaoren = (ToggleButton) findViewById(R.id.accessibility_kick_huifuhongbaoren);
        mGanxieyu = (ToggleButton) findViewById(R.id.accessibility_kick_zidingyiganxieyu);

        if (sp.getInt("huifu", 0) == 1)
        {
            mAutoHuiFu.setChecked(true);
        } else if (sp.getInt("huifu", 0) == 0)
        {
            mAutoHuiFu.setChecked(false);
        }
    }
}
