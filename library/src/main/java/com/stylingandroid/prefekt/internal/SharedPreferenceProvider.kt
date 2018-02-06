package com.stylingandroid.prefekt.internal

import android.content.SharedPreferences
import com.stylingandroid.prefekt.Subscriber
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch

internal class SharedPreferenceProvider<T : Any>(
        private val owner: PrefektOwner,
        private val key: String,
        private val defaultValue: T,
        private val getter: SharedPreferences.(String, T) -> T,
        private val setter: SharedPreferences.Editor.(String, T) -> SharedPreferences.Editor
) : Provider<T>, LifecycleListener, Publisher<T>, SharedPreferences.OnSharedPreferenceChangeListener {
    private lateinit var sharedPreferences: SharedPreferences
    private var subscriber: Subscriber<T>? = null

    override fun onCreate() {
        if (!isInitialised) {
            sharedPreferences = owner.getSharedPreferences()
            initialise()
        }
    }

    private fun initialise() {
        subscriber?.apply {
            sharedPreferences.registerOnSharedPreferenceChangeListener(this@SharedPreferenceProvider)
            launch(owner.backgroundThread()) {
                onChanged(getValue())
            }
        }
    }

    override val isInitialised
        get() = ::sharedPreferences.isInitialized

    override suspend fun getValue(): T {
        if (!isInitialised) {
            throw IllegalStateException("You cannot call getValue until onCreate() has completed successfully")
        }
        return sharedPreferences.getter(key, defaultValue)
    }

    override fun setValue(newValue: T) {
        if (!isInitialised) {
            throw IllegalStateException("You cannot call setValue until onCreate() has completed successfully")
        }
        sharedPreferences.edit().apply {
            setter(key, newValue)
            apply()
        }
    }

    override fun subscribe(subscriber: Subscriber<T>) {
        if (isInitialised) {
            if (this.subscriber == null) {
                sharedPreferences.registerOnSharedPreferenceChangeListener(this)
            }
            async(owner.backgroundThread()) {
                subscriber.onChanged(getValue())
            }
        }
        this.subscriber = subscriber
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        this.key.takeIf { it == key }?.run {
            async(owner.backgroundThread()) {
                subscriber?.onChanged(getValue())
            }
        }
    }

    override fun unsubscribe(subscriber: Subscriber<T>) {
        this.subscriber?.takeIf { it == subscriber }?.also {
            this.subscriber = null
            if (isInitialised) {
                sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
            }
        }
    }

    override fun onDestroy() {
        subscriber?.also {
            sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
        }
    }

    companion object {
        @Suppress("UNCHECKED_CAST")
        internal fun <T : Any> create(owner: PrefektOwner, key: String, defaultValue: T): SharedPreferenceProvider<T> =
                when (defaultValue) {
                    is Boolean -> SharedPreferenceProvider(owner, key, defaultValue,
                            SharedPreferences::getBoolean, SharedPreferences.Editor::putBoolean)
                    is Int -> SharedPreferenceProvider(owner, key, defaultValue,
                            SharedPreferences::getInt, SharedPreferences.Editor::putInt)
                    is Long -> SharedPreferenceProvider(owner, key, defaultValue,
                            SharedPreferences::getLong, SharedPreferences.Editor::putLong)
                    is Float -> SharedPreferenceProvider(owner, key, defaultValue,
                            SharedPreferences::getFloat, SharedPreferences.Editor::putFloat)
                    is String -> SharedPreferenceProvider(owner, key, defaultValue,
                            SharedPreferences::getString, SharedPreferences.Editor::putString)
                    else -> throw IllegalArgumentException("Cannot create SharedPreferenceProvider for type: " +
                            defaultValue::class.java.canonicalName)
                } as SharedPreferenceProvider<T>


    }
}

