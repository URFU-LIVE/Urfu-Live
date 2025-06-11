// app/src/main/java/com/example/urfulive/data/api/ApiService.kt
package com.example.urfulive.data.api

import com.example.urfulive.data.DTOs.TagDto
import retrofit2.http.GET

/**
 * Интерфейс API для работы с тегами
 */
interface ApiService {

    /**
     * Получение всех тегов с сервера
     * Эндпоинт: GET /tags
     */
    @GET("tags")
    suspend fun getAllTags(): List<TagDto>

    // Здесь можно добавить другие методы для работы с тегами:

    // /**
    //  * Поиск тегов по запросу
    //  */
    // @GET("tags/search")
    // suspend fun searchTags(@Query("q") query: String): List<TagDto>

    // /**
    //  * Создание нового тега
    //  */
    // @POST("tags")
    // suspend fun createTag(@Body tagRequest: CreateTagRequest): TagDto
}