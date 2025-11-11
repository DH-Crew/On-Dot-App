//
//  AmplitudeBridge.h
//  iosApp
//
//  Created by Hyeonsu Son on 11/9/25.
//
#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface ONDAmplitude : NSObject
+ (void)configure;
+ (void)logEvent:(NSString *)name parameters:(NSDictionary<NSString *, id> * _Nullable)params;
+ (void)setUserID:(NSString * _Nullable)userId;
@end

NS_ASSUME_NONNULL_END
