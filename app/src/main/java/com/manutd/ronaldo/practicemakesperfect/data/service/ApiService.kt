package com.manutd.ronaldo.practicemakesperfect.data.service

import com.manutd.ronaldo.practicemakesperfect.data.model.DetailNewsResponse
import com.manutd.ronaldo.practicemakesperfect.data.model.NewsResponse
import retrofit2.http.GET

interface ApiService {
    @GET("newsfeed.json")
    suspend fun getNews(): NewsResponse

    @GET("detail.json")
    suspend fun getDetailNews(): DetailNewsResponse
}