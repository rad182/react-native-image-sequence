//
// Created by Mads Lee Jensen on 07/07/16.
// Copyright (c) 2016 Facebook. All rights reserved.
//

#import "RCTImageSequenceManager.h"
#import "RCTImageSequenceView.h"

@interface RCTImageSequenceManager ()

@property (strong, nonatomic) RCTImageSequenceView *imageSequenceView;

@end

@implementation RCTImageSequenceManager

RCT_EXPORT_MODULE(RCTImageSequence);
RCT_EXPORT_VIEW_PROPERTY(images, NSArray);
RCT_EXPORT_VIEW_PROPERTY(framesPerSecond, NSUInteger);
RCT_EXPORT_METHOD(reset) {
    [self.imageSequenceView reset];
}

- (UIView *)view {
    if (self.imageSequenceView == nil) {
        self.imageSequenceView = [RCTImageSequenceView new];
    }
    return self.imageSequenceView;
}

@end
