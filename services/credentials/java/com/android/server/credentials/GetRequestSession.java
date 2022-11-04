/*
 * Copyright (C) 2022 The Android Open Source Project
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

package com.android.server.credentials;

import android.annotation.Nullable;
import android.content.ComponentName;
import android.content.Context;
import android.credentials.GetCredentialRequest;
import android.credentials.GetCredentialResponse;
import android.credentials.IGetCredentialCallback;
import android.credentials.ui.ProviderData;
import android.credentials.ui.RequestInfo;
import android.os.RemoteException;
import android.service.credentials.CredentialProviderInfo;
import android.util.Log;

import java.util.ArrayList;

/**
 * Central session for a single getCredentials request. This class listens to the
 * responses from providers, and the UX app, and updates the provider(S) state.
 */
public final class GetRequestSession extends RequestSession<GetCredentialRequest,
        IGetCredentialCallback>
        implements ProviderSession.ProviderInternalCallback<GetCredentialResponse> {
    private static final String TAG = "GetRequestSession";

    public GetRequestSession(Context context, int userId,
            IGetCredentialCallback callback, GetCredentialRequest request,
            String callingPackage) {
        super(context, userId, request, callback, RequestInfo.TYPE_GET, callingPackage);
    }

    /**
     * Creates a new provider session, and adds it list of providers that are contributing to
     * this session.
     * @return the provider session created within this request session, for the given provider
     * info.
     */
    @Override
    @Nullable
    public ProviderSession initiateProviderSession(CredentialProviderInfo providerInfo,
            RemoteCredentialService remoteCredentialService) {
        ProviderGetSession providerGetSession = ProviderGetSession
                .createNewSession(mContext, mUserId, providerInfo,
                        this, remoteCredentialService);
        if (providerGetSession != null) {
            Log.i(TAG, "In startProviderSession - provider session created and being added");
            mProviders.put(providerGetSession.getComponentName().flattenToString(),
                    providerGetSession);
        }
        return providerGetSession;
    }

    @Override
    protected void launchUiWithProviderData(ArrayList<ProviderData> providerDataList) {
        mHandler.post(() -> mCredentialManagerUi.show(RequestInfo.newGetRequestInfo(
                mRequestId, null, mIsFirstUiTurn, ""),
                providerDataList));
    }

    @Override // from provider session
    public void onProviderStatusChanged(ProviderSession.Status status,
            ComponentName componentName) {
        super.onProviderStatusChanged(status, componentName);
    }


    @Override
    public void onFinalResponseReceived(ComponentName componentName,
            GetCredentialResponse response) {
        Log.i(TAG, "onFinalCredentialReceived from: " + componentName.flattenToString());
        if (response != null) {
            respondToClientAndFinish(response);
        }
    }

    private void respondToClientAndFinish(GetCredentialResponse response) {
        Log.i(TAG, "respondToClientAndFinish");
        try {
            mClientCallback.onResponse(response);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        finishSession();
    }
}
