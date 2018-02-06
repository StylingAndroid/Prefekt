package com.stylingandroid.prefekt

import android.os.Bundle
import android.support.v4.app.FragmentActivity
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.launch

const val TEST_KEY = "TEST_KEY"
const val TEST_VALUE = "Hello World!"

class TestActivity : FragmentActivity() {
    val prefekt = prefektForeground(TEST_KEY, TEST_VALUE) {
        println("Changed: $it")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        launch(CommonPool) {
            prefekt.getValue().also {
                println("onCreate(): $it")
            }
        }
    }
}
