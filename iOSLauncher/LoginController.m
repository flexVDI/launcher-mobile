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

#import "LoginController.h"
#import "MainViewController.h"
#import "Toast+UIView.h"
#import "flexConstants.h"
#import "DesktopTableViewCell.h"
#import "spice.h"
#import "draw.h"
#import "globals.h"


@implementation LoginController
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
    _activityIndicator.hidden = TRUE;
    _btnConnect.layer.cornerRadius = 10.0f;
    _deviceID.layer.cornerRadius = 8.0f;
    _deviceID.text=[self uniqueIDForDevice];
    //_deviceID.text=@"";
    self.domain = [[NSUserDefaults standardUserDefaults] stringForKey:kFlexKeyServerDomain];
    self.desktops = [[NSMutableArray alloc] init];
    self.desktopsKeys = [[NSMutableArray alloc] init];
    _tblDesktop.delegate=self;
    _tblDesktop.dataSource=self;
    _tblDesktop.hidden=TRUE;
    _viewBackTable.hidden=TRUE;
    _txtPassword.delegate = self;

    
}
- (void)viewWillAppear:(BOOL)animated
{
	[super viewWillAppear:animated];
    [_txtUser becomeFirstResponder];
    
    NSString *ver = [[UIDevice currentDevice] systemVersion];
    float ver_float = [ver floatValue];
    if (ver_float > 9.0) {
        UITextInputAssistantItem* item = [_txtUser inputAssistantItem];
        item.leadingBarButtonGroups = @[];
        item.trailingBarButtonGroups = @[];
    
        item = [_txtPassword inputAssistantItem];
        item.leadingBarButtonGroups = @[];
        item.trailingBarButtonGroups = @[];
    }
    
    NSLog(@"Sale viewWillAppear");
}
-(void)viewDidAppear:(BOOL)animated
{
    NSFileManager *fileManager = [[NSFileManager alloc] init];
    NSArray *paths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES);
    NSString *documentsDirectory = [paths objectAtIndex:0];
    NSString *filename = [NSString stringWithFormat:@"%@/main_texture.txt",
                          documentsDirectory];
    
    if ([fileManager fileExistsAtPath:filename] == NO) {
        NSLog(@"Creating placeholder for texture saving");
        
        NSData *data = [@"empty" dataUsingEncoding:NSUTF8StringEncoding];
        if ([fileManager createFileAtPath:filename contents:data attributes:nil] == NO) {
            NSLog(@"Can't create placeholder");
        } else {
            NSError *error = nil;
            NSURL *fileURL = [NSURL fileURLWithPath:filename];
            if ([fileURL setResourceValue:[NSNumber numberWithBool:YES]
                                   forKey:NSURLIsExcludedFromBackupKey error:&error] == NO) {
                NSLog(@"Can't exclude placeholder from backup");
            } else {
                NSLog(@"Placeholder successfully created");
            }
        }
    }
    
    _txtUser.enabled = TRUE;
    _txtPassword.enabled = TRUE;
    _btnConnect.enabled = TRUE;
    
    const char *realpath = [fileManager fileSystemRepresentationWithPath:filename];
    engine_set_save_location(realpath);
    
    self.enableAudio = [[NSUserDefaults standardUserDefaults] boolForKey:kFlexKeyEnableAudio];
    self.enableRetina = [[NSUserDefaults standardUserDefaults] boolForKey:kFlexKeyEnableRetina];
    self.silenceBeeper = [[NSUserDefaults standardUserDefaults] boolForKey:kFlexKeySilenceBeeper];
    self.genericSpice = [[NSUserDefaults standardUserDefaults] boolForKey:kFlexKeyGenericSpice];
    
    if (self.enableRetina) {
        global_state.content_scale = 2;
    } else {
        global_state.content_scale = 1;
    }
    
    NSString* serverIP = [[NSUserDefaults standardUserDefaults] stringForKey:kFlexKeyServerIP];
    NSString* serverPort = [[NSUserDefaults standardUserDefaults] stringForKey:kFlexKeyServerPort];
    
    if (self.genericSpice) {
        if ([_txtUser.text length] == 0) {
            _txtUser.text = [NSString stringWithFormat:@"%@:%@", serverIP, serverPort];
        }
        _txtPassword.text = @"";
        _txtUser.placeholder = NSLocalizedString(@"spice_server_port", nil);
    } else {
        _txtUser.text = @"";
        _txtPassword.text = @"";
        _txtUser.placeholder = NSLocalizedString(@"flexvdi_login_user", nil);
    }
    
    if(!serverIP || serverIP.length==0){
        NSUserDefaults *prefs = [NSUserDefaults standardUserDefaults];
        int defaultOrientationMask = UIInterfaceOrientationMaskLandscapeRight | UIInterfaceOrientationMaskPortrait;
        
        [prefs setBool:NO forKey:kFlexKeyFixOrientation];
        [prefs setInteger:defaultOrientationMask forKey:kFlexKeyOrientationMask];
        
        [self performSegueWithIdentifier:@"loginToConfig" sender:self];
    } else {
        if (self.genericSpice) {
            NSString* spiceServer = [[NSUserDefaults standardUserDefaults] stringForKey:kFlexKeySpiceServer];
            if (!spiceServer || spiceServer.length == 0) {
                return;
            }
            
            NSString* spiceServerPort = [[NSUserDefaults standardUserDefaults] stringForKey:kFlexKeySpiceServerPort];
            if (!spiceServerPort || spiceServerPort.length == 0) {
                return;
            }
            
            NSString *spicePassword = [[NSUserDefaults standardUserDefaults] stringForKey:kFlexKeySpicePassword];
            if (!spicePassword || spicePassword.length == 0) {
                return;
            }
        } else {
            NSString* launcherUser = [[NSUserDefaults standardUserDefaults] stringForKey:kFlexKeyLauncherUser];
            if(!launcherUser || launcherUser.length==0){
                return;
            }
            NSString* launcherPassword = [[NSUserDefaults standardUserDefaults] stringForKey:kFlexKeyLauncherPassword];
            if(!launcherPassword || launcherPassword.length==0){
                return;
            }
            NSString* launcherDesktop = [[NSUserDefaults standardUserDefaults] stringForKey:kFlexKeyLauncherDesktop];
            if(!launcherDesktop || launcherDesktop.length==0){
                launcherDesktop = @"";
            }
            NSString* launcherDevID = [[NSUserDefaults standardUserDefaults] stringForKey:kFlexKeyLauncherDevID];
            if(!launcherDevID || launcherDevID.length==0){
                return;
            }
        }
        
        /* Autoreconnection */
        global_state.conn_state = AUTOCONNECT;
        
        [self performSegueWithIdentifier:@"loginToView" sender:self];
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
- (void)dealloc {
    
//    [_txtUser release];
//    [_txtPassword release];
//    [_viewLoading release];
//    [_activityIndicator release];
//    [_deviceID release];
//    [_btnConnect release];
//    [_viewBackTable release];
//    [_tblDesktop release];
//    [super dealloc];
}

#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender{
    
    if ([segue.identifier isEqualToString:@"loginToView"]) {
        MainViewController *mainViewController = [segue destinationViewController];
        /* display.ip = @"192.168.1.144";//self.ip.text;
         display.port =@"5900";// self.port.text;
         display.pass = @"";//self.pass.text;*/
        
        mainViewController.ip = self.spiceAddress;
        mainViewController.port = self.spicePort;
        mainViewController.enableAudio = self.enableAudio;
        mainViewController.silenceBeeper = self.silenceBeeper;
        mainViewController.pass = self.spicePassword;
        mainViewController.genericSpice = self.genericSpice;
        _viewBackTable.hidden=TRUE;
        _tblDesktop.hidden=TRUE;
    }
    
    
    
    // Pass the selected object to the new view controller.
}



