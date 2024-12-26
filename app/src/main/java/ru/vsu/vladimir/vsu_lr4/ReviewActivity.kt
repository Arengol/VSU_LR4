package ru.vsu.vladimir.vsu_lr4

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.vsu.vladimir.vsu_lr4.data.BookEntity
import ru.vsu.vladimir.vsu_lr4.data.StatusEntity
import ru.vsu.vladimir.vsu_lr4.data.convertToDate
import ru.vsu.vladimir.vsu_lr4.data.convertToYear
import ru.vsu.vladimir.vsu_lr4.databinding.ActivityReviewBinding

@AndroidEntryPoint
class ReviewActivity: AppCompatActivity() {
    private val binding by lazy {ActivityReviewBinding.inflate(layoutInflater)}
    private var isNew = true
    private lateinit var viewModel: ReviewViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        isNew = intent.extras?.getBoolean("STATE") ?: true
        val targetID =  intent.extras?.getLong("ID") ?: 0
        viewModel = ViewModelProvider(this, ReviewVIewModelFactory(application, targetID))
            .get(ReviewViewModel::class.java)
        if (!isNew) {
            viewModel.init()
        }
        binding.apply {
            buttonDelete.setOnClickListener {
                if (!isNew) {
                    viewModel.deleteBook()
                }
            }
            buttonUpdate.setOnClickListener {
                if (isNew) {
                    val status = StatusEntity(
                        readingStatus = spinnerStatus.selectedItemPosition,
                        rate = spinnerRate.selectedItemPosition,
                        start = convertToDate(editTextDateStart.text.toString()),
                        end = convertToDate(editTextDateEnd.text.toString())
                    )
                    viewModel.insertStatus(status)
                    val book = BookEntity(
                        title = editTextTextTitle.text.toString(),
                        author = editTextTextAuthor.text.toString(),
                        year = convertToYear(editTextDateDate.text.toString()),
                    )
                    viewModel.insertBook(book)
                }
                else {
                    val book = BookEntity(
                        title = editTextTextTitle.text.toString(),
                        author = editTextTextAuthor.text.toString(),
                        year = convertToYear(editTextDateDate.text.toString())
                    )
                    val status = StatusEntity(
                        readingStatus = spinnerStatus.selectedItemPosition,
                        rate = spinnerRate.selectedItemPosition,
                        start = convertToDate(editTextDateStart.text.toString()),
                        end = convertToDate(editTextDateEnd.text.toString())
                    )
                    viewModel.update(book, status)
                }
            }
            lifecycleScope.launch(Dispatchers.Main) {
                viewModel.book.collect {
                    editTextTextTitle.setText(it?.title ?: "")
                    editTextTextAuthor.setText(it?.author ?: "")
                    editTextDateDate.setText(it?.createdYearFormatted ?: "")
                }
            }
            lifecycleScope.launch(Dispatchers.Main) {
                viewModel.status.collect {
                    spinnerStatus.setSelection(it?.readingStatus ?: 0)
                    spinnerRate.setSelection(it?.rate ?: 0)
                    editTextDateStart.setText(it?.createdStartFormatted ?: "")
                    editTextDateEnd.setText(it?.createdEndFormatted ?: "")

                }
            }
        }
    }
}