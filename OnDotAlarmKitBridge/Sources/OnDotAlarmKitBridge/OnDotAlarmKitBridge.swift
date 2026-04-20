// The Swift Programming Language
// https://docs.swift.org/swift-book

import Foundation
@preconcurrency import AlarmKit
import AppIntents
import SwiftUI
import MapKit
import os

public struct AlarmBridgeMetadata: AlarmMetadata, Sendable, Codable, Hashable {
    public var scheduleId: Int64
    public var alarmId: Int64
    public var alarmType: String     // "preparation" | "departure" 등
    public var title: String
    public var startLat: Double?
    public var startLng: Double?
    public var endLat: Double?
    public var endLng: Double?

    public init(
        scheduleId: Int64,
        alarmId: Int64,
        alarmType: String,
        title: String,
        startLat: Double? = nil,
        startLng: Double? = nil,
        endLat: Double? = nil,
        endLng: Double? = nil
    ) {
        self.scheduleId = scheduleId
        self.alarmId = alarmId
        self.alarmType = alarmType
        self.title = title
        self.startLat = startLat
        self.startLng = startLng
        self.endLat = endLat
        self.endLng = endLng
    }
}

@available(iOS 26.0, *)
@MainActor
@objcMembers
public final class AlarmKitBridge: NSObject {

    public static let shared = AlarmKitBridge()

    private override init() { super.init() }

    // 외부 ID ↔ UUID 안정 매핑
    private static func mapKey(_ ext: String) -> String { "ak.uuid.\(ext)" }
    static func stableUUID(for ext: String) -> UUID {
        let k = mapKey(ext)
        if let s = UserDefaults.standard.string(forKey: k),
           let u = UUID(uuidString: s) { return u }
        let u = UUID()
        UserDefaults.standard.set(u.uuidString, forKey: k)
        return u
    }
    static func clearStableUUID(for ext: String) {
        UserDefaults.standard.removeObject(forKey: mapKey(ext))
    }

    private let manager = AlarmManager.shared

    // MARK: Authorization
    @MainActor
    public func requestAuthorization(_ completion: @escaping (Bool) -> Void) {
        Task {
            let ok: Bool
            switch manager.authorizationState {
            case .authorized: ok = true
            case .notDetermined:
                do { ok = try await manager.requestAuthorization() == .authorized }
                catch { ok = false }
            default: ok = false
            }
            completion(ok)
        }
    }

    // MARK: Timer (countdown)
    @MainActor
    public func scheduleTimer(
        seconds: Int,
        title: String,
        scheduleId: Int64,
        alarmId: Int64,
        alarmType: String,
        tintHex: String? = nil,
        enableRepeatButton: Bool = true,
        _ completion: @escaping (String?, String?) -> Void
    ) {
        Task {
            do {
                let alert = AlarmPresentation.Alert(
                    title: .init(stringLiteral: title),
                    stopButton: AlarmButton(
                        text: .init(stringLiteral: "Stop"),
                        textColor: .white,
                        systemImageName: "stop.circle.fill"
                    ),
                    secondaryButton: enableRepeatButton ? AlarmButton(
                        text: .init(stringLiteral: "Snooze"),
                        textColor: .white,
                        systemImageName: "clock.arrow.trianglehead.2.counterclockwise.rotate.90"
                    ) : nil,
                    secondaryButtonBehavior: enableRepeatButton ? .countdown : nil
                )

                let attrs = AlarmAttributes<AlarmBridgeMetadata>(
                    presentation: AlarmPresentation(alert: alert),
                    metadata: AlarmBridgeMetadata(
                        scheduleId: scheduleId,
                        alarmId: alarmId,
                        alarmType: alarmType,
                        title: title
                    ),
                    tintColor: (tintHex.flatMap { Color(hex: $0) }) ?? .accentColor
                )

                let config = AlarmManager.AlarmConfiguration.timer(
                    duration: TimeInterval(seconds),
                    attributes: attrs,
                    stopIntent: nil,
                    secondaryIntent: enableRepeatButton ? nil : nil, // 카운트다운이면 버튼 동작은 AlarmKit이 처리
                    sound: .default
                )

                let uuid = UUID()
                _ = try await manager.schedule(id: uuid, configuration: config)
                completion(uuid.uuidString, nil)
            } catch {
                completion(nil, "\(error)")
            }
        }
    }