#pragma mark -
#pragma mark other methods


-(NSString*)uniqueIDForDevice{
    NSString* uniqueIdentifier = nil;
    if( [UIDevice instancesRespondToSelector:@selector(identifierForVendor)] ) {
        // >=iOS 7
        uniqueIdentifier = [[[UIDevice currentDevice] identifierForVendor] UUIDString];
    }else{
        //<=iOS6, Use UDID of Device
        CFUUIDRef uuid = CFUUIDCreate(NULL);
        //uniqueIdentifier = ( NSString*)CFUUIDCreateString(NULL, uuid);//- for non- ARC
        uniqueIdentifier = ( NSString*)CFBridgingRelease(CFUUIDCreateString(NULL, uuid));// for ARC
        CFRelease(uuid);
    }
    
    return uniqueIdentifier;
}
- (IBAction)connect:(id)sender {
    [_txtUser resignFirstResponder];
    [_txtPassword resignFirstResponder];
    
    if (self.genericSpice) {
        NSArray<NSString *> *args = [_txtUser.text componentsSeparatedByString:@":"];
        if (args == nil || [args count] < 2) {
            [[self view] makeToast:NSLocalizedString(@"spice_wrong_server", nil) duration:ToastDurationNormal position:@"center"];
            return;
        }
        
        if ([_txtPassword.text length] == 0) {
            [[self view] makeToast:NSLocalizedString(@"login_pass_empty", nil) duration:ToastDurationNormal position:@"center"];
            return;
        }
     
        engine_spice_set_connection_data([args[0] UTF8String],
                                         [args[1] UTF8String],
                                         [@"-1" UTF8String],
                                         [_txtPassword.text UTF8String],
                                         self.enableAudio);
        

        _txtUser.enabled = FALSE;
        _txtPassword.enabled = FALSE;
        _btnConnect.enabled = FALSE;
        _activityIndicator.hidden = FALSE;
        [_activityIndicator startAnimating];

        dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
            int ret = engine_spice_connect();
        
            dispatch_async(dispatch_get_main_queue(), ^{
                _activityIndicator.hidden = TRUE;
                [_activityIndicator stopAnimating];
                _txtUser.enabled = TRUE;
                _txtPassword.enabled = TRUE;
                _btnConnect.enabled = TRUE;
            });

            if (ret != 0) {
                dispatch_async(dispatch_get_main_queue(), ^{
                    [[self view] makeToast:NSLocalizedString(@"connection_failed", nil) duration:ToastDurationNormal position:@"center"];
                });
                return;
            }
            
            NSUserDefaults *prefs = [NSUserDefaults standardUserDefaults];
            [prefs setObject:args[0] forKey:kFlexKeySpiceServer];
            [prefs setObject:args[1] forKey:kFlexKeySpiceServerPort];
            [prefs setObject:_txtPassword.text forKey:kFlexKeySpicePassword];
        
            global_state.conn_state = CONNECTED;
        
            dispatch_async(dispatch_get_main_queue(), ^{
                [self performSegueWithIdentifier:@"loginToView" sender:self];
            });
        });
    } else {
        self.selectedDesktop=-1;
        BOOL valid=true;
        if([_txtUser.text length]==0){
            [[self view] makeToast:NSLocalizedString(@"login_username_empty", nil) duration:ToastDurationNormal position:@"center"];
            valid=FALSE;
        }else if(valid&&[_txtPassword.text length]==0){
            [[self view] makeToast:NSLocalizedString(@"login_pass_empty", nil) duration:ToastDurationNormal position:@"center"];
            valid=FALSE;
        }
        NSString* serverIP = [[NSUserDefaults standardUserDefaults] stringForKey:kFlexKeyServerIP];
        if(!serverIP || serverIP.length==0){
            [[self view] makeToast:NSLocalizedString(@"login_serverip_empty", nil) duration:ToastDurationNormal position:@"center"];
            valid=FALSE;
        }
        NSString* serverPort = [[NSUserDefaults standardUserDefaults] stringForKey:kFlexKeyServerPort];
        if(!serverPort || serverPort.length==0){
            [[self view] makeToast:NSLocalizedString(@"login_serverport_empty", nil) duration:ToastDurationNormal position:@"center"];
            valid=FALSE;
        }
        
        self.launcherDesktop = @"";
        
        NSString* serverProto =nil;
        serverProto = @"https";
        self.useHttps = TRUE;
        
        if(valid){
            self.strUrlAuthMode = [NSString stringWithFormat:@"%@://%@:%@/vdi/authmode", serverProto,serverIP,serverPort];
            self.strUrlDesktop = [NSString stringWithFormat:@"%@://%@:%@/vdi/desktop", serverProto,serverIP,serverPort];
            
            _txtUser.enabled = FALSE;
            _txtPassword.enabled = FALSE;
            _btnConnect.enabled = FALSE;
            _activityIndicator.hidden = FALSE;
            [_activityIndicator startAnimating];

            connectionState = C_NONE;
            [self launcherConnection:nil];
        }
    }
}

