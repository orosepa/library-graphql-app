package com.example.librarygraphql.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.apollographql.apollo.coroutines.await
import com.apollographql.apollo.exception.ApolloException
import com.example.librarygraphql.CreateBookMutation
import com.example.librarygraphql.databinding.FragmentAddBookBinding
import com.example.librarygraphql.networking.apolloClient
import com.example.librarygraphql.type.InputBook

class AddBookFragment : Fragment() {
    private lateinit var binding: FragmentAddBookBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddBookBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSubmit.setOnClickListener {
            val title = binding.inputAddTitle.text
            val author = binding.inputAddAuthor.text
            val genre = binding.inputAddGenre.text

            if (title.isEmpty() or author.isEmpty() or genre.isEmpty()) {
                Toast.makeText(context, "Please fill in data", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else {
                lifecycleScope.launchWhenResumed {

                    val response = try {
                        apolloClient.mutate(CreateBookMutation(
                            InputBook(title = title.toString(), author = author.toString(), genre = genre.toString()))
                        ).await()
                    } catch (e: ApolloException) { null }

                    if (response?.data?.create == true) {
                        Toast.makeText(context, "Successfully created", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Unable to create", Toast.LENGTH_SHORT).show()
                    }
                    findNavController().navigate(
                        AddBookFragmentDirections.actionAddBookFragmentToBookListFragment()
                    )
                }
            }
        }
    }
}