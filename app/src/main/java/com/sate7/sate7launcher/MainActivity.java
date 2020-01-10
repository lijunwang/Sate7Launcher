package com.sate7.sate7launcher;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.WallpaperManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.GridView;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.SynthesizerListener;
import com.iflytek.cloud.util.ResourceUtil;
import com.sate7.sate7launcher.uitl.StatusBarUtil;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MainActivity extends Activity implements AdapterView.OnItemClickListener, TextToSpeech.OnInitListener {
    private final String TAG = "MainActivity";
    private List<ResolveInfo> mApps = new ArrayList<ResolveInfo>();
    GridView mGrid;
    private MyBroadcastReceiver mMyBroadcastReceiver;
    private static final String ACTION_SCREEN_OFF = Intent.ACTION_SCREEN_OFF;
    private static final String ACTION_TIME_TICK = Intent.ACTION_TIME_TICK;
    private static final String ACTION_PACKAGE_ADDED = "android.intent.action.PACKAGE_ADDED";
    private static final String ACTION_PACKAGE_REMOVED = "android.intent.action.PACKAGE_REMOVED";
    private static final String ACTION_PACKAGE_REPLACED = "android.intent.action.PACKAGE_REPLACED";
    private TextToSpeech textToSpeech; // TTS对象
    private AppsAdapter mAppsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);// 隐藏标题
        setContentView(R.layout.activity_main);
//        SpeechUtility.createUtility(this, SpeechConstant.APPID + "=5cf5d9c6");
        SpeechUtility.createUtility(this, SpeechConstant.APPID + "=5d3158d7");
        StatusBarUtil.setRootViewFitsSystemWindows(this, false);
        StatusBarUtil.setTranslucentStatus(this);
