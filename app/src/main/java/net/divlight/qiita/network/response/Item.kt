package net.divlight.qiita.network.response

import java.util.*

data class Item(
    val id: String,
    val title: String,
    val url: String,
    val createdAt: Date,
    val user: User,
    val tags: List<Tag> = emptyList()
) {
    data class Tag(val name: String)
}
