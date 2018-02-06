package com.stylingandroid.prefekt

import android.support.annotation.VisibleForTesting
import android.support.v4.app.FragmentActivity
import com.stylingandroid.prefekt.internal.PrefektOwner
import kotlinx.coroutines.experimental.CoroutineDispatcher
import kotlinx.coroutines.experimental.Unconfined

fun FragmentActivity.prefekt(key: String, defaultValue: Boolean, subscriber: Subscriber<Boolean>) =
        prefektGeneric(key, defaultValue, subscriber)

fun FragmentActivity.prefekt(key: String, defaultValue: Int, subscriber: Subscriber<Int>) =
        prefektGeneric(key, defaultValue, subscriber)

fun FragmentActivity.prefekt(key: String, defaultValue: Long, subscriber: Subscriber<Long>) =
        prefektGeneric(key, defaultValue, subscriber)

fun FragmentActivity.prefekt(key: String, defaultValue: Float, subscriber: Subscriber<Float>) =
        prefektGeneric(key, defaultValue, subscriber)

fun FragmentActivity.prefekt(key: String, defaultValue: String, subscriber: Subscriber<String>) =
        prefektGeneric(key, defaultValue, subscriber)

private fun <T : Any> FragmentActivity.prefektGeneric(
        key: String,
        defaultValue: T,
        subscriber: Subscriber<T>): Prefekt<T> =
        prefektGeneric(key, defaultValue).apply {
            subscribe(subscriber)
        }

@VisibleForTesting
internal fun <T : Any> FragmentActivity.prefektForeground(
        key: String, defaultValue: T,
        subscriber: Subscriber<T>
): Prefekt<T> =
        prefektGeneric(key, defaultValue, Unconfined, Unconfined).apply {
            subscribe(subscriber)
        }

fun FragmentActivity.prefekt(key: String, defaultValue: Boolean, observer: (Boolean) -> Unit) =
        prefektGeneric(key, defaultValue, observer)

fun FragmentActivity.prefekt(key: String, defaultValue: Int, observer: (Int) -> Unit) =
        prefektGeneric(key, defaultValue, observer)

fun FragmentActivity.prefekt(key: String, defaultValue: Long, observer: (Long) -> Unit) =
        prefektGeneric(key, defaultValue, observer)

fun FragmentActivity.prefekt(key: String, defaultValue: Float, observer: (Float) -> Unit) =
        prefektGeneric(key, defaultValue, observer)

fun FragmentActivity.prefekt(key: String, defaultValue: String, observer: (String) -> Unit) =
        prefektGeneric(key, defaultValue, observer)

private fun <T : Any> FragmentActivity.prefektGeneric(key: String, defaultValue: T, observer: (T) -> Unit): Prefekt<T> =
        prefektGeneric(key, defaultValue).apply {
            subscribe(object : Subscriber<T> {
                override fun onChanged(newValue: T) {
                    observer(newValue)
                }
            })
        }

@VisibleForTesting
internal fun <T : Any> FragmentActivity.prefektForeground(
        key: String,
        defaultValue: T,
        observer: (T) -> Unit
): Prefekt<T> =
        prefektGeneric(key, defaultValue, Unconfined, Unconfined).apply {
            subscribe(object : Subscriber<T> {
                override fun onChanged(newValue: T) {
                    observer(newValue)
                }
            })
        }

private fun <T : Any> FragmentActivity.prefektGeneric(key: String, defaultValue: T): Prefekt<T> =
        Prefekt(PrefektOwner(this), key, defaultValue)

private fun <T : Any> FragmentActivity.prefektGeneric(
        key: String, defaultValue: T,
        mainDispatcher: CoroutineDispatcher,
        backgroundDispatcher: CoroutineDispatcher
): Prefekt<T> =
        Prefekt(
                PrefektOwner(this,
                        mainDispatcher = mainDispatcher,
                        backgroundDispatcher = backgroundDispatcher),
                key,
                defaultValue)
