object ProfileScreen {
    const val route = "profile"
    const val usernameArg = "username"
    const val isOwnProfileArg = "isOwnProfile"

    // Метод для создания полного пути с параметрами
    fun createRoute(username: String? = null, isOwnProfile: Boolean = false): String {
        return if (username != null) {
            "$route/$username?$isOwnProfileArg=$isOwnProfile"
        } else {
            "$route?$isOwnProfileArg=$isOwnProfile"
        }
    }
}
