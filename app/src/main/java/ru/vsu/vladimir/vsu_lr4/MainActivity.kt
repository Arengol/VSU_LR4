package ru.vsu.vladimir.vsu_lr4

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import ru.vsu.vladimir.vsu_lr4.databinding.ActivityMainBinding
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private lateinit var viewModel: MainViewModel
    @Inject
    lateinit var adapter: MainAdapter
    private var filterFlag: Int = 0
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        sharedPreferences = getSharedPreferences("APP_PREFERENCES", MODE_PRIVATE)
        filterFlag = sharedPreferences.getInt("FILTER_FLAG", 0)
        viewModel = ViewModelProvider(this).get(ru.vsu.vladimir.vsu_lr4.MainViewModel::class.java)

        binding.apply {
            recyclerViewBook.layoutManager = LinearLayoutManager(this@MainActivity)
            recyclerViewBook.adapter = adapter

            floatingActionButton.setOnClickListener {
                val intent = Intent(this@MainActivity, ReviewActivity::class.java)
                intent.putExtras(Bundle().apply {
                    putBoolean("STATE", true)
                })
                this@MainActivity.startActivity(intent)
            }

            lifecycleScope.launch {
                viewModel.data.collect {
                    adapter.data = it
                    adapter.notifyDataSetChanged()
                }
            }

            radioGroup.setOnCheckedChangeListener { group, checkedId ->
                when (checkedId) {
                    radioSortDate.id -> {
                        viewModel.sortByDate()
                        filterFlag = 1
                    }
                    radioSortTitle.id -> {
                        viewModel.sortByTitle()
                        filterFlag = 2
                    }
                }
                adapter.notifyDataSetChanged()
            }

        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.getAllBooks()
        when (filterFlag) {
            1 -> binding.radioSortDate.isChecked = true
            2 -> binding.radioSortTitle.isChecked = true
            else -> {}
        }
    }

    override fun onPause() {
        super.onPause()
        sharedPreferences.edit().putInt("FILTER_FLAG",filterFlag).apply()
    }
}