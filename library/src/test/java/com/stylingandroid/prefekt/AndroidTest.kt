package com.stylingandroid.prefekt

import android.app.Application
import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.content.Context
import com.stylingandroid.library.BuildConfig
import org.junit.Rule
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(constants = BuildConfig::class,
        application = AndroidTest.ApplicationStub::class,
        sdk = [21])
abstract class AndroidTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    fun context(): Context {
        return RuntimeEnvironment.application
    }

    internal class ApplicationStub : Application()
}
