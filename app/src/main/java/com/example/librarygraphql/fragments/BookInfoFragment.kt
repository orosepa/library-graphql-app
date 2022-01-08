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
import com.example.librarygraphql.DeleteBookMutation
import com.example.librarygraphql.databinding.FragmentBookInfoBinding
import com.example.librarygraphql.networking.apolloClient

class BookInfoFragment : Fragment() {

    private lateinit var binding: FragmentBookInfoBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBookInfoBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args: BookInfoFragmentArgs by navArgs()

        binding.twBookInfoName.text = "Title: ${args.bookTitle}"
        binding.twBookInfoAuthor.text = "Author: ${args.bookAuthor}"
        binding.twBookInfoGenre.text = "Genre: ${args.bookGenre}"
        binding.twBookInfoId.text = "UUID: ${args.bookId}"

        binding.btnDelete.setOnClickListener {
            lifecycleScope.launchWhenResumed {
                val response = try {
                    apolloClient.mutate(DeleteBookMutation(args.bookId)).await()
                } catch (e: ApolloException) {
                    null
                }
                if (response?.data?.delete == true) {
                    Toast.makeText(context, "Successfully deleted", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Unable to delete", Toast.LENGTH_SHORT).show()
                }
                findNavController().navigate(
                    BookInfoFragmentDirections.actionBookInfoFragmentToBookListFragment()
                )
            }
        }

        binding.btnEdit.setOnClickListener {
            findNavController().navigate(
                BookInfoFragmentDirections.actionBookInfoFragmentToEditBookFragment(
                    args.bookId,
                    args.bookTitle,
                    args.bookAuthor,
                    args.bookGenre
                )
            )
        }
    }
}