package com.stylingandroid.prefekt.internal

import android.arch.lifecycle.MutableLiveData

internal abstract class BlockingMutableLiveData<T : Any> : MutableLiveData<T>(), LifecycleListener {
    abstract suspend fun getValueBlocking(): T
    abstract val isInitialised: Boolean
}
