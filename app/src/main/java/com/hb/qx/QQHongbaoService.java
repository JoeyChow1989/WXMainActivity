package com.hb.qx;

import android.accessibilityservice.AccessibilityService;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.baidu.mobstat.StatService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by zhouzhengyao on 2016/3/4.
 */
public class QQHongbaoService extends AccessibilityService
{

    private AccessibilityNodeInfo mUnpackNode;
    private boolean mNeedUnpack;
    private static final String WECHAT_DETAILS_EN = "Details";
    private static final String WECHAT_DETAILS_CH = "红包详情";
    private static final String WECHAT_BETTER_LUCK_EN = "Better luck next time!";
    private static final String WECHAT_BETTER_LUCK_CH = "手慢了";
    private static final String WECHAT_EXPIRES_CH = "红包已失效";
    private static final String WECHAT_VIEW_SELF_CH = "查看红包";
    private static final String WECHAT_VIEW_OTHERS_CH = "领取红包";
    private static final String WECHAT_NOTIFICATION_TIP = "[微信红包]";

    private static final String WECHAT_OPEN_EN = "Open";
    private static final String WECHAT_OPENED_EN = "You've opened";
    private static final String QQ_NOTIFICATION_TIP = "[QQ红包]";
    private final static String QQ_DEFAULT_CLICK_OPEN = "点击拆开";
    // private final static String QQ_DEFAULT_HAVE_OPENED = "已拆开";
    private final static String QQ_HONG_BAO_PASSWORD = "口令红包";
    private final static String QQ_CLICK_TO_PASTE_PASSWORD = "点击输入口令";

    // 延时抢红包
    private static boolean lastTime = false;
    private HbApplication mApplication;

    private boolean mLuckyMoneyReceived;
    private String lastFetchedHongbaoId = null;
    private long lastFetchedTime = 0;
    private static final int MAX_CACHE_TOLERANCE = 20000;

    private AccessibilityNodeInfo rootNodeInfo;
    private List<AccessibilityNodeInfo> mReceiveNode;

    public int eventTime;

    SharedPreferences sp;


    @Override
    public void onCreate()
    {
        mApplication = HbApplication.getInstance();
        sp = getSharedPreferences("chatpage",MODE_PRIVATE);
        eventTime = 0;
        super.onCreate();
    }

