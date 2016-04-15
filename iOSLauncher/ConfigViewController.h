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
#import "Toast+UIView.h"
#import "flexConstants.h"
@interface ConfigViewController : UIViewController<UITextFieldDelegate>
@property (retain, nonatomic) IBOutlet UITextField *txtDomain;
//@property (retain, nonatomic) IBOutlet UISwitch *switchHttps;
@property (retain, nonatomic) IBOutlet UISwitch *enableAudio;
@property (retain, nonatomic) IBOutlet UISwitch *enableRetina;
@property (retain, nonatomic) IBOutlet UISwitch *silenceBeeper;
@property (retain, nonatomic) IBOutlet UISwitch *genericSpice;
@property (retain, nonatomic) IBOutlet UITextField *txtPort;
@property (retain, nonatomic) IBOutlet UIButton *btnSave;
@property (retain, nonatomic) IBOutlet UITextField *txtIp;
@property (retain, nonatomic) IBOutlet UILabel *lblVersion;
- (IBAction)btnSaveAction:(id)sender;
- (IBAction)backgroudTouched:(id)sender;

@end
