//
//  ES1Renderer.h
//  CubeExample
//
//  Created by Brad Larson on 4/20/2010.
//

#import "ESRenderer.h"

#import <OpenGLES/ES1/gl.h>
#import <OpenGLES/ES1/glext.h>
#import <QuartzCore/QuartzCore.h>

@class PVRTexture;

@interface ES1Renderer : NSObject <ESRenderer>
{
@private
    EAGLContext *context;
    
	PVRTexture *pvrTexture;
    
    // The pixel dimensions of the CAEAGLLayer
    GLint backingWidth;
    GLint backingHeight;
    Boolean resolutionChangeRequested;
    NSDate *resolutionChangeTimestamp;
    Boolean resolutionTooBig;
    Boolean engineInitialized;
    
    // The OpenGL ES names for the framebuffer and renderbuffer used to render to this view
    GLuint defaultFramebuffer, colorRenderbuffer;
	
	CATransform3D currentCalculatedMatrix;
}

- (void)renderByRotatingAroundX:(float)xRotation rotatingAroundY:(float)yRotation;
- (BOOL)resizeFromLayer:(CAEAGLLayer *)layer;

@end
