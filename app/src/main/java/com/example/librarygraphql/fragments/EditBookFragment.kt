package com.example.librarygraphql.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.apollographql.apollo.coroutines.await
import com.apollographql.apollo.exception.ApolloException
import com.example.librarygraphql.EditBookMutation
import com.example.librarygraphql.databinding.FragmentEditBookBinding
import com.example.librarygraphql.networking.apolloClient
import com.example.librarygraphql.type.InputBook


class EditBookFragment : Fragment() {
    private lateinit var binding: FragmentEditBookBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditBookBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args: BookInfoFragmentArgs by navArgs()

        val bookId = args.bookId
        val bookTitle = args.bookTitle
        val bookAuthor = args.bookAuthor
        val bookGenre = args.bookGenre

        binding.twEditId.text = "ID: $bookId"
        binding.inputEditTitle.hint = bookTitle
        binding.inputEditAuthor.hint = bookAuthor
        binding.inputEditGenre.hint = bookGenre

        binding.btnSubmit.setOnClickListener {
            val title = binding.inputEditTitle.text
            val author = binding.inputEditAuthor.text
            val genre = binding.inputEditGenre.text

            if (title.isEmpty() or author.isEmpty() or genre.isEmpty()) {
                Toast.makeText(context, "Please fill in data", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else {
                lifecycleScope.launchWhenResumed {

                    val response = try {
                        apolloClient.mutate(
                            EditBookMutation(
                                bookId, InputBook(
                                    title = title.toString(),
                                    author = author.toString(),
                                    genre = genre.toString()))
                        ).await()
                    } catch (e: ApolloException) { null }

                    if (response?.data?.update == true) {
                        Toast.makeText(context, "Successfully updated", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Unable to update", Toast.LENGTH_SHORT).show()
                    }
                    findNavController().navigate(
                        EditBookFragmentDirections.actionEditBookFragmentToBookInfoFragment(
                            bookId,
                            title.toString(),
                            author.toString(),
                            genre.toString()
                        )
                    )
                }
            }
        }
    }
}