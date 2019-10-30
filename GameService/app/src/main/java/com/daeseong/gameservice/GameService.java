package com.daeseong.gameservice;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.SortedMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;

public class GameService extends Service {

    private static final String TAG = GameService.class.getSimpleName();

    public static Intent serviceIntent = null;

    private TimerTask timerTask = null;
    private Timer timer = null;

    private HashMap<String, gameinfo> gameinfoMap = new HashMap<>();

    private String lastpackagename;
    private String lastStarttm;

    @Override
    public void onCreate() {

        super.onCreate();

        initData();
    }

    private void PreferencesInfo(boolean bload){

        try{

            if(bload) {
                lastpackagename = (String) Preferences_util.getValue(this, "itemID", "");
                lastStarttm = (String) Preferences_util.getValue(this, "itemTime", "");
                Log.e(TAG, "get lastpackagename:" + lastpackagename + "  lastStarttm:" + lastStarttm);
            }else {
                Preferences_util.setValue(this, "itemID", lastpackagename);
                Preferences_util.setValue(this, "itemTime", lastStarttm);
                Log.d(TAG, "set lastpackagename:" + lastpackagename + "  lastStarttm:" + lastStarttm);
            }

        }catch (Exception ex){
            Log.e(TAG, ex.getMessage().toString());
        }

    }

    private void PreferencesInit(){

        try{
            lastpackagename = "";
            lastStarttm = "";
            Preferences_util.setValue(this, "itemID", lastpackagename);
            Preferences_util.setValue(this, "itemTime", lastStarttm);
            Log.d(TAG, "set lastpackagename:" + lastpackagename + "  lastStarttm:" + lastStarttm);
        }catch (Exception ex){
            Log.e(TAG, ex.getMessage().toString());
        }

    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d(TAG, "onStartCommand");

        serviceIntent = intent;

        PreferencesInfo(true);

        startTimer();

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        serviceIntent = null;

        Log.d(TAG, "onDestroy");

        PreferencesInfo(false);

        closeTimer();

        handler.removeMessages(0);

        //서비스 재시작
        sendBroadcast(new Intent("ACTION.RestartService"));
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @SuppressLint("NewApi")
    public void startTimer(){

        try {

            timerTask = new TimerTask() {
                @Override
                public void run() {
                    getListRunPackageName();
                }
            };
            timer = new Timer();
            timer.schedule(timerTask, 0, 10000);

        }catch (Exception ex){
            Log.e(TAG, "startTimer:" + ex.getMessage().toString());
        }
    }

    private void closeTimer(){

        try {

            if (timerTask != null) {
                timerTask.cancel();
                timerTask = null;
            }

            if (timer != null) {
                timer.cancel();
                timer.purge();
                timer = null;
            }

        }catch (Exception ex){
            Log.e(TAG, "stopTimer:" + ex.getMessage().toString());
        }
    }

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {

            try {
                Toast.makeText(getApplicationContext(), msg.obj.toString(), Toast.LENGTH_SHORT).show();
            }catch (Exception ex){
                Log.e(TAG, "Toast" + ex.getMessage().toString());
            }
            return true;
        }
    });

    private void initData(){

        Log.e(TAG, "initData");

        try {

            if (isNetworkAvailable(this)) {
                DownloadJson downloadJson = new DownloadJson();
                downloadJson.execute();
            }
        }catch (Exception ex){
            Log.e(TAG, ex.getMessage().toString());
        }

        //sample
        //iteminfo.getInstance().setGameItem("com.kakaogames.moonlight");
        //iteminfo.getInstance().setGameItem("com.google.android.youtube");
    }

    private void getListRunPackageName() {

        try {

            UsageStatsManager usageStatsManager = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);
            long time = System.currentTimeMillis();
            List<UsageStats> usageStats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_BEST, 0, time);
            if (usageStats != null) {

                SortedMap<Long, UsageStats> runningTask = new TreeMap<Long, UsageStats>();
                for (UsageStats item : usageStats) {
                    runningTask.put(item.getLastTimeUsed(), item);
                }

                if (runningTask != null && !runningTask.isEmpty()) {

                    if (!lastpackagename.equals(runningTask.get(runningTask.lastKey()).getPackageName())) {

                        if (gameinfoMap.containsKey(lastpackagename)) {
                            String sLog = "";
                            sLog = String.format("종료된 앱:%s  시작시간:%s  끝시간:%s  ", gameinfoMap.get(lastpackagename).getPackagename(), gameinfoMap.get(lastpackagename).getStarttm(), gameinfoMap.get(lastpackagename).getEndtm());
                            Log.e(TAG, sLog);

                            Message msg = handler.obtainMessage();
                            msg.what = 0;
                            msg.obj = sLog;
                            handler.sendMessage(msg);

                            //제거
                            gameinfoMap.remove(lastpackagename);
                            PreferencesInit();
                        }
                    }

                    //항목에 있는 패키지명 등록
                    if (iteminfo.getInstance().isGameItem(runningTask.get(runningTask.lastKey()).getPackageName())) {

                        String packagename = runningTask.get(runningTask.lastKey()).getPackageName();

                        if (!gameinfoMap.containsKey(packagename)) {

                            gameinfoMap.put(packagename, new gameinfo(packagename, getTimeDate(), getTimeDate()));

                            String sLog = "";
                            sLog = String.format("시작된 앱이름:%s  시작시간:%s  끝시간:%s  ", packagename, gameinfoMap.get(packagename).getStarttm(), gameinfoMap.get(packagename).getEndtm());
                            Log.e(TAG, sLog);

                            Message msg = handler.obtainMessage();
                            msg.what = 0;
                            msg.obj = sLog;
                            handler.sendMessage(msg);

                            lastpackagename = packagename;
                            lastStarttm = gameinfoMap.get(packagename).getStarttm();

                        } else {
                            String starttime = gameinfoMap.get(packagename).getStarttm();
                            gameinfoMap.put(packagename, new gameinfo(packagename, starttime, getTimeDate()));

                            String sLog = "";
                            sLog = String.format("업데이트된 앱이름:%s  시작시간:%s  끝시간:%s  ", packagename, gameinfoMap.get(packagename).getStarttm(), gameinfoMap.get(packagename).getEndtm());
                            Log.e(TAG, sLog);

                            Message msg = handler.obtainMessage();
                            msg.what = 0;
                            msg.obj = sLog;
                            handler.sendMessage(msg);

                            lastpackagename = packagename;
                            lastStarttm = starttime;
                        }

                    } else {

                        if (gameinfoMap.containsKey(lastpackagename)) {
                            String sLog = "";
                            sLog = String.format("종료된 앱:%s  시작시간:%s  끝시간:%s  ", gameinfoMap.get(lastpackagename).getPackagename(), gameinfoMap.get(lastpackagename).getStarttm(), gameinfoMap.get(lastpackagename).getEndtm());
                            Log.e(TAG, sLog);

                            Message msg = handler.obtainMessage();
                            msg.what = 0;
                            msg.obj = sLog;
                            handler.sendMessage(msg);

                            //제거
                            gameinfoMap.remove(lastpackagename);
                            PreferencesInit();
                        }

                    }

                }

            }

        }catch (Exception ex){
            Log.e(TAG, ex.getMessage().toString());
        }

    }

    private static String getTimeDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        return dateFormat.format(new Date());
    }

    private static String getTimeDate(long date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        return dateFormat.format(new Date(date));
    }

    private static boolean isNetworkAvailable(Context context)
    {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()){
            return true;
        }
        return false;
    }

}