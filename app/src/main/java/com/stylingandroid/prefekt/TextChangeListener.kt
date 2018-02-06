package com.stylingandroid.prefekt

import android.text.Editable
import android.text.TextWatcher

internal class TextChangeListener(private val onChanged: (String) -> Unit) : TextWatcher {

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        //NO-OP
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        //NO-OP
    }

    override fun afterTextChanged(s: Editable?) {
        s?.toString()?.apply {
            onChanged(this)
        }
    }
}
