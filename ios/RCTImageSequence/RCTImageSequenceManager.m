//
// Created by Mads Lee Jensen on 07/07/16.
// Copyright (c) 2016 Facebook. All rights reserved.
//

#import "RCTImageSequenceManager.h"
#import "RCTImageSequenceView.h"
#import <React/RCTUIManager.h>

@implementation RCTImageSequenceManager

- (dispatch_queue_t)methodQueue
{
    return self.bridge.uiManager.methodQueue;
}

RCT_EXPORT_MODULE();
RCT_EXPORT_VIEW_PROPERTY(images, NSArray);
RCT_EXPORT_VIEW_PROPERTY(framesPerSecond, NSUInteger);
RCT_EXPORT_VIEW_PROPERTY(onLoad, RCTDirectEventBlock);
RCT_EXPORT_VIEW_PROPERTY(onEnd, RCTDirectEventBlock);

RCT_EXPORT_METHOD(play: (nonnull NSNumber *)reactTag) {
    [self.bridge.uiManager addUIBlock:^(RCTUIManager *uiManager, NSDictionary<NSNumber *, UIView *> *viewRegistry) {
        UIView *view = viewRegistry[reactTag];
        if (![view isKindOfClass:[RCTImageSequenceView class]]) {
            RCTLog(@"expecting UIView, got: %@", view);
        } else {
            RCTImageSequenceView *imageSequenceView = (RCTImageSequenceView *)view;
            [imageSequenceView play];
        }
    }];
}


RCT_EXPORT_METHOD(stop: (nonnull NSNumber *)reactTag) {
    [self.bridge.uiManager addUIBlock:^(RCTUIManager *uiManager, NSDictionary<NSNumber *, UIView *> *viewRegistry) {
        UIView *view = viewRegistry[reactTag];
        if (![view isKindOfClass:[RCTImageSequenceView class]]) {
            RCTLog(@"expecting UIView, got: %@", view);
        } else {
            RCTImageSequenceView *imageSequenceView = (RCTImageSequenceView *)view;
            [imageSequenceView stop];
        }
    }];
}


- (UIView *)view {
    return [RCTImageSequenceView new];
}

@end