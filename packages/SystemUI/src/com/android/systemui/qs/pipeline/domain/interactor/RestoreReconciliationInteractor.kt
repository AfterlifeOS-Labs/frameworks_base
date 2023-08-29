package com.android.systemui.qs.pipeline.domain.interactor

import com.android.systemui.dagger.SysUISingleton
import com.android.systemui.dagger.qualifiers.Application
import com.android.systemui.dagger.qualifiers.Background
import com.android.systemui.qs.pipeline.data.repository.AutoAddRepository
import com.android.systemui.qs.pipeline.data.repository.QSSettingsRestoredRepository
import com.android.systemui.qs.pipeline.data.repository.TileSpecRepository
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch

/**
 * Interactor in charge of triggering reconciliation after QS Secure Settings are restored. For a
 * given user, it will trigger the reconciliations in the correct order to prevent race conditions.
 *
 * Currently, the order is:
 * 1. TileSpecRepository, with the restored data and the current (before restore) auto add tiles
 * 2. AutoAddRepository
 *
 * [start] needs to be called to trigger the collection of [QSSettingsRestoredRepository].
 */
@SysUISingleton
class RestoreReconciliationInteractor
@Inject
constructor(
    private val tileSpecRepository: TileSpecRepository,
    private val autoAddRepository: AutoAddRepository,
    private val qsSettingsRestoredRepository: QSSettingsRestoredRepository,
    @Application private val applicationScope: CoroutineScope,
    @Background private val backgroundDispatcher: CoroutineDispatcher,
) {

    @OptIn(ExperimentalCoroutinesApi::class)
    fun start() {
        applicationScope.launch(backgroundDispatcher) {
            qsSettingsRestoredRepository.restoreData.flatMapConcat { data ->
                autoAddRepository.autoAddedTiles(data.userId)
                        .take(1)
                        .map { tiles -> data to tiles }
            }.collect { (restoreData, autoAdded) ->
                tileSpecRepository.reconcileRestore(restoreData, autoAdded)
                autoAddRepository.reconcileRestore(restoreData)
            }
        }
    }
}