    @Override
    public void onStart(Intent intent, int startId)
    {
        super.onStart(intent, startId);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void recycle(AccessibilityNodeInfo info)
    {
        if (info.getChildCount() == 0)
        {
            // Log.e(TAG, "child widget----------------------------" +
            // info.getClassName());
            // Log.e(TAG, "showDialog:" + info.canOpenPopup());
            // Log.e(TAG, "Text：" + info.getText());
            // Log.e(TAG, "windowId:" + info.getWindowId());

            if (info.getText() != null
                    && info.getText().toString()
                    .equals(QQ_CLICK_TO_PASTE_PASSWORD))
            {
                info.getParent().performAction(
                        AccessibilityNodeInfo.ACTION_CLICK);
            }

            if (info.getClassName().toString().equals("android.widget.Button")
                    && info.getText().toString().equals("发送"))
            {
                info.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            }
        } else
        {
            for (int i = 0; i < info.getChildCount(); i++)
            {
                if (info.getChild(i) != null)
                {
                    recycle(info.getChild(i));
                }
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event)
    {

		/* 检测通知消息 */
        switch (event.getEventType())
        {
            case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED:

                System.out.println("--------进来了22222-----------------");
                String tip = event.getText().toString();
                System.out.println("-----------------------" + tip);
                if (tip.contains(WECHAT_NOTIFICATION_TIP)
                        || tip.contains(QQ_NOTIFICATION_TIP))
                {
                    if (event.getParcelableData() != null
                            && event.getParcelableData() instanceof Notification)
                    {
                        Notification notification = (Notification) event
                                .getParcelableData();
                        try
                        {
                            notification.contentIntent.send();
                        } catch (PendingIntent.CanceledException e)
                        {
                            e.printStackTrace();
                        }
                    }
                    break;
                }
        }

        this.rootNodeInfo = event.getSource();

        if (rootNodeInfo == null)
        {
            return;
        }
        mReceiveNode = null;
        checkNodeInfo();

		/* 如果已经接收到红包并且还没有戳开 */
        if (mLuckyMoneyReceived && (mReceiveNode != null))
        {
            int size = mReceiveNode.size();
            if (size > 0)
            {

                String id = getHongbaoText(mReceiveNode.get(size - 1));

                long now = System.currentTimeMillis();

                if (this.shouldReturn(id, now - lastFetchedTime))
                    return;

                lastFetchedHongbaoId = id;
                lastFetchedTime = now;

                final AccessibilityNodeInfo cellNode = mReceiveNode
                        .get(size - 1);

                if (cellNode.getText().toString().equals("口令红包已拆开"))
                {
                    return;
                }

                if (lastTime == true
                        && (cellNode.getPackageName().equals("com.tencent.mm") || cellNode
                        .getPackageName()
                        .equals("com.tencent.mobileqq")))
                {
                    // TODO: 2016/3/9 延时抢红包
                    new Handler().postDelayed(new Runnable()
                    {
                        public void run()
                        {
                            // execute the task
                            cellNode.getParent().performAction(
                                    AccessibilityNodeInfo.ACTION_CLICK);
                        }
                    }, 3000);

                } else if (cellNode.getPackageName().equals(
                        "com.tencent.mobileqq"))
                {
                    // execute the task
                    if (cellNode != null)
                    {
                        cellNode.getParent().performAction(
                                AccessibilityNodeInfo.ACTION_CLICK);
                    }
                } else if (cellNode.getPackageName().equals("com.tencent.mm"))
                {
                    new Handler().postDelayed(new Runnable()
                    {
                        public void run()
                        {
                            // execute the task
                            if (cellNode != null)
                            {
                                cellNode.getParent().performAction(
                                        AccessibilityNodeInfo.ACTION_CLICK);
                            }
                        }
                    }, 100);
                }

                // Log.e(TAG, "---------开始----------");
                if (cellNode.getText().toString().equals(QQ_HONG_BAO_PASSWORD))
                {
                    // Log.e(TAG, "///////////");
                    AccessibilityNodeInfo rowNode = getRootInActiveWindow();
                    if (rowNode == null)
                    {
                        return;
                    } else
                    {
                        recycle(rowNode);
                    }
                }
                // Log.e(TAG, "-----------结束------------");
                // Log.e(TAG, "text = " + cellNode.getText().toString());

                System.out
                        .println("rootNodeInfo================================================="
                                + rootNodeInfo);
                eventTime++;

                System.out
                        .println("eventTime======================================================"
                                + eventTime);

                if (rootNodeInfo.getPackageName()
                        .equals("com.tencent.mobileqq"))
                {
                    if (eventTime % 3 == 0)
                    {
                        System.out
                                .println("jump.............................................................");
                        new Handler().postDelayed(new Runnable()
                        {
                            public void run()
                            {
                                // execute the task
                                sendBaidu();
                            }
                        }, 1500);
                    }
                }
                mLuckyMoneyReceived = false;
            }
        }
        /* 如果戳开但还未领取 */
        if (mNeedUnpack
                && (mUnpackNode != null)
                && event.getClassName()
                .equals("com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyReceiveUI"))
        {
            AccessibilityNodeInfo cellNode = mUnpackNode;
            cellNode.performAction(AccessibilityNodeInfo.ACTION_CLICK);

            System.out.println("rootNodeInfo=================" + rootNodeInfo);

            if (cellNode.getText() != null)
            {
                if (cellNode.getText().toString().equals(WECHAT_EXPIRES_CH))
                {
                    return;
                }
            }

            // 发送百度统计
            if (cellNode.getPackageName().equals("com.tencent.mm"))
            {
                new Handler().postDelayed(new Runnable()
                {
                    public void run()
                    {
                        // execute the task
                        sendBaidu();
                    }
                }, 2000);
            }
            // sendBaidu();
            mNeedUnpack = false;
        } else if (event.getClassName().equals(
                "com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyReceiveUI"))
        {
            int count = rootNodeInfo.getChildCount();

            System.out.println("count======================================:"
                    + count);

            if (count != 0)
            {
                AccessibilityNodeInfo resultInfo = null;
                AccessibilityNodeInfo cellNodeInfo = rootNodeInfo
                        .getChild(count - 1);
                int count_text = cellNodeInfo.getChildCount();
                for (int i = 0; i < count_text; i++)
                {
                    AccessibilityNodeInfo temp = cellNodeInfo.getChild(i);
                    String class_name = temp.getClassName().toString();
                    if (class_name.equals("android.widget.Button"))
                    {
                        resultInfo = temp;
                        break;
                    }
                }

                if (resultInfo != null)
                {
                    resultInfo
                            .performAction(AccessibilityNodeInfo.ACTION_CLICK);
                }

                if (cellNodeInfo.getText() != null)
                {
                    if (cellNodeInfo.getText().toString()
                            .equals(WECHAT_EXPIRES_CH))
                    {
                        return;
                    }
                }

                System.out.println("rootNodeInfo================="
                        + rootNodeInfo);
                if (resultInfo != null)
                {
                    if (resultInfo.getPackageName().equals("com.tencent.mm"))
                    {
                        new Handler().postDelayed(new Runnable()
                        {
                            public void run()
                            {
                                // execute the task
                                sendBaidu();
                            }
                        }, 2000);
                    }
                }
            }
            // 发送百度统计
            // sendBaidu();
            mNeedUnpack = false;
            System.out.print("mNeedUnpack");
        }
    }

    /**
     * 检查节点信息
     */
    private void checkNodeInfo()
    {

        if (rootNodeInfo == null)
        {
            return;
        }

		/* 聊天会话窗口，遍历节点匹配“点击拆开”，“口令红包”，“点击输入口令” */
        List<AccessibilityNodeInfo> nodes1 = this
                .findAccessibilityNodeInfosByTexts(this.rootNodeInfo,
                        new String[]{WECHAT_VIEW_OTHERS_CH,
                                WECHAT_VIEW_SELF_CH, QQ_DEFAULT_CLICK_OPEN,
                                QQ_HONG_BAO_PASSWORD,
                                QQ_CLICK_TO_PASTE_PASSWORD, "发送"});

        if (!nodes1.isEmpty())
        {
            String nodeId = Integer.toHexString(System
                    .identityHashCode(this.rootNodeInfo));
            if (!nodeId.equals(lastFetchedHongbaoId))
            {
                mLuckyMoneyReceived = true;
                mReceiveNode = nodes1;
            }
            return;
        }

		/* 戳开红包，红包还没抢完，遍历节点匹配“拆红包” */
        AccessibilityNodeInfo node2 = (this.rootNodeInfo.getChildCount() > 3) ? this.rootNodeInfo
                .getChild(3) : null;
        if (node2 != null
                && node2.getClassName().equals("android.widget.Button"))
        {
            mUnpackNode = node2;
            mNeedUnpack = true;
            return;
        }

    }

    /**
     * 将节点对象的id和红包上的内容合并 用于表示一个唯一的红包
     *
     * @param node 任意对象
     * @return 红包标识字符串
     */
    private String getHongbaoText(AccessibilityNodeInfo node)
    {
        /* 获取红包上的文本 */
        String content;
        try
        {
            AccessibilityNodeInfo i = node.getParent().getChild(0);
            content = i.getText().toString();
        } catch (NullPointerException npe)
        {
            return null;
        }
        return content;
    }

    /**
     * 判断是否返回,减少点击次数 现在的策略是当红包文本和缓存不一致时,戳 文本一致且间隔大于MAX_CACHE_TOLERANCE时,戳
     *
     * @param id       红包id
     * @param duration 红包到达与缓存的间隔
     * @return 是否应该返回
     */
    private boolean shouldReturn(String id, long duration)
    {
        // ID为空
        if (id == null)
            return true;

        // 名称和缓存不一致
        if (duration < MAX_CACHE_TOLERANCE && id.equals(lastFetchedHongbaoId))
        {
            return true;
        }
        return false;
    }

    /**
     * 批量化执行AccessibilityNodeInfo.findAccessibilityNodeInfosByText(text).
     * 由于这个操作影响性能,将所有需要匹配的文字一起处理,尽早返回
     *
     * @param nodeInfo 窗口根节点
     * @param texts    需要匹配的字符串们
     * @return 匹配到的节点数组
     */
    private List<AccessibilityNodeInfo> findAccessibilityNodeInfosByTexts(
            AccessibilityNodeInfo nodeInfo, String[] texts)
    {
        for (String text : texts)
        {
            if (text == null)
                continue;

            List<AccessibilityNodeInfo> nodes = nodeInfo
                    .findAccessibilityNodeInfosByText(text);

            if (!nodes.isEmpty())
            {
                if (text.equals(WECHAT_OPEN_EN)
                        && !nodeInfo.findAccessibilityNodeInfosByText(
                        WECHAT_OPENED_EN).isEmpty())
                {
                    continue;
                }
                return nodes;
            }
        }
        return new ArrayList<AccessibilityNodeInfo>();
    }

    @Override
    public void onInterrupt()
    {
    }

    public String getDate()
    {
        String temp_str = "";
        Date dt = new Date();
        // 最后的aa表示“上午”或“下午” HH表示24小时制 如果换成hh表示12小时制
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        temp_str = sdf.format(dt);
        return temp_str;
    }

    public void sendBaidu()
    {
        String sp_day = mApplication.sp.getString("baidu_day", "");
        String now_day = getDate();
        if (!sp_day.equals(now_day))
        {
            StatService.onEvent(this, "A001", "pass", 1);
            mApplication.editor.putString("baidu_day", getDate());
            mApplication.editor.commit();
        }
        baidu_ad();
    }

    public void baidu_ad()
    {
        mApplication.editor.putString("show_ad_type", "yes");
        mApplication.editor.commit();

        System.out.println("chatpage======================" + mApplication.sp.getInt("chatpage", 0));

        if (sp.getInt("chatpage", 0) == 1)
        {
            Intent intent = new Intent(getApplicationContext(), NewADActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else if (sp.getInt("chatpage", 0) == 0)
        {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
        // NewsAdDialog.showDialog(getApplicationContext());
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        Intent intent = new Intent(getApplicationContext(),
                SplashActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
