//
//  MainViewController.h
//  iOSLauncher
//
//  Created by Sergio Lopez on 11/7/14.
//  Copyright (c) 2014 Flexible Software Solutions S.L. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <QuartzCore/QuartzCore.h>
#import "MainView.h"

@interface MainViewController : UIViewController<UITableViewDelegate, UITableViewDataSource, NSURLConnectionDelegate,MenuDelegate>
{
    CGPoint lastMovementPosition;
    int orientationMask;
    Boolean fixOrientation;
@private
    UITapGestureRecognizer *tapRecognizer;
    UITapGestureRecognizer *doubleTapRecognizer;
    UILongPressGestureRecognizer *longPressRecognizer;
    UIPinchGestureRecognizer *pinchRecognizer;
    UIPanGestureRecognizer *panRecognizer;
    UIPanGestureRecognizer *doublePanRecognizer;
    CGPoint pinchTarget;
    CGPoint pinchDirection;
    CGPoint panTarget;
    int panDirection;
    Boolean dragging;
    double lastTapTimestamp;
    NSDate *lastTapDate;
    
    float panOffsetLastPoint;
    
    CGPoint doublePanLastPoint;
    int doublePanOrientation;
    int doublePanAccumMovement;
    int doublePanEvents;
    int doublePanVelocity;
    
    KeyboardView *keybView;
    Boolean keybVisible;
    
    int connDesiredState;
    int reconnectionState;
    NSMutableData *serverAnswer;
}

enum scrollOrientation {
    SCROLL_HORIZONTAL,
    SCROLL_VERTICAL
};

enum reconnectionStates {
    R_NONE,
    R_LAUNCHER,
    R_LAUNCHER_WAIT,
    R_SPICE,
    R_FAILED
};

@property (retain, nonatomic) IBOutlet UITableView *tblMenu;
@property (retain, nonatomic) IBOutlet UILabel *lblMessage;
@property (strong, nonatomic) NSString *ip;
@property (strong, nonatomic) NSString *port;
@property (strong, nonatomic) NSString *pass;
@property (nonatomic) BOOL enableWebSockets;
@end