    // MARK: Calendar
    @MainActor
    public func scheduleCalendar(
        id: String?,
        dateComponents: DateComponents,   // hour/minute 필수. year/month/day가 있으면 fixed, 아니면 relative
        repeatDays: [Int], // 1: 일, 2: 월, ..., 7: 토
        title: String,
        scheduleId: Int64,
        alarmId: Int64,
        alarmType: String,
        tintHex: String? = nil,
        openMapsOnSecondary: Bool = false,
        startLat: NSNumber?, startLng: NSNumber?,
        endLat: NSNumber?, endLng: NSNumber?,
        mapProvider: String,
        _ completion: @escaping (String?, String?) -> Void
    ) {
        print("[AlarmKitBridge] startLat: \(startLat), startLng: \(startLng)")
        
        Task {
            do {
                var secondaryButton: AlarmButton? = nil
                var secondaryIntent: (any LiveActivityIntent)? = nil
                if openMapsOnSecondary {
                    secondaryButton = AlarmButton(
                        text: .init(stringLiteral: "경로안내 보기"),
                        textColor: .white,
                        systemImageName: "arrow.triangle.turn.up.right.circle.fill"
                    )
                    secondaryIntent = OpenMapsIntent(
                        scheduleId: Int(scheduleId),
                        alarmId: Int(alarmId),
                        startLat: startLat?.doubleValue,
                        startLng: startLng?.doubleValue,
                        endLat: endLat?.doubleValue,
                        endLng: endLng?.doubleValue,
                        name: title,
                        mapProvider: mapProvider
                    )
                }

                let alert = AlarmPresentation.Alert(
                    title: .init(stringLiteral: title),
                    stopButton: AlarmButton(text: .init(stringLiteral: "Stop"), textColor: .white, systemImageName: "stop.circle.fill"),
                    secondaryButton: secondaryButton,
                    secondaryButtonBehavior: secondaryButton != nil ? .custom : nil
                )

                let attrs = AlarmAttributes<AlarmBridgeMetadata>(
                    presentation: AlarmPresentation(alert: alert),
                    metadata: AlarmBridgeMetadata(
                        scheduleId: scheduleId,
                        alarmId: alarmId,
                        alarmType: alarmType,
                        title: title,
                        startLat: startLat?.doubleValue,
                        startLng: startLng?.doubleValue,
                        endLat: endLat?.doubleValue,
                        endLng: endLng?.doubleValue
                    ),
                    tintColor: (tintHex.flatMap { Color(hex: $0) }) ?? .green
                )

                let schedule: Alarm.Schedule = {
                    let weekdays = repeatDays.compactMap { weekday(from: $0) }
                    
                    if !weekdays.isEmpty {
                        let hour = dateComponents.hour ?? 0
                        let minute = dateComponents.minute ?? 0
                        let time = Alarm.Schedule.Relative.Time(hour: hour, minute: minute)

                        let recurrence: Alarm.Schedule.Relative.Recurrence =
                            .weekly(weekdays)

                        let relative = Alarm.Schedule.Relative(
                            time: time,
                            repeats: recurrence
                        )
                        return .relative(relative)
                    }
                    
                    if dateComponents.year != nil || dateComponents.month != nil || dateComponents.day != nil,
                       let date = Calendar.current.date(from: dateComponents) {
                        return .fixed(date)
                    } else {
                        let hour = dateComponents.hour ?? 0
                        let minute = dateComponents.minute ?? 0
                        let time = Alarm.Schedule.Relative.Time(hour: hour, minute: minute)
                        return .relative(.init(time: time, repeats: .never))
                    }
                }()

                let config = AlarmManager.AlarmConfiguration.alarm(
                    schedule: schedule,
                    attributes: attrs,
                    stopIntent: alarmType == "departure" ? OpenMapsIntent(
                        scheduleId: Int(scheduleId),
                        alarmId: Int(alarmId),
                        startLat: startLat?.doubleValue,
                        startLng: startLng?.doubleValue,
                        endLat: endLat?.doubleValue,
                        endLng: endLng?.doubleValue,
                        name: title,
                        mapProvider: mapProvider
                    ) : nil,
                    secondaryIntent: secondaryIntent,
                    sound: .default
                )

                let uuid: UUID = {
                    if let ext = id, !ext.isEmpty { return Self.stableUUID(for: ext) }
                    return UUID()
                }()
                do { try manager.cancel(id: uuid) } catch { /* not found: 무시 */ }
                _ = try await manager.schedule(id: uuid, configuration: config)
                completion(uuid.uuidString, nil)
            } catch {
                completion(nil, "\(error)")
            }
        }
    }
    
