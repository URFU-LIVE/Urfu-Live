package com.example.urfulive.data.api

import com.example.urfulive.data.model.Tag
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.http.HttpHeaders

class TagApiService : BaseApiService() {

    suspend fun getAll(): Result<List<Tag>> {
        val token = getToken()
        return authorizedRequest {
            client.get("$baseUrl/admin/tags") {
                headers { append(HttpHeaders.Authorization, "Bearer $token") }
            }
        }
    }
}