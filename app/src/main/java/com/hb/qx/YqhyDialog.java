package com.hb.qx;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners.SnsPostListener;
import com.umeng.socialize.media.QQShareContent;
import com.umeng.socialize.media.QZoneShareContent;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.sso.QZoneSsoHandler;
import com.umeng.socialize.sso.UMQQSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;
import com.umeng.socialize.weixin.media.CircleShareContent;
import com.umeng.socialize.weixin.media.WeiXinShareContent;

public class YqhyDialog extends PopupWindow implements OnClickListener
{
    private UMSocialService mController = UMServiceFactory.getUMSocialService("com.umeng.share");
    private Activity mActivity;
    private LinearLayout linearLayout;
    private ImageView wx_image;
    private TextView wx_text;
    private ImageView wx_py_image;
    private TextView wx_py_text;
//    private ImageView qq_image;
//    private TextView qq_text;
//    private ImageView qq_qzone_image;
//    private TextView qq_qzone_text;
    private LinearLayout main_textLayout;
    private int type = 0;

    // 分享的内容
//    private int count;

    private SharedPreferences sp;
    private SharedPreferences.Editor editor;


    public YqhyDialog(Activity activity, int type)
    {
        mActivity = activity;
        this.type = type;
        initView();
    }

    /**
     * 检查包名
     */
    public boolean checkApplication(String packageName)
    {
        try
        {
            ApplicationInfo info = mActivity.getPackageManager().getApplicationInfo(packageName, PackageManager.GET_UNINSTALLED_PACKAGES);
            return true;
        } catch (NameNotFoundException e)
        {
            return false;
        }
    }

