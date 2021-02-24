package com.daeseong.gameservice

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

object HttpUtil {

    fun GetGameDataResult(sUrl: String): String {

        var httpURLConnection: HttpURLConnection? = null
        var inputStream: InputStream? = null
        var bufferedReader: BufferedReader? = null
        val stringBuilder = StringBuilder()

        try {

            val url = URL(sUrl)
            httpURLConnection = url.openConnection() as HttpURLConnection
            httpURLConnection.allowUserInteraction = false
            httpURLConnection.instanceFollowRedirects = true
            httpURLConnection.requestMethod = "GET"
            httpURLConnection.connect()

            val resCode: Int = httpURLConnection.responseCode
            if (resCode == HttpURLConnection.HTTP_OK) {
                inputStream = httpURLConnection.inputStream
                bufferedReader = BufferedReader(InputStreamReader(inputStream))
                var line: String? = null
                while (bufferedReader.readLine().also { line = it } != null) {
                    stringBuilder.append(line)
                }
            }
            httpURLConnection.disconnect()

        } catch (e: IOException) {

            return ""

        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close()
                } catch (e: IOException) {
                }
            }
            if (bufferedReader != null) {
                try {
                    bufferedReader.close()
                } catch (e: IOException) {
                }
            }
            if (httpURLConnection != null) {
                try {
                    httpURLConnection.disconnect()
                } catch (e: Exception) {
                }
            }
        }
        return stringBuilder.toString()
    }
}