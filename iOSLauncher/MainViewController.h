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

@interface MainViewController : UIViewController<UITableViewDelegate, UITableViewDataSource, MenuDelegate>
{
    CGPoint lastMovementPosition;
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
    int doublePanEvents;
    int doublePanVelocity;
    
    KeyboardView *keybView;
    Boolean keybVisible;
    
    int connDesiredState;
}
@property (retain, nonatomic) IBOutlet UITableView *tblMenu;
@property (retain, nonatomic) IBOutlet UILabel *lblMessage;
@property (strong, nonatomic) NSString *ip;
@property (strong, nonatomic) NSString *port;
@property (strong, nonatomic) NSString *pass;
@end

