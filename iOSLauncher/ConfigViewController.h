//
//  ConfigViewController.h
//  spice_ios
//
//  Created by Nologin on 14/08/14.
//  Copyright (c) 2014 Wilfredo Carrillo. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "Toast+UIView.h"
#import "flexConstants.h"
@interface ConfigViewController : UIViewController<UITextFieldDelegate>
@property (retain, nonatomic) IBOutlet UITextField *txtDomain;
//@property (retain, nonatomic) IBOutlet UISwitch *switchHttps;
@property (retain, nonatomic) IBOutlet UISwitch *enableWebSockets;
@property (retain, nonatomic) IBOutlet UISwitch *enableRetina;
@property (retain, nonatomic) IBOutlet UITextField *txtPort;
@property (retain, nonatomic) IBOutlet UIButton *btnSave;
@property (retain, nonatomic) IBOutlet UITextField *txtIp;
@property (retain, nonatomic) IBOutlet UILabel *lblVersion;
- (IBAction)btnSaveAction:(id)sender;
- (IBAction)backgroudTouched:(id)sender;

@end
