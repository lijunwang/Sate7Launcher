package com.sate7.sate7launcher;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.provider.CallLog.Calls;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sate7.sate7launcher.uitl.LocationManagerUtil;
import com.sate7.sate7launcher.uitl.LunarCalendar;
import com.sate7.sate7launcher.uitl.StatusBarUtil;

import java.util.Calendar;
import java.util.TimeZone;

public class LocksScreenActivity extends Activity implements View.OnClickListener {

    private static final String TAG = "LocksScreenActivity";

    private SmsMmsContentObserver smsMmsContentObserver;
    private TextView TimeLocationTV;
    private TextView LocationTV;
    private TextView CallNumTV;
    private TextView SmsNumTV;
    private TextView YearTV;
    private TextView DayTV;
    private TextView WeekTV;
    private TextView ChinaDayTV;
    private static int SMS_NUM = 0, PHONE_NUM = 0;

    private String year, day, week, chinaDay;
    private int month;
    private String Location;
    private ViewGroup LockMain;
    Calendar cal;
    LocationManagerUtil mLocationManagerUtil;
    String[] weeks;
    String[] months;

    //{"星期天","星期一","星期二","星期三","星期四","星期五","星期六"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);// 隐藏标
        Resources res = getResources();
        weeks = res.getStringArray(R.array.week);
        months = res.getStringArray(R.array.month);
        setContentView(R.layout.activity_locks_screen);
        StatusBarUtil.setRootViewFitsSystemWindows(this, false);
        StatusBarUtil.setTranslucentStatus(this);
        TimeLocationTV = (TextView) findViewById(R.id.tv_time_location);
        LocationTV = (TextView) findViewById(R.id.tv_location);
        CallNumTV = (TextView) findViewById(R.id.tv_call_num);
        SmsNumTV = (TextView) findViewById(R.id.tv_sms_num);

        DayTV = (TextView) findViewById(R.id.tv_day);
        WeekTV = (TextView) findViewById(R.id.tv_week);
        ChinaDayTV = (TextView) findViewById(R.id.tv_china_day);
        YearTV = (TextView) findViewById(R.id.tv_year);
        findViewById(R.id.iv_call).setOnClickListener(this);
        findViewById(R.id.iv_sms).setOnClickListener(this);
        String timeloction = TimeZone.getDefault().getDisplayName(false, TimeZone.LONG);
        TimeLocationTV.setText(timeloction);
        getDate();
        YearTV.setText(year + " " + getResources().getString(R.string.year) + " " + months[month - 1]);
        DayTV.setText(day);
        ChinaDayTV.setText("农历 " + chinaDay);
        WeekTV.setText(week);
        getContentResolver().registerContentObserver(Uri.parse("content://sms"), true, new SmsMmsContentObserver());
        getContentResolver().registerContentObserver(Uri.parse("content://call_log/calls"), true, new ContactsContentObserver());

