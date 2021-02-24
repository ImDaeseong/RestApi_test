package com.daeseong.gameservice

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Service
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import com.daeseong.gameservice.Preferences_util.getValue
import com.daeseong.gameservice.Preferences_util.setValue
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap


class GameService : Service() {

    private val tag = GameService::class.java.simpleName

    companion object {
        var serviceIntent: Intent? = null
    }

    private var timerTask: TimerTask? = null
    private var timer: Timer? = null

    private val gameinfoMap: HashMap<String?, gameinfo> = HashMap()
    private var lastpackagename: String? = null
    private var lastStarttm: String? = null

    override fun onCreate() {
        super.onCreate()

        initData()
    }

    private fun PreferencesInfo(bload: Boolean) {

        try {
            if (bload) {
                lastpackagename = getValue(this, "itemID", "") as String?
                lastStarttm = getValue(this, "itemTime", "") as String?
                Log.e(tag, "get lastpackagename:$lastpackagename  lastStarttm:$lastStarttm")
            } else {
                setValue(this, "itemID", lastpackagename!!)
                setValue(this, "itemTime", lastStarttm!!)
                Log.d(tag, "set lastpackagename:$lastpackagename  lastStarttm:$lastStarttm")
            }
        } catch (ex: java.lang.Exception) {
            Log.e(tag, ex.message.toString())
        }
    }

    private fun PreferencesInit() {

        try {
            lastpackagename = ""
            lastStarttm = ""
            setValue(this, "itemID", lastpackagename!!)
            setValue(this, "itemTime", lastStarttm!!)
            Log.d(tag, "set lastpackagename:$lastpackagename  lastStarttm:$lastStarttm")
        } catch (ex: Exception) {
            Log.e(tag, ex.message.toString())
        }
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        Log.d(tag, "onStartCommand")

        serviceIntent = intent
        PreferencesInfo(true)
        startTimer()
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()

        serviceIntent = null

        Log.d(tag, "onDestroy")

        PreferencesInfo(false)
        closeTimer()
        handler.removeMessages(0)

        //서비스 재시작
        //applicationContext.sendBroadcast( Intent("ACTION.RestartService"))
        //sendBroadcast( Intent("ACTION.RestartService"))
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    @SuppressLint("NewApi")
    fun startTimer() {
        try {
            timerTask = object : TimerTask() {
                override fun run() {
                    getListRunPackageName()
                }
            }
            timer = Timer()
            timer!!.schedule(timerTask, 0, 10000)
        } catch (ex: java.lang.Exception) {
            Log.e(tag, "startTimer:" + ex.message.toString())
        }
    }

    private fun closeTimer() {
        try {
            if (timerTask != null) {
                timerTask!!.cancel()
                timerTask = null
            }
            if (timer != null) {
                timer!!.cancel()
                timer!!.purge()
                timer = null
            }
        } catch (ex: java.lang.Exception) {
            Log.e(tag, "stopTimer:" + ex.message.toString())
        }
    }

    private val handler = Handler { msg ->
        try {
            Toast.makeText(applicationContext, msg.obj.toString(), Toast.LENGTH_SHORT).show()
        } catch (ex: java.lang.Exception) {
            Log.e(tag, "Toast" + ex.message.toString())
        }
        true
    }

    private fun initData() {

        Log.e(tag, "initData")

        try {

            if (isNetworkAvailable(this)) {
                val downloadJson = DownloadJson()
                downloadJson.execute()
            }
        } catch (ex: java.lang.Exception) {
            Log.e(tag, ex.message.toString())
        }

        //sample
        iteminfo.getInstance().setGameItem("com.kakaogames.moonlight")
        iteminfo.getInstance().setGameItem("com.google.android.youtube")
    }

    private fun getListRunPackageName() {

        try {

            val usageStatsManager = getSystemService(USAGE_STATS_SERVICE) as UsageStatsManager
            val time = System.currentTimeMillis()
            val usageStats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_BEST,0, time)

            if (usageStats != null) {

                val runningTask: SortedMap<Long, UsageStats> = TreeMap()
                for (item in usageStats) {
                    runningTask[item.lastTimeUsed] = item
                }

                if (runningTask != null && !runningTask.isEmpty()) {

                    if (lastpackagename != runningTask[runningTask.lastKey()]!!.packageName) {

                        if (gameinfoMap.containsKey(lastpackagename)) {

                            var sLog = ""
                            sLog = java.lang.String.format("종료된 앱:%s  시작시간:%s  끝시간:%s  ", gameinfoMap[lastpackagename]!!.packagename, gameinfoMap[lastpackagename]!!.starttm, gameinfoMap[lastpackagename]!!.endtm)
                            Log.e(tag, sLog)
                            showMessage(sLog)

                            //제거
                            gameinfoMap.remove(lastpackagename)
                            PreferencesInit()
                        }
                    }

                    //항목에 있는 패키지명 등록
                    if (iteminfo.getInstance().isGameItem(runningTask[runningTask.lastKey()]!!.packageName)) {

                        val packagename = runningTask[runningTask.lastKey()]!!.packageName
                        if (!gameinfoMap.containsKey(packagename)) {

                            gameinfoMap[packagename] = gameinfo(packagename, getTimeDate()!!, getTimeDate()!!)

                            var sLog = ""
                            sLog = java.lang.String.format("시작된 앱이름:%s  시작시간:%s  끝시간:%s  ", packagename, gameinfoMap[packagename]!!.starttm, gameinfoMap[packagename]!!.endtm)
                            Log.e(tag, sLog)
                            showMessage(sLog)

                            lastpackagename = packagename
                            lastStarttm = gameinfoMap[packagename]!!.starttm

                        } else {

                            val starttime: String = gameinfoMap[packagename]!!.starttm
                            gameinfoMap[packagename] = gameinfo(packagename, starttime, getTimeDate()!!)

                            var sLog = ""
                            sLog = java.lang.String.format("업데이트된 앱이름:%s  시작시간:%s  끝시간:%s  ", packagename, gameinfoMap[packagename]!!.starttm, gameinfoMap[packagename]!!.endtm)
                            Log.e(tag, sLog)
                            showMessage(sLog)

                            lastpackagename = packagename
                            lastStarttm = starttime
                        }
                    } else {

                        if (gameinfoMap.containsKey(lastpackagename)) {

                            var sLog = ""
                            sLog = java.lang.String.format("종료된 앱:%s  시작시간:%s  끝시간:%s  ", gameinfoMap[lastpackagename]!!.packagename, gameinfoMap[lastpackagename]!!.starttm, gameinfoMap[lastpackagename]!!.endtm)
                            Log.e(tag, sLog)
                            showMessage(sLog)

                            //제거
                            gameinfoMap.remove(lastpackagename)
                            PreferencesInit()
                        }
                    }
                }
            }

        } catch (ex: java.lang.Exception) {
            Log.e(tag, ex.message.toString())
        }
    }

    private fun getTimeDate(): String? {
        val dateFormat = SimpleDateFormat("HH:mm:ss")
        return dateFormat.format(Date())
    }

    private fun getTimeDate(date: Long): String? {
        val dateFormat = SimpleDateFormat("HH:mm:ss")
        return dateFormat.format(Date(date))
    }

    private fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

    private fun showMessage(sMsg: String) {

        try {
            val msg = handler.obtainMessage()
            msg.what = 0
            msg.obj = sMsg
            handler.sendMessage(msg)
        } catch (ex: java.lang.Exception) {
            Log.e(tag, "Toast" + ex.message.toString())
        }
    }

}