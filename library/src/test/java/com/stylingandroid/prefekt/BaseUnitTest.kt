package com.stylingandroid.prefekt

import android.arch.lifecycle.Lifecycle
import com.nhaarman.mockito_kotlin.argumentCaptor
import com.nhaarman.mockito_kotlin.atLeast
import com.nhaarman.mockito_kotlin.capture
import com.nhaarman.mockito_kotlin.verify
import com.stylingandroid.prefekt.internal.PrefektOwner
import kotlinx.coroutines.experimental.Unconfined
import org.amshove.kluent.When
import org.amshove.kluent.calling
import org.amshove.kluent.itReturns
import org.amshove.kluent.mock
import org.junit.jupiter.api.BeforeEach

internal open class BaseUnitTest {
    protected val owner: PrefektOwner = mock()

    @BeforeEach
    fun baseSetup() {
        When calling owner.backgroundThread() itReturns Unconfined
        When calling owner.mainThread() itReturns Unconfined
    }
}
