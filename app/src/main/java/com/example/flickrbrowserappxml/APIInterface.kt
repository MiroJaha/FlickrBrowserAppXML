package com.example.flickrbrowserappxml

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

interface APIInterface {
    /*@get:GET("?method=flickr.photos.search&api_key=33f1bd9bf4d${Data.count}&tags=cat&per_page=10&format=rest")
    val rsp: Call<Rsp?>?*/

    @GET
    fun rsp(@Url link: String): Call<Rsp?>?

}