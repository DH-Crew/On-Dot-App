package com.dh.ondot.core.util

import com.dh.ondot.domain.model.ui.AlarmRingInfo
import com.dh.ondot.domain.service.AlarmStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import platform.Foundation.NSUserDefaults

class IosAlarmStorage: AlarmStorage {

    private val ALARMS_KEY = "alarm_list"

    private val defaults: NSUserDefaults = NSUserDefaults.standardUserDefaults()
    private val _alarmsFlow = MutableStateFlow(getAlarms())
    override val alarmsFlow: Flow<List<AlarmRingInfo>> = _alarmsFlow.asStateFlow()

    override suspend fun saveAlarms(alarms: List<AlarmRingInfo>) {
        // JSON 으로 직렬화하여 UserDefaults 에 저장
        val json = Json.encodeToString(alarms)
        defaults.setObject(json, ALARMS_KEY)
        defaults.synchronize()

        // Flow 에 새 값 방출
        _alarmsFlow.value = alarms
    }

    private fun getAlarms(): List<AlarmRingInfo> {
        // UserDefaults 에서 꺼낸 문자열이 없으면 빈 리스트
        val json = defaults.stringForKey(ALARMS_KEY) ?: return emptyList()
        return try {
            Json.decodeFromString(json)
        } catch (t: Throwable) {
            // 파싱 실패 시 빈 리스트로 복구
            emptyList()
        }
    }
}