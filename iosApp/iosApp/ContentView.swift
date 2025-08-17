import UIKit
import SwiftUI
import ComposeApp
import KakaoSDKAuth

struct ComposeView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        MainViewControllerKt.MainViewController()
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}

struct ContentView: View {
    var body: some View {
        ComposeView()
                .ignoresSafeArea() // Compose has own keyboard handler
                .onOpenURL { url in
                    print("Received URL: \(url)")
                    if AuthApi.isKakaoTalkLoginUrl(url) {
                        _ = AuthController.handleOpenUrl(url: url, options: [:])
                    }
                }
    }
    
}



