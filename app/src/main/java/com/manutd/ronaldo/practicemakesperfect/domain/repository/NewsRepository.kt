package com.manutd.ronaldo.practicemakesperfect.domain.repository

import com.manutd.ronaldo.practicemakesperfect.data.model.NewsResponse
import com.manutd.ronaldo.practicemakesperfect.domain.model.DetailNews
import com.manutd.ronaldo.practicemakesperfect.domain.model.News
import com.manutd.ronaldo.practicemakesperfect.utils.Resource

interface NewsRepository {
    suspend fun getNews(): Resource<List<News>>
    suspend fun getDetailNews(): Resource<DetailNews>
}