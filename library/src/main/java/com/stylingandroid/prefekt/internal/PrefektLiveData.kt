package com.stylingandroid.prefekt.internal

import com.stylingandroid.prefekt.Subscriber

internal class PrefektLiveData<T : Any>(
        private val owner: PrefektOwner,
        private val provider: Provider<T>
) : BlockingMutableLiveData<T>(), Subscriber<T> {

    override fun onActive() {
        super.onActive()
        provider.subscribe(this)
    }

    override fun onCreate() {
        super.onCreate()
        provider.onCreate()
    }

    override fun onChanged(newValue: T) {
        when(owner.isMainThread) {
            true -> setValue(newValue)
            else -> postValue(newValue)
        }
    }

    override suspend fun getValueBlocking(): T {
        return super.getValue() ?: provider.getValue()
    }

    override val isInitialised: Boolean
        get() = provider.isInitialised

    override fun setValue(value: T) {
        value.takeIf { it != super.getValue() }?.apply {
            super.setValue(value)
            provider.setValue(value)
        }
    }

    override fun onDestroy() {
        provider.onDestroy()
        super.onDestroy()
    }

    override fun onInactive() {
        provider.unsubscribe(this)
        super.onInactive()
    }
}