//        setWallpaper(this);
        loadApps();
        mAppsAdapter = new AppsAdapter(this, mApps);

        mGrid = (GridView) findViewById(R.id.apps_list);
        mGrid.setAdapter(mAppsAdapter);
        mGrid.setOnItemClickListener(this);
        mMyBroadcastReceiver = new MyBroadcastReceiver();
        IntentFilter mIntentFilter = new IntentFilter();

        mIntentFilter.addAction(ACTION_PACKAGE_ADDED);
        mIntentFilter.addAction(ACTION_PACKAGE_REMOVED);
        mIntentFilter.addAction(ACTION_PACKAGE_REPLACED);
        mIntentFilter.addDataScheme("package");
        registerReceiver(mMyBroadcastReceiver, mIntentFilter);
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(ACTION_SCREEN_OFF);
        mIntentFilter.addAction(ACTION_TIME_TICK);
        registerReceiver(mMyBroadcastReceiver, mIntentFilter);
        Log.d(TAG, "onCreate 22ww... ");
        textToSpeech = new TextToSpeech(this, this);

        setParam();
        powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        rebootDialog = new AlertDialog.Builder(this).
                setTitle(R.string.reboot_title).
                setMessage(R.string.reboot_message).
                setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (changeToDialMode()) {
                            powerManager.reboot(null);
                        }
                    }
                }).
                setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).
                create();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy 22ww... ");
        unregisterReceiver(mMyBroadcastReceiver);
        if (null != mTts) {
            mTts.stopSpeaking();
            // 退出时释放连接
            mTts.destroy();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
       /* if (mMyBroadcastReceiver == null) mMyBroadcastReceiver = new MyBroadcastReceiver();
        IntentFilter mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(ACTION_SCREEN_OFF);
        mIntentFilter.addAction(ACTION_TIME_TICK);
        mIntentFilter.addAction(ACTION_PACKAGE_ADDED);
        mIntentFilter.addAction(ACTION_PACKAGE_REMOVED);
        mIntentFilter.addAction(ACTION_PACKAGE_REPLACED);
        mIntentFilter.addDataScheme("package");
        registerReceiver(mMyBroadcastReceiver, mIntentFilter);*/
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGrid.setFocusable(false);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //Log.d("MainActivity", "onKeyDown keyCode = "+keyCode);
        return super.onKeyDown(keyCode, event);
    }

    private void loadApps() {
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> mInstallAppsList = getPackageManager().queryIntentActivities(mainIntent, 0);
        List<ResolveInfo> mSortAppList = new ArrayList<>(10);
        if (getResources().getBoolean(R.bool.sort_open)) {
            int index;
            int size = mInstallAppsList.size();
            ResolveInfo info;
            mApps.clear();
            //store the apps as the order of array in xml;
            String[] sortOder = getResources().getStringArray(R.array.pkgOrder);
            for (String pkg : sortOder) {
                for (index = 0; index < size; index++) {
                    info = mInstallAppsList.get(index);
                    if (pkg.equals(info.activityInfo.packageName)) {
                        mApps.add(mInstallAppsList.get(index));
                        mSortAppList.add(mInstallAppsList.get(index));
                    }
                }
            }
            //add the rest apps
            mInstallAppsList.removeAll(mSortAppList);
            for (ResolveInfo resolveInfo : mInstallAppsList) {
                //remove launcher and xunfei
                if (!resolveInfo.activityInfo.packageName.equals("com.iflytek.inputmethod") &&
                        !resolveInfo.activityInfo.packageName.equals("com.sate7.sate7launcher")) {
                    mApps.add(resolveInfo);
                }
            }
            return;
        }

        mApps = getPackageManager().queryIntentActivities(mainIntent, 0);
        for (int i = 0; i < mApps.size(); i++) {
            ResolveInfo resolveInfo = mApps.get(i);
            String PackageName = resolveInfo.activityInfo.packageName;
            Log.e("MainActivity", "load ... " + PackageName);
            if ("com.iflytek.inputmethod".equals(PackageName)) {
                mApps.remove(i);
            }
            if ("com.sate7.sate7launcher".equals(PackageName)) {
                mApps.remove(i);
            }
        }

    }

    public void onBackPressed() {
        //super.onBackPressed();
        System.out.println("按下了back键   onBackPressed()");
    }

    private AlertDialog rebootDialog;
    private PowerManager powerManager;
    private ConnectivityManager connectivityManager;

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ResolveInfo info = (ResolveInfo) mApps.get(position);

        //该应用的包名
        String pkg = info.activityInfo.packageName;
        //应用的主activity类
        String cls = info.activityInfo.name;

        ComponentName componet = new ComponentName(pkg, cls);
        if (pkg.equals("com.android.dialer") && inNetMode()) {
            rebootDialog.show();
            return;
        }
        Intent i = new Intent();
        i.setComponent(componet);
        startActivity(i);
    }

    private boolean inNetMode() {
        return Settings.Global.getInt(getContentResolver(), Settings.Global.DATA_ROAMING, 0) == 1;
    }

    private boolean changeToDialMode() {
        try {
            Method setMobileDataEnabled = connectivityManager.getClass().getDeclaredMethod("setMobileDataEnabled", boolean.class);
            setMobileDataEnabled.setAccessible(true);
            setMobileDataEnabled.invoke(connectivityManager, false);
            return true;
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            Log.d(TAG, "changeToDialMode Exception ... " + e.getMessage());
            return false;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            Log.d(TAG, "changeToDialMode IllegalAccessException ... " + e.getMessage());
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            Log.d(TAG, "changeToDialMode InvocationTargetException ... " + e.getMessage());
        }
        return false;
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int result = textToSpeech.setLanguage(Locale.CHINA);
            textToSpeech.setPitch(1.2f);
            //设定语速 ，默认1.0正常语速
            textToSpeech.setSpeechRate(0.9f);
            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("MainActivity", "TTS onInit unsport ..");
                //Toast.makeText(this, "数据丢失或不支持", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private final boolean TEST_TTS = false;

    private class MyBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            //Log.e("MainActivity", "MyBroadcastReceiver action" + action);
            //Log.e("MainActivity", "MyBroadcastReceiver value" + value);
            if (ACTION_SCREEN_OFF.equals(action)) {
                Intent i1 = new Intent(context, LocksScreenActivity.class);
                i1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i1);

            } else if (ACTION_TIME_TICK.equals(action)) {
                String value = getProperty("persist.sys.is_speech_time");
                //Log.e("MainActivity", "BroadcastReceiver value=" + value);
                if (!"0".equals(value)) {
                    Calendar cal = Calendar.getInstance();
                    int hour = cal.get(Calendar.HOUR_OF_DAY);
                    int min = cal.get(Calendar.MINUTE);

                    String[] time = value.split("a");
                    int start = Integer.parseInt(time[0]);
                    int end = Integer.parseInt(time[1]);
                    Log.d("MainActivity", "TTL BroadcastReceiver so hour=" + hour + " min=" + min + " start=" + start + " end=" + end + "," + mTts);
                    if (TEST_TTS) {
//                        textToSpeech.stop();
//                        textToSpeech.speak("现在时间" + hour + "点" + min + "分", TextToSpeech.QUEUE_ADD, null);
                        if (mTts != null) {
                            mTts.startSpeaking("现在时间" + hour + "点" + min + "分", mTtsListener);
                        }
                        return;
                    }
                    if (hour >= start && hour <= end) {
                        if (min == 0) {
//                            textToSpeech.stop();
//                            textToSpeech.speak("现在时间" + hour + "点整", TextToSpeech.QUEUE_ADD, null);
                            if (mTts != null) {
                                mTts.startSpeaking("现在时间" + hour + "点整", mTtsListener);
                            }
                        } else if (min == 30) {
//                            textToSpeech.stop();
//                            textToSpeech.speak("现在时间" + hour + "点三十分", TextToSpeech.QUEUE_ADD, null);
                            if (mTts != null) {
                                mTts.startSpeaking("现在时间" + hour + "点三十分", mTtsListener);
                            }
                        }
                    }
                }
            } else if (ACTION_PACKAGE_REMOVED.equals(action)) {
                String packageName = intent.getData()
                        .getSchemeSpecificPart();
                Log.d("MainActivity", "delete packageName =" + packageName);
                removePackage(packageName);
                mAppsAdapter.notifyDataSetChanged();
            } else if (ACTION_PACKAGE_ADDED.equals(action)) {
                String packageName = intent.getData()
                        .getSchemeSpecificPart();
                Log.d("MainActivity", "add packageName 22 = " + packageName);
                findActivitiesForPackage(packageName);
                loadApps();
                mAppsAdapter.notifyDataSetChanged();
            }
        }
    }

    private boolean is24Hour() {
        return DateFormat.is24HourFormat(this);
    }

    @SuppressLint("ResourceType")
    public static void setWallpaper(Context context) {
        try {
            WallpaperManager mWallpaperManager = WallpaperManager.getInstance(context);
            if (mWallpaperManager != null) {
//                mWallpaperManager.setBitmap(BitmapFactory.decodeResource(context.getResources(), R.mipmap.launcher_bg), null, true,
//                        WallpaperManager.FLAG_LOCK | WallpaperManager.FLAG_SYSTEM);
                mWallpaperManager.setResource(R.mipmap.launcher_bg);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    protected void onStop() {
        super.onStop();
       /* textToSpeech.stop(); // 不管是否正在朗读TTS都被打断
        textToSpeech.shutdown(); // 关闭，释放资源*/
    }

    public static String getProperty(String key) {
        String ret = "0";
        try {

            @SuppressWarnings("rawtypes")
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class);
            ret = (String) get.invoke(c, key);

        } catch (IllegalArgumentException iAE) {
            throw iAE;
        } catch (Exception e) {
            ret = "0";

        }
        return ret;
    }

    /**
     * 根据包名寻找应用
     *
     * @param packageName 包名
     * @return
     */
    private void findActivitiesForPackage(String packageName) {
        PackageManager packageManager = getPackageManager();
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        mainIntent.setPackage(packageName);
        List<ResolveInfo> apps = packageManager.queryIntentActivities(mainIntent, 0);
        Log.d(TAG, "findActivitiesForPackage 11... " + packageName);
        for (int i = 0; i < apps.size(); i++) {
            Log.d(TAG, "findActivitiesForPackage 22 ... " + i);
            ResolveInfo resolveInfo = apps.get(i);
            mApps.add(resolveInfo);
        }
    }


    private void removePackage(String mp) {
        for (int i = 0; i < mApps.size(); i++) {
            ResolveInfo resolveInfo = mApps.get(i);
            String PackageName = resolveInfo.activityInfo.packageName;
            Log.e("MainActivity", "PackageName =" + PackageName);
            if (mp.equals(PackageName)) {
                mApps.remove(i);

            }
        }
    }

    //add by wlj for another method to TTS start
    // 语音合成对象
    private SpeechSynthesizer mTts;
    // 默认本地发音人
//    public final String voicerLocal = "xiaoyan";
    public final String voicerLocal = "xiaofeng";

    /**
     * 参数设置
     */
    private void setParam() {
        Log.d(TAG, "start setParam ...");
        mTts = SpeechSynthesizer.createSynthesizer(this, mTtsInitListener);
        if (mTts == null) {
            return;
        }
        // 清空参数
        mTts.setParameter(SpeechConstant.PARAMS, null);
        //设置合成
        //设置使用本地引擎
        mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_LOCAL);
        //设置发音人资源路径
        mTts.setParameter(ResourceUtil.TTS_RES_PATH, getResourcePath());
        //设置发音人
        mTts.setParameter(SpeechConstant.VOICE_NAME, voicerLocal);
        //mTts.setParameter(SpeechConstant.TTS_DATA_NOTIFY,"1");//支持实时音频流抛出，仅在synthesizeToUri条件下支持
        //设置合成语速
        mTts.setParameter(SpeechConstant.SPEED, "50");
        //设置合成音调
        mTts.setParameter(SpeechConstant.PITCH, "50");
        //设置合成音量
        mTts.setParameter(SpeechConstant.VOLUME, "100");
        //设置播放器音频流类型
        mTts.setParameter(SpeechConstant.STREAM_TYPE, "3");
        // 设置播放合成音频打断音乐播放，默认为true
        mTts.setParameter(SpeechConstant.KEY_REQUEST_FOCUS, "true");
        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
//        mTts.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
//        mTts.setParameter(SpeechConstant.TTS_AUDIO_PATH, Environment.getExternalStorageDirectory() + "/msc/tts.wav");
    }

    //获取发音人资源路径
    private String getResourcePath() {
        StringBuffer tempBuffer = new StringBuffer();
        //合成通用资源
        tempBuffer.append(ResourceUtil.generateResourcePath(this, ResourceUtil.RESOURCE_TYPE.assets, "tts/common.jet"));
        tempBuffer.append(";");
        //发音人资源
        tempBuffer.append(ResourceUtil.generateResourcePath(this, ResourceUtil.RESOURCE_TYPE.assets, "tts/" + voicerLocal + ".jet"));
        return tempBuffer.toString();
    }

    /**
     * 初始化监听。
     */
    private InitListener mTtsInitListener = new InitListener() {
        @Override
        public void onInit(int code) {
            Log.d(TAG, "InitListener init() code = " + code);
            if (code != ErrorCode.SUCCESS) {
                Log.d(TAG, "onInit failed ...");
//                showTip("初始化失败,错误码：" + code + ",请点击网址https://www.xfyun.cn/document/error-code查询解决方案");
            } else {
                Log.d(TAG, "onInit success...");
                // 初始化成功，之后可以调用startSpeaking方法
                // 注：有的开发者在onCreate方法中创建完合成对象之后马上就调用startSpeaking进行合成，
                // 正确的做法是将onCreate中的startSpeaking调用移至这里
            }
        }
    };
    /**
     * 合成回调监听。
     */
    private SynthesizerListener mTtsListener = new SynthesizerListener() {
        @Override
        public void onSpeakBegin() {
        }

        @Override
        public void onSpeakPaused() {
        }

        @Override
        public void onSpeakResumed() {
        }

        @Override
        public void onBufferProgress(int percent, int beginPos, int endPos,
                                     String info) {
            // 合成进度
        }

        @Override
        public void onSpeakProgress(int percent, int beginPos, int endPos) {
            // 播放进度
        }

        @Override
        public void onCompleted(SpeechError error) {
            if (error == null) {
            } else if (error != null) {
            }
        }

        @Override
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
        }
    };
    //add by wlj for another method to TTS end

}
