package com.manutd.ronaldo.practicemakesperfect.domain.repository


import androidx.annotation.WorkerThread
import com.manutd.ronaldo.practicemakesperfect.domain.model.Movies
import com.manutd.ronaldo.practicemakesperfect.utils.Resource

interface MoviesRepository {
    @WorkerThread
    suspend fun getMovies(onError:(String?)->Unit): Movies
}