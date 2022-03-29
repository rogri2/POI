package com.example.multichat.models

data class Chat(
    var id: String = "",
    var name: String = "",
    var users: List<String> = emptyList()
)