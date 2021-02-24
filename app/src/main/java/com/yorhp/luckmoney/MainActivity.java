package com.yorhp.luckmoney;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.yorhp.luckmoney.service.LuckMoneyService;
import com.yorhp.luckmoney.util.AccessbilityUtil;
import com.yorhp.luckmoney.util.ScreenUtil;
import com.yorhp.luckmoney.util.SharedPreferencesUtil;

import androidx.appcompat.app.AppCompatActivity;

/**
 * @author yorhp
 */
public class MainActivity extends AppCompatActivity {

    Switch swWx;
    CheckBox ckPause;
    TextView tvTime,tvOpenTime,tvDevice;
    RadioButton radiobutton1,radiobutton2,radiobutton3;
    RadioGroup radioGroup;

    /**
     * 等待红包弹出窗时间
     */
    private static final int MAX_WAIT_WINDOW_TIME=2000;
    private int REQUEST_DIALOG_PERMISSION = 1001;

    /**
     * 保存状态字段
     */
    public static final String NEED_SET_TIME="need_set_time";
    public static final String WAIT_WINDOW_TIME="waitWindowTime";
    public static final String WAIT_GET_MONEY_TIME="waitGetMoneyTime";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferencesUtil.init(getApplication());
        setContentView(R.layout.activity_main);
        ScreenUtil.getScreenSize(this);
        radioGroup = findViewById(R.id.radio);
        radiobutton1 = findViewById(R.id.radiobutton1);
        radiobutton2 = findViewById(R.id.radiobutton2);
        radiobutton3 = findViewById(R.id.radiobutton3);
        LuckMoneyService.HUMAN_LIST_TXT_ID =SharedPreferencesUtil.getString("HUMAN_LIST_TXT_ID", LuckMoneyService.HUMAN_LIST_TXT_ID);
        LuckMoneyService.AVATAR_ID = SharedPreferencesUtil.getString("AVATAR_ID",  LuckMoneyService.AVATAR_ID);
        LuckMoneyService.AUL_ID = SharedPreferencesUtil.getString("AUL_ID", LuckMoneyService.AUL_ID );
        LuckMoneyService.OPEN_ID =SharedPreferencesUtil.getString("OPEN_ID", LuckMoneyService.OPEN_ID);
        LuckMoneyService.HUMAN_LIST = SharedPreferencesUtil.getString("HUMAN_LIST", LuckMoneyService.HUMAN_LIST);
        LuckMoneyService.AUM_ID = SharedPreferencesUtil.getString("AUM_ID", LuckMoneyService.AUM_ID);
        LuckMoneyService.DETAIL_CHAT_LIST_ID =SharedPreferencesUtil.getString("DETAIL_CHAT_LIST_ID", LuckMoneyService.DETAIL_CHAT_LIST_ID);
        ckPause = findViewById(R.id.ckPause);
        swWx = findViewById(R.id.swWx);
        tvDevice=findViewById(R.id.tv_device);
        tvTime=findViewById(R.id.tv_wait_time);
        LuckMoneyService.waitWindowTime=SharedPreferencesUtil.getInt(WAIT_WINDOW_TIME,150);
        tvTime.setText(LuckMoneyService.waitWindowTime+"ms");
        tvOpenTime=findViewById(R.id.tv_wait_open_time);
        LuckMoneyService.waitGetMoneyTime=SharedPreferencesUtil.getInt(WAIT_GET_MONEY_TIME,700);
        tvOpenTime.setText(LuckMoneyService.waitGetMoneyTime+"ms");
        swWx.setOnClickListener((v) -> {
//            if (Build.VERSION.SDK_INT >= 23) {
//                if (!Settings.canDrawOverlays(this)) {
//                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
//                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    startActivityForResult(intent, 1);
//                } else {
////                    addBallView();
//                    //TODO do something you need
//                }
//            }

            startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
        });


