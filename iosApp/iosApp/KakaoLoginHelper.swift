//
//  KakaoLoginHelper.swift
//  iosApp
//
//  Created by 현수 노트북 on 7/3/25.
//

import Foundation
import KakaoSDKAuth
import KakaoSDKUser

@objc(KakaoLoginHelper) 
public class KakaoLoginHelper: NSObject {
    @objc public func login(
        _ callback: @escaping (String?, String?) -> Void
    ) {
        if UserApi.isKakaoTalkLoginAvailable() {
            UserApi.shared.loginWithKakaoTalk { oauthToken, error in
                if let error = error {
                    callback(nil, error.localizedDescription)
                } else {
                    callback(oauthToken?.accessToken, nil)
                }
            }
        } else {
            UserApi.shared.loginWithKakaoAccount { oauthToken, error in
                if let error = error {
                    callback(nil, error.localizedDescription)
                } else {
                    callback(oauthToken?.accessToken, nil)
                }
            }
        }
    }
}
