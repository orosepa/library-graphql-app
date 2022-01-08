package com.example.librarygraphql.networking

import com.apollographql.apollo.ApolloClient

val apolloClient = ApolloClient.builder()
    .serverUrl("https://ktor-graphql-server-library.herokuapp.com/graphql")
    .build()