package ru.vsu.vladimir.vsu_lr4.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface BookDao {
    @Query("SELECT * FROM books")
    suspend fun getAllBooks(): List<BookEntity>

    @Insert
    suspend fun insertBook(bookEntity: BookEntity): Long

    @Delete
    suspend fun deleteBook(bookEntity: BookEntity)

    @Update
    suspend fun updateBook(bookEntity: BookEntity)
}
@Dao
interface StatusDao {
    @Query("SELECT * FROM statuses WHERE uid = :statusId")
    suspend fun getStatusesOfBooks(statusId: Long): StatusEntity
    @Insert
    suspend fun insetStatus(statusEntity: StatusEntity): Long
    @Delete
    suspend fun deleteStatus(statusEntity: StatusEntity)
    @Update
    suspend fun updateStatus(statusEntity: StatusEntity)
}