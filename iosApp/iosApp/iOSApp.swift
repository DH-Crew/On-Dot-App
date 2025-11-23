import SwiftUI
import KakaoSDKCommon
import composeApp
import AlarmKit
import FirebaseAnalytics;

@main
struct iOSApp: App {
    @UIApplicationDelegateAdaptor(AppDelegate.self) var delegate
    @Environment(\.scenePhase) var scenePhase
    
    init() {
        KakaoSDK.initSDK(appKey: KAKAO_APP_KEY)
        
        composeApp.SharedInitKt.doInitKoin(extraModules: [])
    }
    
    var body: some Scene {
        WindowGroup {
            ContentView()
                .onChange(of: scenePhase) {
                    if scenePhase == .active {
                        consumePendingDirectionsAndOpen()
                    }
                }
        }
    }
}

private func consumePendingDirectionsAndOpen() {
    guard let ud = UserDefaults(suiteName: "group.com.dh.ondot.shared"),
          let payload = ud.dictionary(forKey: "pendingOpenDirections") else { return }
    ud.removeObject(forKey: "pendingOpenDirections")
    
    let now = Date()
    let dateFormatter = ISO8601DateFormatter()
    dateFormatter.timeZone = .current
    let isoDate = dateFormatter.string(from: now)
    let epochMs = Int64(now.timeIntervalSince1970 * 1000)

    Analytics.logEvent("departure_alarm_off", parameters: [
        "occurred_at_iso": isoDate,
        "occurred_at_ms": NSNumber(value: epochMs)
    ])
    AmplitudeBridge.shared.track("departure_alarm_off", properties: [
        "occurred_at_iso": isoDate,
        "occurred_at_ms": epochMs
    ])

    // 파싱
    let scheduleId = payload["scheduleId"] as? Int ?? -1
    let alarmId = payload["alarmId"] as? Int ?? -1
    let slat = payload["slat"] as? Double
    let slng = payload["slng"] as? Double
    let elat = payload["elat"] as? Double
    let elng = payload["elng"] as? Double
    let sname = (payload["sname"] as? String) ?? "출발지"
    let ename = (payload["ename"] as? String) ?? "도착지"
    let providerStr = (payload["provider"] as? String)?.lowercased() ?? "kakao"

    guard
      let sLat = slat, let sLng = slng,
      let eLat = elat, let eLng = elng
    else { return }

    let provider: composeApp.DomainMapProvider = {
        switch providerStr {
        case "naver": return .naver
        case "apple": return .apple
        default:      return .kakao
        }
    }()
    
    composeApp.TriggeredAlarmManager().recordTriggeredAlarm(
        scheduleId: Int64(scheduleId),
        alarmId: Int64(alarmId),
        action: DomainAlarmAction.viewRoute
    )
    
    composeApp.DirectionsFacade().openDirections(
        startLat: sLat, startLng: sLng,
        endLat:   eLat, endLng:   eLng,
        provider: provider,
        startName: sname, endName: ename
    )
}
