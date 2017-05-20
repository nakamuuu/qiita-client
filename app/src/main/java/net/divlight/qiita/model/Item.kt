package net.divlight.qiita.model

import java.util.*

data class Item(
        val id: String,
        val title: String,
        val url: String,
        val createdAt: Date,
        val user: User
)
