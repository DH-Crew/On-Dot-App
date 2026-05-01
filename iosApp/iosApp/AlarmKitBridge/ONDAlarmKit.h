//
//  ONDAlarmKit.h
//  iosApp
//
//  Created by 현수 노트북 on 9/29/25.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

#if __IPHONE_OS_VERSION_MAX_ALLOWED >= 260000

@interface ONDAlarmKit : NSObject

+ (void)requestAuthorization:(void(^)(BOOL ok))completion;

+ (void)scheduleTimerWithSeconds:(NSInteger)seconds
                           title:(NSString *)title
                      scheduleId:(long long)scheduleId
                         alarmId:(long long)alarmId
                       alarmType:(NSString *)alarmType
                         tintHex:(NSString * _Nullable)tintHex
              enableRepeatButton:(BOOL)enableRepeatButton
                      completion:(void(^)(NSString * _Nullable alarmUUID,
                                          NSString * _Nullable errorMsg))completion;

+ (void)scheduleCalendarWithId:(NSString * _Nullable)alarmUUID
                dateComponents:(NSDateComponents *)dateComponents
                    repeatDays:(NSArray<NSNumber *> * _Nullable)repeatDays
                         title:(NSString *)title
                    scheduleId:(long long)scheduleId
                       alarmId:(long long)alarmId
                     alarmType:(NSString *)alarmType
                       tintHex:(NSString * _Nullable)tintHex
           openMapsOnSecondary:(BOOL)openMapsOnSecondary
                      startLat:(NSNumber * _Nullable)startLat
                      startLng:(NSNumber * _Nullable)startLng
                        endLat:(NSNumber * _Nullable)endLat
                        endLng:(NSNumber * _Nullable)endLng
                   mapProvider:(NSString * _Nullable)mapProvider
                    completion:(void(^)(NSString * _Nullable alarmUUID,
                                         NSString * _Nullable errorMsg))completion;

/**
 * status:
 * - @"success"
 * - @"not_found"
 * - @"failure"
 */
+ (void)cancelWithId:(NSString *)alarmUUID
          completion:(void(^ _Nullable)(NSString * _Nullable status,
                                        NSString * _Nullable errorMsg))completion;

@end

#else
@interface ONDAlarmKit : NSObject
@end
#endif

NS_ASSUME_NONNULL_END