- (IBAction)btnInfoAction:(id)sender {
    [self performSegueWithIdentifier:@"loginToInfo" sender:self];
}

- (IBAction)touchConfigAction:(id)sender {
    [self performSegueWithIdentifier:@"loginToConfig" sender:self];
}

- (IBAction)backgroundTouched:(id)sender {
    [_txtUser resignFirstResponder];
    
    [_txtPassword resignFirstResponder];
}

- (IBAction)viewBackTableTouch:(id)sender {
    _tblDesktop.hidden=TRUE;
    _viewBackTable.hidden=TRUE;
}
#pragma mark -
#pragma mark UITextField methods delegate
- (BOOL)textFieldShouldReturn:(UITextField *)theTextField {
    if (theTextField == _txtPassword) {
        dispatch_async(dispatch_get_main_queue(), ^{
            [self connect:nil];
        });
    }
    return YES;
}
#pragma mark -
#pragma mark Fetch loads from url
- (NSCachedURLResponse *)connection:(NSURLConnection *)connection willCacheResponse:(NSCachedURLResponse *)cachedResponse {
    NSLog(@"willCacheResponse");
    return nil;
}
- (void)connection:(NSURLConnection *)connection didReceiveResponse:(NSURLResponse *)response {
    
    NSLog(@"didReceiveResponse");
    
    
    NSHTTPURLResponse* httpResponse = (NSHTTPURLResponse*)response;
    unsigned long statusCode = [httpResponse statusCode];
    
    //    int statusCode = [response statusCode];
    if (statusCode != 200){
        NSLog(@"no es 200  ");
        
        
        [[self view] makeToast:NSLocalizedString(@"main_server_error", nil) duration:ToastDurationNormal position:@"center"];
        
        NSLog(@"statusCode %ld  ",statusCode);
        [connection cancel];
        _activityIndicator.hidden = TRUE;
        [_activityIndicator stopAnimating];
        _txtUser.enabled = TRUE;
        _txtPassword.enabled = TRUE;
        _btnConnect.enabled = TRUE;
        
        
    }
}

