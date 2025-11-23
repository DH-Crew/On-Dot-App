//
//  AlarmKitBridgeShim.swift
//  iosApp
//
//  Created by 현수 노트북 on 10/2/25.
//

import Foundation
import OnDotAlarmKitBridge

@available(iOS 26.0, *)
@objc(AlarmKitBridgeShim)
public final class AlarmKitBridgeShim: NSObject {

    @objc public static func requestAuthorization(_ completion: @escaping (Bool) -> Void) {
        Task { @MainActor in
            AlarmKitBridge.shared.requestAuthorization(completion)
        }
    }

    @objc public static func scheduleTimer(
        seconds: Int32,
        title: String,
        scheduleId: Int64,
        alarmId: Int64,
        alarmType: String,
        tintHex: String?,
        enableRepeatButton: Bool,
        completion: @escaping (String?, String?) -> Void
    ) {
        Task { @MainActor in
            AlarmKitBridge.shared.scheduleTimer(
                seconds: Int(seconds),
                title: title,
                scheduleId: scheduleId,
                alarmId: alarmId,
                alarmType: alarmType,
                tintHex: tintHex,
                enableRepeatButton: enableRepeatButton,
                completion
            )
        }
    }

    @objc public static func scheduleCalendar(
        id: String?,
        dateComponents: NSDateComponents,
        repeatDays: [Int],
        title: String,
        scheduleId: Int64,
        alarmId: Int64,
        alarmType: String,
        tintHex: String?,
        openMapsOnSecondary: Bool,
        startLat: NSNumber?, startLng: NSNumber?,
        endLat: NSNumber?,   endLng: NSNumber?,
        mapProvider: String,
        completion: @escaping (String?, String?) -> Void
    ) {
        Task { @MainActor in
            AlarmKitBridge.shared.scheduleCalendar(
                id: id,
                dateComponents: dateComponents as DateComponents,
                repeatDays: repeatDays,
                title: title,
                scheduleId: scheduleId,
                alarmId: alarmId,
                alarmType: alarmType,
                tintHex: tintHex,
                openMapsOnSecondary: openMapsOnSecondary,
                startLat: startLat, startLng: startLng,
                endLat: endLat,     endLng: endLng,
                mapProvider: mapProvider,
                completion
            )
        }
    }

    @objc(cancelWithId:completion:)
    public static func cancel(_ id: String, _ completion: ((Bool) -> Void)?) {
        Task { @MainActor in
            AlarmKitBridge.shared.cancel(id, completion)
        }
    }
}
