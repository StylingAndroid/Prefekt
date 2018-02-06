package com.stylingandroid.prefekt.internal

import android.arch.lifecycle.MutableLiveData

internal open class PrefektViewModel(
        cache: MutableMap<String, MutableLiveData<*>> = mutableMapOf()
) : BaseViewModel(cache) {

    override fun <T : Any> createLiveData(owner: PrefektOwner, key: String, default: T): PrefektLiveData<T> =
            PrefektLiveData(owner, SharedPreferenceProvider.create(owner, key, default))

}
