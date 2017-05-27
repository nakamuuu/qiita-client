package net.divlight.qiita.service

import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import net.divlight.qiita.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object QiitaServiceCreator {
    private const val BASE_URL = "https://qiita.com/"

    fun createService(): QiitaService {
        val gson = GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create()
        val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(createOkHttpClient())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
        return retrofit.create(QiitaService::class.java)
    }

    private fun createOkHttpClient(): OkHttpClient = OkHttpClient.Builder().apply {
        if (BuildConfig.DEBUG) {
            addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC))
        }
    }.build()
}
