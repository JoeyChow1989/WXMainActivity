package com.hb.qx;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.Toast;

public class VIPOpenActivity extends AppCompatActivity
{

    private RadioButton mRbThreeMonth, mRbSixMonth, mRbTewMonth, mRbWeiXin, mRbZhiFuBao;
    private Button mButton;

    SharedPreferences sp;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vipopen);
        initView();
        initEvenet();
    }

    private void initView()
    {
        mRbThreeMonth = (RadioButton) findViewById(R.id.id_vipopen_rb_threemonth);
        mRbSixMonth = (RadioButton) findViewById(R.id.id_vipopen_rb_sixmonth);
        mRbTewMonth = (RadioButton) findViewById(R.id.id_vipopen_rb_tewmonth);

        mRbWeiXin = (RadioButton) findViewById(R.id.id_vipopen_rb_weixin);
        mRbZhiFuBao = (RadioButton) findViewById(R.id.id_vipopen_rb_zhifubao);

        mButton = (Button) findViewById(R.id.id_vipopen_bt);

        sp = getSharedPreferences("chatpage", MODE_PRIVATE);
        editor = sp.edit();
    }

    private void initEvenet()
    {
        mRbThreeMonth.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b)
            {
                if (b)
                {
                    mRbThreeMonth.setTextColor(getResources().getColor(R.color.orange));
                    editor.putString("time", "3");
                    editor.commit();
                } else
                {
                    mRbThreeMonth.setTextColor(getResources().getColor(android.R.color.black));
                }

            }
        });

        mRbSixMonth.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b)
            {
                if (b)
                {
                    mRbSixMonth.setTextColor(getResources().getColor(R.color.orange));
                    editor.putString("time", "6");
                    editor.commit();
                } else
                {
                    mRbSixMonth.setTextColor(getResources().getColor(android.R.color.black));
                }
            }
        });

        mRbTewMonth.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b)
            {
                if (b)
                {
                    mRbTewMonth.setTextColor(getResources().getColor(R.color.orange));
                    editor.putString("time", "12");
                    editor.commit();
                } else
                {
                    mRbTewMonth.setTextColor(getResources().getColor(android.R.color.black));
                }
            }
        });

        mRbWeiXin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b)
            {
                if (b)
                {
                    mRbWeiXin.setTextColor(getResources().getColor(R.color.orange));
                    editor.putString("pay", "weixin");
                    editor.commit();
                } else
                {
                    mRbWeiXin.setTextColor(getResources().getColor(android.R.color.black));
                }
            }
        });

        mRbZhiFuBao.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b)
            {
                if (b)
                {
                    mRbZhiFuBao.setTextColor(getResources().getColor(R.color.orange));
                    editor.putString("pay", "zhifubao");
                    editor.commit();
                } else
                {
                    mRbZhiFuBao.setTextColor(getResources().getColor(android.R.color.black));
                }
            }
        });


        mButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Toast.makeText(VIPOpenActivity.this, sp.getString("pay", "") + "-------------" + sp.getString("time", ""), Toast.LENGTH_SHORT).show();
            }
        });

    }
}
