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

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.opengl.GLSurfaceView;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PowerManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.Locale;

public class MainActivity extends Activity implements View.OnTouchListener {
    private final static String TAG = "flexVDI";
    private GLSurfaceView mGLView;
    private ScaleGestureDetector mScaleDetector;
    private ScaleListener mScaleListener;
    private GestureDetector mGestureDetector;
    private GestureListener mGestureListener;
    private EditText editText;
    private boolean keyboardVisible = false;
    private SharedPreferences settings;
    private SharedPreferences.Editor settingsEditor;

    private final static int ASTATE_RUNNING = 0;
    private final static int ASTATE_PAUSED = 1;
    private final static int ASTATE_STOPPED = 2;
    private static int applicationState;
    private static boolean viewHasFocus;

    private final static int CSTATE_DISCONNECTED = 0;
    private final static int CSTATE_CONNECTED = 1;
    private final static int CSTATE_AUTOCONNECT = 2;
    private static int connDesiredState = CSTATE_CONNECTED;
    private static MainActivity mMainActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        flexJNI.setContext(this);

        settings = getSharedPreferences("flexVDI", MODE_PRIVATE);
        settingsEditor = settings.edit();

        double mouseScale = 1.0;
        double contentScale = 2.0;
        if (settings.getBoolean("staticResolution", true)) {
            mouseScale = 0.5;
            contentScale = 1.0;
        }

        if (Locale.getDefault().toString().equals("es_ES")) {
            KeyboardUtils.initMap(KeyboardUtils.KeyboardMap.PC104_ES);
        } else {
            KeyboardUtils.initMap(KeyboardUtils.KeyboardMap.PC104_US);
        }

        mGLView = new MainGLSurfaceView(this, mouseScale, contentScale);
        mGLView.setOnTouchListener(this);

