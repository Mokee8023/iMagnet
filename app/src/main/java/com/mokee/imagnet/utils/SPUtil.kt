package com.mokee.imagnet.utils

import android.content.Context
import android.content.SharedPreferences
import android.text.TextUtils
import com.mokee.imagnet.App

object SPUtil {
    fun getBooleanSetting(context: Context, key: String, defValue: Boolean = false): Boolean {
        val sp = context.getSharedPreferences(
                context.packageName + "_preferences", Context.MODE_PRIVATE)
        return sp.getBoolean(key, defValue)
    }

    fun registerChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) =
            App.appContext?.let { context ->
                val sp = context.getSharedPreferences(
                        context.packageName + "_preferences", Context.MODE_PRIVATE)
                sp.registerOnSharedPreferenceChangeListener(listener)
            }

    fun updateSetting(arrayHash: HashMap<String, String>) {
        App.appContext?.let { context ->
            val sp = context.getSharedPreferences(
                    context.packageName + "_preferences", Context.MODE_PRIVATE)

            arrayHash.keys.forEach { key ->
                if(TextUtils.isEmpty(sp.getString(key, ""))) {
                    sp.edit().putString(key, arrayHash[key]).apply()
                }
            }
        }
    }

    const val ALI_DIALOG_KEY = "setting_ali_error_auth"
    const val NIMA_HOME_URL_KEY = "nima_home_url"
    const val NIMA_SEARCH_URL_KEY = "nima_search_url"
    const val CILICAT_HOME_URL_KEY = "cilicat_home_url"
    const val ALICILI_HOME_URL_KEY = "alicili_home_url"
//    const val ZH_BTDB_HOME_URL_KEY = "btdb_home_url"
    const val BTDB_ME_HOME_URL_KEY = "btdb_me_home_url"
    const val BTDB_ME_SEARCH_URL_KEY = "btdb_me_search_url"
}