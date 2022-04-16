package com.example.gsdemo.network

import com.example.gsdemo.BuildConfig
import com.example.gsdemo.utils.AppConstants.NETWORK_CALL_TIMEOUT
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * The ProjectRepository class, to build retrofit
 */
class ProjectRepository {
    /**
     * The baseUrl, base url of application
     */
    private val baseUrl = BuildConfig.BASE_URL

    /**
     * The apiInterface, APIInterface instance
     */
    private lateinit var apiInterface: APIInterface

    companion object {
        /**
         * The projectRepository, an instance of ProjectRepository class
         */
        private var projectRepository: ProjectRepository? = null

        /**
         * The getInstance method, to get create singleton instance
         */
        @Synchronized
        @JvmStatic
        fun getInstance(): ProjectRepository {
            if (projectRepository == null) {
                projectRepository = ProjectRepository()
            }
            return projectRepository!!
        }

    }

    /**
     * The callRetrofitBuilder method, to set up retrofit builder
     */
    fun callRetrofitBuilder(baseurl: String = baseUrl): APIInterface? {

        val client = OkHttpClient.Builder().addInterceptor(
            HttpLoggingInterceptor()
                .apply {
                    level = if (BuildConfig.DEBUG)
                        HttpLoggingInterceptor.Level.BODY
                    else
                        HttpLoggingInterceptor.Level.NONE
                })
            .readTimeout(NETWORK_CALL_TIMEOUT.toLong(), TimeUnit.SECONDS)
            .writeTimeout(NETWORK_CALL_TIMEOUT.toLong(), TimeUnit.SECONDS)
            .build()

        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(baseurl)
            .client(client)
            .callFactory { request ->
                var req = request
                req = req.newBuilder().tag(arrayOf<Any?>(null)).build()
                val call = client.newCall(req)
                (req.tag() as Array<Any?>)[0] = call
                call
            }
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .build()

        apiInterface = retrofit.create(APIInterface::class.java)

        return apiInterface
    }

}