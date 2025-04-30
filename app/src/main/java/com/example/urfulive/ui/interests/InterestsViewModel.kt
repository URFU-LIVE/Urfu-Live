import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class InterestsViewModel : ViewModel() {

    // Список доступных интересов
    val interestColorMap: Map<String, Pair<Color, Color>> = mapOf(
//        "Учёба" to (Color(red=118, green = 182, blue = 254) to Color(red=98, green = 149, blue = 207)),
//        "Здоровье" to (Color(red=169, green = 214, blue = 117) to Color(red=139, green = 175, blue = 97)),
//        "Спорт" to (Color(red=238, green = 126, blue = 86) to Color(red=194, green = 105, blue = 73)),
//        "Юмор" to (Color(red=186, green = 85, blue = 211) to Color(red=153, green = 72, blue = 173)),
//        "Внеучебная деятельность" to (Color(red=242, green = 209, blue = 77) to Color(red=197, green = 171, blue = 65)),
//        "Стажировки" to (Color(red=238, green = 126, blue = 86) to Color(red=194, green = 105, blue = 73)),
//        "Знакомства" to (Color(red=118, green = 182, blue = 254) to Color(red=98, green = 149, blue = 207)),
//        "Работа" to (Color(red=169, green = 214, blue = 117) to Color(red=139, green = 175, blue = 97)),
//        "Волонтёрство" to (Color(red=186, green = 85, blue = 211) to Color(red=153, green = 72, blue = 173)),
//        "Новости" to (Color(red=242, green = 209, blue = 77) to Color(red=197, green = 171, blue = 65))
        "Учёба" to (Color(red=118, green = 182, blue = 254) to Color(0xFFC2C2C2)),
        "Здоровье" to (Color(red=169, green = 214, blue = 117) to Color(0xFFC2C2C2)),
        "Спорт" to (Color(red=238, green = 126, blue = 86) to Color(0xFFC2C2C2)),
        "Юмор" to (Color(red=186, green = 85, blue = 211) to Color(0xFFC2C2C2)),
        "Внеучебная деятельность" to (Color(red=242, green = 209, blue = 77) to Color(0xFFC2C2C2)),
        "Стажировки" to (Color(red=238, green = 126, blue = 86) to Color(0xFFC2C2C2)),
        "Знакомства" to (Color(red=118, green = 182, blue = 254) to Color(0xFFC2C2C2)),
        "Работа" to (Color(red=169, green = 214, blue = 117) to Color(0xFFC2C2C2)),
        "Волонтёрство" to (Color(red=186, green = 85, blue = 211) to Color(0xFFC2C2C2)),
        "Новости" to (Color(red=242, green = 209, blue = 77) to Color(0xFFC2C2C2))
    )

    val allInterests: Map<String, Pair<Color, Color>> get() = interestColorMap

    // Текущее множество выбранных интересов
    private val _selectedInterests = MutableStateFlow(setOf<String>())
    val selectedInterests = _selectedInterests.asStateFlow()

    // Тоггл интереса (добавляем, если нет в set, или убираем, если есть)
    fun onToggleInterest(interest: String) {
        val current = _selectedInterests.value
        _selectedInterests.value = if (current.contains(interest)) {
            current - interest
        } else {
            current + interest
        }
    }

    fun onLogoClick(newValue: String) {

    }

    fun onNextClick(newValue: String) {

    }

    fun onSkipClick(newValue: String) {

    }
}
