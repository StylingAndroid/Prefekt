package com.stylingandroid.prefekt.internal

import android.arch.lifecycle.MutableLiveData
import com.nhaarman.mockito_kotlin.spy
import org.amshove.kluent.mock
import org.amshove.kluent.shouldBe
import org.junit.jupiter.api.Test

private const val DEFAULT_VALUE = "DEFAULT_VALUE"

internal class PrefektViewModelTest {
    private val owner: PrefektOwner = mock()
    private val cache: MutableMap<String, MutableLiveData<*>> = spy(mutableMapOf())
    private val viewModel: PrefektViewModel = PrefektViewModel(cache)

    @Test
    fun test1() {
        cache.isEmpty() shouldBe true
    }

    @Test
    fun test2() {
        viewModel.getLiveData(owner, KEY, DEFAULT_VALUE)

        cache.isNotEmpty() shouldBe true
    }

    @Test
    fun test3() {
        val liveData1 = viewModel.getLiveData(owner, KEY, DEFAULT_VALUE)
        val liveData2 = viewModel.getLiveData(owner, KEY, DEFAULT_VALUE)

        assert(liveData1 === liveData2)
    }
}
