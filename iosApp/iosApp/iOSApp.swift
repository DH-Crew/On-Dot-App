import SwiftUI
import KakaoSDKCommon
import composeApp
import AlarmKit

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

    // 파싱
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
    
    composeApp.DirectionsFacade().openDirections(
        startLat: sLat, startLng: sLng,
        endLat:   eLat, endLng:   eLng,
        provider: provider,
        startName: sname, endName: ename
    )
}
