//
//  OnDotAlarmWidgetLiveActivity.swift
//  OnDotAlarmWidget
//
//  Created by 현수 노트북 on 9/27/25.
//

import ActivityKit
import WidgetKit
import SwiftUI

struct OnDotAlarmWidgetAttributes: ActivityAttributes {
    public struct ContentState: Codable, Hashable {
        // Dynamic stateful properties about your activity go here!
        var emoji: String
    }

    // Fixed non-changing properties about your activity go here!
    var name: String
}

struct OnDotAlarmWidgetLiveActivity: Widget {
    var body: some WidgetConfiguration {
        ActivityConfiguration(for: OnDotAlarmWidgetAttributes.self) { context in
            // Lock screen/banner UI goes here
            VStack {
                Text("Hello \(context.state.emoji)")
            }
            .activityBackgroundTint(Color.cyan)
            .activitySystemActionForegroundColor(Color.black)

        } dynamicIsland: { context in
            DynamicIsland {
                // Expanded UI goes here.  Compose the expanded UI through
                // various regions, like leading/trailing/center/bottom
                DynamicIslandExpandedRegion(.leading) {
                    Text("Leading")
                }
                DynamicIslandExpandedRegion(.trailing) {
                    Text("Trailing")
                }
                DynamicIslandExpandedRegion(.bottom) {
                    Text("Bottom \(context.state.emoji)")
                    // more content
                }
            } compactLeading: {
                Text("L")
            } compactTrailing: {
                Text("T \(context.state.emoji)")
            } minimal: {
                Text(context.state.emoji)
            }
            .widgetURL(URL(string: "http://www.apple.com"))
            .keylineTint(Color.red)
        }
    }
}

extension OnDotAlarmWidgetAttributes {
    fileprivate static var preview: OnDotAlarmWidgetAttributes {
        OnDotAlarmWidgetAttributes(name: "World")
    }
}

extension OnDotAlarmWidgetAttributes.ContentState {
    fileprivate static var smiley: OnDotAlarmWidgetAttributes.ContentState {
        OnDotAlarmWidgetAttributes.ContentState(emoji: "😀")
     }
     
     fileprivate static var starEyes: OnDotAlarmWidgetAttributes.ContentState {
         OnDotAlarmWidgetAttributes.ContentState(emoji: "🤩")
     }
}

#Preview("Notification", as: .content, using: OnDotAlarmWidgetAttributes.preview) {
   OnDotAlarmWidgetLiveActivity()
} contentStates: {
    OnDotAlarmWidgetAttributes.ContentState.smiley
    OnDotAlarmWidgetAttributes.ContentState.starEyes
}
