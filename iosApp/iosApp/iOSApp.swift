import SwiftUI
import KakaoSDKCommon
import ComposeApp

@main
struct iOSApp: App {
    init() {
        KakaoSDK.initSDK(appKey: KAKAO_APP_KEY)
        
        ComposeApp.SharedInitKt.doInitShared()
        ComposeApp.SharedInitKt.doInitKoin()
    }
    
    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
