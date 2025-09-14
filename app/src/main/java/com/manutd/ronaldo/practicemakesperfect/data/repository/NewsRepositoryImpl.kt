package com.manutd.ronaldo.practicemakesperfect.data.repository

import android.util.Log.e
import com.manutd.ronaldo.practicemakesperfect.data.di.IODispatcher
import com.manutd.ronaldo.practicemakesperfect.data.mapper.toDomain
import com.manutd.ronaldo.practicemakesperfect.data.mapper.toNews
import com.manutd.ronaldo.practicemakesperfect.data.service.ApiService
import com.manutd.ronaldo.practicemakesperfect.domain.model.DetailNews
import com.manutd.ronaldo.practicemakesperfect.domain.model.News
import com.manutd.ronaldo.practicemakesperfect.domain.repository.NewsRepository
import com.manutd.ronaldo.practicemakesperfect.utils.Resource
import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class NewsRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    @param:IODispatcher private val dispatcher: CoroutineDispatcher
) : NewsRepository {
    override suspend fun getNews(): Resource<List<News>> {
        return withContext(dispatcher) {
            Resource.Loading
            try {
                val response = apiService.getNews()
                val news = response.toDomain()
                Resource.Success(news)
            } catch (e: Exception) {
                Resource.Error(e)
            }
        }
    }

    override suspend fun getDetailNews(): Resource<DetailNews> {
        return withContext(dispatcher) {
            Resource.Loading
            try {
                val response = apiService.getDetailNews()
                val detailNews = response.toDomain()
                Resource.Success(detailNews)
            } catch (e: Exception) {
                Resource.Error(e)
            }
        }
    }

}