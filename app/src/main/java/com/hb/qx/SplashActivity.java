package com.hb.qx;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.mobads.SplashAd;
import com.baidu.mobads.SplashAdListener;
import com.hb.tool.Commonutil;
import com.hb.ui.CircleView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;

import in.srain.cube.app.XActivity;
import in.srain.cube.request.FailData;
import in.srain.cube.request.JsonData;
import in.srain.cube.request.RequestJsonHandler;

public class SplashActivity extends XActivity
{

    private HbApplication application;
    View view;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trial_run_activity);
        application = HbApplication.getInstance();
        //new HttpAsyncTask().execute(URL.UP_URL);
        showAd();
        //request();
        request_new();
        request_lock_type();
    }

    @Override
    protected String getCloseWarning()
    {
        return null;
    }

    @Override
    protected int getFragmentContainerId()
    {
        return 0;
    }

    public void request()
    {
        RequestUtil.reverseget(URL.UP_URL, new RequestJsonHandler()
        {
            public void onRequestFinish(final JsonData data)
            {
                System.out.println("data=============================" + data);
                System.out.println("vsername========================="
                        + Float.parseFloat(application.vsername));
                try
                {
                    int vsercode = data.optInt("vsercode");
                    float vsername = Float.parseFloat(data
                            .optString("vsername"));
                    System.out.println("ssssssssssssssssssssssss" + vsername);
                    if (vsercode > application.vsercode || vsername > Float.parseFloat(application.vsername))
                    {
                        View view = getLayoutInflater().inflate(
                                R.layout.dialog_update, null);
                        AlertDialogContainer container = new AlertDialogContainer(
                                SplashActivity.this, view);
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
                                Intent intent = new Intent(SplashActivity.this,
                                        UpdateService.class);
                                intent.putExtra("dowurl",
                                        data.optString("dowurl"));
                                startService(intent);
                                if (data.optString("compulsory").equals("no"))
                                {
                                    jump();
                                }
                                return false;
                            }

                            @Override
                            public boolean no()
                            {
                                if (data.optString("compulsory").equals("no"))
                                {
                                    jump();
                                    return true;
                                } else
                                {
                                    return false;
                                }
                            }
                        });
                    }
                    // execute the task
                    else
                    {
                        jump();
                    }
                } catch (Exception e)
                {

                }

            }

            @Override
            public void onRequestFail(FailData failData)
            {
                jump();
            }

        });
    }

    public void request_new()
    {
        RequestUtil.reverseget(URL.NEW_URL, new RequestJsonHandler()
        {
            @Override
            public void onRequestFinish(final JsonData data)
            {
                JsonData listJsonData = data.optJson("data").optJson("list");
                System.out.println(listJsonData.toString());
                Commonutil.writeData("new_text.txt", listJsonData.toString(), getApplicationContext());
            }

            @Override
            public void onRequestFail(FailData failData)
            {

            }
        });
    }

    public void request_lock_type()
    {
        RequestUtil.reverseget(URL.LOCK_URL, new RequestJsonHandler()
        {
            @Override
            public void onRequestFinish(final JsonData data)
            {
                if (data != null)
                {
                    String lock = data.optString("lock_type");
                    application.editor.putString("lock_type", lock);
                    application.editor.commit();
                }
            }

            @Override
            public void onRequestFail(FailData failData)
            {

            }
        });
    }

    public void showAd()
    {
        // adUnitContainer
        final RelativeLayout adsParent = (RelativeLayout) this.findViewById(R.id.adsRl);
        // the observer of AD
        SplashAdListener listener = new SplashAdListener()
        {
            @Override
            public void onAdDismissed()
            {
                Log.i("RSplashActivity", "onAdDismissed");
                jumpWhenCanClick(); // 跳转至您的应用主界面
            }

            @Override
            public void onAdFailed(String arg0)
            {
                Log.i("RSplashActivity", "onAdFailed");
                request();
            }

            @Override
            public void onAdPresent()
            {
                Log.i("RSplashActivity", "onAdPresent");
                view = LayoutInflater.from(SplashActivity.this).inflate(R.layout.jumpin, null);
                adsParent.addView(view);
                CircleView textView = (CircleView) view.findViewById(R.id.jumpin);
                textView.setBackgroundColor(getResources().getColor(R.color.gradient_text_color2));
                textView.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        jump();
                    }
                });
            }


            @Override
            public void onAdClick()
            {
                Log.i("RSplashActivity", "onAdClick");
                // 设置开屏可接受点击时，该回调可用
            }
        };

        String adPlaceId = "2402258"; // 重要：请填上您的广告位ID，代码位错误会导致无法请求到广告
        new SplashAd(this, adsParent, listener, adPlaceId, true);
    }

    /**
     * 当设置开屏可点击时，需要等待跳转页面关闭后，再切换至您的主窗口。故此时需要增加canJumpImmediately判断。
     * 另外，点击开屏还需要在onResume中调用jumpWhenCanClick接口。
     */
    public boolean canJumpImmediately = false;

    private void jumpWhenCanClick()
    {
        Log.d("test", "this.hasWindowFocus():" + this.hasWindowFocus());
        if (canJumpImmediately)
        {
            request();
            // this.startActivity(new Intent(SplashActivity.this,
            // MainActivity.class));
            // this.finish();
        } else
        {
            canJumpImmediately = true;
        }

    }

    @Override
    protected void onPause()
    {
        super.onPause();
        canJumpImmediately = false;
    }

    /**
     * 不可点击的开屏，使用该jump方法，而不是用jumpWhenCanClick
     */
    private void jump()
    {
        this.startActivity(new Intent(SplashActivity.this, MainActivity.class));
        this.finish();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        if (canJumpImmediately)
        {
            jumpWhenCanClick();
        }
        canJumpImmediately = true;
    }

    String s;

    class HttpAsyncTask extends AsyncTask<String, Void, String>
    {
        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s)
        {
            super.onPostExecute(s);

            System.out.println("------------onPostExecute-------------");
            try
            {
                JSONObject object = new JSONObject(s);
                String s1 = object.getString("dowurl");

            } catch (JSONException e)
            {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(String... strings)
        {

            InputStream inputStream = null;
            ByteArrayOutputStream bos = null;
            try
            {
                java.net.URL url = new java.net.URL(strings[0]);
                HttpURLConnection httpURLConnection1 = (HttpURLConnection) url
                        .openConnection();

                httpURLConnection1.setConnectTimeout(5000);
                httpURLConnection1.setRequestMethod("GET");

                if (httpURLConnection1.getResponseCode() == 200)
                {
                    inputStream = httpURLConnection1.getInputStream();
                    bos = new ByteArrayOutputStream();
                    byte[] buffer = new byte[1024];
                    int len = -1;
                    while ((len = inputStream.read(buffer)) != -1)
                    {
                        bos.write(buffer, 0, len);
                    }
                    s = new String(bos.toByteArray(), "gb2312");
                }
            } catch (Exception e)
            {
                e.printStackTrace();
            }
            return s;
        }
    }
}
