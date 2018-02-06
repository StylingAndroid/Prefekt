package com.stylingandroid.prefekt.internal

import android.arch.lifecycle.Observer
import com.nhaarman.mockito_kotlin.never
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import com.stylingandroid.prefekt.BaseUnitTest
import com.stylingandroid.prefekt.InstantTaskExecutorExtension
import kotlinx.coroutines.experimental.runBlocking
import org.amshove.kluent.When
import org.amshove.kluent.any
import org.amshove.kluent.calling
import org.amshove.kluent.itReturns
import org.amshove.kluent.mock
import org.amshove.kluent.shouldBe
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

private const val DEFAULT_VALUE = "DEFAULT_VALUE"
private const val NEW_VALUE = "NEW_VALUE"

@ExtendWith(InstantTaskExecutorExtension::class)
internal class PrefektLiveDataTest : BaseUnitTest() {
    private val provider: Provider<String> = mock()
    private val observer: Observer<String> = mock()
    private val liveData: PrefektLiveData<String> = PrefektLiveData(owner, provider)

    @Nested
    @DisplayName("Given an uninitialised provider")
    inner class UninitialisedProvider {
        @BeforeEach
        fun setup() {
            When calling provider.isInitialised itReturns false
        }

        @Test
        @DisplayName("When we call isInitialised Then it returns false")
        fun isInitialisedReturnsFalse() {
            liveData.isInitialised shouldBe false
        }

        @Test
        @DisplayName("When we call getValue Then it requests the value from the provider")
        fun getValueCallsProvider() {
            runBlocking {
                liveData.getValueBlocking()
                verify(provider, times(1)).getValue()
            }
        }

        @Test
        @DisplayName("When onCreate is called Then we call onCreate on the provider")
        fun onCreateCallsOnCreateOnProvider() {
            liveData.onCreate()

            verify(provider, times(1)).onCreate()
        }

        @Test
        @DisplayName("When onActive is called Then we subscribe to the provider")
        fun onActivateSubscribesToProvider() {
            liveData.onCreate()
            liveData::class.java.getDeclaredMethod("onActive")?.apply {
                invoke(liveData)
            }

            verify(provider, times(1)).subscribe(any())
        }

        @Test
        @DisplayName("When onInactive is called Then we un-subscribe from the provider")
        fun onInactiveUnSubscribesFromProvider() {
            liveData.onCreate()
            liveData::class.java.getDeclaredMethod("onActive")?.apply {
                invoke(liveData)
            }
            liveData::class.java.getDeclaredMethod("onInactive")?.apply {
                invoke(liveData)
            }

            verify(provider, times(1)).unsubscribe(any())
        }

        @Test
        @DisplayName("When onDestroy is called Then we call onDestroy on the provider")
        fun onDestroyCallsOnDestroyOnProvider() {
            liveData.onCreate()
            liveData.onDestroy()

            verify(provider, times(1)).onDestroy()
        }
    }

    @Nested
    @DisplayName("Given an initialised provider")
    inner class InitialisedProvider {
        @BeforeEach
        fun setup() {
            When calling provider.isInitialised itReturns true
        }

        @Test
        @DisplayName("When we call isInitialised Then it returns true")
        fun isInitialisedReturnsFalse() {
            liveData.isInitialised shouldBe true
        }
    }

    @Nested
    @DisplayName("Given an initialised liveData instance")
    inner class InitialisedLiveData {
        @BeforeEach
        fun setup() {
            When calling provider.isInitialised itReturns true
            When calling owner.isMainThread itReturns true
            liveData.setValue(DEFAULT_VALUE)
        }

        @Test
        @DisplayName("When we call isInitialised Then it returns true")
        fun isInitialisedReturnsFalse() {
            liveData.isInitialised shouldBe true
        }

        @Test
        @DisplayName("When we call getValue Then it does not request the value from the provider")
        fun getValueCallsProvider() {
            runBlocking {
                liveData.getValueBlocking()
                verify(provider, never()).getValue()
            }
        }

        @Test
        @DisplayName("When we call setValue Then it updates the internal value")
        fun setValueUpdatesValue() {
            liveData.setValue(NEW_VALUE)

            runBlocking {
                liveData.getValueBlocking() shouldBe NEW_VALUE
            }
        }

        @Test
        @DisplayName("When we call setValue Then it updates the provider")
        fun setValueUpdatesProvider() {
            liveData.setValue(NEW_VALUE)

            verify(provider, times(1)).setValue(NEW_VALUE)
        }

        @Test
        @DisplayName("When we call setValue twice Then it only updates the provider once")
        fun setValueTwiceUpdatesProviderOnce() {
            liveData.setValue(NEW_VALUE)
            liveData.setValue(NEW_VALUE)

            verify(provider, times(1)).setValue(NEW_VALUE)
        }
    }

}
