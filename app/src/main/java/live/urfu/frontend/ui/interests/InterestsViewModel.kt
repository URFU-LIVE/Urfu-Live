import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import live.urfu.frontend.data.manager.InterestManagerInstance
import live.urfu.frontend.data.model.Interest

class InterestsViewModel : ViewModel() {

    private val inactiveColor = Color(0xFFC2C2C2)

    private val defaultInterests = listOf(
        Interest("Учёба", "Study", Color(118, 182, 254), inactiveColor),
        Interest("Здоровье", "Health", Color(169, 214, 117), inactiveColor),
        Interest("Спорт", "Sport", Color(238, 126, 86), inactiveColor),
        Interest("Юмор", "Humor", Color(186, 85, 211), inactiveColor),
        Interest("Внеучебная деятельность", "Extracurricular", Color(242, 209, 77), inactiveColor),
        Interest("Стажировка", "Internship", Color(238, 126, 86), inactiveColor),
        Interest("Знакомства", "Dating", Color(118, 182, 254), inactiveColor),
        Interest("Работа", "Work", Color(169, 214, 117), inactiveColor),
        Interest("Волонтёрство", "Volunteer", Color(186, 85, 211), inactiveColor),
        Interest("Новости", "News", Color(242, 209, 77), inactiveColor)
    )
    val allInterests: List<Interest> get() = defaultInterests

    private val _selectedInterests = MutableStateFlow(setOf<Interest>())
    val selectedInterests = _selectedInterests.asStateFlow()

    fun onToggleInterest(interest: Interest) {
        val current = _selectedInterests.value
        _selectedInterests.value = if (current.contains(interest)) current - interest else current + interest
    }

    fun saveInterests() {
        viewModelScope.launch {
            InterestManagerInstance.getInstance().saveSelectedInterests(_selectedInterests.value)
        }
    }
}
