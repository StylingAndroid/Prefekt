package com.stylingandroid.prefekt

import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.widget.CheckedTextView
import android.widget.TextView
import kotlinx.android.synthetic.main.prefekt_test.*
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.launch

const val KEY_BOOLEAN = "BOOLEAN"
const val KEY_STRING = "STRING"

private const val DEFAULT_BOOLEAN = false
private const val DEFAULT_STRING = "Hello World!"

class MainActivity : AppCompatActivity() {

    private val sharedPreferences by lazy(LazyThreadSafetyMode.NONE) {
        PreferenceManager.getDefaultSharedPreferences(this)
    }

    private var booleanValue = prefekt(KEY_BOOLEAN, DEFAULT_BOOLEAN) {
        prefekt_boolean.isChecked = it
    }

    private var stringValue = prefekt(KEY_STRING, DEFAULT_STRING) {
        prefekt_string.updateText(it)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.prefekt_test)

        createPrefektControls()
        createManualControls()
        launch(CommonPool) {
            println("Value: ${stringValue.getValue()}")
        }
        stringValue.subscribe(StringSubscriber)
        stringValue.getValue {

        }
    }

    private object StringSubscriber : Subscriber<String> {
        override fun onChanged(newValue: String) {
            println("New Value: $newValue")
        }
    }

    private fun createPrefektControls() {
        prefekt_boolean.setOnClickListener {
            prefekt_boolean.isChecked = !prefekt_boolean.isChecked
            booleanValue.setValue(prefekt_boolean.isChecked)
        }
        prefekt_string.addTextChangedListener(TextChangeListener {
            stringValue.setValue(it)
        })
    }

    private fun createManualControls() {
        sp_boolean.setupManualControl(KEY_BOOLEAN, DEFAULT_BOOLEAN)
        sp_string.setupManualControl(KEY_STRING, DEFAULT_STRING)

        sharedPreferences.registerOnSharedPreferenceChangeListener(listener)
    }

    private val listener = SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->
        when (key) {
            KEY_BOOLEAN -> sp_boolean?.isChecked = sharedPreferences.getBoolean(key, DEFAULT_BOOLEAN)
            KEY_STRING -> sp_string?.updateText(sharedPreferences.getString(key, DEFAULT_STRING))
        }
    }

    private fun CheckedTextView.setupManualControl(key: String, value: Boolean) {
        setOnClickListener {
            isChecked = !isChecked
            updatePreference(key, isChecked)
        }
        isChecked = sharedPreferences.getBoolean(key, value)
    }

    private fun TextView.setupManualControl(key: String, defaultValue: String) {
        addTextChangedListener(TextChangeListener {
            updatePreference(key, it)
        })
        sp_string?.setText(sharedPreferences.getString(key, defaultValue))
    }

    private fun <T> updatePreference(key: String, value: T) =
            sharedPreferences.edit().apply {
                when (value) {
                    is Boolean -> putBoolean(key, value)
                    is String -> putString(key, value)
                }
            }.apply()

    override fun onDestroy() {
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(listener)
        super.onDestroy()
    }
}
