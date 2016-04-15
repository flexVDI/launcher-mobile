//
//  LoginController.h
//  spice_ios
//
//  Created by Wilfredo Carrillo on 7/24/14.
//  Copyright (c) 2014 Wilfredo Carrillo. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface LoginController : UIViewController<UITextFieldDelegate,UITableViewDelegate, UITableViewDataSource,NSURLConnectionDelegate>
{
 @private
    NSMutableData *serverAnswer;
    int connectionState;
}

enum connectionStates {
    C_NONE,
    C_TERMINAL_POLICY,
    C_DESKTOP_POLICY,
    C_CONNECTING
};

@property (strong, nonatomic) NSString *strUrlAuthMode;
@property (strong, nonatomic) NSString *strUrlDesktop;
@property (strong, nonatomic) NSString *hostName;
@property (strong, nonatomic) NSString *domain;
@property (strong, nonatomic) NSString *spiceAddress;
@property (strong, nonatomic) NSString *spicePassword;
@property (strong, nonatomic) NSString *spicePort;
@property (assign, nonatomic) BOOL use_ws;
@property (strong, nonatomic) NSString *launcherDesktop;
@property (assign, nonatomic) BOOL useHttps;
@property (assign, nonatomic) BOOL enableRetina;
@property (assign, nonatomic) BOOL enableAudio;
@property (assign, nonatomic) BOOL silenceBeeper;
@property (assign, nonatomic) BOOL genericSpice;
@property (assign, nonatomic) int selectedDesktop;
@property (strong, nonatomic) NSString *uniqueIdentifier;
@property (retain, nonatomic) IBOutlet UITextField *pass;
@property (retain, nonatomic) IBOutlet UITextField *txtUser;
@property (retain, nonatomic) IBOutlet UITextField *txtPassword;
@property (retain, nonatomic) IBOutlet UIView *viewLoading;
@property (retain, nonatomic) IBOutlet UIActivityIndicatorView *activityIndicator;
@property (retain, nonatomic) IBOutlet UILabel *deviceID;
@property (retain, nonatomic) IBOutlet UIButton *btnConnect;
@property (retain, nonatomic) IBOutlet UIView *viewBackTable;
@property (retain, nonatomic) IBOutlet UITableView *tblDesktop;
@property (strong, nonatomic) NSMutableArray *desktops;
@property (strong, nonatomic) NSMutableArray *desktopsKeys;

- (IBAction)connect:(id)sender;
- (IBAction)btnInfoAction:(id)sender;
- (IBAction)touchConfigAction:(id)sender;
- (IBAction)backgroundTouched:(id)sender;
- (IBAction)viewBackTableTouch:(id)sender;


@end