- (void)connection:(NSURLConnection *)connection didReceiveData:(NSData *)data {
    NSLog(@"didReceiveData");
    
    if (serverAnswer == nil) {
        serverAnswer = [[NSMutableData alloc] init];
    }
    
    [serverAnswer appendData:data];

}

- (void)connection:(NSURLConnection *)connection didFailWithError:(NSError *)error {
	
    NSLog(@"didFailWithError %@",error);
    [[self view] makeToast:NSLocalizedString(@"main_server_error", nil) duration:ToastDurationNormal position:@"center"];
    
    serverAnswer = nil;
    connection = nil;
    
    _activityIndicator.hidden = TRUE;
    [_activityIndicator stopAnimating];
    _txtUser.enabled = TRUE;
    _txtPassword.enabled = TRUE;
    _btnConnect.enabled = TRUE;
}

- (void)connection:(NSURLConnection *)connection willSendRequestForAuthenticationChallenge:(NSURLAuthenticationChallenge *)challenge
{
    NSURLProtectionSpace *protectionSpace = [challenge protectionSpace];
    NSURLCredential *credential = [NSURLCredential credentialForTrust:[protectionSpace serverTrust]];
    [[challenge sender] useCredential:credential forAuthenticationChallenge:challenge];
}

#pragma mark -
#pragma mark Process load data

