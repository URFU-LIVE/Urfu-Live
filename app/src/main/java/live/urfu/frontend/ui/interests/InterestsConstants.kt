package live.urfu.frontend.ui.interests

import androidx.compose.ui.graphics.Color
import live.urfu.frontend.data.model.Interest

object InterestsConstants {

    private val inactiveColor = Color(0xFFC2C2C2)

    private val STUDY = Interest("Учёба", "Study", Color(118, 182, 254), inactiveColor)
    private val HEALTH = Interest("Здоровье", "Health", Color(169, 214, 117), inactiveColor)
    private val  SPORT = Interest("Спорт", "Sport", Color(238, 126, 86), inactiveColor)
    private val HUMOR = Interest("Юмор", "Humor", Color(186, 85, 211), inactiveColor)
    private val EXTRACURRICULAR = Interest("Внеучебная деятельность", "Extracurricular", Color(242, 209, 77), inactiveColor)
    private val INTERNSHIP = Interest("Стажировка", "Internship", Color(238, 126, 86), inactiveColor)
    private val DATING = Interest("Знакомства", "Dating", Color(118, 182, 254), inactiveColor)
    private val WORK = Interest("Работа", "Work", Color(169, 214, 117), inactiveColor)
    private val VOLUNTEER = Interest("Волонтёрство", "Volunteer", Color(186, 85, 211), inactiveColor)
    private val NEWS = Interest("Новости", "News", Color(242, 209, 77), inactiveColor)

    val ALL_INTERESTS = listOf(
        STUDY, HEALTH, SPORT, HUMOR, EXTRACURRICULAR,
        INTERNSHIP, DATING, WORK, VOLUNTEER, NEWS
    )

    private fun findByNameEn(nameEn: String): Interest? {
        return ALL_INTERESTS.find { it.nameEn == nameEn }
    }

    fun fromNameEnSet(nameEnSet: Set<String>): Set<Interest> {
        return nameEnSet.mapNotNull { findByNameEn(it) }.toSet()
    }
}