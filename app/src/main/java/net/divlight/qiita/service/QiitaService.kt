package net.divlight.qiita.service

import net.divlight.qiita.model.Item
import net.divlight.qiita.model.Tag
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
