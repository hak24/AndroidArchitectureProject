package com.example.androidarchitectureproject.data.remote

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface UnsplashApi {
    @GET("photos")
    suspend fun getPhotos(
        @Query("page") page: Int,
        @Query("per_page") perPage: Int = 20,
        @Query("client_id") clientId: String
    ): List<UnsplashPhoto>

    @GET("search/photos")
    suspend fun searchPhotos(
        @Query("query") query: String,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int = 20,
        @Query("client_id") clientId: String
    ): UnsplashSearchResponse

    @GET("photos/{id}")
    suspend fun getPhoto(
        @Path("id") id: String,
        @Query("client_id") clientId: String
    ): UnsplashPhoto

    companion object {
        const val BASE_URL = "https://api.unsplash.com/"
    }
}

data class UnsplashPhoto(
    val id: String,
    val description: String?,
    val urls: UnsplashPhotoUrls,
    val user: UnsplashUser,
    val likes: Int,
    val created_at: String
)

data class UnsplashPhotoUrls(
    val raw: String,
    val full: String,
    val regular: String,
    val small: String,
    val thumb: String
)

data class UnsplashUser(
    val name: String,
    val username: String
)

data class UnsplashSearchResponse(
    val total: Int,
    val total_pages: Int,
    val results: List<UnsplashPhoto>
) 