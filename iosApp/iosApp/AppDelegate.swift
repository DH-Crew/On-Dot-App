//
//  AppDelegate.swift
//  iosApp
//
//  Created by 현수 노트북 on 7/18/25.
//

import UIKit
import UserNotifications
import ComposeApp

// UIApplicationDelegate: iOS 앱 라이프사이클 이벤트(didFinishLaunching, willTerminate 등)를 받아 처리할 수 있도록 해주는 프로토콜
// UNUserNotificationCenterDelegate: 사용자에게 도달한 로컬/원격 알림(Notification)을 앱 내부에서 직접 다룰 수 있게 해 주는 델리게이트 프로토콜
class AppDelegate: NSObject, UIApplicationDelegate, UNUserNotificationCenterDelegate {
    func application(
    _ application: UIApplication,
    didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey : Any]? = nil
    ) -> Bool {
      
        // 알림 센터 델리게이트 지정
        // 앱이 실행 중일 때(포그라운드/백그라운드) 전달되는 알림도 이 클래스에서 처리하도록 지정
        UNUserNotificationCenter.current().delegate = self

        // 권한 요청 (배너, 사운드)
        // requestAuthorization 메서드로 배너와 사운드 알림 권한을 사용자에게 요구
        // 권한 승인, 거절 결과를 클로저의 granted, error로 전달됨
        UNUserNotificationCenter.current().requestAuthorization(options: [.alert, .sound]) { granted,error in
            if granted {
                // 원격(푸시) 알림 수신을 위해 APNs 서버에 디바이스 토큰 등록
                // APNs(Apple Push Notification service) 서버로 디바이스 토큰을 요청
                // application(_:didRegisterForRemoteNotificationsWithDeviceToken:) 콜백에서 토큰을 받아와 서버와 연동할 수 있음
                DispatchQueue.main.async {
                    UIApplication.shared.registerForRemoteNotifications()
                }
            } else {
                // 권한이 거부되었을 때 처리
                self.handleNotificationPermissionDenied()
            }
        }

        return true
    }

    // 푸시(원격 혹은 로컬) 눌렀을 때 호출
    // didReceive respons: 사용자가 알림을 탭했을 때 호출되는 메서드
    func userNotificationCenter(
    _ center: UNUserNotificationCenter,
    didReceive response: UNNotificationResponse,
    withCompletionHandler completionHandler: @escaping () -> Void
    ) {
      
        // 알림 페이로드(userInfo 딕셔너리)에 담긴 추가 데이터를 꺼낼 수 있음
        // 실제로 userInfo에 정보를 추가하는 로직은 iosMain에 존재함
        let info = response.notification.request.content.userInfo

        if let idStr = info["alarmId"] as? String, let id = Int64(idStr), let typeName = info["type"] as? String {
            // KMP AlarmNotifier 에 이벤트 흘리기
            ComposeApp.SharedMethodKt.notifyAlarmEvent(alarmId: id, type: typeName)
        }

        // 알림 클릭 처리가 끝났음을 시스템에 알려주는 콜백
        completionHandler()
    }
    
    // 권한을 거부한 경우 실행되는 콜백
    private func handleNotificationPermissionDenied() {
        let scenes = UIApplication.shared.connectedScenes
        guard
            let windowScene = scenes
            .filter({ $0.activationState == .foregroundActive })
            .first(where: { $0 is UIWindowScene }) as? UIWindowScene,
            let window = windowScene.windows.first,
            let root = window.rootViewController
        else { return }
        
        let alert = UIAlertController(
            title: "알림 권한 필요",
            message: "알림 권한을 허용해야 알림을 받을 수 있습니다.\n설정으로 이동하시겠습니까?",
            preferredStyle: .alert
        )
        
        alert.addAction(.init(title: "설정으로 이동", style: .default) { _ in
            if let url = URL(string: UIApplication.openSettingsURLString) {
                UIApplication.shared.open(url)
            }
        })
        alert.addAction(.init(title: "취소", style: .cancel, handler: nil))
        
        root.present(alert, animated: true, completion: nil)
    }
}
