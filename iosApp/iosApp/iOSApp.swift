import SwiftUI
import KakaoSDKCommon

@main
struct iOSApp: App {
    init() {
        KakaoSDK.initSDK(appKey: KAKAO_APP_KEY)
    }
    
    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
