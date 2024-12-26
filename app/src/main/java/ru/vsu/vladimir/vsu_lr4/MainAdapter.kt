package ru.vsu.vladimir.vsu_lr4

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.qualifiers.ActivityContext
import ru.vsu.vladimir.vsu_lr4.data.BookEntity
import javax.inject.Inject

class MainAdapter @Inject constructor(
    @ActivityContext var context: Context
): RecyclerView.Adapter<MainAdapter.ViewHolder>() {
    var data: List<BookEntity> = listOf()

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val bookTitle = itemView.findViewById<TextView>(R.id.book_title)
        val bookAuthor = itemView.findViewById<TextView>(R.id.book_author)
        val bookYear = itemView.findViewById<TextView>(R.id.book_year)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_book, parent, false)
        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val book = data[position]
        holder.apply {
            bookTitle.text = book.title
            bookYear.text = book.year.year.toString()
            bookAuthor.text = book.author
            itemView.setOnClickListener {
                context.startActivity(Intent(context, ReviewActivity::class.java).apply {
                    putExtras(Bundle().apply {
                        putBoolean("STATE", false)
                        putLong("ID", book.uid)
                    })
                })
            }
        }
    }
}