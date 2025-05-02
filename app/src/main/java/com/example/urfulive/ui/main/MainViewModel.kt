import androidx.lifecycle.ViewModel
import androidx.compose.ui.graphics.Color

data class Article(
    val title: String,
    val tags: List<String>,
    val author: String,
    val content: String,
    val likes: Int,
    val comments: Int,
    val sakladka: Int,
    val date: String,
    val colorPatternIndex: Int,
)

data class ArticleColorPattern(
    val background: Color,
    val buttonColor: Color,
    val textColor: Color,
    val reactionColor: Color,
)

private val _articleColorPatterns = listOf(
    ArticleColorPattern(
        background = Color(0xFFB2DF8A),
        buttonColor = Color(0xFFF6ECC9),
        textColor = Color.Black,
        reactionColor = (Color(0xFF6E9A3C)),
    ),
    ArticleColorPattern(
        background = Color(0xFFEBE6FD),
        buttonColor = Color(0xFFBA55D3),
        textColor = Color.Black,
        reactionColor = (Color(0xFF8C3F9F)),
    ),
    ArticleColorPattern(
        background = Color(0xFFF6ECC9),
        buttonColor = Color(0xFFEE7E56),
        textColor = Color.Black,
        reactionColor = (Color(0xFFAE451F)),
    ),
)

enum class ArticleExpandState {
    Collapsed, // в карусели
    Partial,   // выдвинута снизу
    Full       // на весь экран
}

val ArticleColorPatterns: List<ArticleColorPattern> get() = _articleColorPatterns

class ArticlesViewModel : ViewModel() {
    // Заглушки статей для проверки
    private val _articles = listOf(
        Article(
            title = "«42, братуха»: что означает популярная фраза из «Тиктока»",
            tags = listOf(
                "Учёба",
                "42Братухи",
                "42Братухи",
                "42Братухи",
                "42Братухи",
                "42Братухи",
                "42Братухи",
                "42Братухи",
                "42Братухи",
                "42Братухи"
            ),
            author = "Peemkay_42",
            content = """
                В начале 2025 года школьники по всей России массово полюбили число 42.
                Его кричат на концертах, Его кричат на концертах, Его кричат на концертах, Его кричат на концертах, Его кричат на концертах, Его кричат на концертах, Его кричат на концертах, Его кричат на концертах, Его кричат на концертах, Его кричат на концертах, Его кричат на концертах, Его кричат на концертах, Его кричат на концертах, Его кричат на концертах, Его кричат на концертах, Его кричат на концертах, 
            """.trimIndent(),
            likes = 42,
            comments = 5,
            sakladka = 1,
            date = "1 марта 2025",
            colorPatternIndex = 0
        ),
        Article(
            title = "Новое слово «Шарк»: тренд от уральских студентов",
            tags = listOf("Внеучебка", "Сленг"),
            author = "Urfu_Shark",
            content = """
                В кампусах УрФУ всё чаще можно услышать слово «Шарк»,
                означающее весёлого и активного студента, который пробует всё новое и
                участвует в различных мероприятиях. По словам авторов, это слово родилось из хэштега #SharkUrFU.
            """.trimIndent(),
            likes = 17,
            comments = 3,
            sakladka = 5,
            date = "5 марта 2025",
            colorPatternIndex = 2
        ),
        Article(
            title = "Новое слово «Шарк»: тренд от уральских студентов",
            tags = listOf("Внеучебка", "Сленг"),
            author = "Urfu_Shark",
            content = """
                В кампусах УрФУ всё чаще можно услышать слово «Шарк»,
                означающее весёлого и активного студента, который пробует всё новое и
                участвует в различных мероприятиях. По словам авторов, это слово родилось из хэштега #SharkUrFU.
            """.trimIndent(),
            likes = 17,
            comments = 3,
            sakladka = 5,
            date = "5 марта 2025",
            colorPatternIndex = 1
        ),
    )

    // Публичный доступ только к чтению
    val articles: List<Article> get() = _articles

    fun onProfileClick() {
    }

    fun checkProfile() {

    }

    fun createPost() {

    }

    fun createArticle() {

    }
}
