/*
 * Copyright (C) 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.systemui.biometrics.ui.viewmodel

import androidx.test.filters.SmallTest
import com.android.systemui.SysuiTestCase
import com.android.systemui.coroutines.collectLastValue
import com.android.systemui.flags.Flags
import com.android.systemui.flags.featureFlagsClassic
import com.android.systemui.keyguard.ui.transitions.DeviceEntryIconTransition
import com.android.systemui.keyguard.ui.viewmodel.deviceEntryIconViewModelTransitionsMock
import com.android.systemui.kosmos.testScope
import com.android.systemui.statusbar.phone.SystemUIDialogManager
import com.android.systemui.statusbar.phone.systemUIDialogManager
import com.android.systemui.testKosmos
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations

@OptIn(ExperimentalCoroutinesApi::class)
@SmallTest
@RunWith(JUnit4::class)
class DeviceEntryUdfpsTouchOverlayViewModelTest : SysuiTestCase() {
    val kosmos =
        testKosmos().apply {
            featureFlagsClassic.apply { set(Flags.FULL_SCREEN_USER_SWITCHER, true) }
        }
    val testScope = kosmos.testScope

    private val testDeviceEntryIconTransitionAlpha = MutableStateFlow(0f)
    private val testDeviceEntryIconTransition: DeviceEntryIconTransition
        get() =
            object : DeviceEntryIconTransition {
                override val deviceEntryParentViewAlpha: Flow<Float> =
                    testDeviceEntryIconTransitionAlpha.asStateFlow()
            }

    init {
        kosmos.deviceEntryIconViewModelTransitionsMock.add(testDeviceEntryIconTransition)
    }
    val systemUIDialogManager = kosmos.systemUIDialogManager
    private val underTest = kosmos.deviceEntryUdfpsTouchOverlayViewModel

    @Captor
    private lateinit var sysuiDialogListenerCaptor: ArgumentCaptor<SystemUIDialogManager.Listener>

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
    }

    @Test
    fun dialogShowing_shouldHandleTouchesFalse() =
        testScope.runTest {
            val shouldHandleTouches by collectLastValue(underTest.shouldHandleTouches)

            testDeviceEntryIconTransitionAlpha.value = 1f
            runCurrent()

            verify(systemUIDialogManager).registerListener(sysuiDialogListenerCaptor.capture())
            sysuiDialogListenerCaptor.value.shouldHideAffordances(true)

            assertThat(shouldHandleTouches).isFalse()
        }

    @Test
    fun transitionAlphaIsSmall_shouldHandleTouchesFalse() =
        testScope.runTest {
            val shouldHandleTouches by collectLastValue(underTest.shouldHandleTouches)

            testDeviceEntryIconTransitionAlpha.value = .3f
            runCurrent()

            verify(systemUIDialogManager).registerListener(sysuiDialogListenerCaptor.capture())
            sysuiDialogListenerCaptor.value.shouldHideAffordances(false)

            assertThat(shouldHandleTouches).isFalse()
        }

    @Test
    fun alphaFullyShowing_noDialog_shouldHandleTouchesTrue() =
        testScope.runTest {
            val shouldHandleTouches by collectLastValue(underTest.shouldHandleTouches)

            testDeviceEntryIconTransitionAlpha.value = 1f
            runCurrent()

            verify(systemUIDialogManager).registerListener(sysuiDialogListenerCaptor.capture())
            sysuiDialogListenerCaptor.value.shouldHideAffordances(false)

            assertThat(shouldHandleTouches).isTrue()
        }
}
