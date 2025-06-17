package live.urfu.frontend.data.model

import androidx.compose.ui.graphics.Color

data class Interest(
    val nameRu: String,
    val nameEn: String,
    val color: Color,
    val backgroundColor: Color
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Interest
        return nameEn == other.nameEn
    }

    override fun hashCode(): Int {
        return nameEn.hashCode()
    }
}