package com.manutd.ronaldo.practicemakesperfect

import android.R.attr.name
import android.R.id.message
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.manutd.ronaldo.practicemakesperfect.data.service.ApiService
import com.skydoves.sandwich.message
import com.skydoves.sandwich.onFailure
import com.skydoves.sandwich.retrofit.adapters.ApiResponseCallAdapterFactory
import com.skydoves.sandwich.suspendOnSuccess
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import org.junit.Before
import retrofit2.Retrofit
import kotlin.test.DefaultAsserter.assertTrue
import kotlin.test.DefaultAsserter.fail
import kotlin.test.Test


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@OptIn(ExperimentalCoroutinesApi::class)
class MoviesApiIntegrationTest {

    private lateinit var apiService: ApiService

    @Before
    fun setup() {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://iptv.rophim.dev/") // base url thật
            .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
            .addCallAdapterFactory(ApiResponseCallAdapterFactory.create()) // từ Sandwich
            .build()

        apiService = retrofit.create(ApiService::class.java)
    }

    @Test
    fun `getMovies should return data from server`() = runTest {
        // when
        val response = apiService.getMovies()

        // then
        response.suspendOnSuccess {
            println("Name: $name")
            println("Groups: ${data.groups.size}")
        }.onFailure {
            fail("API call failed: ${message()}")
        }
    }
}
