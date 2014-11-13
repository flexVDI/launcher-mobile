//
//  EAGLView.m
//  CubeExample
//
//  Created by Brad Larson on 4/20/2010.
//

#import "MainView.h"
#import "ES1Renderer.h"
#import "DesktopTableViewCell.h"
#import "Reachability.h"
#import "io_interface.h"
#import "draw.h"
#import "native.h"
#import "spice.h"
#import "globals.h"

MainView *mainView;

@implementation MainView

// You must implement this method
+ (Class)layerClass
{
    return [CAEAGLLayer class];
}

//The EAGL view is stored in the nib file. When it's unarchived it's sent -initWithCoder:
- (id)initWithCoder:(NSCoder*)coder
{
    if ((self = [super initWithCoder:coder]))
    {
        mainView = self;
		// Set scaling to account for Retina display
		//if ([self respondsToSelector:@selector(setContentScaleFactor:)])
		//{
		//	self.contentScaleFactor = [[UIScreen mainScreen] scale];
		//}
        
        //UIScreen* mainscr = [UIScreen mainScreen];
		//self.contentScaleFactor = [[UIScreen mainScreen] scale];
        
        // Get the layer
        CAEAGLLayer *eaglLayer = (CAEAGLLayer *)self.layer;
        eaglLayer.contentsScale = global_state.content_scale;
        
        eaglLayer.opaque = TRUE;
        eaglLayer.drawableProperties = [NSDictionary dictionaryWithObjectsAndKeys:
                                        [NSNumber numberWithBool:FALSE], kEAGLDrawablePropertyRetainedBacking, kEAGLColorFormatRGBA8, kEAGLDrawablePropertyColorFormat, nil];
        
        if (!renderer)
        {
            renderer = [[ES1Renderer alloc] init];
            
            if (!renderer)
            {
                //[self release];
                return nil;
            }
        }
        
        if (displayLink) {
            [displayLink removeFromRunLoop:[NSRunLoop currentRunLoop] forMode:NSDefaultRunLoopMode];
        }
        
        displayLink = [CADisplayLink displayLinkWithTarget:self selector:@selector(drawView:)];
        
        [displayLink setFrameInterval:1];
        [displayLink addToRunLoop:[NSRunLoop currentRunLoop] forMode:NSDefaultRunLoopMode];
        
    }
    
    return self;
}

- (void)dealloc
{
    [displayLink removeFromRunLoop:[NSRunLoop currentRunLoop] forMode:NSDefaultRunLoopMode];
    displayLink = nil;
    //[renderer release];
    
    //[super dealloc];
}

#pragma mark -
#pragma mark UIView layout methods

- (void)drawView:(id)sender
{
    /* We need to do this here, because SPICE takes a lot of time to
       notice it doesn't have a working connection. */
    Reachability *networkReachability = [Reachability reachabilityForInternetConnection];
    NetworkStatus networkStatus = [networkReachability currentReachabilityStatus];
    
    if (networkStatus == NotReachable && engine_spice_is_connected()) {
        engine_spice_disconnect();
    } else {
        [renderer renderByRotatingAroundX:0 rotatingAroundY:0];
    }
}

- (void)layoutSubviews
{
	NSLog(@"Scale factor: %f", self.contentScaleFactor);
    [renderer resizeFromLayer:(CAEAGLLayer*)self.layer];
    [self drawView:nil];
}


- (void)loadNativePng:(unsigned char **)imgbuf pngWidth:(int *)pngWidth pngHeight:(int *)pngHeight {
    CGImageRef spriteImage;
    CGContextRef spriteContext;
    unsigned char **buf;
    int width;
    int height;
    int bpp;
    
    NSString *imagePath = [[NSBundle mainBundle] pathForResource:@"keyboard" ofType:@"png"];
    NSLog(@"imagePath: %@\n", imagePath);
    spriteImage = [UIImage imageWithContentsOfFile:imagePath].CGImage;
    
    if (spriteImage) {
        width = CGImageGetWidth(spriteImage);
        height = CGImageGetHeight(spriteImage);
        bpp = CGImageGetBitsPerPixel(spriteImage);
        
        buf = (unsigned char**) calloc(1, sizeof(unsigned char*));
        buf[0] = (unsigned char*) calloc(width * height * 4, sizeof(unsigned char));
        //spriteContext = CGBitmapContextCreate(buf[0], width, height, 8, width * 4, CGImageGetColorSpace(spriteImage), kCGImageAlphaPremultipliedLast);
        spriteContext = CGBitmapContextCreate(buf[0], width, height, 8, width * 4, CGImageGetColorSpace(spriteImage), (CGBitmapInfo)kCGImageAlphaPremultipliedLast);
        CGContextTranslateCTM(spriteContext, 0.0, (CGFloat)height);
        CGContextScaleCTM(spriteContext, 1.0, -1.0);
        
        CGContextDrawImage(spriteContext, CGRectMake(0.0, 0.0, (CGFloat)width, (CGFloat)height),
                           spriteImage);
        CGContextRelease(spriteContext);
        
        *imgbuf = buf[0];
        *pngWidth = width;
        *pngHeight = height;
        
    } else {
        NSLog(@"Could not load keyboard PNG\n");
    }
}


void native_load_png(unsigned char **imgbuf, int *width, int *height)
{
    [mainView loadNativePng:imgbuf pngWidth:width pngHeight:height];
}

@end


