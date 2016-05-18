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

package com.flexvdi.androidlauncher;

import android.util.Log;

import java.util.HashMap;

public class KeyboardUtils {
    private static HashMap<Integer, KeySequence> activeKeyMap;

    public static class KeySequence {
        public int prekey;
        public int special_prekey;
        public int special_key;
        public int key;

        public KeySequence(int p, int sp, int sk, int k) {
            prekey = p;
            special_prekey = sp;
            special_key = sk;
            key = k;
        }
    }

    public enum KeyboardMap {
        PC104_ES, PC104_US
    }

    private static HashMap<Integer, KeySequence> BuildMap_pc104es() {
        HashMap<Integer, KeySequence> keyMap = new HashMap<>();

        keyMap.put((int) '€', new KeySequence(0, 0, 0x138, 0x12));
        keyMap.put((int) '¡', new KeySequence(0, 0, 0, 0x0d));
        keyMap.put((int) '¿', new KeySequence(0, 0, 0x36, 0x0d));
        keyMap.put((int) 'º', new KeySequence(0, 0, 0, 0x29));
        keyMap.put((int) 'ª', new KeySequence(0, 0, 0x36, 0x29));
        keyMap.put((int) 'ñ', new KeySequence(0, 0, 0, 0x27));
        keyMap.put((int) 'Ñ', new KeySequence(0, 0, 0x36, 0x27));
        keyMap.put((int) 'ç', new KeySequence(0, 0, 0, 0x2b));
        keyMap.put((int) 'Ç', new KeySequence(0, 0, 0x36, 0x2b));

        keyMap.put((int) 'á', new KeySequence(0x28, 0, 0, 0x1e));
        keyMap.put((int) 'é', new KeySequence(0x28, 0, 0, 0x12));
        keyMap.put((int) 'í', new KeySequence(0x28, 0, 0, 0x17));
        keyMap.put((int) 'ó', new KeySequence(0x28, 0, 0, 0x18));
        keyMap.put((int) 'ú', new KeySequence(0x28, 0, 0, 0x16));
        keyMap.put((int) 'ü', new KeySequence(0x28, 0x36, 0, 0x16));

        keyMap.put((int) 'Á', new KeySequence(0x28, 0, 0x36, 0x1e));
        keyMap.put((int) 'é', new KeySequence(0x28, 0, 0x36, 0x12));
        keyMap.put((int) 'í', new KeySequence(0x28, 0, 0x36, 0x17));
        keyMap.put((int) 'ó', new KeySequence(0x28, 0, 0x36, 0x18));
        keyMap.put((int) 'ú', new KeySequence(0x28, 0, 0x36, 0x16));
        keyMap.put((int) 'ü', new KeySequence(0x28, 0x36, 0x36, 0x16));

        keyMap.put(9, new KeySequence(0, 0, 0, 0x0f)); // Tab

        keyMap.put((int) 'a', new KeySequence(0, 0, 0, 0x1E));
        keyMap.put((int) 'b', new KeySequence(0, 0, 0, 0x30));
        keyMap.put((int) 'c', new KeySequence(0, 0, 0, 0x2E));
        keyMap.put((int) 'd', new KeySequence(0, 0, 0, 0x20));
        keyMap.put((int) 'e', new KeySequence(0, 0, 0, 0x12));
        keyMap.put((int) 'f', new KeySequence(0, 0, 0, 0x21));
        keyMap.put((int) 'g', new KeySequence(0, 0, 0, 0x22));
        keyMap.put((int) 'h', new KeySequence(0, 0, 0, 0x23));
        keyMap.put((int) 'i', new KeySequence(0, 0, 0, 0x17));
        keyMap.put((int) 'j', new KeySequence(0, 0, 0, 0x24));
        keyMap.put((int) 'k', new KeySequence(0, 0, 0, 0x25));
        keyMap.put((int) 'l', new KeySequence(0, 0, 0, 0x26));
        keyMap.put((int) 'm', new KeySequence(0, 0, 0, 0x32));
        keyMap.put((int) 'n', new KeySequence(0, 0, 0, 0x31));
        keyMap.put((int) 'o', new KeySequence(0, 0, 0, 0x18));
        keyMap.put((int) 'p', new KeySequence(0, 0, 0, 0x19));
        keyMap.put((int) 'q', new KeySequence(0, 0, 0, 0x10));
        keyMap.put((int) 'r', new KeySequence(0, 0, 0, 0x13));
        keyMap.put((int) 's', new KeySequence(0, 0, 0, 0x1F));
        keyMap.put((int) 't', new KeySequence(0, 0, 0, 0x14));
        keyMap.put((int) 'u', new KeySequence(0, 0, 0, 0x16));
        keyMap.put((int) 'v', new KeySequence(0, 0, 0, 0x2F));
        keyMap.put((int) 'w', new KeySequence(0, 0, 0, 0x11));
        keyMap.put((int) 'x', new KeySequence(0, 0, 0, 0x2D));
        keyMap.put((int) 'y', new KeySequence(0, 0, 0, 0x15));
        keyMap.put((int) 'z', new KeySequence(0, 0, 0, 0x2C));
        keyMap.put((int) '1', new KeySequence(0, 0, 0, 0x02));
        keyMap.put((int) '2', new KeySequence(0, 0, 0, 0x03));
        keyMap.put((int) '3', new KeySequence(0, 0, 0, 0x04));
        keyMap.put((int) '4', new KeySequence(0, 0, 0, 0x05));
        keyMap.put((int) '5', new KeySequence(0, 0, 0, 0x06));
        keyMap.put((int) '6', new KeySequence(0, 0, 0, 0x07));
        keyMap.put((int) '7', new KeySequence(0, 0, 0, 0x08));
        keyMap.put((int) '8', new KeySequence(0, 0, 0, 0x09));
        keyMap.put((int) '9', new KeySequence(0, 0, 0, 0x0A));
        keyMap.put((int) '0', new KeySequence(0, 0, 0, 0x0B));
        keyMap.put((int) ' ', new KeySequence(0, 0, 0, 0x39));

        keyMap.put((int) '!', new KeySequence(0, 0, 0x36, 0x02));
        keyMap.put((int) '@', new KeySequence(0, 0, 0x138, 0x03));
        keyMap.put((int) '"', new KeySequence(0, 0, 0x36, 0x03));
        keyMap.put((int) '\'', new KeySequence(0, 0, 0, 0x0c));
        keyMap.put((int) '#', new KeySequence(0, 0, 0x138, 0x04));
        keyMap.put((int) '~', new KeySequence(0, 0, 0x138, 0x05));
        keyMap.put((int) '$', new KeySequence(0, 0, 0x36, 0x05));
        keyMap.put((int) '%', new KeySequence(0, 0, 0x36, 0x06));
        keyMap.put((int) '&', new KeySequence(0, 0, 0x36, 0x07));
        keyMap.put((int) '/', new KeySequence(0, 0, 0x36, 0x08));
        keyMap.put((int) '(', new KeySequence(0, 0, 0x36, 0x09));
        keyMap.put((int) ')', new KeySequence(0, 0, 0x36, 0x0a));
        keyMap.put((int) '=', new KeySequence(0, 0, 0x36, 0x0b));
        keyMap.put((int) '?', new KeySequence(0, 0, 0x36, 0x0c));
        keyMap.put((int) '-', new KeySequence(0, 0, 0, 0x35));
        keyMap.put((int) '_', new KeySequence(0, 0, 0x36, 0x35));
        keyMap.put((int) ';', new KeySequence(0, 0, 0x36, 0x33));
        keyMap.put((int) ',', new KeySequence(0, 0, 0, 0x33));
        keyMap.put((int) '.', new KeySequence(0, 0, 0, 0x34));
        keyMap.put((int) ':', new KeySequence(0, 0, 0x36, 0x34));
        keyMap.put((int) '{', new KeySequence(0, 0, 0x138, 0x28));
        keyMap.put((int) '}', new KeySequence(0, 0, 0x138, 0x2B));
        keyMap.put((int) '[', new KeySequence(0, 0, 0x138, 0x1A));
        keyMap.put((int) ']', new KeySequence(0, 0, 0x138, 0x1B));
        keyMap.put((int) '*', new KeySequence(0, 0, 0x36, 0x1B));
        keyMap.put((int) '+', new KeySequence(0, 0, 0, 0x1B));
        keyMap.put((int) '\\', new KeySequence(0, 0, 0x138, 0x29));
        keyMap.put((int) '|', new KeySequence(0, 0, 0x138, 0x02));
        keyMap.put((int) '^', new KeySequence(0, 0, 0x36, 0x1a));
        keyMap.put((int) '`', new KeySequence(0, 0, 0, 0x1a));
        keyMap.put((int) '<', new KeySequence(0, 0, 0, 0x56));
        keyMap.put((int) '>', new KeySequence(0, 0, 0x36, 0x56));
        keyMap.put((int) '\n', new KeySequence(0, 0, 0, 0x1C));

        return keyMap;
    }

