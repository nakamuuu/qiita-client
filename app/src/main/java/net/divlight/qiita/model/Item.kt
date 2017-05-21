package net.divlight.qiita.model

import java.util.*

data class Item(val id: String,
                val title: String,
                val url: String,
                val createdAt: Date,
                val user: User,
                val tags: List<Tag> = emptyList()) {
    data class Tag(val name: String)
}
