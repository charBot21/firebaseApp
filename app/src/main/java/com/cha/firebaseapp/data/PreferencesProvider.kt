package com.cha.firebaseapp.data

import android.content.Context

class PreferencesProvider(context: Context) {
    private val sharedPreferences = context.getSharedPreferences("loginPreferences", 0)

    fun putBoolean(key: String, value: Boolean) {
        sharedPreferences.edit().putBoolean(key, value).apply()
    }

    fun getBoolean(key: String): Boolean {
        return sharedPreferences.getBoolean(key, false)
    }
}