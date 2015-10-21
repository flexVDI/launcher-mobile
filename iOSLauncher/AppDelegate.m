//
//  AppDelegate.m
//  iOSLauncher
//
//  Created by Nologin Consulting on 31/10/14.
//  Copyright (c) 2014 Flexible Software Solutions S.L. All rights reserved.
//

#import "AppDelegate.h"
#import "globals.h"
#import "spice.h"
#import "draw.h"
#import "gst_ios_init.h"
@import HockeySDK;

@implementation AppDelegate

- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions
{
    [[BITHockeyManager sharedHockeyManager] configureWithIdentifier:@"4695c9d7468d7f82744833605fb33a9c"];
    // Do some additional configuration if needed here
    [[BITHockeyManager sharedHockeyManager] startManager];
    [[BITHockeyManager sharedHockeyManager].authenticator
     authenticateInstallation];
    
    gst_ios_init();

    return YES;
}
							
- (void)applicationWillResignActive:(UIApplication *)application
{
    NSLog(@"applicationWillResignActive");
}

- (void)applicationDidEnterBackground:(UIApplication *)application
{
    NSLog(@"applicationDidEnterBackground");
    if (engine_spice_is_connected()) {
        engine_save_main_texture();
        engine_spice_disconnect();
    }
}

- (void)applicationWillEnterForeground:(UIApplication *)application
{
    NSLog(@"applicationWillEnterForeground");
}

- (void)applicationDidBecomeActive:(UIApplication *)application
{
    // Restart any tasks that were paused (or not yet started) while the application was inactive. If the application was previously in the background, optionally refresh the user interface.
}

- (void)applicationWillTerminate:(UIApplication *)application
{
    // Called when the application is about to terminate. Save data if appropriate. See also applicationDidEnterBackground:.
}

@end
