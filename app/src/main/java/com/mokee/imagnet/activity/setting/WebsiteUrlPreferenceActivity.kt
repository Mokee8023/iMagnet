package com.mokee.imagnet.activity.setting

import android.os.Bundle
import android.preference.PreferenceFragment
import com.mokee.imagnet.R
import com.mokee.imagnet.constrant.MagnetConstrant
import com.mokee.imagnet.utils.SPUtil

class WebsiteUrlPreferenceActivity : AppSettingActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        addPreferencesFromResource(R.xml.website_url)

        val sp = preferenceScreen.sharedPreferences
        findPreference(SPUtil.NIMA_HOME_URL_KEY).summary =
                sp.getString(SPUtil.NIMA_HOME_URL_KEY, MagnetConstrant.NIMA_HOME_URL)
        findPreference(SPUtil.NIMA_SEARCH_URL_KEY).summary =
                sp.getString(SPUtil.NIMA_SEARCH_URL_KEY, MagnetConstrant.NIMA_SEARCH_URL)
        findPreference(SPUtil.CILICAT_HOME_URL_KEY).summary =
                sp.getString(SPUtil.CILICAT_HOME_URL_KEY, MagnetConstrant.CILICAT_HOME_URL)
        findPreference(SPUtil.ALICILI_HOME_URL_KEY).summary =
                sp.getString(SPUtil.ALICILI_HOME_URL_KEY, MagnetConstrant.ALICILI_HOME_URL)
        findPreference(SPUtil.BTDB_ME_HOME_URL_KEY).summary =
                sp.getString(SPUtil.BTDB_ME_HOME_URL_KEY, MagnetConstrant.BTDB_ME_HOME_URL)
        findPreference(SPUtil.BTDB_ME_SEARCH_URL_KEY).summary =
                sp.getString(SPUtil.BTDB_ME_SEARCH_URL_KEY, MagnetConstrant.BTDB_ME_SEARCH_URL)
    }

    override fun isValidFragment(fragmentName: String): Boolean {
        return PreferenceFragment::class.java.name == fragmentName ||
                WebsiteUrlPreferenceActivity::class.java.name == fragmentName
    }
}