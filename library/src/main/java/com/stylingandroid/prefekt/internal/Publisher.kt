package com.stylingandroid.prefekt.internal

import com.stylingandroid.prefekt.Subscriber

internal interface Publisher<out T : Any> {
    fun subscribe(subscriber: Subscriber<T>)
    fun unsubscribe(subscriber: Subscriber<T>)
}

