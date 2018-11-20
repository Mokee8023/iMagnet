package com.mokee.imagnet.activity.setting

import android.content.SharedPreferences
import android.os.Bundle
import android.preference.EditTextPreference
import com.mokee.imagnet.R
import com.mokee.imagnet.constrant.MagnetConstrant
import com.mokee.imagnet.utils.SPUtil

class SettingsActivity : AppSettingActivity(), SharedPreferences.OnSharedPreferenceChangeListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupActionBar()

        addPreferencesFromResource(R.xml.settings)

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

    override fun onResume() {
        super.onResume()
        preferenceScreen.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        preferenceScreen.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        val preferences = findPreference(key)
        if(preferences is EditTextPreference) {
            preferences.setSummary(preferences.text)
        }
    }

    private fun setupActionBar() {
        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true)
    }
}
