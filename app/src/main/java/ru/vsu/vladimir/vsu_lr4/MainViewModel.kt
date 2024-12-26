package ru.vsu.vladimir.vsu_lr4

import android.app.Application
import android.content.ContentResolver
import androidx.core.net.toUri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import ru.vsu.vladimir.vsu_lr4.data.BookEntity
import ru.vsu.vladimir.vsu_lr4.data.DataBaseContentProvider
import ru.vsu.vladimir.vsu_lr4.data.cursorToBookList
import javax.inject.Inject

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val contentResolver: ContentResolver = application.contentResolver

    private var _data: MutableStateFlow<MutableList<BookEntity>> = MutableStateFlow(
        mutableListOf()
    )
    val data: StateFlow<List<BookEntity>> get() = _data

    fun getAllBooks(){
        val cursor = contentResolver.query("content://ru.vsu.vladimir.vsu_lr4.data.DataBaseContentProvider/books".toUri(),
            null,
            null,
            null,
            null) ?: throw IllegalArgumentException("Unknown URI")
        _data.value = cursorToBookList(cursor).toMutableList()
    }
    fun sortByDate() {
       val temp = _data.value.toMutableList()
           temp.sortByDescending { it.year }
        _data.value = temp
    }
    fun sortByTitle() {
        val temp = _data.value.toMutableList()
        temp.sortBy { it.title }
        _data.value = temp
    }
}