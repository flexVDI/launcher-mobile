//
//  KeyboardView.m
//  iOSLauncher
//
//  Created by Sergio Lopez on 11/5/14.
//  Copyright (c) 2014 Flexible Software Solutions S.L. All rights reserved.
//

#import "KeyboardView.h"
#import "globals.h"
#import "spice.h"
#import "draw.h"
#import "ctype.h"


@implementation KeyboardView

- (id)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        [self setAutocapitalizationType:UITextAutocapitalizationTypeNone];
        [self setAutocorrectionType:UITextAutocorrectionTypeNo];
        [self setKeyboardType:UIKeyboardTypeASCIICapable];
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(keyboardWillShow:) name:UIKeyboardWillShowNotification object:nil];
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(keyboardWillHide:) name:UIKeyboardWillHideNotification object:nil];
    }
    return self;
}

-(void)insertText:(NSString *)text {
    NSLog(@"insertText: %@, length=%d", text, text.length);
    const char *ctext=[text UTF8String];
    NSLog(@"ctext length=%lu\n", strlen(ctext));
    unsigned char tc = ctext[0];
    unsigned char extchar;
    unsigned char extchar2;
    NSLog(@"char=%d\n", tc);
    int keycode = 0;
    int special = 0;
    int prekey = 0;
    int prekey_special = 0;
    int is_upper = false;
    
    if (isalpha(tc)) {
        if (isupper(tc)) {
            tc = tolower(tc);
            is_upper = true;
        }
    }
    
    switch (tc) {
        case 9:
            /* tab */
            keycode = 0x0f;
            break;
        case 194:
            extchar = ctext[1];
            NSLog(@"extchar=%d\n", extchar);
            if (extchar == 161) {
                /* "¡" */
                keycode = 0x0d;
            } else if (extchar == 191) {
                /* "¿" */
                keycode = 0x0d;
                special = 0x36;
            } else if (extchar == 186) {
                /* º */
                keycode = 0x29;
            } else if (extchar == 170) {
                /* ª */
                keycode = 0x29;
                special = 0x36;
            } else if (extchar == 169) {
                /* Ctrl + C */
                keycode = 0x2e;
                special = 0x1d;
            } else if (extchar == 174) {
                keycode = 0x13;
                special = 0x1d;
            }
            break;
        case 195:
            extchar = ctext[1];
            NSLog(@"extchar=%d\n", extchar);
            if (extchar == 177) {
                /* ñ */
                keycode = 0x27;
            } else if (extchar == 145) {
                /* Ñ */
                keycode = 0x27;
                special = 0x36;
            } else if (extchar == 167) {
                /* ç */
                keycode = 0x2b;
            } else if (extchar == 135) {
                /* Ç */
                keycode = 0x2b;
                special = 0x36;
            } else if (extchar == 161) {
                /* á */
                keycode = 0x1e;
                prekey = 0x28;
            } else if (extchar == 169) {
                /* é */
                keycode = 0x12;
                prekey = 0x28;
            } else if (extchar == 173) {
                /* í */
                keycode = 0x17;
                prekey = 0x28;
            } else if (extchar == 179) {
                /* ó */
                keycode = 0x18;
                prekey = 0x28;
            } else if (extchar == 186) {
                /* ú */
                keycode = 0x16;
                prekey = 0x28;
            } else if (extchar == 188) {
                /* ü */
                keycode = 0x16;
                prekey = 0x28;
                prekey_special = 0x36;
            } else if (extchar == 129) {
                /* Á */
                keycode = 0x1e;
                special = 0x36;
                prekey = 0x28;
            } else if (extchar == 137) {
                /* É */
                keycode = 0x12;
                special = 0x36;
                prekey = 0x28;
            } else if (extchar == 141) {
                /* Í */
                keycode = 0x17;
                special = 0x36;
                prekey = 0x28;
            } else if (extchar == 147) {
                /* Ó */
                keycode = 0x18;
                special = 0x36;
                prekey = 0x28;
            } else if (extchar == 154) {
                /* Ú */
                keycode = 0x16;
                special = 0x36;
                prekey = 0x28;
            } else if (extchar == 156) {
                /* Ü */
                keycode = 0x16;
                special = 0x36;
                prekey = 0x28;
                prekey_special = 0x36;
            }
            break;
        case 226:
            extchar = ctext[1];
            extchar2 = ctext[2];
            NSLog(@"extchar=%d\n", extchar);
            NSLog(@"extchar2=%d\n", extchar2);
            if (extchar == 130 && extchar2 == 172) {
                /* euro */
                keycode = 0x12;
                special = 0x138;
            } else if (extchar == 137 && extchar2 == 164) {
                /* backslash */
                keycode = 0x29;
                special = 0x138;
            } else if (extchar == 136 && extchar2 == 145) {
                /* Ctrl + X */
                keycode = 0x2d;
                special = 0x1d;
            } else if (extchar == 136 && extchar2 == 154) {
                /* Ctrl + V */
                keycode = 0x2f;
                special = 0x1d;
            }
            break;
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
            keycode = 0x05;
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
        case '^':
            keycode = 0x1a;
            special = 0x36;
            break;
        case '`':
            keycode = 0x1a;
            break;
        case '<':
            keycode = 0x56;
            break;
        case '>':
            keycode = 0x56;
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
        
        if (prekey) {
            if (prekey_special) {
                engine_spice_keyboard_event(prekey_special, 1);
            }
            engine_spice_keyboard_event(prekey, 1);
            engine_spice_keyboard_event(prekey, 0);
            if (prekey_special) {
                engine_spice_keyboard_event(prekey_special, 0);
            }
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

-(BOOL)keyboardInputShouldDelete:(UITextField *)textField {
    BOOL shouldDelete = YES;
    
    if ([UITextField instanceMethodForSelector:_cmd]) {
        BOOL (*keyboardInputShouldDelete)(id, SEL, UITextField *) = (BOOL (*)(id, SEL, UITextField *))[UITextField instanceMethodForSelector:_cmd];
        if (keyboardInputShouldDelete) {
            shouldDelete = keyboardInputShouldDelete(self, _cmd, textField);
        }
    }
    
    if ([[[UIDevice currentDevice] systemVersion] intValue] >= 8) {
        [self deleteBackward];
    }
    
    return shouldDelete;
}

-(BOOL)canBecomeFirstResponder {
    return YES;
}

- (void)keyboardWillShow:(NSNotification *)notification {
    if (global_state.width > global_state.height) {
        engine_set_keyboard_offset(0.2);
    }
}

- (void)keyboardWillHide:(NSNotification *)notification {
    engine_set_keyboard_offset(0.0);
}

@end
