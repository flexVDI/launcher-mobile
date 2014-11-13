//
//  LoginController.m
//  spice_ios
//
//  Created by Wilfredo Carrillo on 7/24/14.
//  Copyright (c) 2014 Wilfredo Carrillo. All rights reserved.
//

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
    
}
- (void)viewWillAppear:(BOOL)animated
{
	[super viewWillAppear:animated];
    NSLog(@"Sale viewWillAppear");
}
-(void)viewDidAppear:(BOOL)animated
{
    NSString* serverIP = [[NSUserDefaults standardUserDefaults] stringForKey:kFlexKeyServerIP];
    if(!serverIP || serverIP.length==0){
        [self performSegueWithIdentifier:@"loginToConfig" sender:self];
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
        mainViewController.port =self.spicePort;
        mainViewController.pass = self.spicePassword;
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
    NSString* serverProto =nil;
    serverProto = @"https";
    self.useHttps = TRUE;
    self.enableWebSockets = [[NSUserDefaults standardUserDefaults] boolForKey:kFlexKeyEnableWebSockets];
    self.enableRetina = [[NSUserDefaults standardUserDefaults] boolForKey:kFlexKeyEnableRetina];

    if(valid){
        self.strUrlAuthMode = [NSString stringWithFormat:@"%@://%@:%@/vdi/authmode", serverProto,serverIP,serverPort];
        self.strUrlDesktop = [NSString stringWithFormat:@"%@://%@:%@/vdi/desktop", serverProto,serverIP,serverPort];
        NSLog(@"todos los parametros son validos, continuar strUrlAuthMode %@ strUrlDesktop %@",self.strUrlAuthMode,self.strUrlDesktop );
        _activityIndicator.hidden = FALSE;
        [_activityIndicator startAnimating];
        if(self.useHttps){
            NSLog(@"por https ");
            NSLog(@"por https host %@",[[NSURL URLWithString:self.strUrlAuthMode] host]);
        }
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
        [request setValue:[NSString stringWithFormat:@"%d", body.length] forHTTPHeaderField:@"Content-Length"];
        [request setValue:@"application/json" forHTTPHeaderField:@"Content-Type"];
        
        
        NSURLConnection *connectionAuthMode =[[NSURLConnection alloc] initWithRequest:request delegate:self];
        NSLog(@"antes de conn start connectionAuthMode");
        connectionState = C_NONE;
        [connectionAuthMode start];
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
    if (theTextField == _txtUser) {
        [_txtUser resignFirstResponder];
    }else if (theTextField == _txtPassword) {
        [_txtPassword resignFirstResponder];
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
    int statusCode = [httpResponse statusCode];
    
    //    int statusCode = [response statusCode];
    if (statusCode != 200){
        NSLog(@"no es 200  ");
        
        
        [[self view] makeToast:NSLocalizedString(@"main_server_error", nil) duration:ToastDurationNormal position:@"center"];
        
        NSLog(@"statusCode %d  ",statusCode);
        [connection cancel];
        _activityIndicator.hidden = TRUE;
        [_activityIndicator stopAnimating];
        
        
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
    if (connectionState == C_NONE) {
        NSLog(@"C_NONE");

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

            _activityIndicator.hidden = FALSE;
            [_activityIndicator startAnimating];
            
            NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:[NSURL URLWithString:self.strUrlDesktop]];
            [request setHTTPMethod:@"POST"];
            
            //{"hwaddress": "78:2b:cb:e8:3a:57"}
            NSString *body = [NSString stringWithFormat:@"{\"hwaddress\": \"%@\", \"username\": \"%@\", \"password\": \"%@\", \"desktop\": \"\"}", _deviceID.text,_txtUser.text,_txtPassword.text];
            body = [NSString stringWithFormat:@"{\"hwaddress\": \"%@\", \"username\": \"%@\", \"password\": \"%@\", \"desktop\": \"\"}", _deviceID.text,_txtUser.text,_txtPassword.text];
            
            NSLog(@"body %@",body);
            
            NSData *postData = [body dataUsingEncoding:NSUTF8StringEncoding allowLossyConversion:YES];
            [request setHTTPBody:postData];
            [request setValue:[NSString stringWithFormat:@"%d", body.length] forHTTPHeaderField:@"Content-Length"];
            [request setValue:@"application/json" forHTTPHeaderField:@"Content-Type"];
            
            NSURLConnection *connectionDesktop =[[NSURLConnection alloc] initWithRequest:request delegate:self];
            NSLog(@"antes de connectionDesktop start");
            connectionState = C_TERMINAL_POLICY;
            [connectionDesktop start];
        } else {
            NSLog(@"status NOK");
            [[self view] makeToast:message duration:ToastDurationNormal position:@"center"];
            _activityIndicator.hidden = TRUE;
            [_activityIndicator stopAnimating];
        }
    } else if (connectionState == C_TERMINAL_POLICY){
        NSLog(@"kFlexKeyConnectionDesktop");
        _activityIndicator.hidden = TRUE;
        [_activityIndicator stopAnimating];
        if (self.selectedDesktop==-1) {
            NSDictionary *responseDesktopDict = [NSJSONSerialization JSONObjectWithData:serverAnswer options:kNilOptions error:nil];
            serverAnswer = nil;
            
            NSLog(@"response todo  %@",responseDesktopDict);
            NSString *status=[responseDesktopDict objectForKey:@"status"];
            NSString *message=[responseDesktopDict objectForKey:@"message"];
            NSLog(@"status  %@",status);
            NSLog(@"message %@", message);
            
            if([status isEqualToString:@"OK"]){
                
                self.spiceAddress=[responseDesktopDict objectForKey:@"spice_address"];
                self.spicePassword=[responseDesktopDict objectForKey:@"spice_password"];
                self.spicePort=[responseDesktopDict objectForKey:@"spice_port"];
                
                [self performSegueWithIdentifier:@"GoToDisplay" sender:self];
            } else if([status isEqualToString:@"SelectDesktop"]){
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
                
                [self adjustHeightWithNumberRows:[self.desktops count]];
                _tblDesktop.hidden=FALSE;
                _viewBackTable.hidden=FALSE;
            }else{
                
                NSLog(@"status NOK");
                [[self view] makeToast:message duration:ToastDurationNormal position:@"center"];
                
            }
        } else {
            NSDictionary *responseDesktopDict = [NSJSONSerialization JSONObjectWithData:serverAnswer options:kNilOptions error:nil];
            serverAnswer = nil;
            
            NSLog(@"response todo  %@",responseDesktopDict);
            NSString *status=[responseDesktopDict objectForKey:@"status"];
            NSString *message=[responseDesktopDict objectForKey:@"message"];
            NSLog(@"status  %@",status);
            NSLog(@"message %@", message);
            
            if([status isEqualToString:@"OK"]){
                self.spiceAddress=[responseDesktopDict objectForKey:@"spice_address"];
                self.spicePassword=[responseDesktopDict objectForKey:@"spice_password"];
                self.spicePort=[responseDesktopDict objectForKey:@"spice_port"];
                
                if (self.enableRetina && UI_USER_INTERFACE_IDIOM() != UIUserInterfaceIdiomPad)
                {
                    global_state.content_scale = 2;
                } else {
                    global_state.content_scale = 1;
                }
                
                NSString *wsport;
                if (self.enableWebSockets) {
                    wsport = @"443";
                } else {
                    wsport = @"-1";
                }
                
                engine_spice_set_connection_data([self.spiceAddress UTF8String],
                                                 [self.spicePort UTF8String],
                                                 [wsport UTF8String],
                                                 [self.spicePassword UTF8String]);
                
                if (engine_spice_connect() != 0) {
                    [[self view] makeToast:NSLocalizedString(@"connection_failed", nil) duration:ToastDurationNormal position:@"center"];
                    return;
                }
                
//                engine_init_buffer(1920, 1080);
//                engine_draw();
//                
//                wait_time = 0;
//                while (global_state.guest_width == 0 ||
//                       global_state.guest_height == 0) {
//                    if (wait_time == 15) {
//                        engine_spice_disconnect();
//                        [[self view] makeToast:NSLocalizedString(@"main_server_status_nok", nil) duration:ToastDurationNormal position:@"center"];
//                        return;
//                    }
//                    sleep(1);
//                    engine_draw();
//                }
                
                [self performSegueWithIdentifier:@"loginToView" sender:self];
            } else {
                NSLog(@"status NOK");
                [[self view] makeToast:message duration:ToastDurationNormal position:@"center"];
                
            }
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
    NSLog(@"didSelectRowAtIndexPath %d",indexPath.row);
    
    self.selectedDesktop=indexPath.row;
    NSLog(@"didSelectRowAtIndexPath self.selectedDesktop %d",self.selectedDesktop);
    NSString *desktop=[self.desktopsKeys objectAtIndex:self.selectedDesktop];
    NSLog(@"didSelectRowAtIndexPath desktopsKeys %@",[self.desktopsKeys objectAtIndex:self.selectedDesktop]);
    NSLog(@"didSelectRowAtIndexPath desktops%@",[self.desktops objectAtIndex:self.selectedDesktop]);
    _activityIndicator.hidden=FALSE;
    [_activityIndicator startAnimating];
    
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:[NSURL URLWithString:self.strUrlDesktop]];
    [request setHTTPMethod:@"POST"];
    
    //{"hwaddress": "78:2b:cb:e8:3a:57"}
    NSString *body = [NSString stringWithFormat:@"{\"hwaddress\": \"%@\", \"username\": \"%@\", \"password\": \"%@\", \"desktop\": \"%@\"}", _deviceID.text,_txtUser.text,_txtPassword.text,desktop];
    
    
    NSLog(@"body %@",body);
    
    NSData *postData = [body dataUsingEncoding:NSUTF8StringEncoding allowLossyConversion:YES];
    [request setHTTPBody:postData];
    [request setValue:[NSString stringWithFormat:@"%d", body.length] forHTTPHeaderField:@"Content-Length"];
    [request setValue:@"application/json" forHTTPHeaderField:@"Content-Type"];
    
    NSURLConnection *connectionDesktop =[[NSURLConnection alloc] initWithRequest:request delegate:self];
    NSLog(@"antes de connectionDesktop start");
    //connectionState = C_DESKTOP_POLICY;
    _tblDesktop.hidden = TRUE;
    _activityIndicator.hidden = FALSE;
    [_activityIndicator startAnimating];
    
    [connectionDesktop start];
    
}
- (NSString *)tableView:(UITableView *)tableView titleForHeaderInSection:
(NSInteger)section {
    return @"Escritorios";
    
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
@end
