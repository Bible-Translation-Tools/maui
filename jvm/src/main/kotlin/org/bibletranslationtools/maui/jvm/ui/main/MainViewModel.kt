package org.bibletranslationtools.maui.jvm.ui.main

import com.github.thomasnield.rxkotlinfx.observeOnFx
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleListProperty
import javafx.scene.control.Alert
import org.bibletranslationtools.maui.common.audio.BttrChunk
import org.bibletranslationtools.maui.common.data.*
import org.bibletranslationtools.maui.common.fileprocessor.Mp3Processor
import org.bibletranslationtools.maui.common.fileprocessor.OratureFileProcessor
import org.bibletranslationtools.maui.common.fileprocessor.WavProcessor
import org.bibletranslationtools.maui.common.usecases.*
import org.bibletranslationtools.maui.jvm.client.FtpTransferClient
import org.bibletranslationtools.maui.jvm.io.BooksReader
import org.bibletranslationtools.maui.jvm.io.LanguagesReader
import org.bibletranslationtools.maui.jvm.ui.FileDataItem
import org.bibletranslationtools.maui.jvm.mappers.FileDataMapper
import org.wycliffeassociates.otter.common.audio.wav.WavFile
import org.wycliffeassociates.otter.common.audio.wav.WavMetadata
import tornadofx.*
import java.io.File
import java.text.MessageFormat
import java.util.regex.Pattern
import io.reactivex.rxkotlin.toObservable as toRxObservable

class MainViewModel : ViewModel() {

    val fileDataList = observableListOf<FileDataItem>()
    val fileDataListProperty = SimpleListProperty<FileDataItem>(fileDataList)

    val languages = observableListOf<String>()
    val resourceTypes = ResourceType.values().toList().toObservable()
    val books = observableListOf<String>()
    val mediaExtensions = MediaExtension.values().toList().toObservable()
    val mediaQualities = MediaQuality.values().toList().toObservable()
    val groupings = Grouping.values().toList().toObservable()

    val isProcessing = SimpleBooleanProperty(false)
    val snackBarObservable: PublishSubject<String> = PublishSubject.create()
    val uploadObservable: PublishSubject<Boolean> = PublishSubject.create()

    private val fileProcessRouter = FileProcessingRouter.build()

    init {
        loadLanguages()
        loadBooks()
    }

    fun onDropFiles(files: List<File>) {
        isProcessing.set(true)
        val filesToImport = prepareFilesToImport(files)
        importFiles(filesToImport)
    }

    fun upload() {
        isProcessing.set(true)
        fileDataList.toRxObservable()
            .concatMap { fileDataItem ->
                val fileData = FileDataMapper().toEntity(fileDataItem)
                MakePath(fileData).build()
                    .flatMapCompletable { targetPath ->
                        val transferClient = FtpTransferClient(fileDataItem.file, targetPath)
                        TransferFile(transferClient).transfer()
                    }
                    .andThen(Observable.just(fileDataItem))
                    .doOnError { emitErrorMessage(it, fileDataItem.file) }
                    .onErrorResumeNext(Observable.empty())
            }
            .subscribeOn(Schedulers.io())
            .observeOnFx()
            .buffer(Int.MAX_VALUE)
            .doFinally { isProcessing.set(false) }
            .subscribe {
                fileDataList.removeAll(it)
                uploadObservable.onNext(true)
                showSuccessDialog()
            }
    }

    fun restrictedGroupings(item: FileDataItem): List<Grouping> {
        val groupings = Grouping.values().toList()
        return when {
            item.isContainer -> {
                groupings.filter { it != Grouping.VERSE }
            }
            isChunkOrVerseFile(item.file) -> {
                val bttrChunk = BttrChunk()
                val wavMetadata = WavMetadata(listOf(bttrChunk))
                WavFile(item.file, wavMetadata)
                if (bttrChunk.metadata.mode == Grouping.CHUNK.grouping) {
                    groupings.filter { it != Grouping.CHUNK }
                } else {
                    groupings.filter { it != Grouping.VERSE }
                }
            }
            isChapterFile(item.file) -> {
                groupings.filter { it == Grouping.BOOK }
            }
            else -> listOf()
        }
    }

    private fun prepareFilesToImport(files: List<File>): List<File> {
        val filesToImport = mutableListOf<File>()
        files.forEach { file ->
            file.walk().filter { it.isFile }.forEach {
                filesToImport.add(it)
            }
        }
        return filesToImport
    }

    private fun importFiles(files: List<File>) {
            Observable.fromCallable {
                fileProcessRouter.handleFiles(files)
            }
            .subscribeOn(Schedulers.io())
            .observeOnFx()
            .doFinally { isProcessing.set(false) }
            .subscribe { resultList ->
                resultList.forEach {
                        if (it.status == FileStatus.REJECTED) {
                            emitErrorMessage(
                                    message = "File was not recognized",
                                    fileName = it.requestedFile?.name ?: ""
                            )
                        } else {
                            val item = FileDataMapper().fromEntity(it.data!!)
                            if (!fileDataList.contains(item)) fileDataList.add(item)
                        }
                }
            }
    }

    private fun loadLanguages() {
        LanguagesReader().read()
            .subscribeOn(Schedulers.io())
            .observeOnFx()
            .subscribe { _languages ->
                languages.addAll(_languages)
            }
    }

    private fun loadBooks() {
        BooksReader().read()
            .subscribeOn(Schedulers.io())
            .observeOnFx()
            .subscribe { _books ->
                books.addAll(_books)
            }
    }

    private fun isChunkOrVerseFile(file: File): Boolean {
        val chunkPattern = "_v[\\d]{1,3}(?:-[\\d]{1,3})?"
        val pattern = Pattern.compile(chunkPattern, Pattern.CASE_INSENSITIVE)
        val matcher = pattern.matcher(file.nameWithoutExtension)

        return matcher.find()
    }

    private fun isChapterFile(file: File): Boolean {
        val chapterPattern = "_c([\\d]{1,3})"
        val pattern = Pattern.compile(chapterPattern, Pattern.CASE_INSENSITIVE)
        val matcher = pattern.matcher(file.nameWithoutExtension)

        return matcher.find()
    }

    private fun emitErrorMessage(error: Throwable, file: File) {
        val notImportedText = MessageFormat.format(messages["notImported"], file.name)
        snackBarObservable.onNext("$notImportedText ${error.message ?: ""}")
    }

    private fun emitErrorMessage(message: String, fileName: String) {
        val notImportedText = MessageFormat.format(messages["notImported"], fileName)
        snackBarObservable.onNext("$notImportedText $message")
    }

    private fun showSuccessDialog() {
        Alert(Alert.AlertType.INFORMATION).apply {
            title = messages["successTitle"]
            headerText = null
            contentText = messages["uploadSuccessfull"]

            show()
        }
    }
}
