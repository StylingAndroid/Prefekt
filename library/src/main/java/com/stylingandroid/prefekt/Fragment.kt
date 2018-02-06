package com.stylingandroid.prefekt

import android.support.v4.app.Fragment
import com.stylingandroid.prefekt.internal.PrefektOwner

fun Fragment.prefekt(key: String, defaultValue: Boolean, observer: Subscriber<Boolean>) =
        prefektGeneric(key, defaultValue, observer)

fun Fragment.prefekt(key: String, defaultValue: Int, observer: Subscriber<Int>) =
        prefektGeneric(key, defaultValue, observer)

fun Fragment.prefekt(key: String, defaultValue: Long, observer: Subscriber<Long>) =
        prefektGeneric(key, defaultValue, observer)

fun Fragment.prefekt(key: String, defaultValue: Float, observer: Subscriber<Float>) =
        prefektGeneric(key, defaultValue, observer)

fun Fragment.prefekt(key: String, defaultValue: String, observer: Subscriber<String>) =
        prefektGeneric(key, defaultValue, observer)

private fun <T : Any> Fragment.prefektGeneric(key: String, defaultValue: T, observer: Subscriber<T>): Prefekt<T> =
        prefektGeneric(key, defaultValue).apply {
            subscribe(observer)
        }

fun Fragment.prefekt(key: String, defaultValue: Boolean, observer: (Boolean) -> Unit) =
        prefektGeneric(key, defaultValue, observer)

fun Fragment.prefekt(key: String, defaultValue: Int, observer: (Int) -> Unit) =
        prefektGeneric(key, defaultValue, observer)

fun Fragment.prefekt(key: String, defaultValue: Long, observer: (Long) -> Unit) =
        prefektGeneric(key, defaultValue, observer)

fun Fragment.prefekt(key: String, defaultValue: Float, observer: (Float) -> Unit) =
        prefektGeneric(key, defaultValue, observer)

fun Fragment.prefekt(key: String, defaultValue: String, observer: (String) -> Unit) =
        prefektGeneric(key, defaultValue, observer)


private fun <T : Any> Fragment.prefektGeneric(key: String, defaultValue: T, observer: (T) -> Unit): Prefekt<T> =
        prefektGeneric(key, defaultValue).apply {
            subscribe(object : Subscriber<T> {
                override fun onChanged(newValue: T) {
                    observer(newValue)
                }
            })
        }

private fun <T : Any> Fragment.prefektGeneric(key: String, defaultValue: T): Prefekt<T> =
        Prefekt(PrefektOwner(this), key, defaultValue)
