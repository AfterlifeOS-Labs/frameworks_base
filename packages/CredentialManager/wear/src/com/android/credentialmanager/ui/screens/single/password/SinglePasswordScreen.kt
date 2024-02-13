/*
 * Copyright 2024 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

@file:OptIn(ExperimentalHorologistApi::class)

package com.android.credentialmanager.ui.screens.single.password

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.android.credentialmanager.CredentialSelectorUiState
import com.android.credentialmanager.R
import com.android.credentialmanager.activity.StartBalIntentSenderForResultContract
import com.android.credentialmanager.ui.components.PasswordRow
import com.android.credentialmanager.ui.components.ContinueChip
import com.android.credentialmanager.ui.components.DismissChip
import com.android.credentialmanager.ui.components.SignInHeader
import com.android.credentialmanager.ui.components.SignInOptionsChip
import com.android.credentialmanager.ui.screens.single.SingleAccountScreen
import com.android.credentialmanager.model.get.CredentialEntryInfo
import com.android.credentialmanager.ui.screens.single.UiState
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.compose.layout.ScalingLazyColumnState

/**
 * Screen that shows sign in with provider credential.
 *
 * @param credentialSelectorUiState The app bar view model.
 * @param columnState ScalingLazyColumn configuration to be be applied to SingleAccountScreen
 * @param modifier styling for composable
 * @param viewModel ViewModel that updates ui state for this screen
 * @param navController handles navigation events from this screen
 */
@OptIn(ExperimentalHorologistApi::class)
@Composable
fun SinglePasswordScreen(
    credentialSelectorUiState: CredentialSelectorUiState.Get.SingleEntry,
    columnState: ScalingLazyColumnState,
    modifier: Modifier = Modifier,
    viewModel: SinglePasswordScreenViewModel = hiltViewModel(),
    navController: NavHostController = rememberNavController(),
) {
    viewModel.initialize(credentialSelectorUiState.entry)

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when (val state = uiState) {
        UiState.CredentialScreen -> {
            SinglePasswordScreen(
                credentialSelectorUiState.entry,
                columnState,
                modifier,
                viewModel
            )
        }

        is UiState.CredentialSelected -> {
            val launcher = rememberLauncherForActivityResult(
                StartBalIntentSenderForResultContract()
            ) {
                viewModel.onPasswordInfoRetrieved(it.resultCode, null)
            }

            SideEffect {
                state.intentSenderRequest?.let {
                    launcher.launch(it)
                }
            }
        }

        UiState.Cancel -> {
            // TODO(b/322797032) add valid navigation path here for going back
            navController.popBackStack()
        }
    }
}

@OptIn(ExperimentalHorologistApi::class)
@Composable
private fun SinglePasswordScreen(
    entry: CredentialEntryInfo,
    columnState: ScalingLazyColumnState,
    modifier: Modifier = Modifier,
    viewModel: SinglePasswordScreenViewModel,
) {
    SingleAccountScreen(
        headerContent = {
            SignInHeader(
                icon = entry.icon,
                title = stringResource(R.string.use_password_title),
            )
        },
        accountContent = {
            PasswordRow(
                email = entry.userName,
                modifier = Modifier.padding(top = 10.dp),
            )
        },
        columnState = columnState,
        modifier = modifier.padding(horizontal = 10.dp)
    ) {
        item {
            Column {
                ContinueChip(viewModel::onContinueClick)
                SignInOptionsChip(viewModel::onSignInOptionsClick)
                DismissChip(viewModel::onDismissClick)
            }
        }
    }
}