        mLocationManagerUtil = new LocationManagerUtil();
        mLocationManagerUtil.init(this);
        mLocationManagerUtil.registerLocationUpdate(locationListener);
        mLocationManagerUtil.getLocaitonInfo();
        Location = mLocationManagerUtil.getLocaitonInfo();
        LocationTV.setText(Location);
        LockMain = (ViewGroup) findViewById(R.id.lock_main);
        LockMain.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                finish();
                overridePendingTransition(R.anim.out, R.anim.in);
                return false;
            }
        });

        soundIntent.putExtra(LOCK, true);
        sendBroadcast(soundIntent);
    }

    /*@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_F6) {
            finish();
            overridePendingTransition(R.anim.out, R.anim.in);
        }
        return super.onKeyDown(keyCode, event);
    }*/

    private void adjustStatusBarLocked() {

        Class mStatusBarManager = null;
        try {
            mStatusBarManager = Class.forName("android.app.StatusBarManager");

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        //acquireWakeLock();
        UpdateUnAnsweredCalls(this);
        UpdateUnreadMmsSms(this);
       /* if(SMS_NUM==0){
            SmsNumTV.setVisibility(View.GONE);
        }else{
            SmsNumTV.setVisibility(View.VISIBLE);
            SmsNumTV.setText(SMS_NUM);
        }
        if(PHONE_NUM==0){
            CallNumTV.setVisibility(View.GONE);
        }else{
            CallNumTV.setVisibility(View.VISIBLE);
            CallNumTV.setText(PHONE_NUM);
        }*/
        IntentFilter screenOffFilter = new IntentFilter();
        screenOffFilter.addAction("android.intent.action.DATE_CHANGED");
        screenOffFilter.addAction("android.intent.action.TIMEZONE_CHANGED");
        registerReceiver(dateTimeReceiver, screenOffFilter);
    }

    public void getDate() {
        cal = Calendar.getInstance();
        year = String.valueOf(cal.get(Calendar.YEAR));
        month = cal.get(Calendar.MONTH) + 1;
        day = String.valueOf(cal.get(Calendar.DATE));
        LunarCalendar lunarCalendar = LunarCalendar.getInstance();
        lunarCalendar.set(year + "年" + month + "月" + day + "日");
        chinaDay = lunarCalendar.getLunarMonth() + " " + lunarCalendar.getLunarDate();
        week = weeks[cal.get(Calendar.DAY_OF_WEEK) - 1];


    }

    private static final String V1_7SATE = "com.7sate.play";
    private static final String LOCK = "lock";
    private Intent soundIntent = new Intent(V1_7SATE);

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLocationManagerUtil.removeLocationUpdate(locationListener);
        unregisterReceiver(dateTimeReceiver);
        //releaseWakeLock();
        //play sound,see at Keyguard KeyguardViewMediator.java

        soundIntent.putExtra(LOCK, false);
        sendBroadcast(soundIntent);
    }

    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    if (SMS_NUM == 0) {
                        SmsNumTV.setVisibility(View.GONE);
                    } else {
                        SmsNumTV.setVisibility(View.VISIBLE);
                        SmsNumTV.setText(SMS_NUM + "");
                    }
                    break;
                case 2:
                    Log.e(TAG, "PHONE_NUM CallNumTV 2 " + PHONE_NUM);
                    if (PHONE_NUM == 0) {
                        CallNumTV.setVisibility(View.GONE);
                    } else {
                        CallNumTV.setVisibility(View.VISIBLE);
                        CallNumTV.setText(PHONE_NUM + "");
                    }
                case 3:
                    String timeloction = TimeZone.getDefault().getDisplayName(false, TimeZone.LONG);
                    TimeLocationTV.setText(timeloction);
                    break;
                case 4:
                    getDate();
                    YearTV.setText(year + "年 " + month + "月");
                    DayTV.setText(day);
                    ChinaDayTV.setText("农历 " + chinaDay);
                    WeekTV.setText(week);
                    break;
                case 5:
                    Location = mLocationManagerUtil.getLocaitonInfo();
                    LocationTV.setText(Location);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_call:
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_DIAL);
//                intent.setAction(Intent.ACTION_VIEW);
//                intent.setType(Calls.CONTENT_TYPE);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.out, R.anim.in);
                break;
            case R.id.iv_sms:
                Intent sintent = new Intent(Intent.ACTION_MAIN);
                sintent.addCategory(Intent.CATEGORY_DEFAULT);
                sintent.setType("vnd.android-dir/mms-sms");
                startActivity(sintent);
                finish();
                overridePendingTransition(R.anim.out, R.anim.in);

                break;
            default:
                break;
        }
    }

    private class SmsMmsContentObserver extends ContentObserver {

        public SmsMmsContentObserver() {
            super(new Handler());
        }

        @Override
        public void onChange(boolean selfChange) {
            Log.i(TAG, "SmsMmsContentObserver , onChange");
            // 大数据操作 在线程中 进行
            mHandler.post(new Runnable() {

                @Override
                public void run() {
                    UpdateUnreadMmsSms(getApplicationContext());
                    // mHandler.removeMessages(1);
                    //mHandler.sendEmptyMessage(1);
                }
            });
            super.onChange(selfChange);
        }
    }

    private class ContactsContentObserver extends ContentObserver {

        public ContactsContentObserver() {
            super(new Handler());
        }

        @Override
        public void onChange(boolean selfChange) {
            Log.i(TAG, "ContactsContentObserver , onChange");
            // 大数据操作 在线程中 进行
            mHandler.post(new Runnable() {

                @Override
                public void run() {
                    UpdateUnAnsweredCalls(getApplicationContext());
                    // mHandler.removeMessages(2);
                    // mHandler.sendEmptyMessage(2);
                }
            });
            super.onChange(selfChange);
        }
    }


    /**
     * 获取 未读短信数量
     *
     * @param context
     * @return
     */
    public void UpdateUnreadMmsSms(Context context) {
        Cursor cur = null;

        try {
            cur = context.getContentResolver().query(
                    Uri.parse("content://l-message_summary"),
                    null, "known = 0 and read = 0", null, null);
            if (null != cur) {
                SMS_NUM = cur.getCount();
            }
            if (SMS_NUM > 0) mHandler.removeMessages(1);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cur != null) {
                cur.close();
            }
        }

    }

    /**
     * 获取 未接电话
     *
     * @param context
     * @return
     */
    @SuppressLint("MissingPermission")
    public void UpdateUnAnsweredCalls(Context context) {
        Cursor cur = null;

        try {
            cur = context.getContentResolver().query(Calls.CONTENT_URI, null,
                    "type = 3 and new = 1", null, null);
            if (null != cur) {
                PHONE_NUM = cur.getCount();

            }
            if (PHONE_NUM > 0) {
                //mHandler.removeMessages(2);
                mHandler.sendEmptyMessage(2);
            }
            Log.e(TAG, "PHONE_NUM " + PHONE_NUM);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cur != null) {
                cur.close();
            }
        }

    }

    private BroadcastReceiver dateTimeReceiver = new BroadcastReceiver() {


        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if ("android.intent.action.DATE_CHANGED".equals(action)) {
                mHandler.removeMessages(4);
                mHandler.sendEmptyMessage(4);
            } else if ("android.intent.action.TIMEZONE_CHANGED".equals(action)) {
                mHandler.removeMessages(3);
                mHandler.sendEmptyMessage(3);
            }
        }
    };


    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            // TODO Auto-generated method stub
            mHandler.removeMessages(5);
            mHandler.sendEmptyMessage(5);
        }

        @Override
        public void onProviderDisabled(String arg0) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onProviderEnabled(String arg0) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
            // TODO Auto-generated method stub

        }

    };
    private PowerManager.WakeLock mWakeLock;

    public void acquireWakeLock() {
        if (mWakeLock == null) {
            PowerManager pm = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.ON_AFTER_RELEASE, "SCREEN");
            if (mWakeLock != null) {
                mWakeLock.acquire();
                Log.e(TAG, "get powermanager wakelock!");
            }
        }
    }

    public void releaseWakeLock() {
        if (mWakeLock != null) {
            mWakeLock.release();
            Log.e(TAG, "release powermanager wakelock!");
        }
    }
}
