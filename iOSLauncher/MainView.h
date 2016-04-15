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
#import <QuartzCore/QuartzCore.h>
#import <AudioToolbox/AudioServices.h>

#import "ESRenderer.h"
#import "KeyboardView.h"

@protocol MenuDelegate;

@interface MainView : UIView
{
@private
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
