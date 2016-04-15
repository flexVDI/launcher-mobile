//
//  EAGLView.h
//  CubeExample
//
//  Created by Brad Larson on 4/20/2010.
//

#import <UIKit/UIKit.h>
#import <QuartzCore/QuartzCore.h>
#import <AudioToolbox/AudioServices.h>

#import "ESRenderer.h"
#import "KeyboardView.h"

@protocol MenuDelegate;

@interface MainView : UIView
{
@private
    //id <menuDelegate> menuDelegate;
    id <ESRenderer> renderer;
    CADisplayLink *displayLink;
}

- (void)stopRenderer;

@property (nonatomic, weak) id<MenuDelegate> delegate;

- (void)drawView:(id)sender;

@end

@protocol MenuDelegate <NSObject>
-(void)showMenu;
-(void)hideMenu;
@end
