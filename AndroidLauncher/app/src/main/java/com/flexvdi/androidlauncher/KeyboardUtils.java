package com.flexvdi.androidlauncher;

import android.util.Log;

public class KeyboardUtils {
    public static boolean sendSpecialKeyCode(int keyCode) {
        if (keyCode == 67) {
            flexJNI.sendKeyEvent(0x0E, 1);
            flexJNI.sendKeyEvent(0x0E, 0);
            return true;
        }
        return false;
    }

    public static boolean sendPrintableKeyCode(int uChar) {
        int keycode = 0;
        int special = 0;
        int prekey = 0;
        int prekey_special = 0;
        boolean is_upper = false;

        if (Character.isLetter(uChar) && Character.isUpperCase(uChar)) {
            is_upper = true;
            uChar = Character.toLowerCase(uChar);
        }

        switch (uChar) {
            case '€':
                keycode = 0x12;
                special = 0x138;
                break;
            case '¡':
                keycode = 0x0d;
                break;
            case '¿':
                keycode = 0x0d;
                special = 0x36;
                break;
            case 'º':
                keycode = 0x29;
                break;
            case 'ª':
                keycode = 0x29;
                special = 0x36;
                break;
            case 'ñ':
                keycode = 0x27;
                break;
            case 'Ñ':
                keycode = 0x27;
                special = 0x36;
                break;
            case 'ç':
                keycode = 0x2b;
                break;
            case 'Ç':
                keycode = 0x2b;
                special = 0x36;
                break;
            case 'á':
                keycode = 0x1e;
                prekey = 0x28;
                break;
            case 'é':
                keycode = 0x12;
                prekey = 0x28;
                break;
            case 'í':
                keycode = 0x17;
                prekey = 0x28;
                break;
            case 'ó':
                keycode = 0x18;
                prekey = 0x28;
                break;
            case 'ú':
                keycode = 0x16;
                prekey = 0x28;
                break;
            case 'ü':
                keycode = 0x16;
                prekey = 0x28;
                prekey_special = 0x36;
                break;
            case 'Á':
                keycode = 0x1e;
                special = 0x36;
                prekey = 0x28;
                break;
            case 'É':
                keycode = 0x12;
                special = 0x36;
                prekey = 0x28;
                break;
            case 'Í':
                keycode = 0x17;
                special = 0x36;
                prekey = 0x28;
                break;
            case 'Ó':
                keycode = 0x18;
                special = 0x36;
                prekey = 0x28;
                break;
            case 'Ú':
                keycode = 0x16;
                special = 0x36;
                prekey = 0x28;
                break;
            case 'Ü':
                keycode = 0x16;
                special = 0x36;
                prekey = 0x28;
                prekey_special = 0x36;
                break;
            case 9:
            /* tab */
                keycode = 0x0f;
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
            case '\'':
                keycode = 0x0c;
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

        if (keycode != 0) {
            sendKeyCombination(keycode, is_upper, special, prekey, prekey_special);
            return true;
        }

        return false;
    }

    public static void sendKeyCombination(int keycode, boolean is_upper, int special, int prekey, int prekey_special)
    {
        if (keycode != 0) {
            if (is_upper) {
                special = 0x2A;
            }

            if (prekey != 0) {
                if (prekey_special != 0) {
                    flexJNI.sendKeyEvent(prekey_special, 1);
                }
                flexJNI.sendKeyEvent(prekey, 1);
                flexJNI.sendKeyEvent(prekey, 0);
                if (prekey_special != 0) {
                    flexJNI.sendKeyEvent(prekey_special, 0);
                }
            }

            if (special != 0) {
                flexJNI.sendKeyEvent(special, 1);
            }

            flexJNI.sendKeyEvent(keycode, 1);
            flexJNI.sendKeyEvent(keycode, 0);

            if (special != 0) {
                flexJNI.sendKeyEvent(special, 0);
            }
        }
    }

    public static void sendRawKey(int scanCode, int action) {
        Log.i("androidlauncher", "sendRawKey: " + scanCode + " " + action);

        /* Some scancodes need an special treatment */
        if (scanCode == 100) {
            /* alt-gr needs to be ctrl+alt */
            flexJNI.sendKeyEvent(0x38, action);
            flexJNI.sendKeyEvent(0x1d, action);
        } else if (scanCode == 105) {
            /* left arrow */
            flexJNI.sendKeyEvent(0x4b, action);
        } else if (scanCode == 103) {
            /* up arrow */
            flexJNI.sendKeyEvent(0x48, action);
        } else if (scanCode == 106) {
            /* right arrow */
            flexJNI.sendKeyEvent(0x4d, action);
        } else if (scanCode == 108) {
            /* down arrow */
            flexJNI.sendKeyEvent(0x50, action);
        } else {
            flexJNI.sendKeyEvent(scanCode, action);
        }
    }

    public static boolean sendKeyCode(int keyCode, int uChar) {
        Log.i("key pressed", String.valueOf(uChar) + "-" + String.valueOf(keyCode));

        if (sendSpecialKeyCode(keyCode)) {
            return true;
        }

        if (sendPrintableKeyCode(uChar)) {
            return true;
        }

        return false;
    }
}
