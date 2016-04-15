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
- (IBAction)touchConfigAction:(id)sender;
- (IBAction)backgroundTouched:(id)sender;
- (IBAction)viewBackTableTouch:(id)sender;


@end