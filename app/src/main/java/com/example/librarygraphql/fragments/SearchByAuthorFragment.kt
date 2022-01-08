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
import com.example.librarygraphql.FetchByAuthorQuery
import com.example.librarygraphql.databinding.FragmentSearchByAuthorBinding
import com.example.librarygraphql.models.Book
import com.example.librarygraphql.networking.apolloClient

class SearchByAuthorFragment : Fragment() {

    private lateinit var binding: FragmentSearchByAuthorBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchByAuthorBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launchWhenResumed {
            binding.button.setOnClickListener {
                onSearchClicked(binding.inputAuthor.text.toString())
            }
        }
    }

    private fun onSearchClicked(author: String) {
        val books = mutableListOf<Book>()

        lifecycleScope.launchWhenResumed {
            val response = try {
                binding.progressBar.visibility = View.VISIBLE
                apolloClient.query(FetchByAuthorQuery(author)).await()
            } catch (e: ApolloException) {
                null
            }

            response?.data?.booksByAuthor?.forEach {
                books.add(Book(it.id, it.title, it.author, it.author))
            }

            if (response != null && !response.hasErrors()) {

                binding.progressBar.visibility = View.GONE

                if (books.isEmpty()) {
                    binding.twNoBooks.visibility = View.VISIBLE
                    return@launchWhenResumed
                }

                val adapter = BookListAdapter(books)
                binding.rwBooklist.layoutManager = LinearLayoutManager(requireContext())
                binding.rwBooklist.adapter = adapter
                binding.twNoBooks.visibility = View.GONE

                adapter.onItemClicked = { book ->
                    findNavController().navigate(
                        SearchByAuthorFragmentDirections.actionSearchByAuthorFragmentToBookInfoFragment(
                            book.id,
                            book.title,
                            book.author,
                            book.author
                        )
                    )
                }
            }
        }
    }
}