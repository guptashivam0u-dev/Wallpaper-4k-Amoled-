package com.example.wallpaperapp

import retrofit2.http.GET
import retrofit2.http.Query

data class UnsplashPhoto(
    val id: String,
    val urls: PhotoUrls,
    val description: String? = null
)

data class PhotoUrls(
    val regular: String,
    val full: String,
    val small: String
)

data class SearchResponse(
    val results: List<UnsplashPhoto>
)

data class CategoryItem(
    val name: String,
    val query: String,
    val imageUrl: String
)

interface UnsplashApiService {
    @GET("photos")
    suspend fun getPhotos(
        @Query("client_id") clientId: String,
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 30
    ): List<UnsplashPhoto>

    @GET("search/photos")
    suspend fun searchPhotos(
        @Query("client_id") clientId: String,
        @Query("query") query: String,
        @Query("per_page") perPage: Int = 30
    ): SearchResponse
}
