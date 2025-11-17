//
//  AmplitudeBridge.swift
//  iosApp
//
//  Created by Hyeonsu Son on 11/9/25.
//

import Foundation
import AmplitudeSwift
import AmplitudeSwiftSessionReplayPlugin

@objc(AmplitudeBridge)
public final class AmplitudeBridge: NSObject {
    private var amplitude: Amplitude?

    @objc public static let shared = AmplitudeBridge()

    @objc public func configure(apiKey: String, optOut: Bool = false) {
        print("API KEY: \(apiKey)")
        let config = Configuration(apiKey: apiKey)
        config.optOut = optOut
        amplitude = Amplitude(configuration: config)
        amplitude?.add(plugin: AmplitudeSwiftSessionReplayPlugin(sampleRate: 1))
    }

    @objc public func track(_ name: String, properties: [String: Any]? = nil) {
        print("TRACK: \(name)")
        amplitude?.track(eventType: name, eventProperties: properties ?? [:])
    }

    @objc public func setUserId(_ id: String?) {
        print("SET USER ID: \(id ?? "nil")")
        amplitude?.setUserId(userId: id)
    }
}
