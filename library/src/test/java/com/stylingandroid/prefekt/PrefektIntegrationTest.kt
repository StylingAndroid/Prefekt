package com.stylingandroid.prefekt

import android.preference.PreferenceManager
import kotlinx.coroutines.experimental.runBlocking
import org.amshove.kluent.shouldBeEqualTo
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.robolectric.Robolectric
import org.robolectric.android.controller.ActivityController

private const val VALUE = "Hello World!"

class PrefektIntegrationTest : AndroidTest() {
    private lateinit var controller: ActivityController<TestActivity>
    private lateinit var activity: TestActivity

    @Before
    fun setup() {
        controller = Robolectric.buildActivity(TestActivity::class.java)
        activity = controller.get()
        controller.create()
        controller.start()
    }

    @Test
    fun test() {
        controller.resume()
        PreferenceManager.getDefaultSharedPreferences(activity).apply {
            edit().apply {
                putString(TEST_KEY, VALUE)
                commit()
            }
        }
        runBlocking {
            activity.prefekt.getValue() shouldBeEqualTo VALUE
        }
    }

    @After
    fun cleanup() {
        controller.stop()
        controller.destroy()
    }
}
