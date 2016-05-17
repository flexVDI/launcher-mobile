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
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.X509TrustManager;

import org.freedesktop.gstreamer.GStreamer;


public class LoginActivity extends Activity {
    private final static String TAG = "flexVDI";
    private SharedPreferences settings;
    private SharedPreferences.Editor settingsEditor;
    private EditText textUser;
    private EditText textServer;
    private EditText textPassword;
    private Button goButton;
    private LinearLayout layoutAdvancedOptions;
    private CheckBox checkBoxAdvancedOptions;
    private CheckBox checkBoxEnableSound;
    private CheckBox checkBoxStaticResolution;
    private CheckBox checkBoxGenericSpice;
    private String deviceID;
    private TextView textViewDeviceID;
    private boolean showPending;
    private Context mContext;
    private String selectedDesktop;


    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        mContext = this;

        try {
            GStreamer.init(mContext);
        } catch (Exception e) {
            Log.e(TAG, "Can't initialize GStreamer" + e.getMessage());
            finish();
        }

        settings = getSharedPreferences("flexVDI", MODE_PRIVATE);
        settingsEditor = settings.edit();
		/* Uncomment this for clearing preferences (useful when debugging) */
		//settingsEditor.clear();
        //settingsEditor.apply();
		//settingsEditor.commit();

        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        setContentView(R.layout.activity_login);

        textUser = (EditText) findViewById(R.id.textUser);
        textServer = (EditText) findViewById(R.id.textServer);
        textPassword = (EditText) findViewById(R.id.textPassword);

        //ipText.setText("flexvdi");
        //passwordText.setText("flexvdi");

