package com.mokee.imagnet.activity.gesture

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import com.andrognito.patternlockview.PatternLockView
import com.andrognito.patternlockview.listener.PatternLockViewListener
import com.andrognito.patternlockview.utils.PatternLockUtils
import com.mokee.imagnet.MainActivity
import com.mokee.imagnet.R
import com.mokee.imagnet.constrant.MagnetConstrant
import com.mokee.imagnet.utils.MD5Util
import com.mokee.imagnet.utils.SPUtil
import kotlinx.android.synthetic.main.activity_gesture.*
import timber.log.Timber

class GestureActivity : AppCompatActivity() {
    private var type: Int = Type.UNLOCK

    private lateinit var mPatternLockView: PatternLockView

    private var mHandler = Handler()
    private var resetRunnable = Runnable {
        mPatternLockView.clearPattern()
        isFirstCreatePattern = true
        firstPatternString = null

        clearOp(false, Color.GRAY, View.VISIBLE)
        nextOp(false, Color.GRAY, resources.getString(R.string.gesture_show_next))

        gesture_show_text.text = resources.getString(R.string.gesture_show_setup_one)
        gesture_show_text.setTextColor(Color.WHITE)
    }

    private fun nextOp(isClickable: Boolean? = null, textColor: Int? = null, text: String? = null) {
        isClickable?.let{ gesture_show_next.isEnabled = it }
        textColor?.let { gesture_show_next.setTextColor(it) }
        text?.let { gesture_show_next.text = it }
    }

    private fun clearOp(isClickable: Boolean? = null, textColor: Int? = null, isVisable: Int? = null) {
        isClickable?.let{ gesture_show_clear.isEnabled = it }
        textColor?.let { gesture_show_clear.setTextColor(it) }
        isVisable?.let { gesture_show_clear.visibility = it }
    }

    private fun textOp(text: String? = null, color: Int? = null) {
        text?.let { gesture_show_text.text = it }
        color?.let { gesture_show_text.setTextColor(it) }
    }

    private var matchSuccessDelayRunnable = Runnable {
        setResult(Activity.RESULT_OK)
        finishActivity()
    }

    private var isFirstCreatePattern = true
    private var firstPatternString: String? = null

    private var isMatchSuccess: Boolean = false

    private val mPatternLockViewListener = object : PatternLockViewListener {
        override fun onStarted() {
            Timber.d("Pattern drawing started")

            if(isFirstCreatePattern) {
                clearOp(true, Color.WHITE)
                nextOp(true, Color.WHITE)
            } else {
                clearOp(isVisable = View.GONE)
                nextOp(true, Color.WHITE, resources.getString(R.string.gesture_show_ensure))
            }
            textOp(text = resources.getString(R.string.gesture_show_setup_drawing))
        }
        override fun onCleared() { Timber.d("Pattern has been cleared") }
        override fun onProgress(progressPattern: List<PatternLockView.Dot>) { Timber.d("Pattern progress") }

        override fun onComplete(pattern: List<PatternLockView.Dot>) {
            Timber.d("Pattern complete.")

            processAuth(PatternLockUtils.patternToString(mPatternLockView, pattern))
            isFirstCreatePattern = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gesture)
        intent?.let {
            type = it.getIntExtra(INTENT_EXTRA_KEY, Type.UNLOCK) }

        mPatternLockView = findViewById(R.id.patter_lock_view)
        mPatternLockView.addPatternLockListener(mPatternLockViewListener)

        init()
    }

    override fun onResume() {
        super.onResume()
        fullscreen()
    }

