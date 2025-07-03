//
//  KakaoLoginHelper.h
//  iosApp
//
//  Created by 현수 노트북 on 7/2/25.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface KakaoLoginHelper : NSObject

- (void)login:(void (^)(NSString * _Nullable token,
                       NSString * _Nullable errorMessage))callback;

@end

NS_ASSUME_NONNULL_END
