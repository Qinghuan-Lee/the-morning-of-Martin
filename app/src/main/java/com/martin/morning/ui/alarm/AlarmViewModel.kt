package com.martin.morning.ui.alarm

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.martin.morning.MartinMorningApp
import com.martin.morning.alarm.AlarmScheduler
import com.martin.morning.data.AlarmEntity
import kotlinx.coroutines.launch

class AlarmViewModel(application: Application) : ViewModel() {

    private val db = MartinMorningApp.instance.database
    val alarms: LiveData<List<AlarmEntity>> = db.alarmDao().getAll()

    fun toggleAlarm(alarm: AlarmEntity, enabled: Boolean) {
        viewModelScope.launch {
            db.alarmDao().setEnabled(alarm.id, enabled)
            if (enabled) {
                AlarmScheduler.schedule(MartinMorningApp.instance, alarm.copy(isEnabled = true))
            } else {
                AlarmScheduler.cancel(MartinMorningApp.instance, alarm.id)
            }
        }
    }

    fun deleteAlarm(alarm: AlarmEntity) {
        viewModelScope.launch {
            db.alarmDao().delete(alarm)
            AlarmScheduler.cancel(MartinMorningApp.instance, alarm.id)
        }
    }

    class Factory(private val app: Application) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return AlarmViewModel(app) as T
        }
    }
}
