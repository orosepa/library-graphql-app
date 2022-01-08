package com.example.librarygraphql.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.apollographql.apollo.coroutines.await
import com.apollographql.apollo.exception.ApolloException
import com.example.librarygraphql.FetchByGenreQuery
import com.example.librarygraphql.databinding.FragmentSearchByGenreBinding
import com.example.librarygraphql.models.Book
import com.example.librarygraphql.networking.apolloClient

class SearchByGenreFragment : Fragment() {

    private lateinit var binding: FragmentSearchByGenreBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchByGenreBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launchWhenResumed {
            binding.button.setOnClickListener {
                onSearchClicked(binding.inputGenre.text.toString())
            }
        }
    }

    private fun onSearchClicked(genre: String) {
        val books = mutableListOf<Book>()

        lifecycleScope.launchWhenResumed {
            val response = try {
                binding.progressBar.visibility = View.VISIBLE
                apolloClient.query(FetchByGenreQuery(genre)).await()
            } catch (e: ApolloException) {
                null
            }

            response?.data?.booksByGenre?.forEach {
                books.add(Book(it.id, it.title, it.author, it.genre))
            }

            if (response != null && !response.hasErrors()) {

                if (books.isEmpty()) {
                    binding.twNoBooks.visibility = View.VISIBLE
                    return@launchWhenResumed
                }

                val adapter = BookListAdapter(books)
                binding.rwBooklist.layoutManager = LinearLayoutManager(requireContext())
                binding.rwBooklist.adapter = adapter
                binding.progressBar.visibility = View.GONE

                adapter.onItemClicked = { book ->
                    findNavController().navigate(
                        SearchByGenreFragmentDirections.actionSearchByGenreFragmentToBookInfoFragment(
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