package com.mokee.imagnet.constrant

import android.content.SharedPreferences
import com.mokee.imagnet.utils.SPUtil

object MagnetConstrant {
    var NIMA_HOME_URL = "http://www.nms222.com"
    var NIMA_SEARCH_URL = "http://www.nms222.com/l/"
    var CILICAT_HOME_URL = "https://www.cilimao.me"
    var ALICILI_HOME_URL = "https://alicili.me"
    var ZH_BTDB_HOME_URL = "http://zh.btdb.to"
    var BTDB_ME_HOME_URL = "https://btdb.me"
    var BTDB_ME_SEARCH_URL = "https://btdb.me/list/"

    const val PATTERN_STRING_KEY = "pattern_string_key"

    init {
//        SPUtil.updateSetting(
//                hashMapOf(
//                        Pair(SPUtil.NIMA_HOME_URL_KEY, NIMA_HOME_URL),
//                        Pair(SPUtil.NIMA_SEARCH_URL_KEY, NIMA_SEARCH_URL),
//                        Pair(SPUtil.CILICAT_HOME_URL_KEY, CILICAT_HOME_URL),
//                        Pair(SPUtil.ALICILI_HOME_URL_KEY, ALICILI_HOME_URL),
//                        Pair(SPUtil.BTDB_ME_HOME_URL_KEY, BTDB_ME_HOME_URL),
//                        Pair(SPUtil.BTDB_ME_SEARCH_URL_KEY, BTDB_ME_SEARCH_URL)
//                )
//        )
        SPUtil.registerChangeListener(SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->
            when(key)  {
                SPUtil.NIMA_HOME_URL_KEY -> {
                    NIMA_HOME_URL = sharedPreferences.getString(key, NIMA_HOME_URL)
                }
                SPUtil.NIMA_SEARCH_URL_KEY -> {
                    NIMA_SEARCH_URL = sharedPreferences.getString(key, NIMA_SEARCH_URL)
                }
                SPUtil.CILICAT_HOME_URL_KEY -> {
                    CILICAT_HOME_URL = sharedPreferences.getString(key, CILICAT_HOME_URL)
                }
                SPUtil.ALICILI_HOME_URL_KEY -> {
                    ALICILI_HOME_URL = sharedPreferences.getString(key, ALICILI_HOME_URL)
                }
                SPUtil.BTDB_ME_HOME_URL_KEY -> {
                    BTDB_ME_HOME_URL = sharedPreferences.getString(key, BTDB_ME_HOME_URL)
                }
                SPUtil.BTDB_ME_SEARCH_URL_KEY -> {
                    BTDB_ME_SEARCH_URL = sharedPreferences.getString(key, BTDB_ME_SEARCH_URL)
                }
            }
        })
    }
}