    public func cancel(_ id: String, _ completion: ((Bool) -> Void)? = nil) {
        do {
            let uuid = Self.stableUUID(for: id)
            try manager.cancel(id: uuid)
            Self.clearStableUUID(for: id)
            completion?(true)
        } catch {
            Self.clearStableUUID(for: id)
            completion?(false)
        }
    }
    
    // MARK: Test
//    @MainActor
//    public func scheduleCalendar(
//        id: String?,
//        dateComponents: DateComponents,
//        repeatDays: [Int],
//        title: String,
//        scheduleId: Int64,
//        alarmId: Int64,
//        alarmType: String,
//        tintHex: String? = nil,
//        openMapsOnSecondary: Bool = false,
//        startLat: NSNumber?, startLng: NSNumber?,
//        endLat: NSNumber?, endLng: NSNumber?,
//        mapProvider: String,
//        _ completion: @escaping (String?, String?) -> Void
//    ) {
//        print("[AK_TEST] action=schedule_request id=\(id ?? "nil") alarmId=\(alarmId)")
//
//        Task {
//            do {
//                var secondaryButton: AlarmButton? = nil
//                var secondaryIntent: (any LiveActivityIntent)? = nil
//                if openMapsOnSecondary {
//                    secondaryButton = AlarmButton(
//                        text: .init(stringLiteral: "경로안내 보기"),
//                        textColor: .white,
//                        systemImageName: "arrow.triangle.turn.up.right.circle.fill"
//                    )
//                    secondaryIntent = OpenMapsIntent(
//                        scheduleId: Int(scheduleId),
//                        alarmId: Int(alarmId),
//                        startLat: startLat?.doubleValue,
//                        startLng: startLng?.doubleValue,
//                        endLat: endLat?.doubleValue,
//                        endLng: endLng?.doubleValue,
//                        name: title,
//                        mapProvider: mapProvider
//                    )
//                }
//
//                let alert = AlarmPresentation.Alert(
//                    title: .init(stringLiteral: title),
//                    stopButton: AlarmButton(
//                        text: .init(stringLiteral: "Stop"),
//                        textColor: .white,
//                        systemImageName: "stop.circle.fill"
//                    ),
//                    secondaryButton: secondaryButton,
//                    secondaryButtonBehavior: secondaryButton != nil ? .custom : nil
//                )
//
//                let attrs = AlarmAttributes<AlarmBridgeMetadata>(
//                    presentation: AlarmPresentation(alert: alert),
//                    metadata: AlarmBridgeMetadata(
//                        scheduleId: scheduleId,
//                        alarmId: alarmId,
//                        alarmType: alarmType,
//                        title: title,
//                        startLat: startLat?.doubleValue,
//                        startLng: startLng?.doubleValue,
//                        endLat: endLat?.doubleValue,
//                        endLng: endLng?.doubleValue
//                    ),
//                    tintColor: (tintHex.flatMap { Color(hex: $0) }) ?? .green
//                )
//
//                let schedule: Alarm.Schedule = {
//                    let weekdays = repeatDays.compactMap { weekday(from: $0) }
//
//                    if !weekdays.isEmpty {
//                        let hour = dateComponents.hour ?? 0
//                        let minute = dateComponents.minute ?? 0
//                        let time = Alarm.Schedule.Relative.Time(hour: hour, minute: minute)
//                        let recurrence: Alarm.Schedule.Relative.Recurrence = .weekly(weekdays)
//                        let relative = Alarm.Schedule.Relative(time: time, repeats: recurrence)
//                        return .relative(relative)
//                    }
//
//                    if dateComponents.year != nil || dateComponents.month != nil || dateComponents.day != nil,
//                       let date = Calendar.current.date(from: dateComponents) {
//                        return .fixed(date)
//                    } else {
//                        let hour = dateComponents.hour ?? 0
//                        let minute = dateComponents.minute ?? 0
//                        let time = Alarm.Schedule.Relative.Time(hour: hour, minute: minute)
//                        return .relative(.init(time: time, repeats: .never))
//                    }
//                }()
//
//                let config = AlarmManager.AlarmConfiguration.alarm(
//                    schedule: schedule,
//                    attributes: attrs,
//                    stopIntent: alarmType == "departure" ? OpenMapsIntent(
//                        scheduleId: Int(scheduleId),
//                        alarmId: Int(alarmId),
//                        startLat: startLat?.doubleValue,
//                        startLng: startLng?.doubleValue,
//                        endLat: endLat?.doubleValue,
//                        endLng: endLng?.doubleValue,
//                        name: title,
//                        mapProvider: mapProvider
//                    ) : nil,
//                    secondaryIntent: secondaryIntent,
//                    sound: .default
//                )
//
//                let uuid: UUID = {
//                    if let ext = id, !ext.isEmpty { return Self.stableUUID(for: ext) }
//                    return UUID()
//                }()
//
//                print("[AK_TEST] action=schedule_uuid_resolved id=\(id ?? "nil") alarmId=\(alarmId) uuid=\(uuid.uuidString)")
//
//                do {
//                    try manager.cancel(id: uuid)
//                    print("[AK_TEST] action=pre_cancel_success id=\(id ?? "nil") alarmId=\(alarmId) uuid=\(uuid.uuidString)")
//                } catch {
//                    print("[AK_TEST] action=pre_cancel_fail id=\(id ?? "nil") alarmId=\(alarmId) uuid=\(uuid.uuidString) error=\(error)")
//                }
//
//                #if DEBUG
//                if let id, !id.isEmpty {
//                    Task { @MainActor in
//                        try? await Task.sleep(nanoseconds: 200_000_000) // 0.2초
//                        print("[AK_TEST] action=auto_cancel_fire id=\(id) alarmId=\(alarmId)")
//                        AlarmKitBridge.shared.cancel(id) { ok in
//                            print("[AK_TEST] action=auto_cancel_result id=\(id) alarmId=\(alarmId) ok=\(ok)")
//                        }
//                    }
//                }
//
//                print("[AK_TEST] action=schedule_delay_start id=\(id ?? "nil") alarmId=\(alarmId) uuid=\(uuid.uuidString)")
//                try await Task.sleep(nanoseconds: 3_000_000_000) // 3초 딜레이
//                print("[AK_TEST] action=schedule_delay_end id=\(id ?? "nil") alarmId=\(alarmId) uuid=\(uuid.uuidString)")
//                #endif
//
//                _ = try await manager.schedule(id: uuid, configuration: config)
//                print("[AK_TEST] action=schedule_success id=\(id ?? "nil") alarmId=\(alarmId) uuid=\(uuid.uuidString)")
//                completion(uuid.uuidString, nil)
//            } catch {
//                print("[AK_TEST] action=schedule_fail id=\(id ?? "nil") alarmId=\(alarmId) error=\(error)")
//                completion(nil, "\(error)")
//            }
//        }
//    }
//    
//    public func cancel(_ id: String, _ completion: ((Bool) -> Void)? = nil) {
//        print("[AK_TEST] action=cancel_request id=\(id)")
//
//        do {
//            let uuid = Self.stableUUID(for: id)
//            print("[AK_TEST] action=cancel_uuid_resolved id=\(id) uuid=\(uuid.uuidString)")
//
//            try manager.cancel(id: uuid)
//            Self.clearStableUUID(for: id)
//
//            print("[AK_TEST] action=cancel_success id=\(id) uuid=\(uuid.uuidString)")
//            completion?(true)
//        } catch {
//            Self.clearStableUUID(for: id)
//            print("[AK_TEST] action=cancel_fail id=\(id) error=\(error)")
//            completion?(false)
//        }
//    }
    
