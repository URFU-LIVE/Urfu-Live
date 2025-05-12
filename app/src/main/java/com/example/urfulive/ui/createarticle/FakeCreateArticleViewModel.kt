import com.example.urfulive.data.DTOs.DefaultResponse
import com.example.urfulive.ui.createarticle.CreateArticleViewModel

class FakeCreateArticleViewModel : CreateArticleViewModel() {
    override fun onPublishClick(
        titleText: String,
        contentText: String,
        tagsText: String,
        callback: PostCallBack
    ) {
        // Эмулируем успешный результат
        val fakeResponse = DefaultResponse(
            message = "Фейковый пост успешно создан"
        )

        // Немедленно вызываем onSuccess
        callback.onSuccess(fakeResponse)
    }
}
