// app/src/main/java/com/example/urfulive/data/service/SimpleTagService.kt
package com.example.urfulive.data.service

import android.util.Log
import com.example.urfulive.data.api.ApiService
import com.example.urfulive.data.DTOs.toModel
import com.example.urfulive.data.model.Tag
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SimpleTagService @Inject constructor(
    private val apiService: ApiService
) {

    private var tagsCache: List<Tag> = emptyList()
    private var lastCacheUpdate = 0L
    private val cacheTtl = 5 * 60 * 1000L // 5 минут

    companion object {
        private const val TAG = "SimpleTagService"
    }

    /**
     * Получение всех тегов с кэшированием
     */
    suspend fun getAllTags(): Result<List<Tag>> {
        return try {
            val now = System.currentTimeMillis()

            if (tagsCache.isNotEmpty() && (now - lastCacheUpdate) < cacheTtl) {
                Log.d(TAG, "Возвращаем теги из кэша (${tagsCache.size} тегов)")
                Result.success(tagsCache)
            } else {
                Log.d(TAG, "Загружаем теги с сервера...")
                val tagsDto = apiService.getAllTags()
                val tags = tagsDto.map { tagDto -> tagDto.toModel() }

                tagsCache = tags
                lastCacheUpdate = now

                Log.d(TAG, "Загружено ${tags.size} тегов с сервера")
                Result.success(tags)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка загрузки тегов", e)

            // Возвращаем кэш в случае ошибки сети
            if (tagsCache.isNotEmpty()) {
                Log.d(TAG, "Возвращаем кэш после ошибки (${tagsCache.size} тегов)")
                Result.success(tagsCache)
            } else {
                Result.failure(e)
            }
        }
    }

    /**
     * Поиск тегов по названию с автодополнением
     */
    fun searchTags(query: String, allTags: List<Tag>): List<Tag> {
        if (query.length < 2) return emptyList()

        val normalizedQuery = query.trim().lowercase()

        return allTags
            .filter { tag ->
                tag.name.lowercase().contains(normalizedQuery)
            }
            .sortedWith(compareBy<Tag> {
                // Сначала теги, которые начинаются с запроса
                !it.name.lowercase().startsWith(normalizedQuery)
            }.thenBy { it.name })
    }

    /**
     * Поиск тега по точному названию (для создания новых постов)
     */
    fun findTagByName(name: String, allTags: List<Tag>): Tag? {
        return allTags.find { it.name.equals(name.trim(), ignoreCase = true) }
    }

    /**
     * Проверка актуальности кэша
     */
    fun isCacheValid(): Boolean {
        val now = System.currentTimeMillis()
        return tagsCache.isNotEmpty() && (now - lastCacheUpdate) < cacheTtl
    }

    /**
     * Получение информации о кэше
     */
    fun getCacheInfo(): String {
        val now = System.currentTimeMillis()
        val ageMinutes = (now - lastCacheUpdate) / (60 * 1000)
        return "Кэш: ${tagsCache.size} тегов, возраст: $ageMinutes мин"
    }

    /**
     * Очистка кэша (принудительное обновление)
     */
    fun clearCache() {
        Log.d(TAG, "Очистка кэша тегов")
        tagsCache = emptyList()
        lastCacheUpdate = 0L
    }

    /**
     * Получение статистики использования тегов
     */
    fun getTagStats(): Map<String, Any> {
        return mapOf(
            "totalTags" to tagsCache.size,
            "cacheAge" to (System.currentTimeMillis() - lastCacheUpdate),
            "isValid" to isCacheValid()
        )
    }
}