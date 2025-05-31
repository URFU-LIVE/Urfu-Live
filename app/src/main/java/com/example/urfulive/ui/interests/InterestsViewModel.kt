package com.example.urfulive.ui.interests

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.urfulive.data.manager.InterestManager
import com.example.urfulive.data.manager.InterestManagerInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class InterestsViewModel : ViewModel() {

    // Список доступных интересов
    private val interests = listOf(
        Interest("Учёба", "Study", Color(118, 182, 254), Color(0xFFC2C2C2)),
        Interest("Здоровье", "Health", Color(169, 214, 117), Color(0xFFC2C2C2)),
        Interest("Спорт", "Sport", Color(238, 126, 86), Color(0xFFC2C2C2)),
        Interest("Юмор", "Humor", Color(186, 85, 211), Color(0xFFC2C2C2)),
        Interest("Внеучебная деятельность", "Extracurricular", Color(242, 209, 77), Color(0xFFC2C2C2)),
        Interest("Стажировка", "Internship", Color(238, 126, 86), Color(0xFFC2C2C2)),
        Interest("Знакомства", "Dating", Color(118, 182, 254), Color(0xFFC2C2C2)),
        Interest("Работа", "Work", Color(169, 214, 117), Color(0xFFC2C2C2)),
        Interest("Волонтёрство", "Volunteer", Color(186, 85, 211), Color(0xFFC2C2C2)),
        Interest("Новости", "News", Color(242, 209, 77), Color(0xFFC2C2C2))
    )

    val allInterests: List<Interest> get() = interests


    private val _selectedInterests = MutableStateFlow(setOf<Interest>())
    val selectedInterests = _selectedInterests.asStateFlow()

    fun onToggleInterest(interest: Interest) {
        val current = _selectedInterests.value
        _selectedInterests.value = if (current.contains(interest)) current - interest else current + interest
    }

    fun saveInterests() {
        val interestManager = InterestManagerInstance.getInstance();
        viewModelScope.launch {
            interestManager.saveSelectedInterests(selectedInterests.value);
        }
    }
}

data class Interest(
    val nameRu: String,
    val nameEn: String,
    val color: Color,
    val backgroundColor: Color
)
