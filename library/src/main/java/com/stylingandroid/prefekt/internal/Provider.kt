package com.stylingandroid.prefekt.internal

internal interface Provider<T : Any> : Publisher<T>, LifecycleListener {
    val isInitialised: Boolean
    suspend fun getValue(): T
    fun setValue(newValue: T)
}
