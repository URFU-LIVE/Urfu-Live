import androidx.lifecycle.ViewModel
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewModelScope
import com.example.urfulive.data.api.PostApiService
import com.example.urfulive.data.model.Post
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


data class PostColorPattern(
    val background: Color,
    val buttonColor: Color,
    val textColor: Color,
    val reactionColor: Color,
)

private val postColorPattern = listOf(
    PostColorPattern(
        background = Color(0xFFB2DF8A),
        buttonColor = Color(0xFFF6ECC9),
        textColor = Color.Black,
        reactionColor = (Color(0xFF6E9A3C)),
    ),
    PostColorPattern(
        background = Color(0xFFEBE6FD),
        buttonColor = Color(0xFFBA55D3),
        textColor = Color.Black,
        reactionColor = (Color(0xFF8C3F9F)),
    ),
    PostColorPattern(
        background = Color(0xFFF6ECC9),
        buttonColor = Color(0xFFEE7E56),
        textColor = Color.Black,
        reactionColor = (Color(0xFFAE451F)),
    ),
)

val PostColorPatterns: List<PostColorPattern> get() = postColorPattern

class PostViewModel : ViewModel() {
    private val postApiService = PostApiService()

    private val _posts = MutableStateFlow<List<Post>>(emptyList())
    val posts: StateFlow<List<Post>> get() = _posts

    init {
        fetchPosts()
    }

    private fun fetchPosts() {
        viewModelScope.launch {
            val result = postApiService.getAll()
            result.onSuccess { postListResponse ->
                _posts.value = listOf(postListResponse.posts);
            }.onFailure {
                it.printStackTrace()
            }
        }
    }
}

fun onProfileClick() {
}

fun checkProfile() {

}

fun createPost() {

}

fun createArticle() {

}

