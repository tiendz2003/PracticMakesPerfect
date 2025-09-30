package com.manutd.ronaldo.practicemakesperfect.data.repository

import androidx.annotation.WorkerThread
import com.manutd.ronaldo.practicemakesperfect.data.di.IODispatcher
import com.manutd.ronaldo.practicemakesperfect.data.mapper.toDomain
import com.manutd.ronaldo.practicemakesperfect.data.service.ApiService
import com.manutd.ronaldo.practicemakesperfect.domain.model.Movies
import com.manutd.ronaldo.practicemakesperfect.domain.repository.MoviesRepository
import com.manutd.ronaldo.practicemakesperfect.utils.Resource
import com.skydoves.sandwich.isError
import com.skydoves.sandwich.message
import com.skydoves.sandwich.onError
import com.skydoves.sandwich.onFailure
import com.skydoves.sandwich.suspendOnSuccess
import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class MoviesRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher
) : MoviesRepository {
    @WorkerThread
    override suspend fun getMovies(onError: (String?) -> Unit): Movies {
        var movies: Movies? = null
        return withContext(ioDispatcher) {
            val response = apiService.getMovies()
            response.suspendOnSuccess {
                 movies= data.toDomain()
            }.onFailure {
                onError(message())
            }
            movies!!
        }
    }
}