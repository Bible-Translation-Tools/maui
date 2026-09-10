package org.bibletranslationtools.maui.jvm.ui.upload

import com.github.thomasnield.rxkotlinfx.observeOnFx
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import org.bibletranslationtools.maui.common.data.*
import org.bibletranslationtools.maui.common.io.IBooksReader
import org.bibletranslationtools.maui.common.io.ILanguagesReader
import org.bibletranslationtools.maui.common.io.IResourceTypesReader
import org.bibletranslationtools.maui.common.persistence.*
import org.bibletranslationtools.maui.common.usecases.ExportCsv
import org.bibletranslationtools.maui.common.usecases.FileVerifyingRouter
import org.bibletranslationtools.maui.common.usecases.MakePath
import org.bibletranslationtools.maui.common.usecases.TransferFile
import org.bibletranslationtools.maui.common.usecases.batch.UpdateBatch
import org.bibletranslationtools.maui.jvm.ListenerDisposer
import org.bibletranslationtools.maui.jvm.client.FtpTransferClient
import org.bibletranslationtools.maui.jvm.data.FileStatusFilter
import org.bibletranslationtools.maui.jvm.data.MediaItem
import org.bibletranslationtools.maui.jvm.di.IDependencyGraphProvider
import org.bibletranslationtools.maui.jvm.mappers.MediaMapper
import org.bibletranslationtools.maui.jvm.onChangeAndDoNow
import org.bibletranslationtools.maui.jvm.onChangeWithDisposer
import org.bibletranslationtools.maui.jvm.ui.*
import org.bibletranslationtools.maui.jvm.ui.events.AppSaveDoneEvent
import org.kordamp.ikonli.javafx.FontIcon
import org.kordamp.ikonli.material.Material
import org.kordamp.ikonli.materialdesign.MaterialDesign
import org.slf4j.LoggerFactory
import tornadofx.*
import java.awt.Desktop
import java.io.File
import java.text.MessageFormat
import java.util.function.Predicate
import javax.inject.Inject
import io.reactivex.rxkotlin.toObservable as toRxObservable


class UploadMediaViewModel : ViewModel() {
    private val logger = LoggerFactory.getLogger(javaClass)

    @Inject lateinit var directoryProvider: IDirectoryProvider
    @Inject lateinit var mediaMapper: MediaMapper
    @Inject lateinit var updateBatch: UpdateBatch
    @Inject lateinit var fileVerifyingRouter: FileVerifyingRouter
    @Inject lateinit var languagesReader: ILanguagesReader
    @Inject lateinit var booksReader: IBooksReader
    @Inject lateinit var resourceTypesReader: IResourceTypesReader
    @Inject lateinit var prefRepository: IPrefRepository
    @Inject lateinit var exportCsv: ExportCsv

    private val batchDataStore: BatchDataStore by inject()
    private val importFilesViewModel: ImportFilesViewModel by inject()
    private val dialogViewModel: DialogViewModel by inject()

    private val mediaItems = observableListOf<MediaItem> {
        arrayOf(
            it.selectedProperty,
            it.languageProperty,
            it.resourceTypeProperty,
            it.bookProperty,
            it.chapterProperty,
            it.mediaExtensionProperty,
            it.mediaQualityProperty,
            it.groupingProperty,
            it.statusProperty,
            it.statusMessageProperty
        )
    }
    val filteredMediaItems = SortedFilteredList(mediaItems)

    val uploadTargetProperty = SimpleObjectProperty<UploadTarget>()
    val activeBatchProperty = SimpleObjectProperty<Batch>()
    val appTitleProperty = SimpleStringProperty()
    val uploadTargets = observableListOf<UploadTarget>()
    val batchNameProperty = SimpleStringProperty()
    val uploadedProperty = SimpleBooleanProperty()

    val languages = observableListOf<String>()
    val resourceTypes = observableListOf<String>()
    val books = observableListOf<String>()
    val mediaExtensions = MediaExtension.values().toList().toObservable()
    val mediaQualities = MediaQuality.values().toList().toObservable()
    val groupings = Grouping.values().toList().toObservable()
    val statusFilter = FileStatusFilter.values().toList().toObservable()

    val shouldSaveProperty = SimpleBooleanProperty()
    val defaultPredicate = Predicate<MediaItem> { !it.removed }

    private val listeners = mutableListOf<ListenerDisposer>()

    private val knownServers = mapOf(
        "audiobieldev-ftp.walink.org" to "audiobieldev-content.walink.org/content",
        "audiobiel-ftp.walink.org" to "audio-content.bibleineverylanguage.org/content"
    )

