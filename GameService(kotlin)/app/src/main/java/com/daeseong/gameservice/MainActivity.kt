package com.daeseong.gameservice

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    private val tag: String = MainActivity::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Log.d(tag, "onCreate")

        //서비스 등록
        initService()

        /*
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ){
                val permissionList = arrayOf(Manifest.permission.CAMERA, Manifest.permission.INTERNET, Manifest.permission.ACCESS_NETWORK_STATE)
                ActivityCompat.requestPermissions(this, permissionList, 1)
            }
        }
        */

        //권한 체크 - PACKAGE_USAGE_STATS 강제로 설정 해야함 창이 뜨지 않음
        checkPermissions()

        //프로그램 종료
        finish()
    }

    private fun initService() {

        try {

            if (GameService.serviceIntent == null) {
                val service = Intent(this, GameService::class.java)
                startService(service)
            }
        } catch (ex: Exception) {
            Log.e(tag, ex.message.toString())
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        //서비스 재시작
        //sendBroadcast(Intent("ACTION.RestartService"))
        Log.d(tag, "onDestroy")
    }

    private fun checkPermissions() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.PACKAGE_USAGE_STATS) != PackageManager.PERMISSION_GRANTED ){

                val permissionList = arrayOf(Manifest.permission.PACKAGE_USAGE_STATS, Manifest.permission.INTERNET, Manifest.permission.ACCESS_NETWORK_STATE)
                ActivityCompat.requestPermissions(this, permissionList, 2)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {

        when (requestCode) {
            1 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Log.e(tag, "CAMERA PERMISSION_GRANTED")
                } else {

                    Log.e(tag, "CAMERA PERMISSION_DENIED")
                }
            }

            2 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Log.e(tag, "PACKAGE_USAGE_STATS PERMISSION_GRANTED")
                } else {

                    Log.e(tag, "PACKAGE_USAGE_STATS PERMISSION_DENIED")
                }
            }
        }
    }


}
