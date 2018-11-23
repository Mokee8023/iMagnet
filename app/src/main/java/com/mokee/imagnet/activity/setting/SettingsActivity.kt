package com.mokee.imagnet.activity.setting

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.CheckBoxPreference
import android.preference.EditTextPreference
import android.preference.MultiSelectListPreference
import android.preference.PreferenceFragment
import com.mokee.imagnet.R
import com.mokee.imagnet.activity.gesture.GestureActivity
import com.mokee.imagnet.constrant.MagnetConstrant
import com.mokee.imagnet.event.TabChangeEvent
import com.mokee.imagnet.utils.SPUtil
import org.greenrobot.eventbus.EventBus
import timber.log.Timber
import java.lang.StringBuilder

class SettingsActivity : AppSettingActivity(), SharedPreferences.OnSharedPreferenceChangeListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupActionBar()

        addPreferencesFromResource(R.xml.settings)
    }

    override fun onResume() {
        super.onResume()
        preferenceScreen.sharedPreferences.registerOnSharedPreferenceChangeListener(this)

        val selectedString = getTabSelectedString(SPUtil.getSetSetting(this, "setting_selected_card"))
        if(!selectedString.isEmpty()) {
            findPreference("setting_selected_card").summary = selectedString
        }
    }

    override fun onPause() {
        super.onPause()
        preferenceScreen.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        val preferences = findPreference(key)

        when(preferences) {
            is EditTextPreference -> preferences.setSummary(preferences.text)
            is MultiSelectListPreference -> {
                val selectedIndex = SPUtil.getSetSetting(this, "setting_selected_card")
                val selectedString = getTabSelectedString(selectedIndex)
                if(!selectedString.isEmpty()) {
                    preferences.setSummary(selectedString)
                    preferences.setTitle("${preferences.title}  (修改后请重启应用生效)")
                }
                EventBus.getDefault().post(TabChangeEvent(selectedIndex))
            }
            is CheckBoxPreference -> {
                val gesture = SPUtil.getBooleanSetting(this, "setting_gesture_lock_key")
                if(gesture) {
                    val gestureIntent = Intent(this, GestureActivity::class.java)
                    gestureIntent.putExtra(GestureActivity.INTENT_EXTRA_KEY, GestureActivity.Type.SETUP)
                    startActivityForResult(gestureIntent, GESTURE_REQUEST_CODE)
                } else {
                    SPUtil.setStringSetting(this, MagnetConstrant.PATTERN_STRING_KEY, "")
                }

                (findPreference("setting_gesture_lock_key") as CheckBoxPreference).isChecked = false
            }
        }
    }

    private fun getTabSelectedString(selectedIndex: Set<String>) : String {
        val tabs = resources.getStringArray(R.array.main_tab_text).toList()

        // new tab
        val sb = StringBuilder()
        (0 until tabs.size).forEach {index ->
            if(selectedIndex.contains(index.toString())) {
                if(sb.toString().isEmpty()) {
                    sb.append("已选：").append(tabs[index])
                } else {
                    sb.append("、").append(tabs[index])
                }
            }
        }

        return sb.toString()
    }

    override fun onIsMultiPane(): Boolean {
        return true
    }

    private fun setupActionBar() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun isValidFragment(fragmentName: String): Boolean {
        return PreferenceFragment::class.java.name == fragmentName ||
                WebsiteUrlPreferenceActivity::class.java.name == fragmentName
    }

    companion object {
        const val GESTURE_REQUEST_CODE = 99
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == GESTURE_REQUEST_CODE) {
            if(resultCode == RESULT_OK) {
                (findPreference("setting_gesture_lock_key") as CheckBoxPreference).isChecked = true
            }
        }
    }
}
