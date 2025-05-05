package com.example.urfulive.ui.notifiaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.urfulive.data.api.NotificationApiService
import com.example.urfulive.data.manager.DtoManager
import com.example.urfulive.data.model.Notification
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class NotificationViewModel : ViewModel() {

    private val notificationApiService = NotificationApiService()

    private val _notifications = MutableStateFlow<List<Notification>>(emptyList())
    val notifications: StateFlow<List<Notification>> get() = _notifications

    init {
        viewModelScope.launch {
            fetchNotification()
        }
    }

    private fun fetchNotification() {
        viewModelScope.launch {
            val result = notificationApiService.getAll()
            result.onSuccess { notificationList ->
                val dtoManager = DtoManager()
                _notifications.value = notificationList.map { dtoManager.run { it.toNotification() } }
            }.onFailure {
                it.printStackTrace()
            }
        }
    }
}