    private static HashMap<Integer, KeySequence> BuildMap_pc104us() {
        HashMap<Integer, KeySequence> keyMap = new HashMap<>();

        keyMap.put(9, new KeySequence(0, 0, 0, 0x0f)); // Tab

        keyMap.put((int) 'a', new KeySequence(0, 0, 0, 0x1E));
        keyMap.put((int) 'b', new KeySequence(0, 0, 0, 0x30));
        keyMap.put((int) 'c', new KeySequence(0, 0, 0, 0x2E));
        keyMap.put((int) 'd', new KeySequence(0, 0, 0, 0x20));
        keyMap.put((int) 'e', new KeySequence(0, 0, 0, 0x12));
        keyMap.put((int) 'f', new KeySequence(0, 0, 0, 0x21));
        keyMap.put((int) 'g', new KeySequence(0, 0, 0, 0x22));
        keyMap.put((int) 'h', new KeySequence(0, 0, 0, 0x23));
        keyMap.put((int) 'i', new KeySequence(0, 0, 0, 0x17));
        keyMap.put((int) 'j', new KeySequence(0, 0, 0, 0x24));
        keyMap.put((int) 'k', new KeySequence(0, 0, 0, 0x25));
        keyMap.put((int) 'l', new KeySequence(0, 0, 0, 0x26));
        keyMap.put((int) 'm', new KeySequence(0, 0, 0, 0x32));
        keyMap.put((int) 'n', new KeySequence(0, 0, 0, 0x31));
        keyMap.put((int) 'o', new KeySequence(0, 0, 0, 0x18));
        keyMap.put((int) 'p', new KeySequence(0, 0, 0, 0x19));
        keyMap.put((int) 'q', new KeySequence(0, 0, 0, 0x10));
        keyMap.put((int) 'r', new KeySequence(0, 0, 0, 0x13));
        keyMap.put((int) 's', new KeySequence(0, 0, 0, 0x1F));
        keyMap.put((int) 't', new KeySequence(0, 0, 0, 0x14));
        keyMap.put((int) 'u', new KeySequence(0, 0, 0, 0x16));
        keyMap.put((int) 'v', new KeySequence(0, 0, 0, 0x2F));
        keyMap.put((int) 'w', new KeySequence(0, 0, 0, 0x11));
        keyMap.put((int) 'x', new KeySequence(0, 0, 0, 0x2D));
        keyMap.put((int) 'y', new KeySequence(0, 0, 0, 0x15));
        keyMap.put((int) 'z', new KeySequence(0, 0, 0, 0x2C));
        keyMap.put((int) '1', new KeySequence(0, 0, 0, 0x02));
        keyMap.put((int) '2', new KeySequence(0, 0, 0, 0x03));
        keyMap.put((int) '3', new KeySequence(0, 0, 0, 0x04));
        keyMap.put((int) '4', new KeySequence(0, 0, 0, 0x05));
        keyMap.put((int) '5', new KeySequence(0, 0, 0, 0x06));
        keyMap.put((int) '6', new KeySequence(0, 0, 0, 0x07));
        keyMap.put((int) '7', new KeySequence(0, 0, 0, 0x08));
        keyMap.put((int) '8', new KeySequence(0, 0, 0, 0x09));
        keyMap.put((int) '9', new KeySequence(0, 0, 0, 0x0A));
        keyMap.put((int) '0', new KeySequence(0, 0, 0, 0x0B));
        keyMap.put((int) ' ', new KeySequence(0, 0, 0, 0x39));

        keyMap.put((int) '!', new KeySequence(0, 0, 0x36, 0x02));
        keyMap.put((int) '@', new KeySequence(0, 0, 0x36, 0x03));
        keyMap.put((int) '"', new KeySequence(0, 0, 0x36, 0x28));
        keyMap.put((int) '\'', new KeySequence(0, 0, 0, 0x28));
        keyMap.put((int) '#', new KeySequence(0, 0, 0x36, 0x04));
        keyMap.put((int) '~', new KeySequence(0, 0, 0x36, 0x29));
        keyMap.put((int) '$', new KeySequence(0, 0, 0x36, 0x05));
        keyMap.put((int) '%', new KeySequence(0, 0, 0x36, 0x06));
        keyMap.put((int) '&', new KeySequence(0, 0, 0x36, 0x08));
        keyMap.put((int) '/', new KeySequence(0, 0, 0, 0x35));
        keyMap.put((int) '(', new KeySequence(0, 0, 0x36, 0x0a));
        keyMap.put((int) ')', new KeySequence(0, 0, 0x36, 0x0b));
        keyMap.put((int) '=', new KeySequence(0, 0, 0, 0x0d));
        keyMap.put((int) '?', new KeySequence(0, 0, 0x36, 0x35));
        keyMap.put((int) '-', new KeySequence(0, 0, 0, 0x0c));
        keyMap.put((int) '_', new KeySequence(0, 0, 0x36, 0x0c));
        keyMap.put((int) ';', new KeySequence(0, 0, 0x36, 0x27));
        keyMap.put((int) ',', new KeySequence(0, 0, 0, 0x33));
        keyMap.put((int) '.', new KeySequence(0, 0, 0, 0x34));
        keyMap.put((int) ':', new KeySequence(0, 0, 0, 0x27));
        keyMap.put((int) '{', new KeySequence(0, 0, 0x36, 0x1a));
        keyMap.put((int) '}', new KeySequence(0, 0, 0x36, 0x1b));
        keyMap.put((int) '[', new KeySequence(0, 0, 0, 0x1a));
        keyMap.put((int) ']', new KeySequence(0, 0, 0, 0x1b));
        keyMap.put((int) '*', new KeySequence(0, 0, 0x36, 0x09));
        keyMap.put((int) '+', new KeySequence(0, 0, 0x36, 0x0d));
        keyMap.put((int) '\\', new KeySequence(0, 0, 0, 0x2b));
        keyMap.put((int) '|', new KeySequence(0, 0, 0x36, 0x2b));
        keyMap.put((int) '^', new KeySequence(0, 0, 0x36, 0x07));
        keyMap.put((int) '`', new KeySequence(0, 0, 0, 0x29));
        keyMap.put((int) '<', new KeySequence(0, 0, 0x36, 0x33));
        keyMap.put((int) '>', new KeySequence(0, 0, 0x36, 0x34));
        keyMap.put((int) '\n', new KeySequence(0, 0, 0, 0x1c));

        return keyMap;
    }

    public static void initMap(KeyboardMap map) {
        switch (map) {
            case PC104_ES:
                activeKeyMap = BuildMap_pc104es();
                break;
            default:
                activeKeyMap = BuildMap_pc104us();
        }
    }

    public static boolean sendSpecialKeyCode(int keyCode) {
        if (keyCode == 67) {
            flexJNI.sendKeyEvent(0x0E, 1);
            flexJNI.sendKeyEvent(0x0E, 0);
            return true;
        }
        return false;
    }

    public static boolean sendPrintableKeyCode(int uChar) {
        boolean is_upper = false;

        if (Character.isLetter(uChar) && Character.isUpperCase(uChar)) {
            is_upper = true;
            uChar = Character.toLowerCase(uChar);
        }

        if (activeKeyMap.containsKey(uChar)) {
            KeySequence keySeq = activeKeyMap.get(uChar);
            sendKeyCombination(keySeq.key, is_upper,
                    keySeq.special_key,
                    keySeq.prekey,
                    keySeq.special_prekey);

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
