package com.example.tasks.service.repository.remote

import android.graphics.Interpolator
import com.example.tasks.service.constants.TaskConstants
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitClient private constructor() {

    companion object {

        private lateinit var mRetrofit: Retrofit
        private val baseUrl = "http://devmasterteam.com/CursoAndroidAPI/"
        private var personKey = ""
        private var tokenKey = ""

        private fun getRetrofitInstance(): Retrofit {

            if (!::mRetrofit.isInitialized) {

                val httpClient = OkHttpClient.Builder()
                httpClient.addInterceptor(object : Interceptor {
                    override fun intercept(chain: Interceptor.Chain): Response {
                        val request =
                            chain.request()
                                .newBuilder()
                                .addHeader(TaskConstants.HEADER.PERSON_KEY, personKey)
                                .addHeader(TaskConstants.HEADER.TOKEN_KEY, tokenKey)
                                .build()

                        return chain.proceed(request)
                    }
                })


                mRetrofit = Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .client(httpClient.build())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
            }

            return mRetrofit
        }

        fun addHeader(tokenKey: String, personKey: String) {
            this.tokenKey = tokenKey
            this.personKey = personKey
        }

        fun <T> createService(serviceClass: Class<T>): T =
            getRetrofitInstance().create(serviceClass)
    }

}