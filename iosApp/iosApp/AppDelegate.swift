//
//  AppDelegate.swift
//  iosApp
//
//  Created by 현수 노트북 on 7/18/25.
//

import UIKit
import UserNotifications
import ComposeApp

class AppDelegate: NSObject, UIApplicationDelegate, UNUserNotificationCenterDelegate {
  func application(
    _ application: UIApplication,
    didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey : Any]? = nil
  ) -> Bool {
    UNUserNotificationCenter.current().delegate = self
    UNUserNotificationCenter.current().requestAuthorization(options: [.alert, .sound]) { _,_ in }
    application.registerForRemoteNotifications()
    return true
  }

  // 푸시(원격 혹은 로컬) 눌렀을 때 호출
  func userNotificationCenter(
    _ center: UNUserNotificationCenter,
    didReceive response: UNNotificationResponse,
    withCompletionHandler completionHandler: @escaping () -> Void
  ) {
    let info = response.notification.request.content.userInfo
    if
      let idStr = info["alarmId"] as? String,
      let id = Int64(idStr),
      let typeName = info["type"] as? String
    {
        // KMP AlarmNotifier 에 이벤트 흘리기
        ComposeApp.SharedMethodKt.notifyAlarmEvent(alarmId: id, type: typeName)
    }
    completionHandler()
  }
}
