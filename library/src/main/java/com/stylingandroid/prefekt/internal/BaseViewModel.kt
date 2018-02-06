package com.stylingandroid.prefekt.internal

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel

internal abstract class BaseViewModel(
        private val cache: MutableMap<String, MutableLiveData<*>> = mutableMapOf()
) : ViewModel() {

    fun <T : Any> getLiveData(owner: PrefektOwner, key: String, default: T) =
            buildKey(key, default).let { compoundKey ->
                cache[compoundKey] ?:
                createLiveData(owner, key, default).also { cache[compoundKey] = it }
            }

    private fun <T : Any> buildKey(key: String, default: T, qualifier: String = "default"): String =
            "$key::${default::class.java.canonicalName}::$qualifier"

    protected abstract fun <T : Any> createLiveData(owner: PrefektOwner, key: String, default: T): PrefektLiveData<T>
}
