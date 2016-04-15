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

#import "ConfigViewController.h"

@interface ConfigViewController ()

@end

@implementation ConfigViewController

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    _txtIp.delegate = self;
    _txtPort.delegate = self;
    _txtDomain.delegate = self;
    _lblVersion.text = @"v2.2.6";
    _lblVersion.layer.cornerRadius = 8.0f;
    
    NSString *ver = [[UIDevice currentDevice] systemVersion];
    float ver_float = [ver floatValue];
    if (ver_float > 9.0) {
        UITextInputAssistantItem* item = [_txtIp inputAssistantItem];
        item.leadingBarButtonGroups = @[];
        item.trailingBarButtonGroups = @[];
    
        item = [_txtPort inputAssistantItem];
        item.leadingBarButtonGroups = @[];
        item.trailingBarButtonGroups = @[];
    }
    
    NSString* serverIP = [[NSUserDefaults standardUserDefaults] stringForKey:kFlexKeyServerIP];
    if (serverIP) {
        _txtIp.text = serverIP;
    }
    
    NSString* serverPort = [[NSUserDefaults standardUserDefaults] stringForKey:kFlexKeyServerPort];
    if (serverPort) {
        _txtPort.text = serverPort;
    } else {
        _txtPort.text = @"443";
    }
    
    NSString *serverDomain = [[NSUserDefaults standardUserDefaults] stringForKey:kFlexKeyServerDomain];
    if (serverDomain) {
        _txtDomain.text = serverDomain;
    }
    
    if ([[NSUserDefaults standardUserDefaults] boolForKey:kFlexKeyEnableAudio] == TRUE){;
        _enableAudio.on = TRUE;
    } else {
        _enableAudio.on = FALSE;
    }
    
    if ([[NSUserDefaults standardUserDefaults] objectForKey:kFlexKeyEnableRetina] == nil) {
        if (UI_USER_INTERFACE_IDIOM() == UIUserInterfaceIdiomPad) {
            _enableRetina.on = FALSE;
        } else {
            _enableRetina.on = TRUE;
        }
    } else {
        if ([[NSUserDefaults standardUserDefaults] boolForKey:kFlexKeyEnableRetina] == TRUE){
            _enableRetina.on = TRUE;
        } else {
            _enableRetina.on = FALSE;
        }
    }
    
    if ([[NSUserDefaults standardUserDefaults] boolForKey:kFlexKeySilenceBeeper] == TRUE){
        _silenceBeeper.on = TRUE;
    } else {
        _silenceBeeper.on = FALSE;
    }
    
    if ([[NSUserDefaults standardUserDefaults] boolForKey:kFlexKeyGenericSpice] == TRUE){
        _genericSpice.on = TRUE;
    } else {
        _genericSpice.on = FALSE;
    }
    
    _btnSave.layer.cornerRadius = 10.0f;
}
-(void)viewDidAppear:(BOOL)animated
{
    if (_txtIp.text.length == 0) {
        [[self view] makeToast:NSLocalizedString(@"ask_server_data", nil) duration:ToastDurationShort position:@"center"];
    }
}
- (BOOL)prefersStatusBarHidden {
    return YES;
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}


/*
#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender
{
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
}
*/


