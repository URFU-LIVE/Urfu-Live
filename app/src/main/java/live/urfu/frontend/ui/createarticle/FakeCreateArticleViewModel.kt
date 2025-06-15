import live.urfu.frontend.data.DTOs.DefaultResponse
import live.urfu.frontend.ui.createarticle.CreateArticleViewModel

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
