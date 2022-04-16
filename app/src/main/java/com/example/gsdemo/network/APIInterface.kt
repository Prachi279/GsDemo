package com.example.gsdemo.network
import com.example.gsdemo.model.APODDetail
import retrofit2.Call
import retrofit2.http.*

/**
 * The APIInterface interface, to call APIs
 */
interface APIInterface {
    @GET("planetary/apod")
    fun getTodayImage(
        @QueryMap queryParams: HashMap<String, Any>
    ): Call<APODDetail>

    @GET("planetary/apod")
    fun getAPODList(
        @QueryMap queryParams: HashMap<String, Any>
    ): Call<ArrayList<APODDetail>>
}