        editText = new EditText(this);
        editText.setText("dontlookatme");
        editText.setSingleLine(false);
        editText.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI | EditorInfo.IME_FLAG_NO_ENTER_ACTION);
        editText.setInputType(EditorInfo.TYPE_TEXT_FLAG_NO_SUGGESTIONS | EditorInfo.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                if (cs.toString().length() > 12) {
                    KeyboardUtils.sendKeyCode(0, cs.toString().toCharArray()[6]);
                    editText.setText("dontlookatme");
                    editText.setSelection(6);
                } else if (cs.toString().length() < 12) {
                    // BackSpace;
                    KeyboardUtils.sendKeyCode(KeyEvent.KEYCODE_DEL, '\b');
                    editText.setText("dontlookatme");
                    editText.setSelection(6);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            }

            @Override
            public void afterTextChanged(Editable arg0) {
            }
        });
        editText.setSelection(6);

        RelativeLayout relativeLayout = new RelativeLayout(this);
        relativeLayout.addView(editText);
        relativeLayout.addView(mGLView);
        setContentView(relativeLayout);

        mGestureListener = new GestureListener(this);
        mGestureDetector = new GestureDetector(getApplicationContext(), mGestureListener);

        mScaleListener = new ScaleListener(mGestureListener);
        mScaleDetector = new ScaleGestureDetector(getApplicationContext(), mScaleListener);

        mMainActivity = this;

        applicationState = ASTATE_STOPPED;
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        //Log.d("androidlauncher", "dispatchTouchEvent");
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            mGestureListener.onTouchDown(event);
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            mGestureListener.onTouchUp(event);
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            mGestureListener.onMovement(event);
        } else if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_POINTER_UP) {
            Log.e("androidlauncher", "Two fingers");
            mGestureListener.onTwoFingers(event);
            return true;
        }

        if (event.getPointerCount() > 1) {
            if (mScaleDetector.onTouchEvent(event)) {
                return true;
            }
        }

        if (mGestureDetector.onTouchEvent(event)) {
            return true;
        }

        return false;
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            mGestureListener.onTouchUp(event);
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            mGestureListener.onMovement(event);
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (keyboardVisible) {
            toggleKeyboard();
        }
        showPopup();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        Log.e("androidlauncher", "KeyEvent.Action=" + event.getAction() + " KeyEvent.ScanCode=" + event.getScanCode());
        int scanCode = event.getScanCode();
        int action = event.getAction();
        int keyCode = event.getKeyCode();

        /* Special case for Back and Home buttons */
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (action == KeyEvent.ACTION_UP) {
                if (keyboardVisible) {
                    toggleKeyboard();
                }
                showPopup();
            }
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_HOME) {
            return super.dispatchKeyEvent(event);
        }

        if (scanCode != 0) {
            if (scanCode == 114 || scanCode == 115) {
                return false;
            } else if (scanCode == 139) {
                if (action == KeyEvent.ACTION_UP) {
                    toggleKeyboard();
                }
            } else if (scanCode == 158) {
                if (action == KeyEvent.ACTION_UP) {
                    showPopup();
                }
            } else {
                if (action == KeyEvent.ACTION_DOWN) {
                    KeyboardUtils.sendRawKey(scanCode, 1);
                } else {
                    KeyboardUtils.sendRawKey(scanCode, 0);
                }
            }
            return true;
        }

        if (action == KeyEvent.ACTION_UP) {
            return KeyboardUtils.sendKeyCode(keyCode, event.getUnicodeChar());
        } else if (action == KeyEvent.ACTION_MULTIPLE) {
            char[] uChars = event.getCharacters().toCharArray();
            if (uChars.length == 1) {
                return KeyboardUtils.sendKeyCode(keyCode, uChars[0]);
            }
        }

        return super.dispatchKeyEvent(event);

    }

    public void toggleKeyboard() {
        InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        /*
        if (keyboardVisible) {
            InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
            //flexJNI.setKeyboardOpacity(0.2);
            //flexJNI.setKeyboardOffset(0.0);
            keyboardVisible = false;
        } else {
            InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
            //flexJNI.setKeyboardOpacity(1.0);
            //flexJNI.setKeyboardOffset(0.2);
            keyboardVisible = true;
        }
        */
    }

    private void closeConnection() {
        mGLView.onPause();
        connDesiredState = CSTATE_DISCONNECTED;
        flexJNI.disconnect();
    }

    private void clearCredentials() {
        settingsEditor.remove("spice_address");
        settingsEditor.remove("spice_port");
        settingsEditor.remove("spice_password");
        settingsEditor.remove("use_ws");
        settingsEditor.apply();
        settingsEditor.commit();
    }

    private void sendKeyCombo(int combo) {
        boolean ctrl = false;
        boolean alt = false;
        boolean shift = false;
        char keycode = 0;

        switch (combo) {
            case 1:
                /* ctrl+c */
                ctrl = true;
                keycode = 0x2e;
                break;
            case 2:
                /* ctrl+x */
                ctrl = true;
                keycode = 0x2d;
                break;
            case 3:
                /* ctrl+v */
                ctrl = true;
                keycode = 0x2f;
                break;
            case 4:
                /* ctrl+z */
                ctrl = true;
                keycode = 0x2c;
                break;
            case 5:
                /* esc */
                keycode = 0x01;
                break;
            case 6:
                /* alt+f4 */
                alt = true;
                keycode = 0x3e;
                break;
            case 7:
                /* ctrl+alt+supr */
                ctrl = true;
                alt = true;
                keycode = 0x53;
                break;
            case 8:
                /* ctrl+shift+esc */
                ctrl = true;
                shift = true;
                keycode = 0x01;
                break;
            case 9:
                /* ctrl+alt+f1 */
                ctrl = true;
                alt = true;
                keycode = 0x3b;
                break;
            case 10:
                /* ctrl+alt+f2 */
                ctrl = true;
                alt = true;
                keycode = 0x3c;
                break;
            case 11:
                /* ctrl+alt+f6 */
                ctrl = true;
                alt = true;
                keycode = 0x40;
            case 12:
                /* ctrl+alt+f7 */
                ctrl = true;
                alt = true;
                keycode = 0x41;
                break;
        }

        if (keycode != 0) {
            if (ctrl) {
                flexJNI.sendKeyEvent(0x1d, 1);
            }

            if (alt) {
                flexJNI.sendKeyEvent(0x38, 1);
            }

            if (shift) {
                flexJNI.sendKeyEvent(0x2a, 1);
            }

            flexJNI.sendKeyEvent(keycode, 1);
            flexJNI.sendKeyEvent(keycode, 0);

            if (shift) {
                flexJNI.sendKeyEvent(0x2a, 0);
            }

            if (alt) {
                flexJNI.sendKeyEvent(0x38, 0);
            }

            if (ctrl) {
                flexJNI.sendKeyEvent(0x1d, 0);
            }
        }
    }

    private void showPopup() {
        String[] actions = new String[]{
                getResources().getString(R.string.close_session),
                getResources().getString(R.string.send) + " Ctrl+C",
                getResources().getString(R.string.send) + " Ctrl+X",
                getResources().getString(R.string.send) + " Ctrl+V",
                getResources().getString(R.string.send) + " Ctrl+Z",
                getResources().getString(R.string.send) + " Esc",
                getResources().getString(R.string.send) + " Alt+F4",
                getResources().getString(R.string.send) + " Ctrl+Alt+Del",
                getResources().getString(R.string.send) + " Ctrl+Shift+Esc",
                getResources().getString(R.string.send) + " Ctrl+Alt+F1",
                getResources().getString(R.string.send) + " Ctrl+Alt+F2",
                getResources().getString(R.string.send) + " Ctrl+Alt+F6",
                getResources().getString(R.string.send) + " Ctrl+Alt+F7"};
        Dialog d = new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.menu_general))
                .setNegativeButton("Cancel", null)
                .setItems(actions, new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dlg, int position) {
                        switch (position) {
                            case 0:
                                closeConnection();
                                break;
                            default:
                                sendKeyCombo(position);
                                break;
                        }
                    }
                })
                .create();
        d.show();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        Log.d(TAG, "onWindowFocusChanged");
        viewHasFocus = hasFocus;
        onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
        mGLView.onPause();
        if (applicationState == ASTATE_RUNNING) {
            applicationState = ASTATE_PAUSED;
            connDesiredState = CSTATE_AUTOCONNECT;
            flexJNI.disconnect();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
        if (applicationState != ASTATE_STOPPED) {
            applicationState = ASTATE_STOPPED;
        }
    }

    private boolean isScreenOn() {
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        if (powerManager.isScreenOn()) {
            return true;
        }

        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");

        if (viewHasFocus && isScreenOn()) {
            mGLView.onResume();
            if (applicationState != ASTATE_RUNNING) {
                connDesiredState = CSTATE_CONNECTED;
                applicationState = ASTATE_RUNNING;
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        connectionChange(CSTATE_DISCONNECTED);
                    }
                });
            }
        }
    }

    public static void nativeConnectionChange(int state) {
        mMainActivity.connectionChange(state);
    }

    public void connectionChange(int state) {
        Log.d(TAG, "connectionChange: " + state);
        if (state == CSTATE_DISCONNECTED) {
            if (connDesiredState == CSTATE_DISCONNECTED) {
                clearCredentials();
                finish();
            } else if (connDesiredState == CSTATE_AUTOCONNECT) {
                // Do nothing.
            } else {
                ConnectivityManager cm =
                        (ConnectivityManager)mMainActivity.getSystemService(Context.CONNECTIVITY_SERVICE);

                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                boolean isConnected = activeNetwork != null &&
                        activeNetwork.isConnectedOrConnecting();

                if (!isConnected) {
                    mMainActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(mMainActivity,
                                    getResources().getString(R.string.no_network),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });

                    connDesiredState = CSTATE_DISCONNECTED;
                    finish();
                } else {
                    String spice_address = settings.getString("spice_address", "");
                    String spice_port = settings.getString("spice_port", "");
                    String spice_password = settings.getString("spice_password", "");
                    String tunnel_port = "-1";
                    int enableSound = 0;

                    if (settings.getBoolean("use_ws", true)) {
                        tunnel_port = "443";
                    }

                    if (settings.getBoolean("enableSound", true)) {
                        enableSound = 1;
                    }

                    if (!spice_address.equals("") && !spice_port.equals("") && !spice_password.equals("")) {
                        int connTries;

                        flexJNI.setConnectionData(spice_address, spice_port, tunnel_port, spice_password, enableSound);
                        flexJNI.connect();

                        for (connTries = 0; connTries < 2; connTries++) {
                            try {
                                Thread.sleep(3000);
                            } catch (Exception e) {
                            }

                            if (flexJNI.isConnected() != 0) {
                                connDesiredState = CSTATE_CONNECTED;
                                return;
                            } else {
                                flexJNI.connect();
                            }
                        }
                        mMainActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(mMainActivity,
                                        getResources().getString(R.string.reconnection_error),
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    connDesiredState = CSTATE_DISCONNECTED;
                    flexJNI.disconnect();
                    finish();
                }
            }
        }
    }
}
