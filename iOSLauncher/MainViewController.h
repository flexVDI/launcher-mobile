/*
 * launcher-mobile: a multiplatform flexVDI/SPICE client
 *
 * Copyright (C) 2016 flexVDI (Flexible Software Solutions S.L.)
 *
 * This file is part of launcher-mobile.
 *
 * launcher-mobile is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * launcher-mobile is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with launcher-mobile.  If not, see <http://www.gnu.org/licenses/>.
 */

#import <UIKit/UIKit.h>
#import <QuartzCore/QuartzCore.h>
#import "MainView.h"

@interface MainViewController : UIViewController<UITableViewDelegate, UITableViewDataSource, NSURLConnectionDelegate,MenuDelegate,UITextViewDelegate>
{
    CGPoint lastMovementPosition;
    int orientationMask;
    Boolean fixOrientation;
    Boolean keyboardRequested;
@private
    MainView *mainView;
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
    Boolean keybEnabled;
    
    int connDesiredState;
    int reconnectionState;
    NSMutableData *serverAnswer;
    
    Boolean textViewRangeAutoChanged;
    
    CFURLRef soundFileURL;
    SystemSoundID soundFileID;
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
@property (nonatomic) BOOL use_ws;
@property (nonatomic) BOOL enableAudio;
@property (nonatomic) BOOL silenceBeeper;
@property (nonatomic) BOOL genericSpice;

@end

