package com.example.urfulive.data.api

import com.example.urfulive.data.DTOs.DefaultResponse
import com.example.urfulive.data.DTOs.PostCreateRequest
import com.example.urfulive.data.DTOs.PostDto
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType

class PostApiService : BaseApiService() {

    suspend fun create(title: String, text: String, tags: List<String>): Result<DefaultResponse> {
        val token = getToken()
        val requestBody = PostCreateRequest(title, text, tags)
        return authorizedRequest {
            client.post("$baseUrl/posts") {
                headers {
                    append(HttpHeaders.Authorization, "Bearer $token")
                }
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }
        }
    }

    suspend fun getAll(): Result<List<PostDto>> {
        val token = getToken()
        return authorizedRequest {
            client.get("$baseUrl/posts") {
                headers {
                    append(HttpHeaders.Authorization, "Bearer $token")
                }
            }
        }
    }

    suspend fun like(id: Long): Result<DefaultResponse> {
        val token = getToken()
        return authorizedRequest {
            client.post("$baseUrl/posts/$id/like") {
                headers {
                    append(HttpHeaders.Authorization, "Bearer $token")
                }
            }
        }
    }

    suspend fun dislike(id: Long): Result<DefaultResponse> {
        val token = getToken()
        return authorizedRequest {
            client.delete("$baseUrl/posts/$id/dislike") {
                headers {
                    append(HttpHeaders.Authorization, "Bearer $token")
                }
            }
        }
    }

    suspend fun getRecommendation(page: Int): Result<List<PostDto>> {
        val token = getToken()
        val interests = getInterest().joinToString(",")

        return authorizedRequest<List<PostDto>> {
            client.get("$baseUrl/posts/recommended") {
                headers {
                    append(HttpHeaders.Authorization, "Bearer $token")
                }
                url {
                    parameters.append("categories", interests)
                    parameters.append("size", "10")
                    parameters.append("page", page.toString())
                }
            }
        }
    }
}