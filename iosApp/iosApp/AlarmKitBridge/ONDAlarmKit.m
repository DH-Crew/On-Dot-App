//
//  ONDAlarmKit.m
//  iosApp
//
//  Created by 현수 노트북 on 9/29/25.
//

#import "ONDAlarmKit.h"
#import "OnDot-Swift.h"

#if __IPHONE_OS_VERSION_MAX_ALLOWED >= 260000

@implementation ONDAlarmKit

+ (void)requestAuthorization:(void(^)(BOOL ok))completion {
    [AlarmKitBridgeShim requestAuthorization:^(BOOL ok) {
        if (completion) completion(ok);
    }];
}

+ (void)scheduleTimerWithSeconds:(NSInteger)seconds
                           title:(NSString *)title
                      scheduleId:(long long)scheduleId
                         alarmId:(long long)alarmId
                       alarmType:(NSString *)alarmType
                         tintHex:(NSString * _Nullable)tintHex
              enableRepeatButton:(BOOL)enableRepeatButton
                      completion:(void(^)(NSString * _Nullable, NSString * _Nullable))completion
{
    [AlarmKitBridgeShim scheduleTimerWithSeconds:(int32_t)seconds
                                           title:title
                                      scheduleId:scheduleId
                                         alarmId:alarmId
                                       alarmType:alarmType
                                         tintHex:tintHex
                              enableRepeatButton:enableRepeatButton
                                      completion:completion];
}

+ (void)scheduleCalendarWithId:(NSString * _Nullable)alarmUUID
                dateComponents:(NSDateComponents *)dateComponents
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
                   mapProvider: (NSString * _Nullable)mapProvider
                      completion:(void(^)(NSString * _Nullable, NSString * _Nullable))completion
{
    [AlarmKitBridgeShim scheduleCalendarWithId:alarmUUID
                                 dateComponents:dateComponents
                                          title:title
                                     scheduleId:scheduleId
                                        alarmId:alarmId
                                      alarmType:alarmType
                                         tintHex:tintHex
                              openMapsOnSecondary:openMapsOnSecondary
                                         startLat:startLat
                                         startLng:startLng
                                           endLat:endLat
                                           endLng:endLng
                                      mapProvider:mapProvider
                                       completion:completion];
}

+ (void)cancelWithId:(NSString *)alarmUUID
          completion:(void(^ _Nullable)(BOOL ok))completion
{
    [AlarmKitBridgeShim cancelWithId:alarmUUID completion:completion];
}

@end
#endif
