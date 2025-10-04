//
//  AnalyticsBridge.h
//  iosApp
//
//  Created by 현수 노트북 on 9/27/25.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface ONDAnalytics : NSObject
+ (void)configure;
+ (void)logEvent:(NSString *)name parameters:(NSDictionary<NSString *, id> * _Nullable)params;
+ (void)setUserID:(NSString * _Nullable)userId;
+ (void)setUserProperty:(NSString * _Nullable)value forName:(NSString *)name;
@end

NS_ASSUME_NONNULL_END
