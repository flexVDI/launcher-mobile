package com.flexvdi.androidlauncher;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import java.net.URI;
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


/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends Activity {
    private final static String TAG = "flexVDI";
    private SharedPreferences settings;
    private SharedPreferences.Editor settingsEditor;
    private EditText ipText;
    private EditText flexServerName;
    private EditText passwordText;
    private Button goButton;
    private LinearLayout layoutAdvancedOptions;
    private CheckBox checkBoxAdvancedOptions;
    private CheckBox checkBoxEnableSound;
    private CheckBox checkBoxStaticResolution;
    private String deviceID;
    private TextView textViewDeviceID;
    private boolean showPending;
    private Context mContext;
    private String selectedDesktop;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        mContext = this;

        settings = getSharedPreferences("flexVDI", MODE_PRIVATE);
        settingsEditor = settings.edit();
		/* Uncomment this for clearing preferences (useful when debugging) */
//		settingsEditor.clear();
//		settingsEditor.apply();
//		settingsEditor.commit();

        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        //try {
        //    GStreamer.init(this);
        //} catch (Exception e) {
        //    Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        //    finish();
        //    return;
        //}

        setContentView(R.layout.activity_login);

        //showPending = true;

        ipText = (EditText) findViewById(R.id.textIP);
        flexServerName = (EditText) findViewById(R.id.flexServerName);
        passwordText = (EditText) findViewById(R.id.textPASSWORD);

        ipText.setText("flexvdi");
        passwordText.setText("flexvdi");

        goButton = (Button) findViewById(R.id.buttonGO);
        goButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (flexServerName.getText().length() == 0) {
                    Toast.makeText(view.getContext(),
                            "El campo \"Servidor de flexVDI\" está vacío", Toast.LENGTH_LONG)
                            .show();
                } else {
                    ConnectivityManager cm =
                            (ConnectivityManager)mContext.getSystemService(Context.CONNECTIVITY_SERVICE);

                    NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                    boolean isConnected = activeNetwork != null &&
                            activeNetwork.isConnectedOrConnecting();

                    if (isConnected) {
                        if (ipText.getText().length() != 0
                                && passwordText.getText().length() != 0) {
                            new RequestTask().execute(
                                    "authmode",
                                    flexServerName.getText().toString(),
                                    ipText.getText().toString(),
                                    passwordText.getText().toString(),
                                    "");
                        } else
                            Toast.makeText(view.getContext(),
                                    "Introduzca su nombre de usuario y contraseña", Toast.LENGTH_LONG)
                                    .show();
                    } else {
                        Toast.makeText(view.getContext(),
                                "No hay ninguna conexión de red disponible", Toast.LENGTH_LONG)
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
        if (settings.getBoolean("enableSound", false)) {
            checkBoxEnableSound.setChecked(true);
        } else {
            checkBoxEnableSound.setChecked(false);
        }

        checkBoxStaticResolution = (CheckBox) findViewById(R.id.checkBoxStaticResolution);
        if (settings.getBoolean("staticResolution", true)) {
            checkBoxStaticResolution.setChecked(true);
        } else {
            checkBoxStaticResolution.setChecked(false);
        }

        deviceID = Settings.Secure.getString(mContext.getContentResolver(),
                Settings.Secure.ANDROID_ID);

        textViewDeviceID = (TextView) findViewById(R.id.textViewDeviceID);
        textViewDeviceID.setText("ID: " + deviceID);

        if (settings.contains("flexServerName")) {
            flexServerName.setText(settings.getString("flexServerName", ""));
        } else {
            flexServerName.setText("manager.flexvdi.com");
            checkBoxAdvancedOptions.setChecked(true);
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
        ipText.setEnabled(false);
        passwordText.setEnabled(false);
        goButton.setEnabled(false);
        flexServerName.setEnabled(false);
        checkBoxStaticResolution.setEnabled(false);
        checkBoxEnableSound.setEnabled(false);
    }

    private void enableEntryFields() {
        ipText.setEnabled(true);
        passwordText.setEnabled(true);
        goButton.setEnabled(true);
        flexServerName.setEnabled(true);
        checkBoxStaticResolution.setEnabled(true);
        checkBoxEnableSound.setEnabled(true);
    }

    private void showError(String result) {
        Toast.makeText(this, result, Toast.LENGTH_LONG).show();
    }

    private void showPopup(final String[] desktopList, final String[] descriptionList) {
        Dialog d = new AlertDialog.Builder(this)
                .setTitle("Seleccione su Escritorio")
                .setNegativeButton("Cancel", null)
                .setItems(descriptionList, new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dlg, int position) {
                        selectedDesktop = desktopList[position];
                        new RequestTask().execute(
                                "desktop",
                                flexServerName.getText().toString(),
                                ipText.getText().toString(),
                                passwordText.getText().toString(),
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
                        flexServerName.getText().toString(),
                        ipText.getText().toString(),
                        passwordText.getText().toString(),
                        "");
            } else if (result.equals("pending")) {
                if (showPending) {
                    showError("Su escritorio se está preparando. Por favor, espere unos instantes.");
                    showPending = false;
                }

                new RequestTask().execute(
                        "pending",
                        flexServerName.getText().toString(),
                        ipText.getText().toString(),
                        passwordText.getText().toString(),
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
                    showError("Respuesta inválida del servidor");
                }
            } else if (result.equals("ready")) {
                settingsEditor.putBoolean("enableSound", checkBoxEnableSound.isChecked());
                settingsEditor.putBoolean("staticResolution", checkBoxStaticResolution.isChecked());

                settingsEditor.putString("flexServerName", flexServerName.getText().toString());
                settingsEditor.apply();
                settingsEditor.commit();
                showPending = true;

                startMainActivity();
            } else {
                showPending = true;
                if (result.contains("Usuario o con")) {
                    showError("Usuario o contraseña incorrectos");
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
                    result = "Error conectando con el servidor";
                }
                conn.disconnect();
            } catch (IOException ioe) {
                Log.e(TAG, ioe.getMessage());
                result = "Error en la conexion con IP: " + host;
            } catch (JSONException je) {
                Log.e(TAG, je.getMessage());
                result = "Error parseando los datos";
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
                        settingsEditor.putString("use_ws", jresp.getString("use_ws"));
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
                    result = "Error conectando con el servidor";
                }
                conn.disconnect();
            } catch (IOException ioe) {
                Log.e(TAG, ioe.getMessage());
                result = "Error en la conexion con IP: " + host;
            } catch (JSONException je) {
                Log.e(TAG, je.getMessage());
                result = "Error parseando los datos";
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

