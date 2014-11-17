//
//  KeyboardView.m
//  iOSLauncher
//
//  Created by Sergio Lopez on 11/5/14.
//  Copyright (c) 2014 Flexible Software Solutions S.L. All rights reserved.
//

#import "KeyboardView.h"
#import "spice.h"
#import "ctype.h"


@implementation KeyboardView

- (id)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        [self setAutocorrectionType:UITextAutocorrectionTypeNo];
        [self setKeyboardType:UIKeyboardTypeASCIICapable];
    }
    return self;
}

-(void)insertText:(NSString *)text {
    NSLog(@"insertText: %@", text);
    const char *ctext=[text UTF8String];
    NSLog(@"char=%d\n", ctext[0]);
    unsigned char tc = ctext[0];
    int keycode = 0;
    int special = 0;
    int is_upper = false;
    
    if (isalpha(tc)) {
        if (isupper(tc)) {
            tc = tolower(tc);
            is_upper = true;
        }
    }
    
    switch (tc) {
        case 'a':
            keycode = 0x1E;
            break;
        case 'b':
            keycode = 0x30;
            break;
        case 'c':
            keycode = 0x2E;
            break;
        case 'd':
            keycode = 0x20;
            break;
        case 'e':
            keycode = 0x12;
            break;
        case 'f':
            keycode = 0x21;
            break;
        case 'g':
            keycode = 0x22;
            break;
        case 'h':
            keycode = 0x23;
            break;
        case 'i':
            keycode = 0x17;
            break;
        case 'j':
            keycode = 0x24;
            break;
        case 'k':
            keycode = 0x25;
            break;
        case 'l':
            keycode = 0x26;
            break;
        case 'm':
            keycode = 0x32;
            break;
        case 'n':
            keycode = 0x31;
            break;
        case 195:
            keycode = 0x27;
            break;
        case 'o':
            keycode = 0x18;
            break;
        case 'p':
            keycode = 0x19;
            break;
        case 'q':
            keycode = 0x10;
            break;
        case 'r':
            keycode = 0x13;
            break;
        case 's':
            keycode = 0x1F;
            break;
        case 't':
            keycode = 0x14;
            break;
        case 'u':
            keycode = 0x16;
            break;
        case 'v':
            keycode = 0x2F;
            break;
        case 'w':
            keycode = 0x11;
            break;
        case 'x':
            keycode = 0x2D;
            break;
        case 'y':
            keycode = 0x15;
            break;
        case 'z':
            keycode = 0x2C;
            break;
        case '1':
            keycode = 0x02;
            break;
        case '2':
            keycode = 0x03;
            break;
        case '3':
            keycode = 0x04;
            break;
        case '4':
            keycode = 0x05;
            break;
        case '5':
            keycode = 0x06;
            break;
        case '6':
            keycode = 0x07;
            break;
        case '7':
            keycode = 0x08;
            break;
        case '8':
            keycode = 0x09;
            break;
        case '9':
            keycode = 0x0A;
            break;
        case '0':
            keycode = 0x0B;
            break;
        case ' ':
            keycode = 0x39;
            break;
        case '!':
            keycode = 0x02;
            special = 0x36;
            break;
        case '@':
            keycode = 0x03;
            special = 0x138;
            break;
        case '"':
            keycode = 0x03;
            special = 0x36;
            break;
        case '#':
            keycode = 0x04;
            special = 0x138;
            break;
        case '~':
            keycode = 0x27;
            special = 0x138;
            break;
        case '$':
            keycode = 0x05;
            special = 0x36;
            break;
        case '%':
            keycode = 0x06;
            special = 0x36;
            break;
        case '&':
            keycode = 0x07;
            special = 0x36;
            break;
        case '/':
            keycode = 0x08;
            special = 0x36;
            break;
        case '(':
            keycode = 0x09;
            special = 0x36;
            break;
        case ')':
            keycode = 0x0a;
            special = 0x36;
            break;
        case '=':
            keycode = 0x0b;
            special = 0x36;
            break;
        case '?':
            keycode = 0x0c;
            special = 0x36;
            break;
        case '-':
            keycode = 0x35;
            break;
        case '_':
            keycode = 0x35;
            special = 0x36;
            break;
        case ';':
            keycode = 0x33;
            special = 0x36;
            break;
        case ',':
            keycode = 0x33;
            break;
        case '.':
            keycode = 0x34;
            break;
        case ':':
            keycode = 0x34;
            special = 0x36;
            break;
        case '{':
            keycode = 0x28;
            special = 0x138;
            break;
        case '}':
            keycode = 0x2B;
            special = 0x138;
            break;
        case '[':
            keycode = 0x1A;
            special = 0x138;
            break;
        case ']':
            keycode = 0x1B;
            special = 0x138;
            break;
        case '*':
            keycode = 0x1B;
            special = 0x36;
            break;
        case '+':
            keycode = 0x1B;
            break;
        case '\\':
            keycode = 0x29;
            special = 0x138;
            break;
        case '|':
            keycode = 0x02;
            special = 0x138;
            break;
//        case '^':
//            keycode = 35;
//            special = 0x36;
//            break;
        case '<':
            keycode = 0x5E;
            break;
        case '>':
            keycode = 94;
            special = 0x36;
            break;
        case '\n':
            keycode = 0x1C;
            break;
    }
    
    if (keycode) {
        if (is_upper) {
            special = 0x2A;
        }
        
        if (special) {
            engine_spice_keyboard_event(special, 1);
        }
        
        engine_spice_keyboard_event(keycode, 1);
        engine_spice_keyboard_event(keycode, 0);
        
        if (special) {
            engine_spice_keyboard_event(special, 0);
        }
    }
}

-(void)deleteBackward {
    NSLog(@"DeleteKey");
    engine_spice_keyboard_event(0x0E, 1);
    engine_spice_keyboard_event(0x0E, 0);
}

-(BOOL)hasText {
    NSLog(@"hasText");
    return YES;
}

-(BOOL)canBecomeFirstResponder {
    return YES;
}

@end
