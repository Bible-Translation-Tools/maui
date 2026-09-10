package org.bibletranslationtools.maui.jvm.ui.batch

import com.github.thomasnield.rxkotlinfx.subscribeOnFx
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.collections.transformation.FilteredList
import javafx.collections.transformation.SortedList
import org.bibletranslationtools.maui.common.data.Batch
import org.bibletranslationtools.maui.common.persistence.IBatchRepository
import org.bibletranslationtools.maui.common.persistence.IDirectoryProvider
import org.bibletranslationtools.maui.common.usecases.FileProcessingRouter
import org.bibletranslationtools.maui.common.usecases.batch.DeleteBatch
import org.bibletranslationtools.maui.common.usecases.batch.UpdateBatch
import org.bibletranslationtools.maui.jvm.di.IDependencyGraphProvider
import org.bibletranslationtools.maui.jvm.mappers.MediaMapper
import org.bibletranslationtools.maui.jvm.ui.*
import org.bibletranslationtools.maui.jvm.ui.upload.UploadPage
import org.slf4j.LoggerFactory
import tornadofx.*
import java.io.File
import java.util.function.Predicate
import javax.inject.Inject

class BatchViewModel : ViewModel() {
    private val logger = LoggerFactory.getLogger(javaClass)

    @Inject lateinit var mediaMapper: MediaMapper
    @Inject lateinit var fileProcessRouter: FileProcessingRouter
    @Inject lateinit var directoryProvider: IDirectoryProvider
    @Inject lateinit var batchRepository: IBatchRepository
    @Inject lateinit var deleteBatch: DeleteBatch
    @Inject lateinit var updateBatch: UpdateBatch

    private val navigator: NavigationMediator by inject()
    private val batchDataStore: BatchDataStore by inject()
    private val importFilesViewModel: ImportFilesViewModel by inject()
    private val dialogViewModel: DialogViewModel by inject()

    private val batches = observableListOf<Batch>()
    private val filteredBatches = FilteredList(batches)
    val sortedBatches = SortedList(filteredBatches)
    val searchQueryProperty = SimpleStringProperty()

    val appTitleProperty = SimpleStringProperty()
    val uploadTargetProperty = SimpleObjectProperty<UploadTarget>()
    val uploadTargets = observableListOf<UploadTarget>()

    init {
        (app as IDependencyGraphProvider).dependencyGraph.inject(this)

        appTitleProperty.bind(batchDataStore.appTitleProperty)
        uploadTargetProperty.bindBidirectional(batchDataStore.uploadTargetProperty)
        uploadTargets.bind(batchDataStore.uploadTargets) { it }

        setupBatchSearchListener()
    }

    fun onDock() {
        loadBatches()
    }

    fun onUndock() {
    }

    fun onDropFiles(files: List<File>) {
        importFilesViewModel.onDropFiles(files, ImportType.CREATE)
    }

    fun openBatch(batch: Batch) {
        batchDataStore.activeBatchProperty.set(batch)
        navigator.dock<UploadPage>()
    }

    fun deleteBatch(batch: Batch) {
        deleteBatch.delete(batch)
            .subscribeOn(Schedulers.io())
            .doOnError {
                logger.error("Error in deleteBatch", it)
            }
            .subscribe {
                batches.remove(batch)
                dialogViewModel.showSuccess(
                    messages["deleteSuccessful"],
                    messages["batchDeleted"],
                    batch.name
                )
            }
    }

    fun editBatchName(batch: Batch, name: String) {
        if (batch.name != name) {
            val newBatch = batch.copy(name = name)
            updateBatch.update(newBatch)
                .subscribeOn(Schedulers.io())
                .doOnError {
                    logger.error("Error in editBatchName", it)
                }
                .subscribe {
                    batches.remove(batch)
                    batches.add(newBatch)
                }
        }
    }

    private fun loadBatches() {
        batches.clear()

        sortedBatches.comparator = compareByDescending { it.created }

        Single.fromCallable {
            batchRepository.getAll()
        }
            .observeOn(Schedulers.io())
            .subscribeOnFx()
            .subscribe { list ->
                batches.addAll(list)
            }
    }

    private fun setupBatchSearchListener() {
        searchQueryProperty.onChange { q ->
            val query = q?.trim()

            if (query.isNullOrEmpty()) {
                filteredBatches.predicate = Predicate { true }
            } else {
                filteredBatches.predicate = Predicate { batch ->
                    batch.name.contains(query, true)
                }
            }
        }
    }
}