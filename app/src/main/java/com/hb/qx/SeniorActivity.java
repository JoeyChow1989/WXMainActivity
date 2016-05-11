package com.hb.qx;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class SeniorActivity extends Activity implements OnClickListener
{

    private ImageView mBack;
    private LinearLayout ly_open, ly_nokick, ly_about;

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

        ly_open.setOnClickListener(this);
        ly_nokick.setOnClickListener(this);
        //ly_about.setOnClickListener(this);


        //Intent intent = new Intent(Settings.ACTION_SECURITY_SETTINGS);
    }

    private void init()
    {
        // TODO Auto-generated method stub
        mBack = (ImageView) findViewById(R.id.img_senior_back);
        ly_open = (LinearLayout) findViewById(R.id.id_openclick);
        ly_nokick = (LinearLayout) findViewById(R.id.id_nokick);
        // ly_about = (LinearLayout) findViewById(R.id.id_about);
    }

    @Override
    public void onClick(View v)
    {
        // TODO Auto-generated method stub
        Intent intent = new Intent();
        switch (v.getId())
        {
            case R.id.id_openclick:
                intent.setClass(SeniorActivity.this, OpenUseActivity.class);
                break;
            case R.id.id_nokick:
                intent.setClass(SeniorActivity.this, NoKickActivity.class);
                break;
//           case R.id.id_about:
//              intent.setClass(SeniorActivity.this, MoreActivity.class);
//              break;
        }
        startActivity(intent);
    }

}
