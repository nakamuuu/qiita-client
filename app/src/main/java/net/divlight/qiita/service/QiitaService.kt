package net.divlight.qiita.service

import net.divlight.qiita.model.Item
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
}
