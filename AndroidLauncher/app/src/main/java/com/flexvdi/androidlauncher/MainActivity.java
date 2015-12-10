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
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

public class MainActivity extends Activity implements View.OnTouchListener {
    private final static String TAG = "flexVDI";
    private GLSurfaceView mGLView;
    private ScaleGestureDetector mScaleDetector;
    private ScaleListener mScaleListener;
    private GestureDetector mGestureDetector;
    private GestureListener mGestureListener;
    private boolean keyboardVisible = false;
    private SharedPreferences settings;
    private SharedPreferences.Editor settingsEditor;

    private final static int CSTATE_DISCONNECTED = 0;
    private final static int CSTATE_CONNECTED = 1;
    private final static int CSTATE_AUTOCONNECT = 2;
    private static int connDesiredState = CSTATE_CONNECTED;
    private static MainActivity mMainActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        settings = getSharedPreferences("flexVDI", MODE_PRIVATE);
        settingsEditor = settings.edit();

        double scale = 1.0;
        if (settings.getBoolean("staticResolution", true)) {
            scale = 0.5;
        }

        mGLView = new MainGLSurfaceView(this, scale);
        setContentView(mGLView);
        mGLView.setOnTouchListener(this);

        mGestureListener = new GestureListener(this);
        mGestureDetector = new GestureDetector(getApplicationContext(), mGestureListener);

        mScaleListener = new ScaleListener(mGestureListener);
        mScaleDetector = new ScaleGestureDetector(getApplicationContext(), mScaleListener);

        mMainActivity = this;
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        //Log.d("androidlauncher", "dispatchTouchEvent");
        if (event.getAction() == MotionEvent.ACTION_UP) {
            mGestureListener.onTouchUp(event);
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            mGestureListener.onMovement(event);
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
    public boolean dispatchKeyEvent(KeyEvent event) {
        Log.e("androidlauncher", "KeyEvent.Action=" + event.getAction() + " KeyEvent.ScanCode=" + event.getScanCode());
        int scanCode = event.getScanCode();
        int action = event.getAction();
        int keyCode = event.getKeyCode();

        if (scanCode != 0) {
            if (scanCode == 139) {
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
        if (keyboardVisible) {
            InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
            keyboardVisible = false;
        } else {
            InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
            keyboardVisible = true;
        }
    }

    private void closeConnection() {
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
                "Cerrar sesión",
                "Enviar Ctrl+C",
                "Enviar Ctrl+X",
                "Enviar Ctrl+V",
                "Enviar Ctrl+Z",
                "Enviar Esc",
                "Enviar Alt+F4",
                "Enviar Ctrl+Alt+Supr",
                "Enviar Ctrl+Shift+Esc",
                "Enviar Ctrl+Alt+F1",
                "Enviar Ctrl+Alt+F2",
                "Enviar Ctrl+Alt+F6",
                "Enviar Ctrl+Alt+F7"};
        Dialog d = new AlertDialog.Builder(this)
                .setTitle("Menú General")
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
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
        mGLView.onPause();
        connDesiredState = CSTATE_AUTOCONNECT;
        flexJNI.disconnect();
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
        connDesiredState = CSTATE_AUTOCONNECT;
        flexJNI.disconnect();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        mGLView.onResume();
        String spice_address = settings.getString("spice_address", "");
        String spice_port = settings.getString("spice_port", "");
        String spice_password = settings.getString("spice_password", "");
        String use_ws = settings.getString("use_ws", "");
        String tunnel_port = "443";

        if (use_ws.equals("true")) {
            tunnel_port = "-1";
        }

        if (!spice_address.equals("") && !spice_port.equals("") && !spice_password.equals("")) {
            connDesiredState = CSTATE_CONNECTED;
            flexJNI.setConnectionData(spice_address, spice_port, tunnel_port, spice_password, Boolean.TRUE);
            flexJNI.connect();
        } else {
            finish();
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
                            Toast.makeText(mMainActivity, "No hay ninguna conexión de red disponible", Toast.LENGTH_SHORT).show();
                        }
                    });

                    connDesiredState = CSTATE_DISCONNECTED;
                    finish();
                } else {
                    String spice_address = settings.getString("spice_address", "");
                    String spice_port = settings.getString("spice_port", "");
                    String spice_password = settings.getString("spice_password", "");
                    String use_ws = settings.getString("use_ws", "");
                    String tunnel_port = "443";

                    if (use_ws.equals("true")) {
                        tunnel_port = "-1";
                    }

                    if (!spice_address.equals("") && !spice_port.equals("") && !spice_password.equals("")) {
                        int connTries;

                        flexJNI.setConnectionData(spice_address, spice_port, tunnel_port, spice_password, Boolean.TRUE);
                        flexJNI.connect();

                        for (connTries = 0; connTries < 3; connTries++) {
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
                                Toast.makeText(mMainActivity, "No fue posible reconectar con su escritorio", Toast.LENGTH_SHORT).show();
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
