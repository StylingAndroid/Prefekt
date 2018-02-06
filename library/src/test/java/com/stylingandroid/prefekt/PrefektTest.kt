package com.stylingandroid.prefekt

import android.arch.lifecycle.Lifecycle
import com.nhaarman.mockito_kotlin.argumentCaptor
import com.nhaarman.mockito_kotlin.atLeast
import com.nhaarman.mockito_kotlin.never
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import com.stylingandroid.prefekt.internal.PrefektLiveData
import kotlinx.coroutines.experimental.TimeoutCancellationException
import kotlinx.coroutines.experimental.runBlocking
import kotlinx.coroutines.experimental.withTimeout
import org.amshove.kluent.When
import org.amshove.kluent.calling
import org.amshove.kluent.itReturns
import org.amshove.kluent.mock
import org.amshove.kluent.shouldBeInstanceOf
import org.amshove.kluent.shouldNotThrow
import org.amshove.kluent.shouldThrow
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

private const val KEY = "KEY"
private const val DEFAULT_VALUE = "DEFAULT_VALUE"
private const val NEW_VALUE = "NEW_VALUE"

private const val BLOCKING_TIMEOUT = 50L

@DisplayName("Given a valid Prefekt object")
internal class PrefektTest : BaseUnitTest() {
    private val liveData: PrefektLiveData<String> = mock()
    private val subscriber: Subscriber<String> = mock()
    private val lifecycle: Lifecycle = mock()
    private val observerCaptor = argumentCaptor<Prefekt<String>.PrefektObserver>()

    private lateinit var prefekt: Prefekt<String>

    @BeforeEach
    fun setup() {
        When calling owner.getLiveData(KEY, DEFAULT_VALUE) itReturns liveData
        When calling owner.lifecycle itReturns lifecycle
        prefekt = Prefekt(owner, KEY, DEFAULT_VALUE)
        observerCaptor.apply {
            verify(lifecycle, atLeast(0)).addObserver(capture())
        }
    }

    @Test
    @DisplayName("When it is created Then it adds an observer")
    fun addObserver() {
        argumentCaptor<Prefekt<String>.PrefektObserver>().apply {
            verify(lifecycle, times(1)).addObserver(capture())
            firstValue shouldBeInstanceOf Prefekt.PrefektObserver::class
        }
    }

    @Test
    @DisplayName("When it is created Then getValue blocks")
    fun getValueBlocks() {

        {
            runBlocking {
                withTimeout(BLOCKING_TIMEOUT) {
                    prefekt.getValue()
                }
            }
        } shouldThrow TimeoutCancellationException::class
    }

    @Test
    @DisplayName("When the Activity has not been created Then the LiveData is not retrieved")
    fun liveDataRetrieved() {
        verify(owner, never()).getLiveData(KEY, DEFAULT_VALUE)
    }

    @Nested
    @DisplayName("When the Activity is created")
    inner class ActivityCreated {
        @BeforeEach
        fun setup() {
            lifecycleAdapter().callMethods(owner, Lifecycle.Event.ON_CREATE, false, null)
        }

        @Test
        @DisplayName("Then the LiveData is retrieved")
        fun liveDataRetrieved() {
            verify(owner, times(1)).getLiveData(KEY, DEFAULT_VALUE)
        }

        @Test
        @DisplayName("Then the LiveData is sent an onCreate event")
        fun liveDataOnCreate() {
            verify(liveData, times(1)).onCreate()
        }

        @Test
        @DisplayName("Then we start observing the LiveData")
        fun liveDataObserve() {
            verify(liveData, times(1)).observe(owner, prefektObserver())
        }
    }

    @Nested
    @DisplayName("When the Activity is destroyed")
    inner class ActivityDestroyed {
        @BeforeEach
        fun setup() {
            lifecycleAdapter().callMethods(owner, Lifecycle.Event.ON_CREATE, false, null)
            lifecycleAdapter().callMethods(owner, Lifecycle.Event.ON_DESTROY, false, null)
        }

        @Test
        @DisplayName("Then the LiveData is sent an onDestroy event")
        fun liveDataOnDestroy() {
            verify(liveData, times(1)).onDestroy()
        }

        @Test
        @DisplayName("Then we stop observing the LiveData")
        fun liveDataObserve() {
            verify(liveData, times(1)).removeObserver(prefektObserver())
        }
    }

    @Nested
    @DisplayName("When the LiveData is not initialised")
    inner class LiveDataNotInitialised {
        @BeforeEach
        fun setup() {
            lifecycleAdapter().callMethods(owner, Lifecycle.Event.ON_CREATE, false, null)
            When calling liveData.isInitialised itReturns false
        }

        @Test
        @DisplayName("Then getValue blocks")
        fun liveDataRetrieved() {
            {
                runBlocking {
                    withTimeout(BLOCKING_TIMEOUT) {
                        prefekt.getValue()
                    }
                }
            } shouldThrow TimeoutCancellationException::class
        }
    }

    @Nested
    @DisplayName("When the LiveData is initialised")
    inner class LiveDataIsInitialised {
        @BeforeEach
        fun setup() {
            lifecycleAdapter().callMethods(owner, Lifecycle.Event.ON_CREATE, false, null)
            When calling liveData.isInitialised itReturns true
            runBlocking {
                When calling liveData.getValueBlocking() itReturns DEFAULT_VALUE
            }
        }

        @Test
        @DisplayName("Then getValue does not block")
        fun liveDataRetrieved() {
            {
                runBlocking {
                    withTimeout(BLOCKING_TIMEOUT) {
                        prefekt.getValue()
                    }
                }
            } shouldNotThrow TimeoutCancellationException::class
        }

        @Test
        @DisplayName("Then setValue sets the LiveData value")
        fun setValue() {
            prefekt.setValue(NEW_VALUE)

            verify(liveData, times(1)).setValue(NEW_VALUE)
        }

        @Test
        @DisplayName("Then an un-subscribed subscriber is not notified onChange")
        fun doNotNotifyUnsubscribeOnChange() {
            prefekt.subscribe(subscriber)
            prefekt.unsubscribe(subscriber)

            prefektObserver().onChanged(NEW_VALUE)

            verify(subscriber, never()).onChanged(NEW_VALUE)
        }

        @Test
        @DisplayName("Then a subscriber is notified onChange")
        fun notifySubscriberOnChange() {
            prefekt.subscribe(subscriber)

            prefektObserver().onChanged(NEW_VALUE)

            verify(subscriber, times(1)).onChanged(NEW_VALUE)
        }
    }

    private fun lifecycleAdapter() =
            Prefekt_PrefektObserver_LifecycleAdapter(prefektObserver())

    private fun prefektObserver() =
            observerCaptor.firstValue

}