        ckPause.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                LuckMoneyService.isPause = b;
            }
        });

        findViewById(R.id.ll_wait_time).setOnClickListener(v->{
            if(LuckMoneyService.needSetTime==0){
                Toast.makeText(MainActivity.this,"当前不可修改",Toast.LENGTH_SHORT).show();
                return;
            }
            if(LuckMoneyService.waitWindowTime<MAX_WAIT_WINDOW_TIME/4){
                LuckMoneyService.waitWindowTime=LuckMoneyService.waitWindowTime+30;
            }else {
                LuckMoneyService.waitWindowTime=0;
            }
            tvTime.setText(LuckMoneyService.waitWindowTime+"ms");
        });

        findViewById(R.id.ll_wait_open_time).setOnClickListener(v->{
            if(LuckMoneyService.needSetTime==0){
                Toast.makeText(MainActivity.this,"当前不可修改",Toast.LENGTH_SHORT).show();
                return;
            }
            if(LuckMoneyService.waitGetMoneyTime<MAX_WAIT_WINDOW_TIME){
                LuckMoneyService.waitGetMoneyTime=LuckMoneyService.waitGetMoneyTime+100;
            }else {
                LuckMoneyService.waitGetMoneyTime=0;
            }
            tvOpenTime.setText(LuckMoneyService.waitGetMoneyTime+"ms");
        });
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
               switch (checkedId){
                   case R.id.radiobutton1:
                       LuckMoneyService.isBackHome = false;
                       LuckMoneyService.isSingle =false;
                       break;
                   case R.id.radiobutton2:
                       LuckMoneyService.isBackHome = false;
                       LuckMoneyService.isSingle =true;
                       break;
                   case R.id.radiobutton3:
                       LuckMoneyService.isBackHome = true;
                       LuckMoneyService.isSingle =true;
                       break;
               }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.my_menu, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() ==R.id.action_settings){
            startActivity(new Intent(this,SettingActivity.class));
        }
        return super.onOptionsItemSelected(item);

    }

    private View mFloatBallView;
    private WindowManager mWindowManager;
    private int mScreenWidth,mScreenHeight;
    private View  mFBWindow;
    private WindowManager.LayoutParams mFBParams;
    private GestureDetector gestureDetector;
    public void addBallView() {
//        Log.e(TAG, "addBallView: " );
        if (mFloatBallView == null) {
//            Log.e(TAG, "addBallView: " );
            //获取 整个手机的宽度和高度
            DisplayMetrics dm = new DisplayMetrics();
            mWindowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
            mWindowManager.getDefaultDisplay().getMetrics(dm);
            mScreenWidth = dm.widthPixels;
            mScreenHeight = dm.heightPixels;
            //填充悬浮球 布局
            mFloatBallView = new View(this);
            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(100,100);
            mFloatBallView.setLayoutParams(layoutParams);
            mFloatBallView.setBackgroundColor(Color.BLUE);
            mFBParams = new WindowManager.LayoutParams(); //设置悬浮球布局的参数
            mFBParams.x = 540;
            mFBParams.y = 1371;
            mFBParams.width = 100;
            mFBParams.height = 100;

            mFBParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY  ;
            mFBParams.format = 1;
            //设置添加View的标识
//            mFBParams.flags = WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS |
//                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
            mFBParams.gravity = Gravity.LEFT | Gravity.TOP;
//            mFBParams.format = PixelFormat.TRANSLUCENT;//半透明
            mFBParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;//设置没有焦点不能touch,这样其他的界面才可以滑动和操作
            mWindowManager.addView(mFloatBallView, mFBParams);

            //将View 按照mFBParams参数设置,添加到mWindowManager ,mFBParams是告诉mFBWindow 的父view
             gestureDetector =new GestureDetector(this, new GestureDetector.OnGestureListener() {
                @Override
                public boolean onDown(MotionEvent e) {
                    return false;
                }

                @Override
                public void onShowPress(MotionEvent e) {

                }

                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return false;
                }

                @Override
                public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                    return false;
                }

                @Override
                public void onLongPress(MotionEvent e) {

                }

                @Override
                public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                    ViewGroup.LayoutParams layoutParams1 = mFloatBallView.getLayoutParams();
                    mFBParams.x= (int) e2.getRawX();
                    mFBParams.y= (int) e2.getRawY();
                    Log.e("main", "onFling: x == "+e2.getRawX());
                    Log.e("main", "onFling: y =="+e2.getRawY());
                    mWindowManager.updateViewLayout(mFloatBallView, mFBParams);
                    return false;
                }
            });
        }
        mFloatBallView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                gestureDetector.onTouchEvent(event);
                return false;
            }
        });
    }
    private void requestSettingCanDrawOverlays() {
        Toast.makeText(MainActivity.this, "请打开显示悬浮窗开关!", Toast.LENGTH_LONG).show();
        int sdkInt = Build.VERSION.SDK_INT;
        if (sdkInt >= Build.VERSION_CODES.O) {//8.0以上
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
            startActivityForResult(intent, REQUEST_DIALOG_PERMISSION);
        } else if (sdkInt >= Build.VERSION_CODES.M) {//6.0-8.0
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
            intent.setData(Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, REQUEST_DIALOG_PERMISSION);
        } else {//4.4-6.0一下
            //无需处理了
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
//        Log.e("main", "onResume: "+ LuckMoneyService.needSetTime);
        if(LuckMoneyService.isBackHome){
            radiobutton3.setChecked(true);
        }else if(LuckMoneyService.isSingle){
            radiobutton2.setChecked(true);
        }else if(!LuckMoneyService.isSingle){
            radiobutton1.setChecked(true);
        }
        ckPause.setChecked( LuckMoneyService.isPause);
        if(LuckMoneyService.needSetTime==-1){
            LuckMoneyService.needSetTime=SharedPreferencesUtil.getInt(NEED_SET_TIME,-1);
        }
        swWx.setChecked(AccessbilityUtil.isAccessibilitySettingsOn(this, LuckMoneyService.class));
        if(LuckMoneyService.needSetTime==1){
            tvDevice.setText("当前设备需要进行下面两项时间设置以达到最佳状态，值的大小不会影响抢红包的速度，值越大越能确保抢到红包，但是值太大返回流程可能会出问题，无法继续抢下一个");
        }else if(LuckMoneyService.needSetTime==0){
            tvDevice.setText("当前设备不需要关心下面两项设置");
        }
        SharedPreferencesUtil.save(NEED_SET_TIME,LuckMoneyService.needSetTime);
    }

    @Override
    protected void onStop() {
        super.onStop();
        SharedPreferencesUtil.save(WAIT_WINDOW_TIME,LuckMoneyService.waitWindowTime);
        SharedPreferencesUtil.save(WAIT_GET_MONEY_TIME,LuckMoneyService.waitGetMoneyTime);
    }
}