- (void)connectionDidFinishLoading:(NSURLConnection *)connection {
    [self launcherConnection:connection];
}

- (void)launcherConnection:(NSURLConnection *) connection {
    if (connectionState == C_NONE) {
        NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:[NSURL URLWithString:self.strUrlAuthMode]];
        [request setHTTPMethod:@"POST"];
        //self.macAddress=[self getMacAddress];
        //self.macAddress=@"78:2b:cb:e8:3a:57";
        
        //{"hwaddress": "78:2b:cb:e8:3a:57"}
        //_deviceID.text=@"4e:6f:0a:00:00:01";
        NSString *body = [NSString stringWithFormat:@"{\"hwaddress\": \"%@\"}", _deviceID.text];
        
        NSLog(@"body %@",body);
        
        NSData *postData = [body dataUsingEncoding:NSUTF8StringEncoding allowLossyConversion:YES];
        [request setHTTPBody:postData];
        [request setValue:[NSString stringWithFormat:@"%lu", (unsigned long)body.length] forHTTPHeaderField:@"Content-Length"];
        [request setValue:@"application/json" forHTTPHeaderField:@"Content-Type"];
        
        
        NSURLConnection *connectionAuthMode =[[NSURLConnection alloc] initWithRequest:request delegate:self];
        NSLog(@"antes de conn start connectionAuthMode");
        connectionState = C_TERMINAL_POLICY;
        [connectionAuthMode start];
    } else if (connectionState == C_TERMINAL_POLICY) {
        NSLog(@"C_TERMINAL_POLICY");
        
        NSDictionary *responseDict = [NSJSONSerialization JSONObjectWithData:serverAnswer options:kNilOptions error:nil];
        serverAnswer = nil;
        NSLog(@"response status  %@",[responseDict objectForKey:@"status"]);
        NSLog(@"response auth_mode  %@",[responseDict objectForKey:@"auth_mode"]);
        NSLog(@"response todo  %@",responseDict);
        NSLog(@"despues response");
        NSString *status=[responseDict objectForKey:@"status"];
        NSString *message=[responseDict objectForKey:@"message"];
        NSLog(@"status  %@",status);
        NSLog(@"message %@", message);
        if([status isEqualToString:@"OK"]){
            NSLog(@"Es OK pedir ip strUrlDesktop %@",self.strUrlDesktop );
            
            _txtUser.enabled = FALSE;
            _txtPassword.enabled = FALSE;
            _btnConnect.enabled = FALSE;
            _activityIndicator.hidden = FALSE;
            [_activityIndicator startAnimating];
            
            NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:[NSURL URLWithString:self.strUrlDesktop]];
            [request setHTTPMethod:@"POST"];
            
            //{"hwaddress": "78:2b:cb:e8:3a:57"}
            NSString *body = [NSString stringWithFormat:@"{\"hwaddress\": \"%@\", \"username\": \"%@\", \"password\": \"%@\", \"desktop\": \"%@\"}", _deviceID.text,_txtUser.text,_txtPassword.text, self.launcherDesktop];
            
            NSLog(@"body %@",body);
            
            NSData *postData = [body dataUsingEncoding:NSUTF8StringEncoding allowLossyConversion:YES];
            [request setHTTPBody:postData];
            [request setValue:[NSString stringWithFormat:@"%lu", (unsigned long)body.length] forHTTPHeaderField:@"Content-Length"];
            [request setValue:@"application/json" forHTTPHeaderField:@"Content-Type"];
            
            NSURLConnection *connectionDesktop =[[NSURLConnection alloc] initWithRequest:request delegate:self];
            NSLog(@"antes de connectionDesktop start");
            connectionState = C_DESKTOP_POLICY;
            [connectionDesktop start];
        } else {
            NSLog(@"status NOK");
            [[self view] makeToast:message duration:ToastDurationNormal position:@"center"];
            _activityIndicator.hidden = TRUE;
            [_activityIndicator stopAnimating];
            _txtUser.enabled = TRUE;
            _txtPassword.enabled = TRUE;
            _btnConnect.enabled = TRUE;
        }
    } else if (connectionState == C_DESKTOP_POLICY){
        NSLog(@"C_DESKTOP_POLICY");
        
        _activityIndicator.hidden = TRUE;
        [_activityIndicator stopAnimating];
        
        NSDictionary *responseDesktopDict = [NSJSONSerialization JSONObjectWithData:serverAnswer options:kNilOptions error:nil];
        serverAnswer = nil;
        
        NSLog(@"response todo  %@",responseDesktopDict);
        NSString *status=[responseDesktopDict objectForKey:@"status"];
        NSString *message=[responseDesktopDict objectForKey:@"message"];
        NSLog(@"status  %@",status);
        NSLog(@"message %@", message);
        
        if([status isEqualToString:@"Pending"]){
            NSLog(@"Pending");
            
            dispatch_async(dispatch_get_main_queue(), ^{
                            [[self view] makeToast:NSLocalizedString(@"wait_for_desktop", nil) duration:ToastDurationNormal position:@"center"];
            });
            
            sleep(3);
            connectionState = C_NONE;
            [self launcherConnection:connection];
        } else if ([status isEqualToString:@"SelectDesktop"]){
            NSString *jsonString = [responseDesktopDict objectForKey:@"message"];;
            NSData *data = [jsonString dataUsingEncoding:NSUTF8StringEncoding];
            
            NSDictionary *desktopDic =[NSJSONSerialization JSONObjectWithData:data options:0 error:nil];
            [self.desktops removeAllObjects];
            [self.desktopsKeys removeAllObjects];
            NSLog(@"desktopDic  %@",desktopDic);
            for (NSString *key in desktopDic){
                NSLog(@"%@ : %@", key, [desktopDic valueForKey:key]);
                [self.desktops addObject:[desktopDic valueForKey:key]];
                [self.desktopsKeys addObject:key];
            }
            
            [self adjustHeightWithNumberRows:(int)[self.desktops count]];
            _tblDesktop.hidden=FALSE;
            _viewBackTable.hidden=FALSE;
        } else if ([status isEqualToString:@"OK"]) {
            /* Save user credentials for reconnection */
            NSUserDefaults *prefs = [NSUserDefaults standardUserDefaults];
            [prefs setObject:_txtUser.text forKey:kFlexKeyLauncherUser];
            [prefs setObject:_txtPassword.text forKey:kFlexKeyLauncherPassword];
            [prefs setObject:_deviceID.text forKey:kFlexKeyLauncherDevID];
            
            self.spiceAddress=[responseDesktopDict objectForKey:@"spice_address"];
            self.spicePassword=[responseDesktopDict objectForKey:@"spice_password"];
            self.spicePort=[responseDesktopDict objectForKey:@"spice_port"];
            NSNumber *useWS=(NSNumber *)[responseDesktopDict objectForKey: @"use_ws"];
            if ([useWS boolValue] == YES) {
                self.use_ws = YES;
            } else {
                self.use_ws = NO;
            }
            
            if (self.enableRetina /*&& UI_USER_INTERFACE_IDIOM() != UIUserInterfaceIdiomPad*/)
            {
                global_state.content_scale = 2;
            } else {
                global_state.content_scale = 1;
            }
            
            NSString *wsport;
            if (self.use_ws) {
                wsport = @"443";
            } else {
                wsport = @"-1";
            }
            
            engine_spice_set_connection_data([self.spiceAddress UTF8String],
                                             [self.spicePort UTF8String],
                                             [wsport UTF8String],
                                             [self.spicePassword UTF8String],
                                             self.enableAudio);
            
            if (engine_spice_connect() != 0) {
                [[self view] makeToast:NSLocalizedString(@"connection_failed", nil) duration:ToastDurationNormal position:@"center"];
                return;
            }
            
            [self performSegueWithIdentifier:@"loginToView" sender:self];
        } else {
            NSLog(@"status NOK");
            [[self view] makeToast:message duration:ToastDurationNormal position:@"center"];
            
        }
    }
    
    connection =nil;
    NSLog(@"despues de todo");
}

