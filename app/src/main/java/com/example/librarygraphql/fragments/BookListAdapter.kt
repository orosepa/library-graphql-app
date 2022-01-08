package com.example.librarygraphql.fragments

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.librarygraphql.BookListQuery
import com.example.librarygraphql.databinding.ItemBookListBinding
import com.example.librarygraphql.models.Book

class BookListAdapter(
//    private val books: List<BookListQuery.Book>
    private val books: List<Book>
) : RecyclerView.Adapter<BookListAdapter.ViewHolder>() {

    var onItemClicked: ((Book) -> Unit)? = null

    class ViewHolder(val binding: ItemBookListBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemBookListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val book = books[position]

        holder.binding.twBookName.text = book.title
        holder.binding.twBookAuthor.text = book.author
        holder.binding.twBookGenre.text = book.genre

        holder.binding.root.setOnClickListener {
            onItemClicked?.invoke(book)
        }
    }

    override fun getItemCount(): Int {
        return books.size
    }
}