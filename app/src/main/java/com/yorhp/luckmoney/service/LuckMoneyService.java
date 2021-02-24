package com.yorhp.luckmoney.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.os.PowerManager;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;

import com.yorhp.luckmoney.R;
import com.yorhp.luckmoney.util.ScreenUtil;
import com.yorhp.luckmoney.util.threadpool.AppExecutors;

/**
 * 抢红包辅助
 *
 * @author Tyhj
 * @date 2019/6/30
 */

public class LuckMoneyService extends BaseAccessbilityService {

    public static final String TAG="LuckMoneyService";

    /**
     * 单独抢一个群
     */
    public static boolean isSingle = true;
    /**
     * 是否返回桌面
     */
    public static boolean isBackHome = false;
    /**
     * 暂停抢红包
     */
    public static boolean isPause = false;

    /**
     * 当前界面是否在聊天消息里面
     */
    public static boolean isInChatList=false;


    /**
     * 微信包名
     */
    private static final String WX_PACKAGE_NAME = "com.tencent.mm";



    /**
     * 红包标识字段
     */
    public static final String HONG_BAO_TXT = "[微信红包]";

    /**
     * 联系人列表的红包ID 1
     */
    public static  String HUMAN_LIST_TXT_ID = "com.tencent.mm:id/cyv";
//    private static final String HUMAN_LIST_TXT_ID = "com.tencent.mm:id/bal";

    /**
     * 头像ID 2
     */
    public static  String AVATAR_ID = "com.tencent.mm:id/au2";

    /**
     * 已领取ID 3
     */
//    private static final String AUL_ID = "com.tencent.mm:id/aul";
    public static  String AUL_ID = "com.tencent.mm:id/tt";
    /**
     * 开红包id 4
     */
    public static  String OPEN_ID = "com.tencent.mm:id/f4f";



    /**
     * 联系人列表 5
     */
//    private static final String HUMAN_LIST = "com.tencent.mm:id/dcf";
    public static  String HUMAN_LIST = "com.tencent.mm:id/f67";

    /**
     * 红包ID 6
     */
//    private static final String AUM_ID = "com.tencent.mm:id/aum";
    public static  String AUM_ID = "com.tencent.mm:id/ahs";

    /**
     * 详情界面的聊天List 的ID  7
     */
//    public static final String DETAIL_CHAT_LIST_ID = "com.tencent.mm:id/ag";
    public static  String DETAIL_CHAT_LIST_ID = "com.tencent.mm:id/awv";
    /**
     * 红包详情页
     */
    private static String LUCKY_MONEY_DETAIL = "com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyDetailUI";


    /**
     * 红包弹出的class的名字
     */
    private static final String ACTIVITY_DIALOG_LUCKYMONEY = "com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyNotHookReceiveUI";

    /**
     * 红包的开字在屏幕中的比例
     */
    private static final float POINT_OPEN_Y_SCAL = 0.62F;

    /**
     * 红包弹窗的关闭按钮在屏幕中的比例
     */
    private static final float POINT_CANCEL_Y_SCAL=0.81F;

    /**
     * 红包弹窗中，查看领取详情在屏幕中的比例
     */
    private static final float POINT_DETAIL_Y_SCAL=0.705F;

    /**
     * 等待弹窗弹出时间
     */
    public static int waitWindowTime=150;


    /**
     * 等待红包领取时间
     */
    public static int waitGetMoneyTime=700;


    /**
     * 当前机型是否需要配置时间，是否能获取到弹窗
     */
    public static int needSetTime=-1;

    /**
     * 获取屏幕宽高
     */
    private int screenWidth = ScreenUtil.SCREEN_WIDTH;
    private int screenHeight = ScreenUtil.SCREEN_HEIGHT;

    /**
     * 计算领取红包的时间
     */
    private static long luckMoneyComingTime;

    /**
     * 是否在领取详情页
     */
    private static boolean inMoneyDetail=false;


    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "onCreate: " );

    }




    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
