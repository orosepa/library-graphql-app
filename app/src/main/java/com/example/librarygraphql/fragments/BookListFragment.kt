package com.example.librarygraphql.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.apollographql.apollo.coroutines.await
import com.apollographql.apollo.exception.ApolloException
import com.example.librarygraphql.BookListQuery
import com.example.librarygraphql.databinding.FragmentBookListBinding
import com.example.librarygraphql.models.Book
import com.example.librarygraphql.networking.apolloClient

class BookListFragment : Fragment() {

    private lateinit var binding: FragmentBookListBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBookListBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launchWhenResumed {
            val books = mutableListOf<Book>()
            val response = try {
                apolloClient.query(BookListQuery()).await()
            } catch (e: ApolloException) {
                null
            }

            response?.data?.books?.forEach {
                books.add(Book(it.id, it.title, it.author, it.genre))
            }

            if (response != null && !response.hasErrors()) {
                val adapter = BookListAdapter(books)
                binding.rwBooklist.layoutManager = LinearLayoutManager(requireContext())
                binding.rwBooklist.adapter = adapter
                binding.progressBar.visibility = View.GONE

                adapter.onItemClicked = { book ->
                    findNavController().navigate(
                        BookListFragmentDirections.actionBookListFragmentToBookInfoFragment(
                            book.id,
                            book.title,
                            book.author,
                            book.genre
                        )
                    )
                }
            }
        }
    }
}