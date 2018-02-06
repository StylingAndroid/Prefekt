package com.stylingandroid.prefekt

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.Observer
import android.arch.lifecycle.OnLifecycleEvent
import com.stylingandroid.prefekt.internal.AggregatePublisher
import com.stylingandroid.prefekt.internal.BlockingMutableLiveData
import com.stylingandroid.prefekt.internal.PrefektOwner
import com.stylingandroid.prefekt.internal.Publisher
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import java.util.concurrent.TimeUnit

class Prefekt<T : Any> internal constructor(
        private val owner: PrefektOwner,
        key: String,
        default: T,
        private val aggregatePublisher: AggregatePublisher<T> = AggregatePublisher()
) : Publisher<T> by aggregatePublisher {
    private val observer: PrefektObserver = PrefektObserver(key, default)
    private lateinit var liveData: BlockingMutableLiveData<T>

    init {
        owner.lifecycle.addObserver(observer)
    }

    suspend fun getValue(): T =
            if (isInitialised) {
                liveData.getValueBlocking()
            } else {
                getAfterInit().await()
            }

    private val isInitialised: Boolean
        get() = ::liveData.isInitialized && liveData.isInitialised

    private fun getAfterInit(): Deferred<T> = async(owner.backgroundThread()) {
        while (!isInitialised) {
            delay(20, TimeUnit.MILLISECONDS)
        }
        return@async liveData.getValueBlocking()
    }

    fun getValue(callback: (T) -> Unit) {
        launch(owner.backgroundThread()) {
            callback(getValue())
        }
    }

    fun setValue(newValue: T) {
        liveData.value = newValue
    }

    inner class PrefektObserver(
            private val key: String,
            private val default: T
    ) : LifecycleObserver, Observer<T> {

        @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
        fun onCreate() {
            liveData = owner.getLiveData(key, default)
            liveData.onCreate()
            liveData.observe(owner, this)
        }

        override fun onChanged(newValue: T?) {
            newValue?.also { value ->
                launch(owner.mainThread()) {
                    aggregatePublisher.onChanged(value)
                }
            }
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        fun onDestroy() {
            liveData.removeObserver(this)
            liveData.onDestroy()
        }
    }

}
