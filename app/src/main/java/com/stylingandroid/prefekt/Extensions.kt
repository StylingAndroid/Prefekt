package com.stylingandroid.prefekt

import android.widget.CheckedTextView
import android.widget.TextView

internal fun TextView.updateText(newValue: CharSequence) {
    if (!hasFocus()) {
        setText(newValue, TextView.BufferType.NORMAL)
    }
}

