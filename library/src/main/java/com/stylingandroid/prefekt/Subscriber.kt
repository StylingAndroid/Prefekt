package com.stylingandroid.prefekt

interface Subscriber<in T : Any> {
    fun onChanged(newValue: T)
}
