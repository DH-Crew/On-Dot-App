import SwiftUI
import KakaoSDKCommon
import ComposeApp

@main
struct iOSApp: App {
    init() {
        KakaoSDK.initSDK(appKey: KAKAO_APP_KEY)
        
        ComposeApp.SharedInitKt.doInitShared()
    }
    
    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
