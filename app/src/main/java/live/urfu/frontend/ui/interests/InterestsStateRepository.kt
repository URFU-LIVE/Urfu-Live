package live.urfu.frontend.ui.interests

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import live.urfu.frontend.data.manager.InterestManagerInstance
import live.urfu.frontend.data.model.Interest

class InterestsStateRepository {

    private val _interestsChanged = MutableSharedFlow<InterestsChangeEvent>()
    val interestsChanged: SharedFlow<InterestsChangeEvent> = _interestsChanged.asSharedFlow()


    suspend fun saveInterests(interests: Set<Interest>): Result<Unit> {
        return try {
            val interestManager = InterestManagerInstance.getInstance()

            val oldInterests = interestManager.getSelectedInterestsBlocking()
            val newInterests = interests.map { it.nameEn }.toSet()

            interestManager.saveSelectedInterests(interests)

            if (oldInterests != newInterests) {
                _interestsChanged.emit(
                    InterestsChangeEvent.Updated(
                        oldInterests = oldInterests,
                        newInterests = newInterests,
                        timestamp = System.currentTimeMillis()
                    )
                )
            }

            Result.success(Unit)
        } catch (e: Exception) {
            android.util.Log.e("InterestsStateRepository", "Failed to save interests", e)
            Result.failure(e)
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: InterestsStateRepository? = null

        fun getInstance(): InterestsStateRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: InterestsStateRepository().also { INSTANCE = it }
            }
        }
    }
}

sealed class InterestsChangeEvent {
    data class Updated(
        val oldInterests: Set<String>,
        val newInterests: Set<String>,
        val timestamp: Long
    ) : InterestsChangeEvent()
}