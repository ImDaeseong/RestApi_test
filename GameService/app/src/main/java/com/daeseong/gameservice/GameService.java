package com.daeseong.gameservice;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.PendingIntent;
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
import android.os.SystemClock;
import android.text.TextUtils;
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

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d(TAG, "onStartCommand");

        lastpackagename = (String)Preferences_util.getValue(this, "itemID", "");
        lastStarttm = (String)Preferences_util.getValue(this, "itemTime", "");
        Log.d(TAG, "onStartCommand lastpackagename:" + lastpackagename + "  lastStarttm:" + lastStarttm);

        startTimer();

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.d(TAG, "onDestroy");

        Preferences_util.setValue(this, "itemID", lastpackagename);
        Preferences_util.setValue(this, "itemTime", lastStarttm);
        Log.d(TAG, "onDestroy lastpackagename:" + lastpackagename + "  lastStarttm:" + lastStarttm);

        closeTimer();

        handler.removeMessages(0);

        registerAlarm();
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

    public void registerAlarm() {

        Intent intent = new Intent(GameService.this, RestartService.class);
        intent.setAction("ACTION.RestartService");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(GameService.this, 0, intent, 0);
        long elapsedRealtime = SystemClock.elapsedRealtime();
        elapsedRealtime += 1*1000;
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        //알람 등록
        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, elapsedRealtime, 10*1000, pendingIntent);
    }

    public void unregisterAlarm() {

        Intent intent = new Intent(GameService.this, RestartService.class);
        intent.setAction("ACTION.RestartService");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(GameService.this, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
        //알람 취소
        alarmManager.cancel(pendingIntent);
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

        if (isNetworkAvailable(this)) {
            DownloadJson downloadJson = new DownloadJson();
            downloadJson.execute();
        }

    }

    private void getListRunPackageName() {

        UsageStatsManager usageStatsManager = (UsageStatsManager)getSystemService(Context.USAGE_STATS_SERVICE);
        long time = System.currentTimeMillis();
        List<UsageStats> usageStats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_BEST, 0, time);
        if (usageStats != null){

            SortedMap<Long, UsageStats> runningTask = new TreeMap<Long,UsageStats>();
            for (UsageStats item : usageStats) {
                runningTask.put(item.getLastTimeUsed(), item);
            }

            if (runningTask != null && !runningTask.isEmpty()){

                //항목에 있는 패키지명 등록
                if(iteminfo.getInstance().isGameItem(runningTask.get(runningTask.lastKey()).getPackageName())) {

                    String packagename = runningTask.get(runningTask.lastKey()).getPackageName();

                    if(!gameinfoMap.containsKey(packagename)){

                        if(TextUtils.isEmpty(lastStarttm)){

                            Log.e(TAG, "시작시간 존재");
                            gameinfoMap.put(packagename, new gameinfo(packagename, getTimeDate(), getTimeDate()) );
                        }else {

                            Log.e(TAG, "시작시간 미존재");
                            gameinfoMap.put(packagename, new gameinfo(packagename, lastStarttm, getTimeDate()) );
                        }

                        String sLog = "";
                        sLog = String.format("시작된 앱이름:%s  시작시간:%s  끝시간:%s  ", packagename,  gameinfoMap.get(packagename).getStarttm(), gameinfoMap.get(packagename).getEndtm());
                        Log.e(TAG, sLog);

                        Message msg = handler.obtainMessage();
                        msg.what = 0;
                        msg.obj = sLog;
                        handler.sendMessage(msg);

                        lastpackagename = packagename;
                        lastStarttm = gameinfoMap.get(packagename).getStarttm();

                    } else {
                        String starttime =  gameinfoMap.get(packagename).getStarttm();
                        gameinfoMap.put(packagename, new gameinfo(packagename, starttime, getTimeDate()) );

                        String sLog = "";
                        sLog = String.format("업데이트된 앱이름:%s  시작시간:%s  끝시간:%s  ", packagename,  gameinfoMap.get(packagename).getStarttm(), gameinfoMap.get(packagename).getEndtm());
                        Log.e(TAG, sLog);

                        Message msg = handler.obtainMessage();
                        msg.what = 0;
                        msg.obj = sLog;
                        handler.sendMessage(msg);

                        lastpackagename = packagename;
                        lastStarttm = starttime;
                    }

                }else {

                    if(gameinfoMap.containsKey(lastpackagename)){
                        String sLog = "";
                        sLog = String.format("종료된 앱:%s  시작시간:%s  끝시간:%s  ", gameinfoMap.get(lastpackagename).getPackagename(),  gameinfoMap.get(lastpackagename).getStarttm(), gameinfoMap.get(lastpackagename).getEndtm());
                        Log.e(TAG, sLog);

                        Message msg = handler.obtainMessage();
                        msg.what = 0;
                        msg.obj = sLog;
                        handler.sendMessage(msg);

                        //미존재시 제거
                        gameinfoMap.remove(lastpackagename);
                        lastpackagename = "";
                        lastStarttm = "";
                        Preferences_util.setValue(this, "itemID", lastpackagename);
                        Preferences_util.setValue(this, "itemTime", lastStarttm);
                    }

                }
            } else {

                if(gameinfoMap.containsKey(lastpackagename)){
                    String sLog = "";
                    sLog = String.format("종료된 앱:%s  시작시간:%s  끝시간:%s  ", gameinfoMap.get(lastpackagename).getPackagename(),  gameinfoMap.get(lastpackagename).getStarttm(), gameinfoMap.get(lastpackagename).getEndtm());
                    Log.e(TAG, sLog);

                    Message msg = handler.obtainMessage();
                    msg.what = 0;
                    msg.obj = sLog;
                    handler.sendMessage(msg);

                    //미존재시 제거
                    gameinfoMap.remove(lastpackagename);
                    lastpackagename = "";
                    lastStarttm = "";
                    Preferences_util.setValue(this, "itemID", lastpackagename);
                    Preferences_util.setValue(this, "itemTime", lastStarttm);
                }

            }
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