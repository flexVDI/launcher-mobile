//
//  ES1Renderer.m
//  CubeExample
//
//  Created by Brad Larson on 4/20/2010.
//

#import "ES1Renderer.h"
#import "globals.h"
#import "spice.h"
#import "draw.h"

//#define DRAWCOLORS 1
#define DRAWTEXTURE 1

@implementation ES1Renderer

// Create an OpenGL ES 1.1 context
- (id)init
{
    if ((self = [super init]))
    {
        if (defaultFramebuffer)
        {
            glDeleteFramebuffersOES(1, &defaultFramebuffer);
            defaultFramebuffer = 0;
        }
        
        if (colorRenderbuffer)
        {
            glDeleteRenderbuffersOES(1, &colorRenderbuffer);
            colorRenderbuffer = 0;
        }
        
        // Tear down context
        if ([EAGLContext currentContext] == context)
            [EAGLContext setCurrentContext:nil];
        
        context = [[EAGLContext alloc] initWithAPI:kEAGLRenderingAPIOpenGLES1];
        
        if (!context || ![EAGLContext setCurrentContext:context])
        {
            //[self release];
            return nil;
        }
		
		currentCalculatedMatrix = CATransform3DIdentity;
        
        // Create default framebuffer object. The backing will be allocated for the current layer in -resizeFromLayer
        glGenFramebuffersOES(1, &defaultFramebuffer);
        glGenRenderbuffersOES(1, &colorRenderbuffer);
        glBindFramebufferOES(GL_FRAMEBUFFER_OES, defaultFramebuffer);
        glBindRenderbufferOES(GL_RENDERBUFFER_OES, colorRenderbuffer);
        glFramebufferRenderbufferOES(GL_FRAMEBUFFER_OES, GL_COLOR_ATTACHMENT0_OES, GL_RENDERBUFFER_OES, colorRenderbuffer);
        
        glGetRenderbufferParameterivOES(GL_RENDERBUFFER_OES, GL_RENDERBUFFER_WIDTH_OES, &backingWidth);
        glGetRenderbufferParameterivOES(GL_RENDERBUFFER_OES, GL_RENDERBUFFER_HEIGHT_OES, &backingHeight);
        NSLog(@"RendererBuffer Width: %d, height: %d", backingWidth, backingHeight);
        
        global_state.width = backingWidth;
        global_state.height = backingHeight;
        resolutionChangeRequested = false;
        engineInitialized = false;
        //engine_init_buffer(global_state.width, global_state.height);
        //engine_spice_connect();

    }
    
    return self;
}

- (void)renderByRotatingAroundX:(float)xRotation rotatingAroundY:(float)yRotation;
{
    // This application only creates a single context which is already set current at this point.
    // This call is redundant, but needed if dealing with multiple contexts.
    [EAGLContext setCurrentContext:context];
    
    // This application only creates a single default framebuffer which is already bound at this point.
    // This call is redundant, but needed if dealing with multiple framebuffers.
    glBindFramebufferOES(GL_FRAMEBUFFER_OES, defaultFramebuffer);
    glViewport(0, 0, backingWidth, backingHeight);
    
    glMatrixMode(GL_PROJECTION);
    glLoadIdentity();
	//glOrthof(-2.0, 2.0, -2.0 * 480.0 / 320.0, 2.0 * 480.0 / 320.0, -3.0, 3.0);
    glOrthof(0, backingWidth, backingHeight, 0, 0, 1);
    //glMatrixMode(GL_MODELVIEW);
    
    if (global_state.width != 0 &&
        global_state.height != 0) {
        
        if (!engineInitialized) {
            engine_init_buffer(global_state.width, global_state.height);
            engine_init_screen();
            engineInitialized = true;
        }
        
        int result = engine_draw(global_state.width, global_state.height);
        int resolution_mismatch = 0;
        if (global_state.guest_width != 0 &&
            global_state.guest_height != 0) {
            if (global_state.guest_width != global_state.width ||
                global_state.guest_height != global_state.height) {
                resolution_mismatch = 1;
            }
        }
        
        if (result == -2 || resolution_mismatch) {
            if (!resolutionChangeRequested) {
                engine_spice_request_resolution(global_state.width, global_state.height);
                resolutionChangeRequested = true;
                resolutionChangeTimestamp = [NSDate date];
            }
        } else if (resolutionChangeRequested) {
            engine_spice_resolution_changed();
            resolutionChangeRequested = false;
        }
        
        if (resolutionChangeRequested && resolutionChangeTimestamp) {
            double timeSinceRequest = [resolutionChangeTimestamp timeIntervalSinceNow] * -1000.0;
            if (timeSinceRequest > 5000.0) {
                resolutionChangeRequested = false;
                resolutionChangeTimestamp = nil;
            }
        }
        
        if (result == -2) {
            engine_draw_disconnected(global_state.width, global_state.height);
        }

//        if (result == -2 || resolution_mismatch) {
//            if (!resolutionChangeRequested) {
//                engine_spice_request_resolution(backingWidth, backingHeight);
//                resolutionChangeRequested = true;
//                resolutionChangeTimestamp = [NSDate date];
//            }
//        } else if (resolutionChangeRequested) {
//            engine_spice_resolution_changed();
//            resolutionChangeRequested = false;
//        }
//        
//        if (resolutionChangeRequested) {
//            double timeSinceRequest = [resolutionChangeTimestamp timeIntervalSinceNow] * -1000.0;
//            if (timeSinceRequest > 5000.0) {
//                resolutionChangeRequested = false;
//            }
//        }
    }

    glBindRenderbufferOES(GL_RENDERBUFFER_OES, colorRenderbuffer);
    [context presentRenderbuffer:GL_RENDERBUFFER_OES];
}

