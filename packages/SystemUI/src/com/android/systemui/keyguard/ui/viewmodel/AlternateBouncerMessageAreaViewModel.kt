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

package com.android.systemui.keyguard.ui.viewmodel

import com.android.systemui.bouncer.domain.interactor.AlternateBouncerInteractor
import com.android.systemui.deviceentry.domain.interactor.BiometricMessageInteractor
import com.android.systemui.deviceentry.shared.model.BiometricMessage
import com.android.systemui.deviceentry.shared.model.FaceMessage
import com.android.systemui.deviceentry.shared.model.FaceTimeoutMessage
import com.android.systemui.deviceentry.shared.model.FingerprintLockoutMessage
import com.android.systemui.deviceentry.shared.model.FingerprintMessage
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNot
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.merge

/** View model for the alternate bouncer message area. */
@ExperimentalCoroutinesApi
class AlternateBouncerMessageAreaViewModel
@Inject
constructor(
    biometricMessageInteractor: BiometricMessageInteractor,
    alternateBouncerInteractor: AlternateBouncerInteractor,
) {

    private val faceMessage: Flow<FaceMessage> =
        biometricMessageInteractor.faceMessage.filterNot { it is FaceTimeoutMessage }
    private val fingerprintMessage: Flow<FingerprintMessage> =
        biometricMessageInteractor.fingerprintMessage.filterNot { it is FingerprintLockoutMessage }

    val message: Flow<BiometricMessage?> =
        alternateBouncerInteractor.isVisible.flatMapLatest { isVisible ->
            if (isVisible) {
                merge(
                    faceMessage,
                    fingerprintMessage,
                )
            } else {
                flowOf(null)
            }
        }
}