        goButton = (Button) findViewById(R.id.buttonGO);
        goButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (textServer.getText().length() == 0) {
                    Toast.makeText(view.getContext(),
                            getResources().getString(R.string.empty_flexvdi_server),
                            Toast.LENGTH_LONG)
                            .show();
                } else {
                    ConnectivityManager cm =
                            (ConnectivityManager)mContext.getSystemService(Context.CONNECTIVITY_SERVICE);

                    NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                    boolean isConnected = activeNetwork != null &&
                            activeNetwork.isConnectedOrConnecting();

                    if (isConnected) {
                        if (textUser.getText().length() != 0
                                && textPassword.getText().length() != 0) {
                            new RequestTask().execute(
                                    "authmode",
                                    textServer.getText().toString(),
                                    textUser.getText().toString(),
                                    textPassword.getText().toString(),
                                    "");
                        } else
                            Toast.makeText(view.getContext(),
                                    getResources().getString(R.string.empty_credentials),
                                    Toast.LENGTH_LONG)
                                    .show();
                    } else {
                        Toast.makeText(view.getContext(),
                                getResources().getString(R.string.no_network),
                                Toast.LENGTH_LONG)
                                .show();
                    }
                }
            }
        });

        // The advanced settings button.
        checkBoxAdvancedOptions = (CheckBox) findViewById(R.id.checkBoxAdvancedSettings);
        layoutAdvancedOptions = (LinearLayout) findViewById(R.id.layoutAdvancedOptions2);
        checkBoxAdvancedOptions.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton arg0,
                                         boolean checked) {
                if (checked)
                    layoutAdvancedOptions.setVisibility(View.VISIBLE);
                else
                    layoutAdvancedOptions.setVisibility(View.GONE);
            }
        });

        checkBoxEnableSound = (CheckBox) findViewById(R.id.checkBoxEnableSound);
        if (settings.getBoolean("enableSound", true)) {
            checkBoxEnableSound.setChecked(true);
        } else {
            checkBoxEnableSound.setChecked(false);
        }

        if (!settings.contains("staticResolution")) {
            Display display = getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            if ((size.x + size.y) > 2340) {
                /* 2340 = 1440+900 */
                settingsEditor.putBoolean("staticResolution", true);
            } else {
                settingsEditor.putBoolean("staticResolution", false);
            }
            settingsEditor.apply();
            settingsEditor.commit();
        }

        checkBoxStaticResolution = (CheckBox) findViewById(R.id.checkBoxStaticResolution);
        if (settings.getBoolean("staticResolution", true)) {
            checkBoxStaticResolution.setChecked(true);
        } else {
            checkBoxStaticResolution.setChecked(false);
        }

        checkBoxGenericSpice = (CheckBox) findViewById(R.id.checkBoxGenericSpice);
        if (settings.getBoolean("genericSpice", false)) {
            checkBoxGenericSpice.setChecked(true);
        } else {
            checkBoxGenericSpice.setChecked(false);
        }

        checkBoxGenericSpice.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton arg0,
                                         boolean checked) {
                if (checked) {
                    textServer.setHint(getResources().getString(R.string.spice_server));
                    textUser.setHint(getResources().getString(R.string.spice_server_port));
                    String server = textServer.getText().toString();
                    if (server.length() != 0) {
                        if (server.contains(":")) {
                            textUser.setText(server);
                        } else {
                            textUser.setText(server + ":5900");
                            textServer.setText(server + ":5900");
                        }
                    }
                } else {
                    textServer.setHint(getResources().getString(R.string.flexvdi_server));
                    String server = textServer.getText().toString();
                    if (server.length() != 0 && server.contains(":")) {
                        textServer.setText(server.substring(0, server.indexOf(":")));
                    }
                    textUser.setText("");
                    textUser.setHint(getResources().getString(R.string.user));
                }
            }
        });

        deviceID = Settings.Secure.getString(mContext.getContentResolver(),
                Settings.Secure.ANDROID_ID);

        textViewDeviceID = (TextView) findViewById(R.id.textViewDeviceID);
        textViewDeviceID.setText("ID: " + deviceID + " (v2.2.8)");

        if (checkBoxGenericSpice.isChecked()) {
            checkBoxAdvancedOptions.setChecked(true);

            if (settings.contains("spiceServerPort")) {
                textServer.setText(settings.getString("spiceServerPort", ""));
            } else {
                textServer.setText("");
            }
        } else {
            if (settings.contains("flexServerName")) {
                textServer.setText(settings.getString("flexServerName", ""));
            } else {
                textServer.setText("manager.flexvdi.com");
                checkBoxAdvancedOptions.setChecked(true);
            }
        }

        try {
            HttpsURLConnection.setDefaultHostnameVerifier(new NullHostNameVerifier());
            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, new X509TrustManager[]{new NullX509TrustManager()}, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());
        } catch (NoSuchAlgorithmException nsae) {
            Log.e(TAG, nsae.getMessage());
        } catch (KeyManagementException kme) {
            Log.e(TAG, kme.getMessage());
        }
    }

    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void disableEntryFields() {
        textUser.setEnabled(false);
        textPassword.setEnabled(false);
        goButton.setEnabled(false);
        textServer.setEnabled(false);
        checkBoxStaticResolution.setEnabled(false);
        checkBoxEnableSound.setEnabled(false);
    }

    private void enableEntryFields() {
        textUser.setEnabled(true);
        textPassword.setEnabled(true);
        goButton.setEnabled(true);
        textServer.setEnabled(true);
        checkBoxStaticResolution.setEnabled(true);
        checkBoxEnableSound.setEnabled(true);
    }

    private void showError(String result) {
        Toast.makeText(this, result, Toast.LENGTH_LONG).show();
    }

    private void showPopup(final String[] desktopList, final String[] descriptionList) {
        Dialog d = new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.choose_desktop))
                .setNegativeButton("Cancel", null)
                .setItems(descriptionList, new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dlg, int position) {
                        selectedDesktop = desktopList[position];
                        new RequestTask().execute(
                                "desktop",
                                textServer.getText().toString(),
                                textUser.getText().toString(),
                                textPassword.getText().toString(),
                                selectedDesktop);
                    }
                })
                .create();
        d.show();
    }

    class RequestTask extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            disableEntryFields();
            setProgressBarIndeterminateVisibility(true);
        }

        @Override
        protected void onPostExecute(String result) {
            enableEntryFields();
            setProgressBarIndeterminateVisibility(false);

            if (result.equals("desktop")) {
                new RequestTask().execute(
                        "desktop",
                        textServer.getText().toString(),
                        textUser.getText().toString(),
                        textPassword.getText().toString(),
                        "");
            } else if (result.equals("pending")) {
                if (showPending) {
                    showError(getResources().getString(R.string.desktop_pending));
                    showPending = false;
                }

                new RequestTask().execute(
                        "pending",
                        textServer.getText().toString(),
                        textUser.getText().toString(),
                        textPassword.getText().toString(),
                        selectedDesktop);
            } else if (result.contains("selectdesktop")) {
                try {
                    JSONObject jObj = (JSONObject) new JSONTokener(result.split("¡")[1]).nextValue();
                    //String[] args = new String[]{"windowsXP","GNULinux"};
                    List<String> desktopList = new ArrayList<String>();
                    List<String> descriptionList = new ArrayList<String>();
                    Iterator<?> keys = jObj.keys();

                    while (keys.hasNext()) {
                        String desktopName = keys.next().toString();
                        String desktopDescription = jObj.getString(desktopName);
                        desktopList.add(desktopName);
                        if (desktopDescription.equals("")) {
                            descriptionList.add(desktopName);
                        } else {
                            descriptionList.add(desktopDescription);
                        }
                    }

                    showPopup(desktopList.toArray(new String[0]), descriptionList.toArray(new String[0]));
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG, e.getMessage());
                    showError(getResources().getString(R.string.invalid_answer));
                }
            } else if (result.equals("ready")) {
                settingsEditor.putBoolean("enableSound", checkBoxEnableSound.isChecked());
                settingsEditor.putBoolean("staticResolution", checkBoxStaticResolution.isChecked());

                settingsEditor.putString("flexServerName", textServer.getText().toString());
                settingsEditor.apply();
                settingsEditor.commit();
                showPending = true;

                startMainActivity();
            } else {
                showPending = true;
                if (result.contains("Usuario o con")) {
                    showError(getResources().getString(R.string.invalid_credentials));
                } else {
                    showError(result);
                }
            }
        }

        private String doAuthModeRequest(String host) {
            String result = "Unexpected error";

            try {
                URL url = new URL("https://" + host + "/vdi/authmode");
                HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();

                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Accept", "application/json");
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                JSONObject authmode = new JSONObject();
                authmode.put("hwaddress", deviceID);
                String request = authmode.toString();

                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                wr.write(request);
                wr.flush();
                //connAuth.connect();

                StringBuilder sb = new StringBuilder();
                int HttpResult = conn.getResponseCode();
                if (HttpResult == HttpURLConnection.HTTP_OK) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line + "\n");
                    }

                    br.close();
                    JSONObject jresponse = new JSONObject(sb.toString());
                    String status = jresponse.getString("status");
                    if (status.equals("OK")) {
                        result = "desktop";
                    } else {
                        result = jresponse.getString("msg");
                    }
                } else {
                    Log.e(TAG, conn.getResponseMessage());
                    result = getResources().getString(R.string.connection_error);
                }
                conn.disconnect();
            } catch (IOException ioe) {
                Log.e(TAG, ioe.getMessage());
                result = getResources().getString(R.string.connection_error_ip) + host;
            } catch (JSONException je) {
                Log.e(TAG, je.getMessage());
                result = getResources().getString(R.string.parse_error);
            }

            return result;
        }

        private String doDesktopRequest(String host, String username, String password, String desktop) {
            String result = "";
            try {
                URL url = new URL("https://" + host + "/vdi/desktop");
                HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();

                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Accept", "application/json");
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                JSONObject desktopAuth = new JSONObject();
                desktopAuth.put("hwaddress", deviceID);
                desktopAuth.put("username", username);
                desktopAuth.put("password", password);
                desktopAuth.put("desktop", desktop);

                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                wr.write(desktopAuth.toString());
                wr.flush();

                StringBuilder sb = new StringBuilder();
                int HttpResult = conn.getResponseCode();
                if (HttpResult == HttpURLConnection.HTTP_OK) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line + "\n");
                    }

                    br.close();
                    JSONObject jresp = new JSONObject(sb.toString());
                    String statusDesktop = jresp.getString("status");
                    if (statusDesktop.equals("OK")) {
                        settingsEditor.putString("spice_address", jresp.getString("spice_address"));
                        settingsEditor.putString("spice_port", jresp.getString("spice_port"));
                        settingsEditor.putString("spice_password", jresp.getString("spice_password"));
                        settingsEditor.putBoolean("use_ws", jresp.getBoolean("use_ws"));
                        result = "ready";
                    } else if (statusDesktop.equals("Pending")) {
                        result = "pending";
                    } else if (statusDesktop.equals("SelectDesktop")) {
                        result = "selectdesktop¡" + jresp.getString("message");
                    } else if (statusDesktop.equals("Error")){
                        result = jresp.getString("message");
                    }
                } else {
                    Log.e(TAG, conn.getResponseMessage());
                    result = getResources().getString(R.string.connection_error);
                }
                conn.disconnect();
            } catch (IOException ioe) {
                Log.e(TAG, ioe.getMessage());
                result = getResources().getString(R.string.connection_error_ip) + host;
            } catch (JSONException je) {
                Log.e(TAG, je.getMessage());
                result = getResources().getString(R.string.parse_error);
            }

            return result;
        }

        @Override
        protected String doInBackground(String... args) {

            if (args[0].equals("authmode")) {
                String host = args[1];
                return doAuthModeRequest(host);
            } else if (args[0].equals("desktop") || args[0].equals("pending")) {
                String host = args[1];
                String username = args[2];
                String password = args[3];
                String desktop = args[4];

                if (args[0].equals("pending")) {
                    try {
                        Thread.sleep(5000);
                    } catch (Exception e) {

                    }
                }

                return doDesktopRequest(host, username, password, desktop);
            }

            return "Internal error in doInBackground";
        }
    }
}