- (BOOL)resizeFromLayer:(CAEAGLLayer *)layer
{
    if (defaultFramebuffer)
    {
        glDeleteFramebuffersOES(1, &defaultFramebuffer);
        defaultFramebuffer = 0;
    }
    
    if (colorRenderbuffer)
    {
        glDeleteRenderbuffersOES(1, &colorRenderbuffer);
        colorRenderbuffer = 0;
    }
    glGenFramebuffersOES(1, &defaultFramebuffer);
    glGenRenderbuffersOES(1, &colorRenderbuffer);
    glBindFramebufferOES(GL_FRAMEBUFFER_OES, defaultFramebuffer);
    glBindRenderbufferOES(GL_RENDERBUFFER_OES, colorRenderbuffer);
    // Allocate color buffer backing based on the current layer size
    //glBindRenderbufferOES(GL_RENDERBUFFER_OES, colorRenderbuffer);
    glFramebufferRenderbufferOES(GL_FRAMEBUFFER_OES, GL_COLOR_ATTACHMENT0_OES, GL_RENDERBUFFER_OES, colorRenderbuffer);
    
    [context renderbufferStorage:GL_RENDERBUFFER_OES fromDrawable:layer];
    glGetRenderbufferParameterivOES(GL_RENDERBUFFER_OES, GL_RENDERBUFFER_WIDTH_OES, &backingWidth);
    glGetRenderbufferParameterivOES(GL_RENDERBUFFER_OES, GL_RENDERBUFFER_HEIGHT_OES, &backingHeight);
	NSLog(@"Width: %d, height: %d", backingWidth, backingHeight);
    
    global_state.width = backingWidth;
    global_state.height = backingHeight;
    resolutionChangeRequested = false;
    
    if (glCheckFramebufferStatusOES(GL_FRAMEBUFFER_OES) != GL_FRAMEBUFFER_COMPLETE_OES)
    {
        NSLog(@"Failed to make complete framebuffer object %x", glCheckFramebufferStatusOES(GL_FRAMEBUFFER_OES));
        return NO;
    }
    
    return YES;
}

- (void)dealloc
{
    // Tear down GL
    if (defaultFramebuffer)
    {
        glDeleteFramebuffersOES(1, &defaultFramebuffer);
        defaultFramebuffer = 0;
    }
    
    if (colorRenderbuffer)
    {
        glDeleteRenderbuffersOES(1, &colorRenderbuffer);
        colorRenderbuffer = 0;
    }
    
    // Tear down context
    if ([EAGLContext currentContext] == context)
        [EAGLContext setCurrentContext:nil];
    
    //[context release];
    context = nil;
    
	//[pvrTexture release];
    //[super dealloc];
}

@end
