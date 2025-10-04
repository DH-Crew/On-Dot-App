//
//  OnDotAlarmWidgetBundle.swift
//  OnDotAlarmWidget
//
//  Created by 현수 노트북 on 9/27/25.
//

import WidgetKit
import SwiftUI

@main
struct OnDotAlarmWidgetBundle: WidgetBundle {
    var body: some Widget {
        OnDotAlarmWidget()
        OnDotAlarmWidgetControl()
        OnDotAlarmWidgetLiveActivity()
    }
}
