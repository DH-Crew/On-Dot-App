//
//  AppIntent.swift
//  OnDotAlarmWidget
//
//  Created by 현수 노트북 on 9/27/25.
//

import WidgetKit
import AppIntents

struct ConfigurationAppIntent: WidgetConfigurationIntent {
    static var title: LocalizedStringResource { "알람 위젯 설정" }
    static var description: IntentDescription { "OnDot 알람 위젯 구성" }

    @Parameter(title: "알람 타입", default: "출발")
    var alarmType: String
}