    public void initView()
    {
        View rootView = LayoutInflater.from(mActivity).inflate(R.layout.dialog_share, null);
        linearLayout = (LinearLayout) rootView.findViewById(R.id.context_view);
        wx_py_image = (ImageView) rootView.findViewById(R.id.wx_py_image);
        wx_py_image.setOnClickListener(this);
        wx_image = (ImageView) rootView.findViewById(R.id.wx_image);
        wx_image.setOnClickListener(this);
        wx_py_text = (TextView) rootView.findViewById(R.id.wx_py_text);
        wx_py_text.setOnClickListener(this);
        wx_text = (TextView) rootView.findViewById(R.id.wx_text);
        wx_text.setOnClickListener(this);
        main_textLayout = (LinearLayout) rootView.findViewById(R.id.main_linearlayout);
        main_textLayout.setOnClickListener(this);
        setContentView(rootView);
        setWidth(LayoutParams.MATCH_PARENT);
        setHeight(LayoutParams.MATCH_PARENT);
        setFocusable(true);
        setTouchable(true);
        setOutsideTouchable(true);
        linearLayout.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dismiss();
            }
        });

        sp = mActivity.getSharedPreferences("chatpage", mActivity.MODE_PRIVATE);
        editor = sp.edit();

    }

    @Override
    public void dismiss()
    {
        super.dismiss();
    }

    @Override
    public void onClick(View v)
    {
        try
        {
            int id = v.getId();
            switch (id)
            {
                // 微信朋友圈
                case R.id.wx_py_image:
                    // wxf11f76680d531d8c是你在微信开发平台注册应用的AppID, 这里需要替换成你注册的AppID
                    // 支持微信朋友圈
                    init_wechatmoments();
                    return;
                case R.id.wx_py_text:
                    // wxf11f76680d531d8c是你在微信开发平台注册应用的AppID, 这里需要替换成你注册的AppID
                    // 支持微信朋友圈
                    init_wechatmoments();
                    return;

                // 微信好友
                case R.id.wx_image:
                    // wxf11f76680d531d8c是你在微信开发平台注册应用的AppID, 这里需要替换成你注册的AppID
                    // 支持微信朋友圈
                    // 微信好友
                    init_wechathy();
                    return;
                case R.id.wx_text:
                    // wxf11f76680d531d8c是你在微信开发平台注册应用的AppID, 这里需要替换成你注册的AppID
                    // 支持微信朋友圈
                    // 微信好友
                    init_wechathy();
                    return;

                default:
                    break;
            }
        } catch (Exception e)
        {
            dismiss();
        }
    }

    // 初始化朋友圈
    public void init_wechatmoments()
    {
        UMImage defaulturlImage = new UMImage(mActivity, R.drawable.fx_default);
        CircleShareContent circleMedia = new CircleShareContent();
        circleMedia.setShareContent(URL.FX_TEXT);
        circleMedia.setTitle(URL.FX_TITLE);
        circleMedia.setShareImage(defaulturlImage);
        circleMedia.setTargetUrl(URL.FX_URL);
        mController.setShareMedia(circleMedia);
        wechats_listener();

    }

    // 启动微信朋友圈
    public void wechats_listener()
    {
        UMWXHandler wxCircleHandler = new UMWXHandler(mActivity, URL.appId, URL.appSecret);
        wxCircleHandler.setToCircle(true);
        wxCircleHandler.addToSocialSDK();
        wxCircleHandler.showCompressToast(false);
        mController.postShare(mActivity, SHARE_MEDIA.WEIXIN_CIRCLE, wechatmomentsListener);
    }

    // 回调微信朋友圈
    private SnsPostListener wechatmomentsListener = new SnsPostListener()
    {
        @Override
        public void onStart()
        {
        }

        @Override
        public void onComplete(SHARE_MEDIA platform, int eCode, SocializeEntity entity)
        {
            if (eCode == 200)
            {
                editor.putInt("share", 1);
                editor.commit();
                if (type != 0)
                {
                    HbApplication.instance.editor.putString("get_share", "yes");
                    HbApplication.instance.editor.commit();
                }
                Toast.makeText(mActivity, "分享成功", Toast.LENGTH_SHORT).show();
            }
            if (isShowing())
            {
                dismiss();
            }
        }
    };

    // 初始化微信好友
    public void init_wechathy()
    {
        UMImage defaulturlImage = new UMImage(mActivity, R.drawable.fx_default);
        WeiXinShareContent weixinContent = new WeiXinShareContent();
        weixinContent.setShareContent(URL.FX_TEXT);
        weixinContent.setTitle(URL.FX_TITLE);
        weixinContent.setTargetUrl(URL.FX_URL);
        weixinContent.setShareMedia(defaulturlImage);
        mController.setShareMedia(weixinContent);
        wechatht_listener();

    }

    // 启动微信好友
    public void wechatht_listener()
    {
        UMWXHandler wxHandler = new UMWXHandler(mActivity, URL.appId, URL.appSecret);
        wxHandler.addToSocialSDK();
        // 关闭默显示信息
        wxHandler.showCompressToast(false);
        mController.postShare(mActivity, SHARE_MEDIA.WEIXIN, wxmSnsListener);
    }

    // 回调微信好友
    private SnsPostListener wxmSnsListener = new SnsPostListener()
    {
        @Override
        public void onStart()
        {

        }

        @Override
        public void onComplete(SHARE_MEDIA platform, int eCode, SocializeEntity entity)
        {
            if (eCode == 200)
            {
                Toast.makeText(mActivity, "分享成功", Toast.LENGTH_SHORT).show();
                editor.putInt("share", 1);
                editor.commit();
                if (type != 0)
                {
                    HbApplication.instance.editor.putString("get_share", "yes");
                    HbApplication.instance.editor.commit();
                }
            }
            if (isShowing())
            {
                dismiss();
            }
        }
    };

    // 初始化QQ好友
    public void init_qq()
    {
        UMImage defaulturlImage = new UMImage(mActivity, R.drawable.fx_default);
        QQShareContent qqShareContent = new QQShareContent();
        qqShareContent.setShareContent(URL.FX_TEXT);
        qqShareContent.setTitle(URL.FX_TITLE);
        qqShareContent.setShareImage(defaulturlImage);
        qqShareContent.setTargetUrl(URL.FX_URL);
        mController.setShareMedia(qqShareContent);
        qq_listener();
    }

    // 启动qq好友
    public void qq_listener()
    {
        UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(mActivity, URL.qq_appId, URL.appSecret);
        qqSsoHandler.addToSocialSDK();
        mController.postShare(mActivity, SHARE_MEDIA.QQ, qqmSnsListener);
    }

    // qq好友
    private SnsPostListener qqmSnsListener = new SnsPostListener()
    {
        @Override
        public void onStart()
        {

        }

        @Override
        public void onComplete(SHARE_MEDIA platform, int eCode, SocializeEntity entity)
        {
            if (eCode == 200)
            {
                Toast.makeText(mActivity, "分享成功", Toast.LENGTH_SHORT).show();
            }
            if (isShowing())
            {
                dismiss();
            }
        }
    };

    //初始化QQ空间
    public void init_qqzone()
    {
        UMImage defaulturlImage = new UMImage(mActivity, R.drawable.fx_default);
        QZoneShareContent qzone = new QZoneShareContent();
        qzone.setShareContent(URL.FX_TEXT);
        qzone.setTargetUrl(URL.FX_URL);
        qzone.setTitle(URL.FX_TITLE);
        qzone.setShareImage(defaulturlImage);
        mController.setShareMedia(qzone);
        qqzone_listener();
    }

    //启动qq空间
    public void qqzone_listener()
    {
        QZoneSsoHandler qZoneSsoHandler = new QZoneSsoHandler((Activity) mActivity, URL.qq_appId, URL.appSecret);
        qZoneSsoHandler.addToSocialSDK();
        mController.postShare(mActivity, SHARE_MEDIA.QZONE, qqmqzoneListener);
    }

    //回调qq空间
    private SnsPostListener qqmqzoneListener = new SnsPostListener()
    {
        @Override
        public void onStart()
        {
        }

        @Override
        public void onComplete(SHARE_MEDIA platform, int eCode, SocializeEntity entity)
        {
            if (eCode == 200)
            {
                Toast.makeText(mActivity, "分享成功", Toast.LENGTH_SHORT).show();
            }
            if (isShowing())
            {
                dismiss();
            }
        }
    };

}
