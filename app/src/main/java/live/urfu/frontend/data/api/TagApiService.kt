package live.urfu.frontend.data.api

import live.urfu.frontend.data.model.Tag
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.http.HttpHeaders

class TagApiService : BaseApiService() {

    suspend fun getAll(): Result<List<Tag>> {
        val token = getToken()
        return authorizedRequest {
            client.get("$baseUrl/tags") {
                headers { append(HttpHeaders.Authorization, "Bearer $token") }
            }
        }
    }
}