#import <React/RCTBridgeModule.h>
#import <React/RCTEventEmitter.h>

@interface RCT_EXTERN_MODULE(ShortcutModule, RCTEventEmitter)

RCT_EXTERN_METHOD(setShortcuts:(NSArray *)shortcuts)
RCT_EXTERN_METHOD(getInitialShortcut:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject)

@end
