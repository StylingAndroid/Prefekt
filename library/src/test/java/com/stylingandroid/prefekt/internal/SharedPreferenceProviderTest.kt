package com.stylingandroid.prefekt.internal

import android.content.SharedPreferences
import com.nhaarman.mockito_kotlin.never
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import com.stylingandroid.prefekt.BaseUnitTest
import com.stylingandroid.prefekt.Subscriber
import kotlinx.coroutines.experimental.runBlocking
import org.amshove.kluent.When
import org.amshove.kluent.any
import org.amshove.kluent.calling
import org.amshove.kluent.itReturns
import org.amshove.kluent.mock
import org.amshove.kluent.shouldNotThrow
import org.amshove.kluent.shouldThrow
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.lang.NullPointerException

const val KEY: String = "KEY"

internal class SharedPreferenceProviderTest : BaseUnitTest() {
    private val sharedPreferences: SharedPreferences = mock()
    private val editor: SharedPreferences.Editor = mock()

    @BeforeEach
    fun setup() {
        When calling sharedPreferences.edit() itReturns editor
    }

    @Test
    @DisplayName("Given a non-supported type When we try to preferenceProvider a SharedPreferenceProvider Then an IllegalArgumentException is thrown")
    fun unsupportedType() {
        {
            SharedPreferenceProvider.create(owner, KEY, 1.0)
        } shouldThrow IllegalArgumentException::class
    }

    @Nested
    @DisplayName("Given a Boolean SharedPreference value")
    inner class BooleanTest : GenericTest<Boolean>(owner, sharedPreferences, KEY, true, false,
            SharedPreferences::getBoolean, SharedPreferences.Editor::putBoolean)

    @Nested
    @DisplayName("Given an Int SharedPreference value")
    inner class IntTest : GenericTest<Int>(owner, sharedPreferences, KEY, 1, 2,
            SharedPreferences::getInt, SharedPreferences.Editor::putInt)

    @Nested
    @DisplayName("Given a Long SharedPreference value")
    inner class LongTest : GenericTest<Long>(owner, sharedPreferences, KEY, 1L, 2L,
            SharedPreferences::getLong, SharedPreferences.Editor::putLong)

    @Nested
    @DisplayName("Given a Float SharedPreference value")
    inner class FloatTest : GenericTest<Float>(owner, sharedPreferences, KEY, 1f, 2f,
            SharedPreferences::getFloat, SharedPreferences.Editor::putFloat)

    @Nested
    @DisplayName("Given a String SharedPreference value")
    inner class StringTest : GenericTest<String>(owner, sharedPreferences, KEY, "Initial", "New",
            SharedPreferences::getString, SharedPreferences.Editor::putString)