//        Log.e(TAG, "onAccessibilityEvent: " +AccessibilityEvent.eventTypeToString(event.getEventType()));
//        Log.e(TAG, "onAccessibilityEvent: " +event.getClassName());
//        Log.e(TAG, "onAccessibilityEvent: " +event.getPackageName());

        //暂停
        if (isPause) {
            isInChatList=false;
            return;
        }

        String packageName = event.getPackageName().toString();
//        Log.e(TAG, "onAccessibilityEvent: "+packageName );
        if (!packageName.contains(WX_PACKAGE_NAME)) {
            //不是微信就退出
            isInChatList=false;
            return;
        }

        //当前类名
        String className = event.getClassName().toString();

        //当前为红包弹出窗（那个开的那个弹窗）
        if (className.equals(ACTIVITY_DIALOG_LUCKYMONEY)) {
            //进行红包开点击
            inMoneyDetail=false;
            clickOpen();
            return;
        }


        //获取聊天消息列表List控件
        AccessibilityNodeInfo nodeInfo = findViewByID(DETAIL_CHAT_LIST_ID);
        //这个消息列表不为空，那么肯定在聊天详情页
        if (nodeInfo != null) {
            luckMoneyComingTime=System.currentTimeMillis();
            //判断有没有未领取红包并进行点击
            if(!clickItem(nodeInfo)&&!isSingle&&event.getEventType()==AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED){
//                Log.e(TAG, "onAccessibilityEvent:返回会话列表 " );
                performGlobalAction(GLOBAL_ACTION_BACK);
            }
            isInChatList=true;
            return;
        }


        //通知栏消息，判断是不是红包消息
        if (event.getParcelableData() != null && event.getParcelableData() instanceof Notification) {
            Log.e(TAG,"接收到通知栏消息");
            //如果当前界面是在消息列表内，并且单独抢这个群，则不必点击通知消息
//            if(!isInChatList||!isSingle){
                Notification notification = (Notification) event.getParcelableData();
                //获取通知消息详情
                String content = notification.tickerText.toString();
                Log.e(TAG,"接收到通知栏消息"+content);
                //解析消息
                String[] msg = content.split(":");
                String text = msg[1].trim();
                if (text.contains(HONG_BAO_TXT)) {
//                    Log.e(TAG,"接收到通知栏红包消息");
                    PendingIntent pendingIntent = notification.contentIntent;
                    try {
                        //点击消息，进入聊天界面
                        pendingIntent.send();
                    } catch (PendingIntent.CanceledException e) {
                        e.printStackTrace();
                    }
                }
//            }
            return;
        }


        //红包领取后的详情页面，自动返回
        if (className.equals(LUCKY_MONEY_DETAIL)) {
            inMoneyDetail=true;
//            Log.e(TAG,"领取红包时间为："+"isSingle == "+isSingle+(System.currentTimeMillis()-luckMoneyComingTime)+"ms");
            //返回聊天界面
            performGlobalAction(GLOBAL_ACTION_BACK);
            if(isBackHome){
                performGlobalAction(GLOBAL_ACTION_HOME);
            }
            return;
        }


        //在最近聊天列表，检测有没有红包消息出现
        nodeInfo = findViewByID(HUMAN_LIST);
        //联系人列表
        if (nodeInfo != null) {
            //判断最近聊天列表有没有未领取红包
           clickHumanItem(nodeInfo);
            return;
        }
    }

    @Override
    public void onInterrupt() {

    }

    /**
     * 点击开红包按钮
     */
    private void clickOpen() {
        //获取开字的控件
        AccessibilityNodeInfo target = findViewByID(OPEN_ID);
        if (target != null) {
            needSetTime=0;
            performViewClick(target);
//            Log.e(TAG,"获取到了开按钮");
            return;
        } else
            {
            needSetTime=1;
            //如果没有找到按钮，再进行模拟点击
            //此处根据手机性能进行等待弹窗弹出
            AppExecutors.getInstance().networkIO().execute(()->{
                long startTime=System.currentTimeMillis();
                while (System.currentTimeMillis()-startTime<waitWindowTime&&!inMoneyDetail){
                    //计算了一下这个開字在屏幕中的位置，按照屏幕比例计算
                    Log.e(TAG,"循环点击"+"x == "+screenWidth/2+"Y =="+screenHeight*POINT_OPEN_Y_SCAL);
                    clickOnScreen(screenWidth / 2, screenHeight * POINT_OPEN_Y_SCAL, 1, null);
                    SystemClock.sleep(10);
                }
                if(inMoneyDetail){
                    Log.e(TAG,"按钮点击完成，已到领取详情页");
                    return;
                }
                //防止红包已经被领完后无法跳转到下一个界面
                SystemClock.sleep(waitGetMoneyTime);
                if(inMoneyDetail){
                    Log.e(TAG,"等待时间后，已到领取详情页");
                    return;
                }
                if(isSingle){
                    //点击取消按钮，返回聊天界面
                    clickOnScreen(screenWidth/2,screenHeight*POINT_CANCEL_Y_SCAL,1,null);
                }else {
                    //点击详情进入到详情界面，触发返回操作
                    clickOnScreen(screenWidth/2,screenHeight*POINT_DETAIL_Y_SCAL,1,null);
                }
            });
        }
    }

    @Override
    protected boolean onKeyEvent(KeyEvent event) {
        Log.e(TAG, "onKeyEvent: "+event.getAction());
        return super.onKeyEvent(event);

    }

    /**
     * 进行消息列表未领取红包的点击
     *
     * @param nodeInfo
     */
    private boolean clickItem(AccessibilityNodeInfo nodeInfo) {
//        Log.e(TAG, "clickItem: "+nodeInfo.getChildCount());
        //遍历消息列表的每个消息
        for (int i = 0; i < nodeInfo.getChildCount(); i++) {
            //获取到子控件
            AccessibilityNodeInfo nodeInfoChild = nodeInfo.getChild(i);
            //获取红包控件
            AccessibilityNodeInfo target = findViewByID(nodeInfoChild, AUM_ID);
//            Log.e(TAG, "clickItem:target == "+target);
            //获取头像的控件
            AccessibilityNodeInfo avatar = findViewByID(nodeInfoChild, AVATAR_ID);
            boolean selfLuckMoney = false;
            //获取头像的位置，判断红包是否是自己发的，自己发的不抢
            if (avatar != null) {
                    Rect rect = new Rect();
                    avatar.getBoundsInScreen(rect);
                    if (rect.left > screenWidth / 2) {
                        selfLuckMoney = true;
                    }
            }
            //如果不是自己发的红包，并且获取到的微信红包这个控件不为空
            if (target != null && !selfLuckMoney) {
                //已领取这个控件为空，红包还没有被领取
                AccessibilityNodeInfo AUL_ID_View = findViewByID(nodeInfoChild, AUL_ID);
                if(AUL_ID_View!=null){
                    Log.e(TAG, "clickItem: "+AUL_ID_View.getText().toString() );

                }

                if (AUL_ID_View == null|| TextUtils.isEmpty(AUL_ID_View.getText().toString())) {
                    //点击红包控件
                    performViewClick(target);
                    return true;
                }
            }
        }
        return false;
    }


    /**
     * 进行联系人列表的红包消息点击
     *
     * @param nodeInfo
     */
    private boolean clickHumanItem(AccessibilityNodeInfo nodeInfo) {
        for (int i = 0; i < nodeInfo.getChildCount(); i++) {
            AccessibilityNodeInfo nodeInfoChild = nodeInfo.getChild(i);
            AccessibilityNodeInfo target = findViewByID(nodeInfoChild, HUMAN_LIST_TXT_ID);
            if (target != null && target.getText() != null && target.getText().toString().contains(HONG_BAO_TXT)) {
                performViewClick(target);
                return true;
            }
        }
        return  false;
    }


    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        Log.e(TAG, "onServiceConnected: " );
        ScreenUtil.getScreenSize(this);
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        // 创建唤醒锁
        PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "WxService:wakeLock");
        // 获得唤醒锁
        wakeLock.acquire();
        performGlobalAction(GLOBAL_ACTION_BACK);
        performGlobalAction(GLOBAL_ACTION_BACK);
        performGlobalAction(GLOBAL_ACTION_BACK);
    }
}