#pragma mark UITableView methods delegate and source
- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return [self.desktops count];
}
- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath{
    static NSString *simpleTableIdentifier = @"CellDesktop";
    
    DesktopTableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:simpleTableIdentifier];
    
    cell.lblKey.text=[self.desktopsKeys objectAtIndex:indexPath.row];
    cell.lblName.text=[self.desktops objectAtIndex:indexPath.row];
    
    return cell;
}
- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath{
    NSLog(@"didSelectRowAtIndexPath %ld",(long)indexPath.row);
    
    self.selectedDesktop=(int)indexPath.row;
    NSString *desktop=[self.desktopsKeys objectAtIndex:self.selectedDesktop];
    
    self.launcherDesktop = desktop;
    NSUserDefaults *prefs = [NSUserDefaults standardUserDefaults];
    [prefs setObject:desktop forKey:kFlexKeyLauncherDesktop];

    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:[NSURL URLWithString:self.strUrlDesktop]];
    [request setHTTPMethod:@"POST"];

    NSString *body = [NSString stringWithFormat:@"{\"hwaddress\": \"%@\", \"username\": \"%@\", \"password\": \"%@\", \"desktop\": \"%@\"}", _deviceID.text,_txtUser.text,_txtPassword.text,desktop];
    
    NSData *postData = [body dataUsingEncoding:NSUTF8StringEncoding allowLossyConversion:YES];
    [request setHTTPBody:postData];
    [request setValue:[NSString stringWithFormat:@"%lu", (unsigned long)body.length] forHTTPHeaderField:@"Content-Length"];
    [request setValue:@"application/json" forHTTPHeaderField:@"Content-Type"];
    
    NSURLConnection *connectionDesktop =[[NSURLConnection alloc] initWithRequest:request delegate:self];

    _txtUser.enabled = FALSE;
    _txtPassword.enabled = FALSE;
    _btnConnect.enabled = FALSE;
    _tblDesktop.hidden = TRUE;
    _activityIndicator.hidden = FALSE;
    [_activityIndicator startAnimating];
    
    [connectionDesktop start];
    
}
- (NSString *)tableView:(UITableView *)tableView titleForHeaderInSection:
(NSInteger)section {
    return NSLocalizedString(@"desktops", nil);
    
}
- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section{
    return 40;
}
-(void) adjustHeightWithNumberRows:(int)numberOfRows{
    if(numberOfRows>=9){
        NSLog(@"reloadDataForTable muchas filas %d",numberOfRows);
        numberOfRows=8;
        
    }
    CGRect tblResultFrame = _tblDesktop.frame;
    tblResultFrame.size.height = 44+(numberOfRows)*44;
    
    _tblDesktop.frame = tblResultFrame;
    [_tblDesktop reloadData];
}
-(BOOL)shouldAutorotate{
    if (UI_USER_INTERFACE_IDIOM() == UIUserInterfaceIdiomPad) {
        return YES;
    } else {
        return NO;
    }
}
-(UIInterfaceOrientationMask)supportedInterfaceOrientations{
    if (UI_USER_INTERFACE_IDIOM() == UIUserInterfaceIdiomPad) {
        return UIInterfaceOrientationMaskPortrait | UIInterfaceOrientationMaskLandscapeRight;
    } else {
        return UIInterfaceOrientationMaskPortrait;
    }
}
@end
