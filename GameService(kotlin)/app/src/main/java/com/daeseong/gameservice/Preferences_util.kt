package com.daeseong.gameservice

import android.content.Context
import android.content.SharedPreferences


object Preferences_util {

    private const val FILE_NAME = "GameData"

    fun setValue(context: Context, sKey: String?, oData: Any) {

        val sType = oData.javaClass.simpleName
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        when (sType) {
            "String" -> {
                editor.putString(sKey, oData as String)
            }
            "Integer" -> {
                editor.putInt(sKey, (oData as Int))
            }
            "Boolean" -> {
                editor.putBoolean(sKey, (oData as Boolean))
            }
            "Float" -> {
                editor.putFloat(sKey, (oData as Float))
            }
            "Long" -> {
                editor.putLong(sKey, (oData as Long))
            }
        }
        editor.commit()
    }

    fun getValue(context: Context, sKey: String?, oData: Any): Any? {

        val sType = oData.javaClass.simpleName
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)

        when (sType) {
            "String" -> {
                return sharedPreferences.getString(sKey, oData as String)
            }
            "Integer" -> {
                return sharedPreferences.getInt(sKey, (oData as Int))
            }
            "Boolean" -> {
                return sharedPreferences.getBoolean(sKey, (oData as Boolean))
            }
            "Float" -> {
                return sharedPreferences.getFloat(sKey, (oData as Float))
            }
            "Long" -> {
                return sharedPreferences.getLong(sKey, (oData as Long))
            }
            else -> return null
        }
    }

    fun remove(context: Context, sKey: String?) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.remove(sKey)
        editor.apply()
    }

    fun clear(context: Context) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
    }

    fun contains(context: Context, sKey: String?): Boolean {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.contains(sKey)
    }

    fun getAll(context: Context): Map<String, *> {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.all
    }
}