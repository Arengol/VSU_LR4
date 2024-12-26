package ru.vsu.vladimir.vsu_lr4.data

import android.database.Cursor
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.time.temporal.ChronoField
import java.util.Date

@Entity (tableName = "books",
    indices = [Index("uid")],
    foreignKeys = [
        ForeignKey(
            entity = StatusEntity::class,
            parentColumns = ["uid"],
            childColumns = ["statusId"],
            onDelete = ForeignKey.CASCADE
        )
    ])
data class BookEntity(
    @PrimaryKey(autoGenerate = true) val uid: Long = 0,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "author") val author: String,
    @ColumnInfo(name = "year") val year: LocalDate,
    @ColumnInfo(name = "statusId") val statusId: Long? = null
) {
    val createdYearFormatted : String
        get() =year.format(DateTimeFormatter.ofPattern("yyyy"))
}

fun convertToYear(str: String) = LocalDate.parse(str, DateTimeFormatterBuilder()
    .appendPattern("yyyy").parseDefaulting(ChronoField.MONTH_OF_YEAR, 1)
    .parseDefaulting(ChronoField.DAY_OF_MONTH, 1)
    .toFormatter())

fun convertToYearF(str: String) = LocalDate.parse(str, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
fun cursorToBook(cursor: Cursor): BookEntity {
    val uidIndex = cursor.getColumnIndexOrThrow("uid")
    val titleIndex = cursor.getColumnIndexOrThrow("title")
    val authorIndex = cursor.getColumnIndexOrThrow("author")
    val yearIndex = cursor.getColumnIndexOrThrow("year")
    val statusIdIndex = cursor.getColumnIndexOrThrow("statusId")

    val uid = cursor.getLong(uidIndex)
    val title = cursor.getString(titleIndex)
    val author = cursor.getString(authorIndex)
    val year = convertToYearF(cursor.getString(yearIndex))
    val statusId = cursor.getLong(statusIdIndex)

    return BookEntity(
        uid = uid,
        title = title,
        author = author,
        year = year,
        statusId = statusId
    )
}

fun cursorToBookList(cursor: Cursor): List<BookEntity> {
    val books = mutableListOf<BookEntity>()
    while (cursor.moveToNext()) {
        val user = cursorToBook(cursor)
        books.add(user)
    }
    return books
}
