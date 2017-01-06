package com.hb.qx;

import android.app.Activity;
import android.content.Intent;
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

public class YqhyDialog2 extends PopupWindow implements OnClickListener
{
    private Activity mActivity;
    private LinearLayout linearLayout;
    private TextView open;
    private int type = 0;

    public YqhyDialog2(Activity activity, int type)
    {
        mActivity = activity;
        this.type = type;
        initView();

    }

    public void initView()
    {
        View rootView = LayoutInflater.from(mActivity).inflate(R.layout.dialog_share2, null);
        linearLayout = (LinearLayout) rootView.findViewById(R.id.context_view2);
        open = (TextView) rootView.findViewById(R.id.id_dialog2_openvip);
        open.setOnClickListener(this);
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
                case R.id.id_dialog2_openvip:
                    Intent intent = new Intent(mActivity, VIPOpenActivity.class);
                    mActivity.startActivity(intent);
                    dismiss();
                    break;
                default:
                    break;
            }
        } catch (Exception e)
        {
            dismiss();
        }
    }
}
