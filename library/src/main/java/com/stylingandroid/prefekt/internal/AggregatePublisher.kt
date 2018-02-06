package com.stylingandroid.prefekt.internal

import com.stylingandroid.prefekt.Subscriber

internal class AggregatePublisher<T : Any> : Publisher<T>, Subscriber<T> {
    private val subscribers: MutableSet<Subscriber<T>> = mutableSetOf()

    override fun subscribe(subscriber: Subscriber<T>) {
        subscribers += subscriber
    }

    override fun onChanged(newValue: T) {
        subscribers.forEach {
            it.onChanged(newValue)
        }
    }

    override fun unsubscribe(subscriber: Subscriber<T>) {
        subscribers -= subscriber
    }
}
