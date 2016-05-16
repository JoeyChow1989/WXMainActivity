package com.hb.qx;

import android.accessibilityservice.AccessibilityService;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.EditText;

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
    //未抢的红包事件
    private AccessibilityNodeInfo mUnpackNode;
    //判断是否抢过了
    private boolean mNeedUnpack;

    private static final String WECHAT_DETAILS_EN = "Details";
    private static final String WECHAT_DETAILS_CH = "红包详情";
    private static final String WECHAT_BETTER_LUCK_EN = "Better luck next time!";
    private static final String WECHAT_BETTER_LUCK_CH = "手慢了";

    /**
     * 各种文字判断
     */
    private static final String WECHAT_EXPIRES_CH = "红包已失效";
    private static final String WECHAT_VIEW_SELF_CH = "查看领取详情";
    private static final String WECHAT_VIEW_OTHERS_CH = "领取红包";
    private static final String WECHAT_NOTIFICATION_TIP = "[微信红包]";
    private static final String WECHAT_OPEN_EN = "Open";
    private static final String WECHAT_OPENED_EN = "You've opened";
    private static final String QQ_NOTIFICATION_TIP = "[QQ红包]";
    private final static String QQ_DEFAULT_CLICK_OPEN = "点击拆开";
    private final static String QQ_DEFAULT_HAVE_OPENED = "已拆开";
    private final static String QQ_HONG_BAO_PASSWORD = "口令红包";
    private final static String QQ_HONG_BAO_PASSWORD_OPENED = "口令红包已拆开";
    private final static String QQ_CLICK_TO_PASTE_PASSWORD = "点击输入口令";
    private final static String QQ_SEND = "发送";

    // application
    private HbApplication mApplication;

    //获取过滤后红包事件的list的大小
    int size;

    //是否相应红包
    private boolean mLuckyMoneyReceived;

    //最后一个红包的ID
    private String lastFetchedHongbaoId = null;
    //初始化最后红包的抢到时间
    private long lastFetchedTime = 0;

    //最大时间不抢
    private static final int MAX_CACHE_TOLERANCE = 20000;

    //红包的事件
    private AccessibilityNodeInfo rootNodeInfo;
    //过滤的红包事件的LIST
    private List<AccessibilityNodeInfo> mReceiveNode;

    //红包的文字判断
    String tip;

    //判断是否回复
    private boolean huifu = false;
    private boolean huifu_weixin = false;

    //保证只回复一次
    private boolean huifu_QQ = false;
    private boolean huifu_mm = false;

    //判断是否@发红包的人
    private boolean aite = false;

    //接收事件的次数
    public int eventTime;

    //发红包的人的名字
    String user;

    //延时抢红包的延时时间
    private int yanshi;
    //判断是否是否自动抢
    private int buzidong;
    private boolean huiche = false;

    //配置文件
    SharedPreferences sp;

    @Override
    public void onCreate()
    {
        mApplication = HbApplication.getInstance();
        //初始化
        sp = getSharedPreferences("chatpage", MODE_PRIVATE);
        eventTime = 0;
        super.onCreate();
    }

    @Override
    public void onStart(Intent intent, int startId)
    {
        super.onStart(intent, startId);
    }


    /**
     * 遍历控件，自动发送口令红包
     *
     * @param info
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void recycle(AccessibilityNodeInfo info)
    {
        if (info.getChildCount() == 0)
        {
            if (info.getText() != null && info.getText().toString()
                    .equals(QQ_CLICK_TO_PASTE_PASSWORD))
            {
                info.getParent().performAction(
                        AccessibilityNodeInfo.ACTION_CLICK);
            }

            if (info.getText() != null && info.getClassName().toString().equals("android.widget.Button")
                    && info.getText().toString().equals(QQ_SEND))
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

    /**
     * 遍历控件，自动发送回复钱数
     *
     * @param info
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void recycle1(AccessibilityNodeInfo info)
    {
        if (info.getChildCount() == 0)
        {
            if (info.getClassName().toString().equals("android.widget.EditText"))
            {
                Bundle arguments = new Bundle();
                arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, MainActivity.qianshu + "    " + sp.getString("ganxieyu", "谢谢"));
                info.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments);
                MainActivity.qianshu = null;
            }

            if (info.getClassName().toString().equals("android.widget.Button") && info.getText().toString().equals(QQ_SEND))
            {
                info.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            }

            huifu = false;

        } else
        {
            for (int i = 0; i < info.getChildCount(); i++)
            {
                if (info.getChild(i) != null)
                {
                    recycle1(info.getChild(i));
                }
            }
        }
    }

    /**
     * 遍历控件，自动发送回复
     *
     * @param info
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void recycle2(AccessibilityNodeInfo info)
    {
        if (info.getChildCount() == 0)
        {
            if (info.getClassName().toString().equals("android.widget.EditText"))
            {
                Bundle arguments = new Bundle();
                arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, sp.getString("ganxieyu", "谢谢"));
                info.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments);
                MainActivity.qianshu = null;
            }

            if (info.getClassName().toString().equals("android.widget.Button") && info.getText().toString().equals(QQ_SEND))
            {
                info.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            }
            huifu = false;

        } else
        {
            for (int i = 0; i < info.getChildCount(); i++)
            {
                if (info.getChild(i) != null)
                {
                    recycle2(info.getChild(i));
                }
            }
        }
    }

    /**
     * 艾特发红包的人的判断
     *
     * @param info
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void recycle3(AccessibilityNodeInfo info)
    {
        if (info.getChildCount() == 0)
        {
            if (info.getClassName().toString().equals("android.widget.EditText"))
            {
                Bundle arguments = new Bundle();
                arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, "@" + user + "   " + sp.getString("ganxieyu", "谢谢"));
                info.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments);
                user = null;
            }

            if (info.getClassName().toString().equals("android.widget.Button") && info.getText().toString().equals(QQ_SEND))
            {
                info.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            }
            huifu = false;

        } else
        {
            for (int i = 0; i < info.getChildCount(); i++)
            {
                if (info.getChild(i) != null)
                {
                    recycle3(info.getChild(i));
                }
            }
        }
    }

    /**
     * 接收事件过滤红包并处理的核心方法
     *
     * @param event
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event)
    {
        /* 检测通知消息 */
        switch (event.getEventType())
        {
            case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED:

                System.out.println("--------进来了22222-----------------");
                tip = event.getText().toString();
                System.out.println("-----------------------" + tip);

                //根据文字判断事件
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

        //获取所有的事件资源
        this.rootNodeInfo = event.getSource();

        if (rootNodeInfo == null)
        {
            return;
        }

        //获取抢到的钱数(微信);
        if (rootNodeInfo.getText() != null)
        {
            if (rootNodeInfo.getText().toString().contains(".") && rootNodeInfo.getText().toString().length() == 4)
            {
                MainActivity.qianshu = rootNodeInfo.getText().toString();
                huifu_weixin = true;
                huifu_mm = true;
            }
        }

        //获取抢到的钱数(QQ);
        if (rootNodeInfo.getText() != null)
        {
            if (rootNodeInfo.getText().toString().contains(".") && rootNodeInfo.getText().toString().length() == 5)
            {
                MainActivity.qianshu = rootNodeInfo.getText().toString();
                huifu_QQ = true;
                huifu = true;
                aite = true;
            }
        }

        //获取发红包人的名字
        if (rootNodeInfo.getText() != null)
        {
            if (rootNodeInfo.getText().toString().contains("来自"))
            {
                user = rootNodeInfo.getText().toString().replace("来自", "");
            }
        }


//        if (rootNodeInfo.getClassName().equals("android.widget.TextView") && rootNodeInfo.isLongClickable() && rootNodeInfo.getText() != null && !rootNodeInfo.getText().toString().contains("你领取了"))
//        {
//            System.out.println("-----------------root------------" + rootNodeInfo);
//            rootNodeInfo.performAction(AccessibilityNodeInfo.ACTION_LONG_CLICK);
//        }


        //消息撤回
        if (huiche == true && rootNodeInfo.getText() != null && sp.getInt("chexiao", 1) == 1)
        {
            if (rootNodeInfo.getClassName().equals("android.widget.TextView") && rootNodeInfo.isLongClickable() && !rootNodeInfo.getText().toString().contains("你领取了"))
            {
                System.out.println("--------------------rootNodeInfo------------撤销------" + rootNodeInfo);
                rootNodeInfo.performAction(AccessibilityNodeInfo.ACTION_LONG_CLICK);
            }

            if (rootNodeInfo.getClassName().equals("android.widget.TextView") && rootNodeInfo.getText().toString().equals("撤回"))
            {
                rootNodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                huiche = false;
            }
        }

        //list先初始化;
        mReceiveNode = null;

        /**
         * 检索事件
         */
        checkNodeInfo();

		/* 如果已经接收到红包并且还没有戳开 */
        if (mLuckyMoneyReceived && (mReceiveNode != null))
        {
            size = mReceiveNode.size();
            if (size > 0)
            {
                final AccessibilityNodeInfo cellNode = mReceiveNode
                        .get(size - 1);
                final AccessibilityNodeInfo rowNode = getRootInActiveWindow();
                String id = getHongbaoText(mReceiveNode.get(size - 1));
                long now = System.currentTimeMillis();

                /**
                 * 触发微信回复
                 */
                if (huifu_weixin == true && cellNode.getPackageName().equals("com.tencent.mm") && MainActivity.qianshu != null)
                {
                    if (rowNode == null)
                    {
                        return;
                    } else if (huifu_mm == true && sp.getInt("huifu", 0) == 1)
                    {
                        recycle2(rowNode);
                        huifu_mm = false;
                    } else if (huifu_mm == true && sp.getInt("qianshu", 0) == 1)
                    {
                        recycle1(rowNode);
                        huifu_mm = false;
                    }
                }

                /**
                 * 触发QQ的回复
                 */
                if (huifu == true && cellNode.getPackageName().equals("com.tencent.mobileqq") && MainActivity.qianshu != null)
                {
                    if (rowNode == null)
                    {
                        return;
                    } else if (huifu_QQ == true && sp.getInt("huifu", 0) == 1)
                    {
                        recycle2(rowNode);
                        huifu_QQ = false;
                    } else if (huifu_QQ == true && sp.getInt("qianshu", 0) == 1)
                    {
                        recycle1(rowNode);
                        huifu_QQ = false;
                    }
                }

                //触发@发红包人的事件
                if (huifu == true && cellNode.getPackageName().equals("com.tencent.mobileqq") && user != null)
                {
                    if (rowNode == null)
                    {
                        return;
                    } else if (huifu_QQ == true && sp.getInt("aite", 0) == 1)
                    {
                        recycle3(rowNode);
                        huifu_QQ = false;
                    }
                    aite = false;
                }

                //缓存判断是否再次点击
                if (this.shouldReturn(id, now - lastFetchedTime))
                    return;

                lastFetchedHongbaoId = id;
                lastFetchedTime = now;

                if (cellNode.getText().toString().equals(QQ_DEFAULT_HAVE_OPENED))
                {
                    return;
                }

                if (cellNode.getText().toString().equals(QQ_HONG_BAO_PASSWORD_OPENED))
                {
                    return;
                }

                //在QQ红包里
                if (cellNode.getPackageName().equals(
                        "com.tencent.mobileqq"))
                {
                    yanshi = MainActivity.YANSHI;
                    buzidong = sp.getInt("buzidong", 0);

                    System.out.println("yanshi------------service:" + yanshi);
                    // execute the task
                    if (cellNode != null)
                    {
                        new Handler().postDelayed(new Runnable()
                        {
                            public void run()
                            {
                                // 点击抢红包
                                if (cellNode.getParent() != null && buzidong == 0)
                                {
                                    cellNode.getParent().performAction(
                                            AccessibilityNodeInfo.ACTION_CLICK);
                                }
                            }
                        }, yanshi * 1000);
                    }

                    System.out.println("huifu--------------------------" + huifu);
                    System.out.println("aite----------------------------" + aite);

                }

                /**
                 * 在微信里
                 */
                else if (cellNode.getPackageName().equals("com.tencent.mm"))
                {
                    yanshi = MainActivity.YANSHI;
                    buzidong = sp.getInt("buzidong", 0);
                    System.out.println("yanshi------------service:" + yanshi);

                    new Handler().postDelayed(new Runnable()
                    {
                        public void run()
                        {
                            // 第一次点击
                            if (cellNode != null && buzidong == 0)
                            {
                                cellNode.getParent().performAction(
                                        AccessibilityNodeInfo.ACTION_CLICK);
                            }
                        }
                    }, yanshi * 1000 + 100);
                }

                // 判断是否触发口令红包事件
                if (cellNode.getText().toString().equals(QQ_HONG_BAO_PASSWORD))
                {
                    //获取所有的控件
                    AccessibilityNodeInfo rowNode1 = getRootInActiveWindow();
                    if (rowNode == null)
                    {
                        return;
                    } else
                    {
                        huiche = true;
                        recycle(rowNode1);
                    }
                }
                eventTime++;

                //qq 抢完后弹广告
                if (rootNodeInfo.getPackageName()
                        .equals("com.tencent.mobileqq"))
                {
                    System.out.println("eventTime----------------------------------" + eventTime);

                    if (eventTime % 13 == 0)
                    {
                        System.out
                                .println("jump.............................................................");
                        new Handler().postDelayed(new Runnable()
                        {
                            public void run()
                            {
                                // 发送百度统计
                                sendBaidu();
                            }
                        }, 4000);
                    }
                }
                mLuckyMoneyReceived = false;
            }
        }

        /**
         *  ----微信---
         *  如果戳开但还未领取
         * */
        if (mNeedUnpack
                && (mUnpackNode != null)
                && event.getClassName()
                .equals("com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyReceiveUI"))
        {
            AccessibilityNodeInfo cellNode = mUnpackNode;
            cellNode.performAction(AccessibilityNodeInfo.ACTION_CLICK);

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
                }, 4000);
            }
            // sendBaidu();
            mNeedUnpack = false;
        } else if (event.getClassName().equals(
                "com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyReceiveUI"))
        {
            int count = rootNodeInfo.getChildCount();

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

                //微信红包弹广告
                if (resultInfo != null)
                {
                    if (resultInfo.getPackageName().equals("com.tencent.mm"))
                    {
                        new Handler().postDelayed(new Runnable()
                        {
                            public void run()
                            {
                                // 发送百度统计
                                sendBaidu();
                            }
                        }, 2000);
                    }
                }
            }
            mNeedUnpack = false;
        }
    }

    List<AccessibilityNodeInfo> nodes1;

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
        nodes1 = this
                .findAccessibilityNodeInfosByTexts(this.rootNodeInfo,
                        new String[]{WECHAT_VIEW_OTHERS_CH,
                                WECHAT_VIEW_SELF_CH, QQ_DEFAULT_CLICK_OPEN,
                                QQ_HONG_BAO_PASSWORD,
                                QQ_CLICK_TO_PASTE_PASSWORD, QQ_SEND});

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

    /**
     * 发送百度统计
     */
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

    /**
     * 百度广告
     */
    public void baidu_ad()
    {
        mApplication.editor.putString("show_ad_type", "yes");
        mApplication.editor.commit();

        System.out.println("chatpage======================" + mApplication.sp.getInt("chatpage", 0));

        //自动跳转聊天页
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
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        // service 关闭后重新打开
        Intent intent = new Intent(getApplicationContext(),
                MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
