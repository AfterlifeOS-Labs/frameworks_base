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

package com.android.systemui.keyguard.domain.interactor

import com.android.systemui.flags.featureFlagsClassic
import com.android.systemui.keyguard.data.repository.keyguardTransitionRepository
import com.android.systemui.kosmos.Kosmos
import com.android.systemui.kosmos.applicationCoroutineScope
import com.android.systemui.kosmos.testDispatcher
import com.android.systemui.power.domain.interactor.powerInteractor
import com.android.systemui.shade.data.repository.shadeRepository
import dagger.Lazy

val Kosmos.fromLockscreenTransitionInteractor by
    Kosmos.Fixture {
        FromLockscreenTransitionInteractor(
            transitionRepository = keyguardTransitionRepository,
            transitionInteractor = keyguardTransitionInteractor,
            scope = applicationCoroutineScope,
            bgDispatcher = testDispatcher,
            mainDispatcher = testDispatcher,
            keyguardInteractor = keyguardInteractor,
            flags = featureFlagsClassic,
            shadeRepository = shadeRepository,
            powerInteractor = powerInteractor,
            glanceableHubTransitions = glanceableHubTransitions,
            inWindowLauncherUnlockAnimationInteractor =
                Lazy { inWindowLauncherUnlockAnimationInteractor },
        )
    }
