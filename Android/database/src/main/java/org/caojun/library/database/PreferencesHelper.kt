package org.caojun.library.database

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson

class PreferencesHelper(context: Context, name: String = "database_preferences") {
    @PublishedApi
    internal val sharedPref: SharedPreferences =
        context.getSharedPreferences(name, Context.MODE_PRIVATE)
    @PublishedApi
    internal val gson = Gson()

    // 存储基本类型
    fun putString(key: String, value: String) = sharedPref.edit().putString(key, value).apply()
    fun putInt(key: String, value: Int) = sharedPref.edit().putInt(key, value).apply()
    fun putBoolean(key: String, value: Boolean) = sharedPref.edit().putBoolean(key, value).apply()
    fun putFloat(key: String, value: Float) = sharedPref.edit().putFloat(key, value).apply()
    fun putLong(key: String, value: Long) = sharedPref.edit().putLong(key, value).apply()

    // 读取基本类型
    fun getString(key: String, defaultValue: String = "") = sharedPref.getString(key, defaultValue) ?: defaultValue
    fun getInt(key: String, defaultValue: Int = 0) = sharedPref.getInt(key, defaultValue)
    fun getBoolean(key: String, defaultValue: Boolean = false) = sharedPref.getBoolean(key, defaultValue)
    fun getFloat(key: String, defaultValue: Float = 0f) = sharedPref.getFloat(key, defaultValue)
    fun getLong(key: String, defaultValue: Long = 0L) = sharedPref.getLong(key, defaultValue)

    // 存储对象
    fun <T> putObject(key: String, obj: T) {
        val jsonString = gson.toJson(obj)
        sharedPref.edit().putString(key, jsonString).apply()
    }

    // 读取对象
    inline fun <reified T> getObject(key: String, defaultValue: T? = null): T? {
        val jsonString = sharedPref.getString(key, null)
        return if (jsonString != null) {
            gson.fromJson(jsonString, T::class.java)
        } else {
            defaultValue
        }
    }

    // 删除键
    fun remove(key: String) = sharedPref.edit().remove(key).apply()

    // 清空所有数据
    fun clear() = sharedPref.edit().clear().apply()
}