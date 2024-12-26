package ru.vsu.vladimir.vsu_lr4.data

import android.database.Cursor
import android.util.Log
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date

@Entity(tableName = "statuses")
data class StatusEntity(
    @PrimaryKey(autoGenerate = true) val uid: Long = 0,
    @ColumnInfo(name = "readingStatus") val readingStatus: Int,
    @ColumnInfo(name = "start") val start: LocalDate?,
    @ColumnInfo(name = "end") val end: LocalDate?,
    @ColumnInfo(name = "rate") val rate: Int?,
) {
    val createdStartFormatted : String
        get() =start?.format(DateTimeFormatter.ofPattern("yyyy.MM.dd")) ?: ""
    val createdEndFormatted : String
        get() =end?.format(DateTimeFormatter.ofPattern("yyyy.MM.dd")) ?: ""

}
   fun convertToDate (str: String): LocalDate {
       val pattern = DateTimeFormatter.ofPattern("yyyy.MM.dd")
       return LocalDate.parse(str, pattern)
   }
   fun convertToDateF (str: String): LocalDate {
        val pattern = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        return LocalDate.parse(str, pattern)
    }
    fun cursorToStatus(cursor: Cursor): StatusEntity {
    val uidIndex = cursor.getColumnIndexOrThrow("uid")
    val readingStatusIndex = cursor.getColumnIndexOrThrow("readingStatus")
    val startIndex = cursor.getColumnIndexOrThrow("start")
    val endIndex = cursor.getColumnIndexOrThrow("end")
    val rateIndex = cursor.getColumnIndexOrThrow("rate")

    val uid = cursor.getLong(uidIndex)
    val readingStatus = cursor.getInt(readingStatusIndex)
    val start = convertToDateF(cursor.getString(startIndex))
    val end = convertToDateF(cursor.getString(endIndex))
    val rate = cursor.getInt(rateIndex)

    return StatusEntity(
        uid = uid,
        readingStatus = readingStatus,
        start = start,
        end = end,
        rate = rate
    )
}

fun cursorToStatusList(cursor: Cursor): List<StatusEntity> {
    val statuses = mutableListOf<StatusEntity>()
    while (cursor.moveToNext()) {
        val status = cursorToStatus(cursor)
        statuses.add(status)
    }
    return statuses
}
