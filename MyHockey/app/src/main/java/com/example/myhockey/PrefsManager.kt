package com.example.myhockey

import android.content.Context
import android.content.SharedPreferences

object PrefsManager {
    private const val PREF_NAME = "myhockey_prefs"
    private const val KEY_SIGNED_UP = "has_signed_up"
    private const val KEY_SKIPPED = "skipped_signup"
    private const val KEY_FIRST_TIME = "is_first_time"

    private lateinit var prefs: SharedPreferences

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    var isFirstTime: Boolean
        get() = prefs.getBoolean(KEY_FIRST_TIME, true)
        set(value) = prefs.edit().putBoolean(KEY_FIRST_TIME, value).apply()

    var hasSignedUp: Boolean
        get() = prefs.getBoolean(KEY_SIGNED_UP, false)
        set(value) = prefs.edit().putBoolean(KEY_SIGNED_UP, value).apply()

    var skippedSignUp: Boolean
        get() = prefs.getBoolean(KEY_SKIPPED, false)
        set(value) = prefs.edit().putBoolean(KEY_SKIPPED, value).apply()

    fun reset() {
        prefs.edit().clear().apply()
    }
}
