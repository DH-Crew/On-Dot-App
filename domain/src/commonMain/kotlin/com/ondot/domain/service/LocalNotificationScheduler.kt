package com.ondot.domain.service

import com.ondot.domain.model.request.local_notification.LocalNotificationRequest

interface LocalNotificationScheduler {
    /** 같은 id로 여러 번 호출해도 마지막 스케줄만 살아있게 구현 */
    fun schedule(request: LocalNotificationRequest)
    /** id 기반으로 예약 + 표시된 알림 모두 취소 */
    fun cancel(id: String)
}