    private fun init() {
        if(type == Type.UNLOCK) {
            val enable = SPUtil.getBooleanSetting(this, ENABLE_GESTURE_KEY, false)
            if(!enable) {
                startActivity(Intent(this, MainActivity::class.java))
                // Exit gesture activity, then to main activity
                finishActivity()
            }

            textOp(text = resources.getString(R.string.gesture_show_unlock_home))
            hide()
        } else if(type == Type.SETUP) {
            textOp(text = resources.getString(R.string.gesture_show_setup_one))
            clearOp(isClickable = false)
            nextOp(isClickable = false)
            show()
            gesture_return.visibility = View.VISIBLE
        }

        gesture_show_clear.setOnClickListener { mHandler.post(resetRunnable) }

        gesture_show_next.setOnClickListener {
            mPatternLockView.clearPattern()

            textOp(text = resources.getString(R.string.gesture_show_setup_again))
            clearOp(isVisable = View.GONE)
            nextOp(false, Color.GRAY, resources.getString(R.string.gesture_show_ensure))

            if(isMatchSuccess) {
                mHandler.removeCallbacks(matchSuccessDelayRunnable)
                setResult(Activity.RESULT_OK)
                finishActivity()
            }
        }

        gesture_return.setOnClickListener {
            setResult(Activity.RESULT_CANCELED)
            finishActivity()
        }
    }

    private fun hide() {
        supportActionBar?.hide()
        fullscreen_content_controls.visibility = View.GONE
    }

    private fun show() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.show()

        fullscreen_content.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION

        fullscreen_content_controls.visibility = View.VISIBLE
    }

    private fun processAuth(pattern: String?) {
        pattern?.let {
            if(type == Type.UNLOCK) {
                val storePattern = SPUtil.getStringSetting(this, MagnetConstrant.PATTERN_STRING_KEY, "")
                if(storePattern == MD5Util.generateMD5(pattern)) {
                    // verify success
                    textOp(text = resources.getString(R.string.gesture_show_unlock_success))
                    startActivity(Intent(this, MainActivity::class.java))
                    finishActivity()
                } else {
                    // verify fail
                    textOp(text = resources.getString(R.string.gesture_show_unlock_error))
                    mPatternLockView.setViewMode(PatternLockView.PatternViewMode.WRONG)
                }
            } else if(type == Type.SETUP) {
                if(isFirstCreatePattern) {
                    // first draw, store temp pattern string
                    textOp(text = resources.getString(R.string.gesture_show_setup_first_save))
                    firstPatternString = MD5Util.generateMD5(pattern)
                } else {
                    val secondPattern = MD5Util.generateMD5(pattern)
                    secondPattern?.let {
                        if(secondPattern == firstPatternString) {
                            // match success
                            isMatchSuccess = true
                            textOp(text = resources.getString(R.string.gesture_show_setup_success), color = Color.GREEN)

                            mPatternLockView.setViewMode(PatternLockView.PatternViewMode.CORRECT)
                            SPUtil.setStringSetting(this, MagnetConstrant.PATTERN_STRING_KEY, secondPattern)
                            mHandler.postDelayed(matchSuccessDelayRunnable, MATCH_SUCCESS_DELAY_TIME * 1000L)
                        } else {
                            // match fail
                            textOp(text = resources.getString(R.string.gesture_show_setup_error), color = Color.RED)

                            nextOp(false, Color.GRAY)
                            mPatternLockView.setViewMode(PatternLockView.PatternViewMode.WRONG)
                            mHandler.postDelayed(resetRunnable, RESET_DELAY_TIME * 1000L)
                        }
                    }?: Timber.d("Pattern is null.")
                }
            }
        }?: Timber.e("Pattern is null.")
    }

    private fun clearRunnable() {
        mHandler.removeCallbacks(matchSuccessDelayRunnable)
        mHandler.removeCallbacks(resetRunnable)
    }

    private fun finishActivity() {
        clearRunnable()
        finish()
    }

    private fun fullscreen() {
        fullscreen_content.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LOW_PROFILE or View.SYSTEM_UI_FLAG_FULLSCREEN or
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
    }

    companion object {
        const val INTENT_EXTRA_KEY = "gesture_type_key"
        const val ENABLE_GESTURE_KEY = "setting_gesture_lock_key"

        private const val RESET_DELAY_TIME = 3
        private const val MATCH_SUCCESS_DELAY_TIME = 3
    }

    object Type {
        const val UNLOCK = 1
        const val SETUP = 2
    }
}
