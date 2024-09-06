package com.daeseong.gameservice

import android.os.AsyncTask
import android.util.Log
import com.daeseong.gameservice.HttpUtil.GetGameDataResult
import org.json.JSONArray

class DownloadJson : AsyncTask<Void?, Void?, String>() {

    private val tag = DownloadJson::class.java.simpleName

    //사용시에는 리얼 IP 필요
    private val sUrl = "http://127.0.0.1:8080/api/AllList"

    override fun doInBackground(vararg voids: Void?): String {

        var sResult = ""

        try {

            sResult = GetGameDataResult(sUrl)

        } catch (ex: java.lang.Exception) {

            Log.e(tag, ex.message.toString())
        }

        return sResult
    }

    override fun onPostExecute(sResult: String?) {
        super.onPostExecute(sResult)

        try {

            val jsonArray = JSONArray(sResult)
            for (i in 0 until jsonArray.length()) {

                val jsonObject = jsonArray.getJSONObject(i)
                //Log.e(tag, "jsonObject:$jsonObject")

                val id = jsonObject.getString("id")
                val packagename = jsonObject.getString("packagename")

                var gametitle: String = ""
                if (jsonObject.has("gametitle")) {
                    gametitle = jsonObject.getString("gametitle")
                }

                var gamedesc: String = ""
                if (jsonObject.has("gamedesc")) {
                    gamedesc = jsonObject.getString("gamedesc")
                }

                iteminfo.getInstance().setGameItem(packagename)
            }

        } catch (ex: Exception) {
            Log.e(tag, ex.message.toString())
        }
    }

}