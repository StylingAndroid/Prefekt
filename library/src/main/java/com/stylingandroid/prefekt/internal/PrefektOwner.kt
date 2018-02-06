package com.stylingandroid.prefekt.internal

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Looper
import android.preference.PreferenceManager
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.CoroutineDispatcher
import kotlinx.coroutines.experimental.android.UI

internal class PrefektOwner(
        private val lifecycleOwner: LifecycleOwner,
        private val constructor: () -> PrefektViewModel = { PrefektViewModel() },
        private val mainDispatcher: CoroutineDispatcher = UI,
        private val backgroundDispatcher: CoroutineDispatcher = CommonPool
) : LifecycleOwner by lifecycleOwner {

    @Suppress("UNCHECKED_CAST")
    fun <T : Any> getLiveData(key: String, default: T) =
            viewModelProvider(constructor).getLiveData(this, key, default) as BlockingMutableLiveData<T>

    private inline fun <reified VM : ViewModel> viewModelProvider(crossinline provider: () -> VM) =
                object : ViewModelProvider.Factory {
                    @Suppress("UNCHECKED_CAST")
                    override fun <T : ViewModel> create(modelClass: Class<T>): T =
                            provider() as T
                }.let {
                    ViewModelProviders.of(activity, it).get(VM::class.java)
                } as PrefektViewModel

    private val activity: FragmentActivity
        get() = when (lifecycleOwner) {
            is Fragment -> lifecycleOwner.activity
            is FragmentActivity -> lifecycleOwner
            else -> null
        } ?: throw IllegalStateException("Unable to obtain FragmentActivity instance")

    fun getSharedPreferences() =
            context?.let {
                PreferenceManager.getDefaultSharedPreferences(it)
            } ?: throw RuntimeException(
                    "Unable to obtain Application Context. " +
                            "Please ensure that the context has been created before attempting to use this Prefekt data"
            )

    private val context: Context?
        get() = when (lifecycleOwner) {
            is Fragment -> lifecycleOwner.context?.applicationContext
            is FragmentActivity -> lifecycleOwner.applicationContext
            else -> null
        }

    fun mainThread(): CoroutineDispatcher = mainDispatcher

    fun backgroundThread(): CoroutineDispatcher = backgroundDispatcher

    val isMainThread: Boolean
        get() = Looper.getMainLooper().thread === Thread.currentThread()
}
