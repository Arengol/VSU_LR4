package ru.vsu.vladimir.vsu_lr4.data

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.database.MatrixCursor
import android.net.Uri
import android.util.Log
import androidx.room.Room
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Singleton
import kotlin.math.log


class DataBaseContentProvider: ContentProvider() {
    private lateinit var db: AppDatabase
    private lateinit var bookDao: BookDao
    private lateinit var statusDao: StatusDao

    override fun onCreate(): Boolean {
        context?.let {
            db = Room.databaseBuilder(
                context =it.applicationContext, klass = AppDatabase::class.java, "vsu_LR4.db"
            ).build()
            bookDao = db.BookDao()
            statusDao = db.StatusDao()
        }
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        val cursor: MatrixCursor
        when (uri.pathSegments[0]) {
            "books" -> {
                cursor = MatrixCursor(arrayOf("uid", "title", "author", "year", "statusId"))
                runBlocking(Dispatchers.IO) {
                    val books = bookDao.getAllBooks()
                    books.forEach { book ->
                        cursor.addRow(
                            arrayOf(
                                book.uid,
                                book.title,
                                book.author,
                                book.year.toString(),
                                book.statusId
                            )
                        )
                    }
                }
            }
            "status" -> {
                val statusId = uri.lastPathSegment?.toLong() ?: return null
                cursor = MatrixCursor(arrayOf("uid", "readingStatus", "start", "end", "rate"))
                runBlocking(Dispatchers.IO) {
                    val status = statusDao.getStatusesOfBooks(statusId)
                    cursor.addRow(
                        arrayOf(
                            status.uid,
                            status.readingStatus,
                            status.start.toString(),
                            status.end.toString(),
                            status.rate
                        )
                    )
                }
            }
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
        return cursor
    }

    override fun getType(uri: Uri): String {
        return when (uri.pathSegments[0]) {
            "books" -> "vnd.android.cursor.dir/vnd.ru.vsu.vladimir.books"
            "status" -> "vnd.android.cursor.dir/vnd.ru.vsu.vladimir.status"
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        var uriResult: Uri? = null
        when (uri.pathSegments[0]) {
            "books" -> {
                val book = BookEntity(
                    title = values?.getAsString("title") ?: "",
                    author = values?.getAsString("author") ?: "",
                    year = convertToYear(values?.getAsString("year") ?: ""),
                    statusId = (values?.getAsLong("statusId") ?: 0).toLong()
                )
                runBlocking(Dispatchers.IO) {
                    val bookId = bookDao.insertBook(book)
                    uriResult = Uri.withAppendedPath(uri, bookId.toString())
                }
            }
            "status" -> {
                val pattern = DateTimeFormatter.ofPattern("yyyy.MM.dd")
                val status = StatusEntity(
                    readingStatus = values?.getAsInteger("readingStatus") ?: 0,
                    start = LocalDate.parse(values?.getAsString("start"), pattern),
                    end = LocalDate.parse(values?.getAsString("end"), pattern),
                    rate = values?.getAsInteger("rate") ?: 0
                )
                runBlocking(Dispatchers.IO) {
                    val statusId = statusDao.insetStatus(status)
                    uriResult = Uri.withAppendedPath(uri, statusId.toString())
                }
            }
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
        return uriResult
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        val count: Int
        when (uri.pathSegments[0]) {
            "books" -> {
                val bookId = selectionArgs?.get(0)?.toLong() ?: return 0
                val statusId = selectionArgs?.get(0)?.toLong() ?: return 0
                val book = BookEntity(
                    uid = bookId,
                    title = "",
                    author = "",
                    year = LocalDate.now(),
                    statusId = 0
                )
                val status = StatusEntity(
                    uid = statusId,
                    readingStatus = 0,
                    start = LocalDate.now(),
                    end = LocalDate.now(),
                    rate = 0
                )
                runBlocking(Dispatchers.IO) {
                    bookDao.deleteBook(book)
                    statusDao.deleteStatus(status)
                }
                count = 1
            }
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
        return count
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int {
        val count: Int
        when (uri.pathSegments[0]) {
            "books" -> {
                val bookId = selectionArgs?.first()?.toLong() ?: return 0
                val book = BookEntity(
                    uid = bookId,
                    title = values?.getAsString("title") ?: "",
                    author = values?.getAsString("author") ?: "",
                    year = convertToYear(values?.getAsString("year") ?: ""),
                    statusId = (values?.getAsLong("statusId") ?: 0).toLong()
                )
                runBlocking(Dispatchers.IO) {
                    bookDao.updateBook(book)
                }
                count = 1
            }
            "status" -> {
                val statusId = selectionArgs?.first()?.toLong() ?: return 0
                val pattern = DateTimeFormatter.ofPattern("yyyy.MM.dd")
                val status = StatusEntity(
                    uid = statusId,
                    readingStatus = values?.getAsInteger("readingStatus") ?: 0,
                    start = LocalDate.parse(values?.getAsString("start"), pattern),
                    end = LocalDate.parse(values?.getAsString("end"), pattern),
                    rate = values?.getAsInteger("rate") ?: 0
                )
                runBlocking(Dispatchers.IO) {
                    statusDao.updateStatus(status)
                }
                count = 1
            }
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
        return count
    }
}