    init {
        (app as IDependencyGraphProvider).dependencyGraph.inject(this)

        batchDataStore.activeBatchProperty.onChangeAndDoNow {
            it?.let { batch ->
                batchNameProperty.set(batch.name)
            }
        }

        uploadTargetProperty.bindBidirectional(batchDataStore.uploadTargetProperty)
        activeBatchProperty.bind(batchDataStore.activeBatchProperty)
        appTitleProperty.bind(batchDataStore.appTitleProperty)
        uploadTargets.bind(batchDataStore.uploadTargets) { it }

        batchNameProperty.onChange {
            it?.let { shouldSaveProperty.set(true) }
        }

        loadLanguages()
        loadResourceTypes()
        loadBooks()
    }

    fun onDock() {
        shouldSaveProperty.set(false)
        uploadedProperty.set(false)
        loadMediaItems()
    }

    fun onUndock() {
        if (shouldSaveProperty.value) {
            saveBatch(true)
        } else {
            mediaItems.clear()
        }
        listeners.forEach(ListenerDisposer::dispose)
        listeners.clear()
    }

    fun onDropFiles(files: List<File>) {
        importFilesViewModel.onDropFiles(files, ImportType.UPDATE)
    }

    fun onNewMedia(media: List<Media>) {
        mediaItems.addAll(
            media.map(mediaMapper::fromEntity).filter {
                !mediaItems.contains(it)
            }
        )
    }

    fun saveBatch(clearMedia: Boolean = false) {
        val newBatch = activeBatchProperty.value.copy(
            name = batchNameProperty.value,
            media = lazy {
                mediaItems.map(mediaMapper::toEntity)
            }
        )
        updateBatch.update(newBatch)
            .subscribeOn(Schedulers.io())
            .doOnError {
                logger.error("Error in saveBatch", it)
            }
            .subscribe {
                shouldSaveProperty.set(false)
                batchDataStore.activeBatchProperty.set(newBatch)
                fire(AppSaveDoneEvent())

                if (clearMedia) mediaItems.clear()
            }
    }

    fun viewUploadedFiles() {
        val server = batchDataStore.serverProperty.value
        val url = knownServers[server] ?: server
        hostServices.showDocument("https://$url")
    }

    fun exportCsv(output: File) {
        dialogViewModel.showProgress(messages["exportingCsv"], messages["exportingCsvMessage"])

        exportCsv.export(
            filteredMediaItems.map(mediaMapper::toEntity),
            output
        )
            .subscribeOn(Schedulers.io())
            .observeOnFx()
            .doFinally {
                dialogViewModel.hideProgress()
            }
            .subscribe({
                dialogViewModel.showConfirm(
                    title = messages["csvExported"],
                    message = MessageFormat.format(messages["csvExportedMessage"], output),
                    details = messages["openCsvMessage"],
                    primaryText = messages["openCsv"],
                    primaryIcon = FontIcon(MaterialDesign.MDI_OPEN_IN_NEW),
                    primaryAction = {
                        Desktop.getDesktop().open(output)
                    }
                )
            }, {
                dialogViewModel.showError(
                    title = messages["csvFailed"],
                    messages["csvFailedErrorMessage"],
                    it.message
                )
            })
    }

    fun removeSelected() {
        dialogViewModel.showConfirm(
            title = messages["removingFiles"],
            message = messages["removingFilesMessage"],
            details = messages["wishToContinue"],
            primaryText = messages["cancel"],
            primaryIcon = FontIcon(MaterialDesign.MDI_CLOSE_CIRCLE),
            secondaryText = messages["remove"],
            secondaryIcon = FontIcon(Material.DELETE_OUTLINE),
            secondaryAction = {
                filteredMediaItems
                    .filter { it.selected }
                    .forEach { it.removed = true }

                filteredMediaItems.filteredItems.apply {
                    predicate = defaultPredicate.and(predicate)
                }
            },
            isWarning = true,
        )
    }

    fun verify() {
        dialogViewModel.showProgress(messages["verifyingFiles"], messages["verifyingFilesMessage"])

        Single.fromCallable {
            filteredMediaItems
                .filter { it.selected }
                .map { item ->
                    Pair(item, fileVerifyingRouter.handleMedia(mediaMapper.toEntity(item)))
                }
        }
            .subscribeOn(Schedulers.io())
            .observeOnFx()
            .doFinally {
                dialogViewModel.hideProgress()
            }
            .subscribe({ items ->
                items.forEach { (item, result) ->
                    item.status = result.status
                    item.statusMessage = result.message
                }

                dialogViewModel.showSuccess(messages["filesVerified"], messages["filesVerifiedMessage"])
            }, {
                dialogViewModel.showError(
                    messages["filesVerified"],
                    messages["filesVerifiedErrorMessage"],
                    it.message
                )
            })
    }

