object JwtParser {
    fun extractUserIdFromToken(token: String): String? {
        return try {
            val parts = token.split(".")
            if (parts.size != 3) return null

            val payload = parts[1]
            val decodedBytes = android.util.Base64.decode(payload, android.util.Base64.URL_SAFE)
            val decodedString = String(decodedBytes)

            val subjectRegex = "\"sub\":\"(\\d+)\"".toRegex()
            subjectRegex.find(decodedString)?.groupValues?.get(1)
        } catch (e: Exception) {
            null
        }
    }
}