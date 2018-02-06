package com.stylingandroid.prefekt.internal

import com.nhaarman.mockito_kotlin.never
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import com.stylingandroid.prefekt.Subscriber
import org.amshove.kluent.mock
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("Given an AggregatePublisher<String> instance" )
internal class AggregatePublisherTest {
    private val aggregatePublisher = AggregatePublisher<String>()

    private val subscriber1: Subscriber<String> = mock()
    private val subscriber2: Subscriber<String> = mock()

    private val string1 = "String 1"
    private val string2 = "String 2"

    @Nested
    @DisplayName("When we add a subscriber")
    inner class AddSubscriber {
        @BeforeEach
        fun setup() {
            aggregatePublisher.subscribe(subscriber1)
        }

        @Test
        @DisplayName("Then onChange is called once when we update")
        fun updateOnce() {
            aggregatePublisher.onChanged(string1)

            verify(subscriber1, times(1)).onChanged(string1)
        }

        @Test
        @DisplayName("Then onChange is called twice when we update twice")
        fun updateTwice() {
            aggregatePublisher.onChanged(string1)
            aggregatePublisher.onChanged(string1)

            verify(subscriber1, times(2)).onChanged(string1)
        }
    }

    @Nested
    @DisplayName("When we add two subscribers")
    inner class AddTwoSubscribers {
        @BeforeEach
        fun setup() {
            aggregatePublisher.subscribe(subscriber1)
            aggregatePublisher.subscribe(subscriber2)
        }

        @Test
        @DisplayName("Then onChange is called once on each when we update")
        fun updateOnce() {
            aggregatePublisher.onChanged(string1)

            verify(subscriber1, times(1)).onChanged(string1)
            verify(subscriber2, times(1)).onChanged(string1)
        }

        @Test
        @DisplayName("Then onChange is called twice on each when we update twice")
        fun updateTwice() {
            aggregatePublisher.onChanged(string1)
            aggregatePublisher.onChanged(string1)

            verify(subscriber1, times(2)).onChanged(string1)
            verify(subscriber2, times(2)).onChanged(string1)
        }
    }

    @Nested
    @DisplayName("When we add two subscribers and remove one")
    inner class AddTwoSubscribersRemoveOne {
        @BeforeEach
        fun setup() {
            aggregatePublisher.subscribe(subscriber1)
            aggregatePublisher.subscribe(subscriber2)
            aggregatePublisher.unsubscribe(subscriber2)
        }

        @Test
        @DisplayName("Then onChange is called once only on the subscribed one when we update")
        fun updateOnce() {
            aggregatePublisher.onChanged(string1)

            verify(subscriber1, times(1)).onChanged(string1)
            verify(subscriber2, never()).onChanged(string1)
        }

        @Test
        @DisplayName("Then onChange is called twice only on the subscribed one  when we update twice")
        fun updateTwice() {
            aggregatePublisher.onChanged(string1)
            aggregatePublisher.onChanged(string1)

            verify(subscriber1, times(2)).onChanged(string1)
            verify(subscriber2, never()).onChanged(string1)
        }
    }

    @Nested
    @DisplayName("When we add two subscribers and remove the same one twice")
    inner class AddTwoSubscribersRemoveOneTwice {
        @BeforeEach
        fun setup() {
            aggregatePublisher.subscribe(subscriber1)
            aggregatePublisher.subscribe(subscriber2)
            aggregatePublisher.unsubscribe(subscriber2)
            aggregatePublisher.unsubscribe(subscriber2)
        }

        @Test
        @DisplayName("Then onChange is called once only on the subscribed one when we update")
        fun updateOnce() {
            aggregatePublisher.onChanged(string1)

            verify(subscriber1, times(1)).onChanged(string1)
            verify(subscriber2, never()).onChanged(string1)
        }

        @Test
        @DisplayName("Then onChange is called twice only on the subscribed one  when we update twice")
        fun updateTwice() {
            aggregatePublisher.onChanged(string1)
            aggregatePublisher.onChanged(string1)

            verify(subscriber1, times(2)).onChanged(string1)
            verify(subscriber2, never()).onChanged(string1)
        }
    }

    @Nested
    @DisplayName("When we add two subscribers and remove both")
    inner class AddTwoSubscribersRemoveBoth {
        @BeforeEach
        fun setup() {
            aggregatePublisher.subscribe(subscriber1)
            aggregatePublisher.subscribe(subscriber2)
            aggregatePublisher.unsubscribe(subscriber1)
            aggregatePublisher.unsubscribe(subscriber2)
        }

        @Test
        @DisplayName("Then onChange is never called on either when we update")
        fun updateOnce() {
            aggregatePublisher.onChanged(string1)

            verify(subscriber1, never()).onChanged(string1)
            verify(subscriber2, never()).onChanged(string1)
        }

        @Test
        @DisplayName("Then onChange is never called on either when we update twice")
        fun updateTwice() {
            aggregatePublisher.onChanged(string1)
            aggregatePublisher.onChanged(string1)

            verify(subscriber1, never()).onChanged(string1)
            verify(subscriber2, never()).onChanged(string1)
        }
    }

    @Nested
    @DisplayName("When we remove an ubsubscribed subscriber")
    inner class Remove {
        @BeforeEach
        fun setup() {
            aggregatePublisher.unsubscribe(subscriber1)
        }

        @Test
        @DisplayName("Then onChange is never called when we update")
        fun updateOnce() {
            aggregatePublisher.onChanged(string1)

            verify(subscriber1, never()).onChanged(string1)
        }

        @Test
        @DisplayName("Then onChange is never called when we update twice")
        fun updateTwice() {
            aggregatePublisher.onChanged(string1)

            verify(subscriber1, never()).onChanged(string1)
        }
    }
}
