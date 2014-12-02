//
//  ConfigViewController.m
//  spice_ios
//
//  Created by Nologin on 14/08/14.
//  Copyright (c) 2014 Wilfredo Carrillo. All rights reserved.
//

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
    _lblVersion.text = @"v1.7";
    _lblVersion.layer.cornerRadius = 8.0f;
    
    NSString* serverIP = [[NSUserDefaults standardUserDefaults] stringForKey:kFlexKeyServerIP];
    NSLog(@"serverIP %@",serverIP);
    if(serverIP){
        _txtIp.text=serverIP;
    }
    NSString* serverPort = [[NSUserDefaults standardUserDefaults] stringForKey:kFlexKeyServerPort];
    NSLog(@"serverPort %@",serverPort);
    if(serverPort){
        _txtPort.text=serverPort;
    } else {
        _txtPort.text=@"443";
    }
    NSString *serverDomain = [[NSUserDefaults standardUserDefaults] stringForKey:kFlexKeyServerDomain];
    NSLog(@"serverDomain %@",serverDomain);
    if(serverDomain){
        _txtDomain.text=serverDomain;
    }
    
    if([[NSUserDefaults standardUserDefaults] boolForKey:kFlexKeyEnableWebSockets]==TRUE){;
        _enableWebSockets.on=TRUE;
    }else{
        _enableWebSockets.on=FALSE;
    }
    
    if([[NSUserDefaults standardUserDefaults] boolForKey:kFlexKeyEnableRetina]==TRUE){;
        _enableRetina.on=TRUE;
    }else{
        _enableRetina.on=FALSE;
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
    
    if([_enableWebSockets isOn]){
        [prefs setBool:TRUE forKey:kFlexKeyEnableWebSockets];
        
    }else{
        [prefs setBool:NO forKey:kFlexKeyEnableWebSockets];
    }
    
    if([_enableRetina isOn]){
        [prefs setBool:TRUE forKey:kFlexKeyEnableRetina];
        
    }else{
        [prefs setBool:NO forKey:kFlexKeyEnableRetina];
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
    [self dismissViewControllerAnimated:YES completion:nil];
}
#pragma mark -
#pragma mark UITextField methods delegate
- (BOOL)textFieldShouldReturn:(UITextField *)theTextField {
    [theTextField resignFirstResponder];
    return YES;
}
@end
