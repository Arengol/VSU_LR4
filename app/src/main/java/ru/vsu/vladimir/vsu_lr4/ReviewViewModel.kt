package ru.vsu.vladimir.vsu_lr4

import android.app.Application
import android.content.ContentResolver
import android.content.ContentValues
import android.util.Log
import androidx.core.net.toUri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import ru.vsu.vladimir.vsu_lr4.data.BookEntity
import ru.vsu.vladimir.vsu_lr4.data.DataBaseContentProvider
import ru.vsu.vladimir.vsu_lr4.data.StatusEntity
import ru.vsu.vladimir.vsu_lr4.data.cursorToBookList
import ru.vsu.vladimir.vsu_lr4.data.cursorToStatus
import ru.vsu.vladimir.vsu_lr4.data.cursorToStatusList
import javax.inject.Inject

class ReviewViewModel(application: Application, val targetUid: Long) : AndroidViewModel(application) {
    private val contentResolver: ContentResolver = application.contentResolver

    private var _book: MutableStateFlow<BookEntity?> = MutableStateFlow(null)
    val book: StateFlow<BookEntity?> get() = _book

    private var _status: MutableStateFlow<StatusEntity?> = MutableStateFlow(null)
    val status: StateFlow<StatusEntity?> get() = _status
    var statusID: Long = 0
    fun init() {
        var cursor = contentResolver.query("content://ru.vsu.vladimir.vsu_lr4.data.DataBaseContentProvider/books".toUri(),
            null,
            null,
            null,
            null) ?: throw IllegalArgumentException("Unknown URI")
        _book.value = cursorToBookList(cursor).find { it.uid == targetUid }
        cursor = contentResolver.query("content://ru.vsu.vladimir.vsu_lr4.data.DataBaseContentProvider/status/$targetUid".toUri(),
            null,
            null,
            null,
            null) ?: throw IllegalArgumentException("Unknown URI")
        _status.value = cursorToStatusList(cursor).first()
    }
    fun insertBook(book: BookEntity) {
        val bookValues = ContentValues().apply {
            put("title", book.title)
            put("author", book.author)
            put("year", book.createdYearFormatted)
            put("statusId", statusID)
        }
        contentResolver.insert("content://ru.vsu.vladimir.vsu_lr4.data.DataBaseContentProvider/books".toUri(),
            bookValues
        )
    }
    fun insertStatus(status: StatusEntity) {
        val statusValue = ContentValues().apply {
            put("readingStatus", status.readingStatus.toString())
            put("start", status.createdStartFormatted)
            put("end", status.createdEndFormatted)
            put("rate", status.rate.toString())
        }
        val uri = contentResolver.insert("content://ru.vsu.vladimir.vsu_lr4.data.DataBaseContentProvider/status".toUri(),
            statusValue
        )
        statusID =  uri!!.lastPathSegment!!.toLong()
    }
    fun update(book: BookEntity, status: StatusEntity) {
        val bookValues = ContentValues().apply {
            put("uid", targetUid)
            put("title", book.title)
            put("author", book.author)
            put("year", book.createdYearFormatted)
            put("statusId", _status.value!!.uid)
        }
        contentResolver.update("content://ru.vsu.vladimir.vsu_lr4.data.DataBaseContentProvider/books".toUri(),
            bookValues,
            "uid = ?",
            arrayOf(targetUid.toString())
        )
        val statusValue = ContentValues().apply {
            put("uid", _status.value!!.uid)
            put("readingStatus", status.readingStatus.toString())
            put("start", status.createdStartFormatted)
            put("end", status.createdEndFormatted)
            put("rate", status.rate.toString())
        }
        contentResolver.update("content://ru.vsu.vladimir.vsu_lr4.data.DataBaseContentProvider/status".toUri(),
            statusValue,
            "uid = ?",
            arrayOf(_status.value!!.uid.toString())
        )
    }
    fun deleteBook() {
        contentResolver.delete("content://ru.vsu.vladimir.vsu_lr4.data.DataBaseContentProvider/books".toUri(), "uid = ?", arrayOf(targetUid.toString()))
    }
}