    internal abstract class GenericTest<T : Any>(
            private val owner: PrefektOwner,
            private val sharedPreferences: SharedPreferences,
            private val key: String,
            private val initialValue: T,
            private val newValue: T,
            private val getter: SharedPreferences.(String, T) -> T,
            private val setter: SharedPreferences.Editor.(String, T) -> SharedPreferences.Editor) {
        private val provider = SharedPreferenceProvider.create(owner, key, initialValue)
        private val subscriber1: Subscriber<T> = mock()
        private val subscriber2: Subscriber<T> = mock()

        @BeforeEach
        fun setup() {
            When calling owner.getSharedPreferences() itReturns sharedPreferences
            When calling sharedPreferences.getter(key, initialValue) itReturns initialValue
        }

        @Nested
        @DisplayName("When we get the value once")
        inner class GetOnce {

            @BeforeEach
            fun setup() {
                provider.onCreate()
                runBlocking { provider.getValue() }
            }

            @Test
            @DisplayName("Then the getter is called once")
            fun getterCalledOnce() {
                verify(sharedPreferences, times(1)).getter(key, initialValue)
            }

            @Test
            @DisplayName("Then the editor is never used")
            fun editorNeverCalled() {
                verify(sharedPreferences, never()).edit()
            }
        }

        @Nested
        @DisplayName("When we get the value twice")
        inner class GetTwice {

            @BeforeEach
            fun setup() {
                provider.onCreate()
                runBlocking {
                    provider.getValue()
                    provider.getValue()
                }
            }

            @Test
            @DisplayName("Then the getter is called twice")
            fun getterCalledTwice() {
                verify(sharedPreferences, times(2)).getter(key, initialValue)
            }

            @Test
            @DisplayName("Then the editor is never used")
            fun editorNeverCalled() {
                verify(sharedPreferences, never()).edit()
            }
        }

        @Nested
        @DisplayName("When we set the value once")
        inner class SetOnce {
            @BeforeEach
            fun setup() {
                provider.onCreate()
                provider.setValue(newValue)
            }

            @Test
            @DisplayName("Then the getter is never called")
            fun getterNeverCalled() {
                verify(sharedPreferences, never()).getter(key, initialValue)
            }

            @Test
            @DisplayName("Then the setter is called once")
            fun setterCalledOnce() {
                verify(sharedPreferences.edit(), times(1)).setter(key, newValue)
            }

            @Test
            @DisplayName("Then apply is called once")
            fun applyCalledOnce() {
                verify(sharedPreferences.edit(), times(1)).apply()
            }
        }

        @Nested
        @DisplayName("When we set the value twice")
        inner class SetTwice {
            @BeforeEach
            fun setup() {
                provider.onCreate()
                provider.setValue(newValue)
                provider.setValue(newValue)
            }

            @Test
            @DisplayName("Then the getter is never called")
            fun getterNeverCalled() {
                verify(sharedPreferences, never()).getter(key, initialValue)
            }

            @Test
            @DisplayName("Then the setter is called twice")
            fun setterCalledOnce() {
                verify(sharedPreferences.edit(), times(2)).setter(key, newValue)
            }

            @Test
            @DisplayName("Then apply is called twice")
            fun applyCalledOnce() {
                verify(sharedPreferences.edit(), times(2)).apply()
            }
        }

        @Nested
        @DisplayName("When we subscribe")
        inner class Subscribe {
            @BeforeEach
            fun setup() {
                provider.onCreate()
                provider.subscribe(subscriber1)
            }

            @Test
            @DisplayName("Then we subscribed for SharedPreference changes once")
            fun registerOnce() {
                verify(sharedPreferences, times(1)).registerOnSharedPreferenceChangeListener(provider)
            }
        }

        @Nested
        @DisplayName("When we unsubscribe")
        inner class Unsubscribe {
            @BeforeEach
            fun setup() {
                provider.onCreate()
                provider.subscribe(subscriber1)
                provider.unsubscribe(subscriber1)
            }

            @Test
            @DisplayName("Then we unsubscribe for SharedPreference changes once")
            fun unregisterOnce() {
                verify(sharedPreferences, times(1)).unregisterOnSharedPreferenceChangeListener(provider)
            }

            @Test
            @DisplayName("Then we we don't throw an NPE")
            fun doesNotThrow() {
                { provider.onSharedPreferenceChanged(sharedPreferences, key) } shouldNotThrow NullPointerException::class
            }

            @Test
            @DisplayName("Then the subscriber is only notified once")
            fun subscriberNotifiedOnce() {
                provider.onSharedPreferenceChanged(sharedPreferences, key)
                verify(subscriber1, times(1)).onChanged(initialValue)
            }
        }

        @Nested
        @DisplayName("When we subscribe twice")
        inner class SubscribeTwice {
            @BeforeEach
            fun setup() {
                provider.onCreate()
                provider.subscribe(subscriber1)
                provider.subscribe(subscriber2)
            }

            @Test
            @DisplayName("Then we subscribed for SharedPreference changes once")
            fun registerOnce() {
                verify(sharedPreferences, times(1)).registerOnSharedPreferenceChangeListener(provider)
            }
        }

        @Nested
        @DisplayName("When we subscribe twice and unsubscribe once")
        inner class SubscribeTwiceUnsubscribeOnce {
            @BeforeEach
            fun setup() {
                provider.onCreate()
                provider.subscribe(subscriber1)
                provider.subscribe(subscriber2)
                provider.unsubscribe(subscriber1)
            }

            @Test
            @DisplayName("Then we never unsubscribe for SharedPreference changes")
            fun unregisterNever() {
                verify(sharedPreferences, never()).unregisterOnSharedPreferenceChangeListener(provider)
            }
        }

        @Nested
        @DisplayName("When we subscribe twice and unsubscribe twice")
        inner class SubscribeTwiceUnsubscribeTwice {
            @BeforeEach
            fun setup() {
                provider.onCreate()
                provider.subscribe(subscriber1)
                provider.subscribe(subscriber2)
                provider.unsubscribe(subscriber1)
                provider.unsubscribe(subscriber2)
            }

            @Test
            @DisplayName("Then we unsubscribe for SharedPreference changes once")
            fun unregisterNever() {
                verify(sharedPreferences, times(1)).unregisterOnSharedPreferenceChangeListener(provider)
            }
        }

        @Nested
        @DisplayName("When there is no subscriber")
        inner class NoSubscriber {

            @Test
            @DisplayName("Then we we don't throw an NPE")
            fun doesNotThrow() {
                { provider.onSharedPreferenceChanged(sharedPreferences, "DUMMY") } shouldNotThrow NullPointerException::class
            }

            @Test
            @DisplayName("Then the subscriber is not notified")
            fun subscriberNotInformed() {
                provider.onSharedPreferenceChanged(sharedPreferences, "DUMMY")
                verify(subscriber1, never()).onChanged(initialValue)
            }
        }

        @Nested
        @DisplayName("When a different key is changed")
        inner class ChangedSame {
            @BeforeEach
            fun setup() {
                provider.onCreate()
                When calling sharedPreferences.getter(key, initialValue) itReturns initialValue
                provider.setValue(initialValue)
                provider.subscribe(subscriber1)
                provider.onSharedPreferenceChanged(sharedPreferences, "DUMMY")
            }

            @Test
            @DisplayName("Then the subscriber is notified once")
            fun subscriberNotifiedOnce() {
                verify(subscriber1, times(1)).onChanged(initialValue)
            }
        }

        @Nested
        @DisplayName("When the correct key is changed")
        inner class ChangedDifferent {
            @BeforeEach
            fun setup() {
                provider.onCreate()
                When calling sharedPreferences.getter(key, initialValue) itReturns newValue
                provider.setValue(initialValue)
                provider.subscribe(subscriber1)
                provider.onSharedPreferenceChanged(sharedPreferences, key)
            }

            @Test
            @DisplayName("Then the subscriber is notified twice")
            fun subscriberNotifiedTwice() {
                verify(subscriber1, times(2)).onChanged(newValue)
            }
        }

        @Nested
        @DisplayName("When we subscribe and unsubscribe before initialisation completes")
        inner class SubscribeUnsubscribeBeforeInitiasation {
            @BeforeEach
            fun setup() {
                provider.subscribe(subscriber1)
                provider.unsubscribe(subscriber1)
            }

            @Test
            @DisplayName("Then we don't subscribe for SharedPref updates")
            fun doNotSubscribe() {
                verify(sharedPreferences, never()).registerOnSharedPreferenceChangeListener(any())
            }

            @Test
            @DisplayName("Then we don't unsubscribe for SharedPref updates")
            fun doNotUnsubscribe() {
                verify(sharedPreferences, never()).unregisterOnSharedPreferenceChangeListener(any())
            }
        }

        @Nested
        @DisplayName("When we unsubscribe a different subscriber")
        inner class UnsubscribeDifferentSubscriber {
            @BeforeEach
            fun setup() {
                provider.onCreate()
                provider.subscribe(subscriber1)
                provider.unsubscribe(subscriber2)
            }

            @Test
            @DisplayName("Then we don't unsubscribe for SharedPref updates")
            fun doNotUnsubscribe() {
                verify(sharedPreferences, never()).unregisterOnSharedPreferenceChangeListener(any())
            }
        }

        @Nested
        @DisplayName("When onCreate() is called twice")
        inner class OnCreateCalledTwice {
            @BeforeEach
            fun setup() {
                provider.onCreate()
                provider.onCreate()
            }

            @Test
            @DisplayName("Then we only initialise once")
            fun doNotSubscribe() {
                verify(owner, times(1)).getSharedPreferences()
            }
        }

        @Nested
        @DisplayName("When initialise is called with a subscriber")
        inner class InitialiseWithSubscriber {
            @BeforeEach
            fun setup() {
                provider.subscribe(subscriber1)
                provider.onCreate()
            }

            @Test
            @DisplayName("Then we subscribe for SharedPref updates")
            fun subscribe() {
                verify(sharedPreferences, times(1)).registerOnSharedPreferenceChangeListener(any())
            }

            @Test
            @DisplayName("Then the value is updated")
            fun update() {
                verify(subscriber1, times(1)).onChanged(initialValue)
            }
        }

        @Nested
        @DisplayName("When the context is destroyed")
        inner class OnDestroy {
            @BeforeEach
            fun setup() {
                provider.onCreate()
            }

            @Test
            @DisplayName("Then we unsubscribe for SharedPref updates if there's an active subscriber")
            fun subscribe() {
                provider.subscribe(subscriber1)
                provider.onDestroy()

                verify(sharedPreferences, times(1)).unregisterOnSharedPreferenceChangeListener(any())
            }

            @Test
            @DisplayName("Then we don't unsubscribe for SharedPref updates if there's no active subscriber")
            fun update() {
                provider.onDestroy()

                verify(sharedPreferences, never()).unregisterOnSharedPreferenceChangeListener(any())
            }
        }

        @Test
        @DisplayName("When we getValue on an uninitialised object Then and exception is thrown")
        fun subscribe() {
            {
                runBlocking {
                    provider.getValue()
                }
            } shouldThrow IllegalStateException::class
        }

        @Test
        @DisplayName("When we setValue on an uninitialised object Then and exception is thrown")
        fun update() {
            {
                provider.setValue(newValue)
            } shouldThrow IllegalStateException::class
        }

    }
}
