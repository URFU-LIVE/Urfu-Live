package live.urfu.frontend.data.api

import live.urfu.frontend.data.DTOs.NotificationDto
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.http.HttpHeaders

class NotificationApiService : BaseApiService() {

    suspend fun getAll(): Result<List<NotificationDto>> {
        val token = getToken()
        return authorizedRequest {
            client.get("$baseUrl/notifications") {
                headers { append(HttpHeaders.Authorization, "Bearer $token")}
            }
        }
    }
}