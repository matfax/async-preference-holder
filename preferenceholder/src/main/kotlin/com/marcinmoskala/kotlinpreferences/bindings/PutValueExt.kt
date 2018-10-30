@file:Suppress("UNCHECKED_CAST", "IMPLICIT_CAST_TO_ANY")

package com.marcinmoskala.kotlinpreferences.bindings

import android.content.SharedPreferences
import kotlinx.serialization.json.JSON
import kotlinx.serialization.serializer
import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf

internal fun <T : Any> SharedPreferences.Editor.putValue(
        clazz: KClass<T>,
        value: T,
        key: String
) {
    when {
        clazz.isSubclassOf(Long::class) -> putLong(key, value as Long)
        clazz.isSubclassOf(Int::class) -> putInt(key, value as Int)
        clazz.isSubclassOf(String::class) -> putString(key, value as String)
        clazz.isSubclassOf(Boolean::class) -> putBoolean(key, value as Boolean)
        clazz.isSubclassOf(Float::class) -> putFloat(key, value as Float)
        else -> putString(key, JSON.stringify(clazz.serializer(), value))
    }
}

internal fun <T : Any> SharedPreferences.getFromPreference(
        clazz: KClass<T>,
        key: String,
        default: T? = getDefault(clazz)
): T? {
    val serializedDefault: String? by lazy {
        default?.let { JSON.stringify(clazz.serializer(), it) }
    }

    return when {
        clazz.isSubclassOf(Long::class) -> getLong(key, default as Long)
        clazz.isSubclassOf(Int::class) -> getInt(key, default as Int)
        clazz.isSubclassOf(String::class) -> getString(key, default as String)
        clazz.isSubclassOf(Boolean::class) -> getBoolean(key, default as Boolean)
        clazz.isSubclassOf(Float::class) -> getFloat(key, default as Float)
        else -> getString(key, serializedDefault)?.let { JSON.parse(clazz.serializer(), it) }
    } as? T?
}

private fun <T : Any> getDefault(clazz: KClass<T>): T? = when {
    clazz.isSubclassOf(Long::class) -> -1L
    clazz.isSubclassOf(Int::class) -> -1
    clazz.isSubclassOf(String::class) -> ""
    clazz.isSubclassOf(Boolean::class) -> false
    clazz.isSubclassOf(Float::class) -> -1.0F
    else -> null
} as? T?