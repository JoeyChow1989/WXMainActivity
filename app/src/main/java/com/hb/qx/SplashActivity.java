package com.hb.qx;

import in.srain.cube.app.XActivity;
import in.srain.cube.request.FailData;
import in.srain.cube.request.JsonData;
import in.srain.cube.request.RequestJsonHandler;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.mobads.SplashAd;
import com.baidu.mobads.SplashAdListener;
import com.hb.tool.Commonutil;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

public class SplashActivity extends XActivity {

    private HbApplication application;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
    protected String getCloseWarning() {
        return null;
    }

    @Override
    protected int getFragmentContainerId() {
        return 0;
    }

    public void request() {
        RequestUtil.reverseget(URL.UP_URL, new RequestJsonHandler() {
            @Override
            public void onRequestFinish(final JsonData data) {
                try {
                    System.out.println("data======================" + data);
                    if (data != null) {
                        int vsercode = data.optInt("vsercode");
                        System.out.println(application.vsercode);
                        if (vsercode > application.vsercode || !application.vsername.equals(data.optString("vsername"))) {
                            View view = getLayoutInflater().inflate(R.layout.dialog_update, null);
                            AlertDialogContainer container = new AlertDialogContainer(SplashActivity.this, view);
                            container.setNoText("否");
                            container.setOkText("是");
                            container.setTitle("更新通知");
                            TextView textView = (TextView) view.findViewById(R.id.msg);
                            textView.setText(Html.fromHtml(data.optString("msg")));
                            container.setCallBack(new AlertDialogCallBack() {
                                @Override
                                public boolean ok() {
                                    Intent intent = new Intent(SplashActivity.this, UpdateService.class);
                                    intent.putExtra("dowurl", data.optString("dowurl"));
                                    startService(intent);
                                    if (data.optString("compulsory").equals("no")) {
                                        jump();
                                    }
                                    return false;
                                }

                                @Override
                                public boolean no() {
                                    if (data.optString("compulsory").equals("no")) {
                                        jump();
                                        return true;
                                    } else {
                                        return false;
                                    }
                                }
                            });
                        } else {
                            jump();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onRequestFail(FailData failData) {
                System.out.println("failData:=========" + failData);
                jump();
            }
        });
    }

    public void request_new() {
        RequestUtil.reverseget(URL.NEW_URL, new RequestJsonHandler() {
            @Override
            public void onRequestFinish(final JsonData data) {
                JsonData listJsonData = data.optJson("data").optJson("list");
                System.out.println(listJsonData.toString());
                Commonutil.writeData("new_text.txt", listJsonData.toString(), getApplicationContext());
            }

            @Override
            public void onRequestFail(FailData failData) {

            }
        });
    }

    public void request_lock_type() {
        RequestUtil.reverseget(URL.LOCK_URL, new RequestJsonHandler() {
            @Override
            public void onRequestFinish(final JsonData data) {
                if (data != null) {
                    String lock = data.optString("lock_type");
                    application.editor.putString("lock_type", lock);
                    application.editor.commit();
                }
            }

            @Override
            public void onRequestFail(FailData failData) {

            }
        });
    }

    public void showAd() {
        // adUnitContainer
        RelativeLayout adsParent = (RelativeLayout) this.findViewById(R.id.adsRl);
        // the observer of AD
        SplashAdListener listener = new SplashAdListener() {
            @Override
            public void onAdDismissed() {
                Log.i("RSplashActivity", "onAdDismissed");
                jumpWhenCanClick(); // 跳转至您的应用主界面
            }

            @Override
            public void onAdFailed(String arg0) {
                Log.i("RSplashActivity", "onAdFailed");
                request();
            }

            @Override
            public void onAdPresent() {
                Log.i("RSplashActivity", "onAdPresent");
            }

            @Override
            public void onAdClick() {
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

    private void jumpWhenCanClick() {
        Log.d("test", "this.hasWindowFocus():" + this.hasWindowFocus());
        if (canJumpImmediately) {
            request();
            // this.startActivity(new Intent(SplashActivity.this,
            // MainActivity.class));
            // this.finish();
        } else {
            canJumpImmediately = true;
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        canJumpImmediately = false;
    }

    /**
     * 不可点击的开屏，使用该jump方法，而不是用jumpWhenCanClick
     */
    private void jump() {
        this.startActivity(new Intent(SplashActivity.this, MainActivity.class));
        this.finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (canJumpImmediately) {
            jumpWhenCanClick();
        }
        canJumpImmediately = true;
    }

    public InputStream inputStream;
    public ByteArrayOutputStream bos;
    String s;

    class HttpAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            System.out.println("------------onPostExecute-------------");
            try {
                JSONObject object = new JSONObject(s);

                String s1 = object.getString("dowurl");

                System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" + s1);


            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        @Override
        protected String doInBackground(String... strings) {

            HttpClient httpclient = new DefaultHttpClient();
            try {
                // 创建httpget.
                HttpGet httpget = new HttpGet(strings[0]);
                System.out.println("executing request " + httpget.getURI());
                // 执行get请求.
                HttpResponse response = httpclient.execute(httpget);
                try {
                    // 获取响应实体
                    HttpEntity entity = response.getEntity();
                    System.out.println("--------------------------------------");
                    // 打印响应状态
                    System.out.println(response.getStatusLine());
                    if (entity != null) {
                        // 打印响应内容长度
                        System.out.println("Response content length: " + entity.getContentLength());
                        // 打印响应内容
                        //System.out.println("Response content: " + EntityUtils.toString(entity, "utf-8"));

                        inputStream = entity.getContent();

                        bos = new ByteArrayOutputStream();

                        byte[] buffer = new byte[1024];

                        int len = -1;
                        while ((len = inputStream.read(buffer)) != -1) {
                            bos.write(buffer, 0, len);
                        }

                        s = new String(bos.toByteArray(), "UTF-8");

                        System.out.println("size" + s.length());

                        System.out.println("content===========================" + s);

                    }
                    System.out.println("------------------------------------");
                } finally {
                }
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                // 关闭连接,释放资源
            }

            return s;
        }
    }

}
