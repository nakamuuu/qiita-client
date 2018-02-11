package net.divlight.qiita.network.response

data class Tag(
    val id: String,
    val iconUrl: String,
    val itemsCount: Int,
    val followersCount: Int
)
