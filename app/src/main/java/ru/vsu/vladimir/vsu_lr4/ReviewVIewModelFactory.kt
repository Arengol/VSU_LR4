package ru.vsu.vladimir.vsu_lr4

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ReviewVIewModelFactory(
    private val application: Application,
    private val targetUid: Long
): ViewModelProvider.Factory  {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ReviewViewModel::class.java)) {
            return ReviewViewModel(application, targetUid) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}