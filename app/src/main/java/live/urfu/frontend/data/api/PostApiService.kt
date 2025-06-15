package live.urfu.frontend.data.api

import live.urfu.frontend.data.DTOs.DefaultResponse
import live.urfu.frontend.data.DTOs.PostCreateRequest
import live.urfu.frontend.data.DTOs.PostDto
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

    suspend fun searchByTag(tag: String, limit: Int = 20, offset: Int = 0): Result<List<PostDto>> {
        val token = getToken()
        return authorizedRequest {
            client.get("$baseUrl/posts") {
                headers {
                    append(HttpHeaders.Authorization, "Bearer $token")
                }
                url {
                    parameters.append("tag", tag)
                    parameters.append("limit", limit.toString())
                    parameters.append("offset", offset.toString())
                }
            }
        }
    }

    suspend fun get(id: Long): Result<PostDto> {
        val token = getToken()
        return authorizedRequest {
            client.get("$baseUrl/posts/$id") {
                headers {
                    append(HttpHeaders.Authorization, "Bearer $token")
                }
            }
        }
    }
}