package live.urfu.frontend.data.api

import live.urfu.frontend.data.DTOs.CommentDto
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType

class CommentApiService : BaseApiService() {

    suspend fun create(id: Long, text: String): Result<CommentDto> {
        val token = getToken()
        return authorizedRequest {
            client.post("$baseUrl/posts/$id/comments") {
                headers { append(HttpHeaders.Authorization, "Bearer $token") }
                contentType(ContentType.Application.Json)
                setBody(mapOf("text" to text))
            }
        }
    }

    suspend fun getAll(id: Long): Result<List<CommentDto>> {
        val token = getToken()
        return authorizedRequest {
            client.get("$baseUrl/posts/$id/comments") {
                headers { append(HttpHeaders.Authorization, "Bearer $token") }
            }
        }
    }
}