package com.example.librarygraphql.models

import com.example.librarygraphql.BookListQuery

data class Book (
    val id: String,
    val title: String,
    val author: String,
    val genre: String
)
