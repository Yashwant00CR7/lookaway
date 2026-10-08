package com.lookaway.data

import android.content.Context
import android.content.SharedPreferences

interface KeyValueStore {
    fun getInt(key: String, defValue: Int): Int
    fun putInt(key: String, value: Int)

    fun getLong(key: String, defValue: Long): Long
    fun putLong(key: String, value: Long)

    fun getBoolean(key: String, defValue: Boolean): Boolean
    fun putBoolean(key: String, value: Boolean)

    fun getString(key: String, defValue: String?): String?
    fun putString(key: String, value: String)
}

class SharedPrefsKeyValueStore(private val prefs: SharedPreferences) : KeyValueStore {

    constructor(context: Context, name: String = "lookaway_prefs") : this(
        context.getSharedPreferences(name, Context.MODE_PRIVATE)
    )

    override fun getInt(key: String, defValue: Int): Int = prefs.getInt(key, defValue)

    override fun putInt(key: String, value: Int) {
        prefs.edit().putInt(key, value).apply()
    }

    override fun getLong(key: String, defValue: Long): Long = prefs.getLong(key, defValue)

    override fun putLong(key: String, value: Long) {
        prefs.edit().putLong(key, value).apply()
    }

    override fun getBoolean(key: String, defValue: Boolean): Boolean = prefs.getBoolean(key, defValue)

    override fun putBoolean(key: String, value: Boolean) {
        prefs.edit().putBoolean(key, value).apply()
    }

    override fun getString(key: String, defValue: String?): String? = prefs.getString(key, defValue)

    override fun putString(key: String, value: String) {
        prefs.edit().putString(key, value).apply()
    }
}
