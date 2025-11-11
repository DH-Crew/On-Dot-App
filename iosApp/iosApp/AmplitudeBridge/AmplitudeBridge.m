//
//  AmplitudeBridge.m
//  iosApp
//
//  Created by Hyeonsu Son on 11/9/25.
//

#import <Foundation/Foundation.h>
#import "AmplitudeBridge.h"
#import "OnDot-Swift.h"

@implementation ONDAmplitude
+ (void)configure {
    
}
+ (void)logEvent:(NSString *)name parameters:(NSDictionary<NSString *, id> * _Nullable)params {
    [[AmplitudeBridge shared] track:name properties:params];
}

+ (void)setUserID:(NSString * _Nullable)userId {
    [[AmplitudeBridge shared] setUserId:userId];
}
@end
