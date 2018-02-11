package net.divlight.qiita.network

import net.divlight.qiita.network.response.Item
import net.divlight.qiita.network.response.Tag
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface QiitaService {
    // https://qiita.com/api/v2/docs#get-apiv2items
    @GET("/api/v2/items")
    fun getItems(
        @Query("page") page: Int,
        @Query("per_page") perPage: Int,
        @Query("query") query: String? = null
    ): Call<List<Item>>

    // https://qiita.com/api/v2/docs#get-apiv2tags
    @GET("/api/v2/tags")
    fun getTags(
        @Query("page") page: Int,
        @Query("per_page") perPage: Int,
        @Query("sort") sort: String? = null
    ): Call<List<Tag>>
}