    fun tryUpload() {
        val hasUnverified = filteredMediaItems
            .filter { it.selected }
            .any {
                it.status == null || it.status == FileStatus.REJECTED
            }

        val hasSelected = mediaItems.any { it.selected }

        val server = batchDataStore.serverProperty.value
        val user = batchDataStore.userProperty.value
        val password = batchDataStore.passwordProperty.value

        when {
            hasUnverified -> {
                dialogViewModel.showError(messages["errorOccurred"], messages["hasUnverifiedFilesError"])
            }
            !hasSelected -> {
                dialogViewModel.showError(messages["errorOccurred"], messages["noSelectedFilesError"])
            }
            server.isEmpty() || user.isEmpty() || password.isEmpty() -> {
                dialogViewModel.showLogin {
                    updateLoginCredentials()
                    runLater { tryUpload() }
                }
            }
            else -> doUpload()
        }
    }

    private fun doUpload() {
        val progressProperty = SimpleDoubleProperty()
        dialogViewModel.showProgressWithBar(
            messages["processingUpload"],
            messages["processingUploadMessage"],
            progressProperty
        )

        var current = 1.0
        val total = filteredMediaItems.filter { it.selected }.size

        filteredMediaItems.filter { it.selected }
            .toRxObservable()
            .map(mediaMapper::toEntity)
            .flatMapCompletable { media ->
                MakePath(media).build()
                    .flatMapCompletable { targetPath ->
                        val transferClient = FtpTransferClient(
                            media.file,
                            targetPath,
                            batchDataStore.serverProperty.value,
                            batchDataStore.userProperty.value,
                            batchDataStore.passwordProperty.value
                        )
                        TransferFile(transferClient).transfer().doFinally {
                            progressProperty.set(current / total)
                            current++
                        }
                    }
            }
            .subscribeOn(Schedulers.io())
            .observeOnFx()
            .doFinally {
                dialogViewModel.hideProgress()
            }
            .subscribe({
                uploadedProperty.set(true)
                dialogViewModel.showSuccess(messages["filesUploaded"], messages["filesUploadedMessage"])
            },{
                dialogViewModel.showError(
                    messages["errorOccurred"],
                    messages["uploadFailed"],
                    it.message,
                )

                // Clear password on transfer error to allow user to update credentials
                batchDataStore.passwordProperty.set("")
            })
    }

    private fun loadMediaItems() {
        batchDataStore.activeBatchProperty.value
            ?.media
            ?.value
            ?.map(mediaMapper::fromEntity)
            ?.forEach(mediaItems::add)

        filteredMediaItems.sortedItems.setComparator { o1, o2 ->
            o1.file.name.compareTo(o2.file.name, ignoreCase = true)
        }

        filteredMediaItems.filteredItems.predicate = defaultPredicate

        mediaItems.onChangeWithDisposer {
            shouldSaveProperty.set(true)
        }.also(listeners::add)
    }

    private fun loadLanguages() {
        Single.fromCallable {
            languagesReader.read()
        }
            .subscribeOn(Schedulers.io())
            .observeOnFx()
            .subscribe { list ->
                languages.addAll(list)
            }
    }

    private fun loadResourceTypes() {
        Single.fromCallable {
            resourceTypesReader.read()
        }
            .subscribeOn(Schedulers.io())
            .observeOnFx()
            .subscribe { list ->
                resourceTypes.addAll(list)
            }
    }

    private fun loadBooks() {
        Single.fromCallable {
            booksReader.read()
        }
            .subscribeOn(Schedulers.io())
            .observeOnFx()
            .subscribe { list ->
                books.addAll(list)
            }
    }

    private fun updateLoginCredentials() {
        val server = batchDataStore.serverProperty.value
        val user = batchDataStore.userProperty.value

        when (uploadTargetProperty.value) {
            UploadTarget.DEV -> {
                prefRepository.put(DEV_SERVER_NAME_KEY, server)
                prefRepository.put(DEV_USER_NAME_KEY, user)
            }
            UploadTarget.PROD -> {
                prefRepository.put(PROD_SERVER_NAME_KEY, server)
                prefRepository.put(PROD_USER_NAME_KEY, user)
            }
            else -> {}
        }
    }
}