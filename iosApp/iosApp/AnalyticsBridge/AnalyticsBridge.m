//
//  AnalyticsBridge.m
//  iosApp
//
//  Created by 현수 노트북 on 9/27/25.
//

@import FirebaseCore;
@import FirebaseAnalytics;

#import "AnalyticsBridge.h"

@implementation ONDAnalytics
+ (void)configure {
    if ([FIRApp defaultApp] == nil) { [FIRApp configure]; }
}
+ (void)logEvent:(NSString *)name parameters:(NSDictionary<NSString *, id> * _Nullable)params {
    [FIRAnalytics logEventWithName:name parameters:params];
}
+ (void)setUserID:(NSString * _Nullable)userId {
    [FIRAnalytics setUserID:userId];
}
+ (void)setUserProperty:(NSString * _Nullable)value forName:(NSString *)name {
    [FIRAnalytics setUserPropertyString:value forName:name];
}
@end
