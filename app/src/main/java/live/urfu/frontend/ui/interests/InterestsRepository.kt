package live.urfu.frontend.data.repository

import androidx.compose.ui.graphics.Color
import live.urfu.frontend.data.model.Interest

object InterestsRepository {

    private val inactiveColor = Color(0xFFC2C2C2)

    val STUDY = Interest("Учёба", "Study", Color(118, 182, 254), inactiveColor)
    val HEALTH = Interest("Здоровье", "Health", Color(169, 214, 117), inactiveColor)
    val SPORT = Interest("Спорт", "Sport", Color(238, 126, 86), inactiveColor)
    val HUMOR = Interest("Юмор", "Humor", Color(186, 85, 211), inactiveColor)
    val EXTRACURRICULAR = Interest("Внеучебная деятельность", "Extracurricular", Color(242, 209, 77), inactiveColor)
    val INTERNSHIP = Interest("Стажировка", "Internship", Color(238, 126, 86), inactiveColor)
    val DATING = Interest("Знакомства", "Dating", Color(118, 182, 254), inactiveColor)
    val WORK = Interest("Работа", "Work", Color(169, 214, 117), inactiveColor)
    val VOLUNTEER = Interest("Волонтёрство", "Volunteer", Color(186, 85, 211), inactiveColor)
    val NEWS = Interest("Новости", "News", Color(242, 209, 77), inactiveColor)

    val ALL_INTERESTS = listOf(
        STUDY, HEALTH, SPORT, HUMOR, EXTRACURRICULAR,
        INTERNSHIP, DATING, WORK, VOLUNTEER, NEWS
    )

    fun findByNameEn(nameEn: String): Interest? {
        return ALL_INTERESTS.find { it.nameEn == nameEn }
    }

    fun fromNameEnSet(nameEnSet: Set<String>): Set<Interest> {
        return nameEnSet.mapNotNull { findByNameEn(it) }.toSet()
    }

    fun toNameEnSet(interests: Set<Interest>): Set<String> {
        return interests.map { it.nameEn }.toSet()
    }
}