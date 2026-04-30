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
    public var alarmType: String
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

    private static func mapKey(_ ext: String) -> String { "ak.uuid.\(ext)" }

    /// schedule에서만 사용
    static func stableUUID(for ext: String) -> UUID {
        let key = mapKey(ext)
        if let s = UserDefaults.standard.string(forKey: key),
           let u = UUID(uuidString: s) {
            return u
        }
        let u = UUID()
        UserDefaults.standard.set(u.uuidString, forKey: key)
        return u
    }

    /// cancel에서는 이 조회 전용 메서드만 사용
    static func existingUUID(for ext: String) -> UUID? {
        let key = mapKey(ext)
        guard let s = UserDefaults.standard.string(forKey: key),
              let u = UUID(uuidString: s) else {
            return nil
        }
        return u
    }

    static func clearStableUUID(for ext: String) {
        UserDefaults.standard.removeObject(forKey: mapKey(ext))
    }

    private let manager = AlarmManager.shared

    public func requestAuthorization(_ completion: @escaping (Bool) -> Void) {
        Task {
            let ok: Bool
            switch manager.authorizationState {
            case .authorized:
                ok = true
            case .notDetermined:
                do { ok = try await manager.requestAuthorization() == .authorized }
                catch { ok = false }
            default:
                ok = false
            }
            completion(ok)
        }
    }

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
                    secondaryIntent: nil,
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

    @MainActor
    public func scheduleCalendar(
        id: String?,
        dateComponents: DateComponents,
        repeatDays: [Int],
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
                    stopButton: AlarmButton(
                        text: .init(stringLiteral: "Stop"),
                        textColor: .white,
                        systemImageName: "stop.circle.fill"
                    ),
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
                        let recurrence: Alarm.Schedule.Relative.Recurrence = .weekly(weekdays)
                        return .relative(.init(time: time, repeats: recurrence))
                    }

                    if (dateComponents.year != nil || dateComponents.month != nil || dateComponents.day != nil),
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
                    if let ext = id, !ext.isEmpty {
                        return Self.stableUUID(for: ext)
                    }
                    return UUID()
                }()
                
//                print("[AK_TEST] action=schedule_uuid_resolved id=\(id ?? "nil") alarmId=\(alarmId) uuid=\(uuid.uuidString)")

                // 기존 OS 등록이 있다면 best-effort cancel 후 재등록
                do {
                    try manager.cancel(id: uuid)
//                    print("[AK_TEST] action=pre_cancel_success id=\(id ?? "nil") alarmId=\(alarmId) uuid=\(uuid.uuidString)")
                } catch {
                    // not found면 그냥 진행
//                    print("[AK_TEST] action=pre_cancel_fail id=\(id ?? "nil") alarmId=\(alarmId) uuid=\(uuid.uuidString) error=\(error)")
                }
                
//                #if DEBUG
//                if let id, !id.isEmpty {
//                    Task { @MainActor in
//                        try? await Task.sleep(nanoseconds: 2_000_000_000)
//                        print("[AK_TEST] action=auto_cancel_fire id=\(id) alarmId=\(alarmId)")
//                        AlarmKitBridge.shared.cancel(id) { status, error in
//                            print("[AK_TEST] action=auto_cancel_result id=\(id) alarmId=\(alarmId) status=\(status ?? "nil") error=\(error ?? "nil")")
//                        }
//                    }
//                }
//                #endif

                _ = try await manager.schedule(id: uuid, configuration: config)
//                print("[AK_TEST] action=schedule_success id=\(id ?? "nil") alarmId=\(alarmId) uuid=\(uuid.uuidString)")
                completion(uuid.uuidString, nil)
            } catch {
//                print("[AK_TEST] action=schedule_fail id=\(id ?? "nil") alarmId=\(alarmId) error=\(error)")
                completion(nil, "\(error)")
            }
        }
    }

    /**
     * status:
     * - success
     * - not_found
     * - failure
     */
    public func cancel(
        _ id: String,
        _ completion: @escaping (String?, String?) -> Void
    ) {
//        print("[AK_TEST] action=cancel_request id=\(id)")
        
        guard let uuid = Self.existingUUID(for: id) else {
//            print("[AK_TEST] action=cancel_uuid_missing id=\(id)")
            completion("not_found", nil)
            return
        }
        
//        print("[AK_TEST] action=cancel_uuid_resolved id=\(id) uuid=\(uuid.uuidString)")

        do {
            try manager.cancel(id: uuid)
            Self.clearStableUUID(for: id)
//            print("[AK_TEST] action=cancel_success id=\(id) uuid=\(uuid.uuidString)")
            completion("success", nil)
        } catch {
            // stale mapping 가능성이 높으므로 매핑은 정리
            Self.clearStableUUID(for: id)
//            print("[AK_TEST] action=cancel_fail id=\(id) uuid=\(uuid.uuidString) error=\(error)")
            completion("not_found", "\(error)")
        }
    }

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