    // repeatDays를 AlarmKit에서 활용할 수 있도록 변환
    private func weekday(from value: Int) -> Locale.Weekday? {
        switch value {
        case 1: return .sunday
        case 2: return .monday
        case 3: return .tuesday
        case 4: return .wednesday
        case 5: return .thursday
        case 6: return .friday
        case 7: return .saturday
        default: return nil
        }
    }
}

@available(iOS 26.0, *)
struct OpenMapsIntent: LiveActivityIntent {
    static var title: LocalizedStringResource { "Open Maps" }
    static var openAppWhenRun: Bool { true }

    @Parameter(title: "Schedule Id") var scheduleId: Int
    @Parameter(title: "Alarm Id") var alarmId: Int
    @Parameter(title: "Start Latitude") var startLat: Double?
    @Parameter(title: "Start Longitude") var startLng: Double?
    @Parameter(title: "End Latitude")   var endLat: Double?
    @Parameter(title: "End Longitude")  var endLng: Double?
    @Parameter(title: "Name")           var name: String?
    @Parameter(title: "Map Provider")   var mapProvider: String?

    init() {}

    init(
        scheduleId: Int,
        alarmId: Int,
        startLat: Double?,
        startLng: Double?,
        endLat: Double?,
        endLng: Double?,
        name: String?,
        mapProvider: String?
    ) {
        self.scheduleId = scheduleId
        self.alarmId = alarmId
        self.startLat = startLat
        self.startLng = startLng
        self.endLat   = endLat
        self.endLng   = endLng
        self.name     = name
        self.mapProvider = mapProvider
    }

    @MainActor
    func perform() async throws -> some IntentResult {
        guard let sLat = startLat, let sLng = startLng,
              let eLat = endLat,   let eLng = endLng, let mapProvider = mapProvider else {
            return .result()
        }

        if let ud = UserDefaults(suiteName: "group.com.dh.ondot.shared") {
            ud.set(
                [
                    "scheduleId": scheduleId,
                    "alarmId": alarmId,
                    "slat": sLat,
                    "slng": sLng,
                    "elat": eLat,
                    "elng": eLng,
                    "sname": "출발지",
                    "ename": name ?? "도착지",
                    "provider": mapProvider
                ],
                forKey: "pendingOpenDirections"
            )
            ud.synchronize()
        }

        return .result()
    }
}

extension Color {
    init?(hex: String) {
        var s = hex.trimmingCharacters(in: .whitespacesAndNewlines).uppercased()
        if s.hasPrefix("#") { s.removeFirst() }
        guard s.count == 6, let v = Int(s, radix: 16) else { return nil }
        self = Color(
            red:   Double((v >> 16) & 0xFF) / 255.0,
            green: Double((v >>  8) & 0xFF) / 255.0,
            blue:  Double( v        & 0xFF) / 255.0
        )
    }
}
