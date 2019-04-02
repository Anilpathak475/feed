package com.twittwe.feedmanager.network


import android.util.Log
import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class ClientGenerator {
    private var retrofit: Retrofit? = null
    private val gson = GsonBuilder()
        .setDateFormat("HH:mm:ss")
        .create()

    init {
        createRetrofit()
    }

    private fun createRetrofit() {
        retrofit = null
        retrofit = Retrofit.Builder()
            .baseUrl("https://api.twitter.com/1.1/")
            .client(
                OkHttpClient.Builder()
                    .retryOnConnectionFailure(true).addInterceptor(Interceptor { chain ->
                        val request = chain.request()

                        val response = chain.proceed(request)
                        try {

                            assert(response.body() != null)
                            val responseString = String(response.body()!!.bytes())
                            Log.d(
                                "-OkHttp-NET-Interceptor",
                                "Api:-" + request.url() +"--- Header "+request.headers() +request.body().toString() +" :- Response: " + responseString
                            )


                            return@Interceptor response.newBuilder()
                                .body(
                                    ResponseBody.create(
                                        response.body()!!.contentType(),
                                        responseString
                                    )
                                )
                                .build()

                        } catch (ex: KotlinNullPointerException) {
                            throw ex
                        }
                    })
                    .build()
            )
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    fun <S> createClient(clientClass: Class<S>): S {
        return retrofit!!.create(clientClass)
    }
}