- (void)dealloc {
//    [_txtIp release];
//    [_txtPort release];
//    [_switchHttps release];
//    [_txtDomain release];
//    [_btnSave release];
//    [super dealloc];
}
#pragma mark -
#pragma mark other methods
- (IBAction)btnSaveAction:(id)sender {
    NSLog(@"save");
    [_txtIp resignFirstResponder];
    [_txtPort resignFirstResponder];
    [_txtDomain resignFirstResponder];
    if (_txtIp.text.length == 0 || _txtPort.text.length == 0) {
        [[self view] makeToast:NSLocalizedString(@"ask_server_data", nil) duration:ToastDurationShort position:@"center"];
        return;
    }
    NSUserDefaults *prefs = [NSUserDefaults standardUserDefaults];
    [prefs setObject:_txtIp.text forKey:kFlexKeyServerIP];
    [prefs setObject:_txtPort.text forKey:kFlexKeyServerPort];
    [prefs setObject:_txtDomain.text forKey:kFlexKeyServerDomain];
    
    if([_enableAudio isOn]){
        [prefs setBool:TRUE forKey:kFlexKeyEnableAudio];
        
    }else{
        [prefs setBool:NO forKey:kFlexKeyEnableAudio];
    }
    
    if([_enableRetina isOn]){
        [prefs setBool:TRUE forKey:kFlexKeyEnableRetina];
        
    }else{
        [prefs setBool:NO forKey:kFlexKeyEnableRetina];
    }
    
    if([_silenceBeeper isOn]){
        [prefs setBool:TRUE forKey:kFlexKeySilenceBeeper];
        
    }else{
        [prefs setBool:NO forKey:kFlexKeySilenceBeeper];
    }
    
    if([_genericSpice isOn]){
        [prefs setBool:TRUE forKey:kFlexKeyGenericSpice];
        
    }else{
        [prefs setBool:NO forKey:kFlexKeyGenericSpice];
    }
    
    [prefs synchronize];
    
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        sleep(ToastDurationShort);
        dispatch_async(dispatch_get_main_queue(), ^{
            [self dismissViewControllerAnimated:YES completion:nil];
        });
    });
                   
    [[self view] makeToast:NSLocalizedString(@"settings_save_success", nil) duration:ToastDurationShort position:@"center"];
                   
    // To Access this value
    NSLog(@"To Access this value %@",[prefs stringForKey:kFlexKeyServerIP]);
}

- (IBAction)backgroudTouched:(id)sender {
    [_txtIp resignFirstResponder];
    [_txtPort resignFirstResponder];

    [_txtDomain resignFirstResponder];
}
- (IBAction)btnBackAction:(id)sender {
    [_txtIp resignFirstResponder];
    [_txtPort resignFirstResponder];
    [_txtDomain resignFirstResponder];
//    if (_txtIp.text.length == 0 || _txtPort.text.length == 0) {
//        [[self view] makeToast:NSLocalizedString(@"ask_server_data", nil) duration:ToastDurationShort position:@"center"];
//        return;
//    }
    NSUserDefaults *prefs = [NSUserDefaults standardUserDefaults];
    [prefs setObject:_txtIp.text forKey:kFlexKeyServerIP];
    [prefs setObject:_txtPort.text forKey:kFlexKeyServerPort];
    [prefs setObject:_txtDomain.text forKey:kFlexKeyServerDomain];
    
    if([_enableAudio isOn]){
        [prefs setBool:TRUE forKey:kFlexKeyEnableAudio];
        
    }else{
        [prefs setBool:NO forKey:kFlexKeyEnableAudio];
    }
    
    if([_enableRetina isOn]){
        [prefs setBool:TRUE forKey:kFlexKeyEnableRetina];
        
    }else{
        [prefs setBool:NO forKey:kFlexKeyEnableRetina];
    }
    
    if([_silenceBeeper isOn]){
        [prefs setBool:TRUE forKey:kFlexKeySilenceBeeper];
        
    }else{
        [prefs setBool:NO forKey:kFlexKeySilenceBeeper];
    }
    
    if([_genericSpice isOn]){
        [prefs setBool:TRUE forKey:kFlexKeyGenericSpice];
        
    }else{
        [prefs setBool:NO forKey:kFlexKeyGenericSpice];
    }
    
    [prefs synchronize];
    
    [self dismissViewControllerAnimated:YES completion:nil];
}
#pragma mark -
#pragma mark UITextField methods delegate
- (BOOL)textFieldShouldReturn:(UITextField *)theTextField {
    [theTextField resignFirstResponder];
    return YES;
}
@end
