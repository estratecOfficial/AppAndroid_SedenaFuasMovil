package com.codinginflow.sedena;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.codinginflow.sedena.Globales.ConfiguracionCifrado;
import com.codinginflow.sedena.Globales.GlobalesCifrado;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Login extends AppCompatActivity {

    private EditText edtusuario, edtpassword;
    private Button btninicarsesion, btnsalir;
    private ArrayAdapter<CharSequence> adapter;
    private Spinner spitipoUsuario;
    private ProgressDialog pDialog;
    public static String idUsuario, modoConexion, usuario, password, tipoUsuario;
    private static final int MY_WRITE_EXTERNAL_STORAGE = 0;
    private static final int MY_CAMERA = 0;
    private View mLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mLayout = findViewById(R.id.ConstraintlayoutLogin);

        edtusuario = (EditText) findViewById(R.id.edtUsuario);
        edtpassword = (EditText) findViewById(R.id.edtPassword);
        btninicarsesion = (Button) findViewById(R.id.btnIniciarSesion);
        btnsalir = (Button) findViewById(R.id.btnSalir);

        //Spinner tipo usuario
        spitipoUsuario = (Spinner) findViewById(R.id.spiTipoUsuario);
        adapter = ArrayAdapter.createFromResource(this, R.array.TipoUsuario, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spitipoUsuario.setAdapter(adapter);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        btninicarsesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                usuario = edtusuario.getText().toString().trim();
                password = edtpassword.getText().toString().trim();
                tipoUsuario = spitipoUsuario.getSelectedItem().toString().trim();

                if (modoConexion == "Online") {
                    showAlertDialogInternet(Login.this, "SIN CONEXIÓN",
                            "La aplicación requiere conexión a Internet para iniciar sesión...", true);
                }else{

                    if(!usuario.isEmpty()  && !password.isEmpty() ) {
                        if(spitipoUsuario.getSelectedItem().toString().trim().equals("Selecciona un tipo de usuario")){
                            View view = findViewById(android.R.id.content);
                            Snackbar snack = Snackbar.make(view, "Selecciona un tipo de usuario.", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null);
                            ViewGroup group = (ViewGroup) snack.getView();
                            group.setBackgroundColor(ContextCompat.getColor(Login.this, R.color.colorSnackBarError));
                            snack.show();
                        } else {

                            int permissionCheck = ContextCompat.checkSelfPermission(Login.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                            int permissionCheck2 = ContextCompat.checkSelfPermission(Login.this, Manifest.permission.CAMERA);

                            //VERIFICAR SI SE CONDECIDERON PERMISOS STORAGE
                            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                                requestPermissionWriteExternaStorage();
                            }else if (permissionCheck2 != PackageManager.PERMISSION_GRANTED) {
                                requestPermissionCamera();
                            }else {

                                //VERIFICA SI EL INICIO ES OFFLINE U ONLINE
                                if (!ConfiguracionCifrado.compruebaConexion(Login.this) || modoConexion == "Offline") {
                                    android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(Login.this);
                                    alertDialog.setIcon(R.drawable.atencion);
                                    alertDialog.setTitle("SIN CONEXIÓN");
                                    alertDialog.setMessage("Deseas cambiar el tipo de conexión...");
                                    alertDialog.setPositiveButton("Modo Online", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                                        }
                                    });
                                    alertDialog.setNegativeButton("Modo Offline", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                            @SuppressLint("WrongConstant")
                                            SharedPreferences sh = getApplicationContext().getSharedPreferences("MyPref", MODE_APPEND); // 0 - for private mode
                                            String userOffline = sh.getString("usuario", "").trim();
                                            String passOffline = sh.getString("pass", "").trim();
                                            String tipoOffline = sh.getString("tipo", "").trim();
                                            String idOffline = sh.getString("id", "").trim();


                                            if (userOffline.equals(usuario) && passOffline.equals(password) && tipoOffline.equals(tipoUsuario)) {

                                                Intent intent = new Intent(Login.this, MainActivity.class);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                intent.putExtra("usuarioLogin", idOffline);
                                                startActivity(intent);
                                                finish();

                                            } else {

                                                View view = findViewById(android.R.id.content);
                                                Snackbar snack = Snackbar.make(view, "Las credenciales son incorrectas, vuelve a intentarlo ", Snackbar.LENGTH_LONG)
                                                        .setAction("Action", null);
                                                ViewGroup group = (ViewGroup) snack.getView();
                                                group.setBackgroundColor(ContextCompat.getColor(Login.this, R.color.colorSnackBarError));
                                                snack.show();
                                                hideDialog();
                                            }

                                        }
                                    });
                                    alertDialog.show();

                                } else {
                                    checkLogin(usuario, tipoUsuario, password);
                                }
                            }//END ELSE PERMISOS STORAGE


                        }
                    }else{
                        Snackbar snack = Snackbar.make(v, "Ingresa tus credenciales", Snackbar.LENGTH_LONG)
                                .setAction("Action", null);
                        ViewGroup group = (ViewGroup) snack.getView();
                        group.setBackgroundColor(ContextCompat.getColor(Login.this, R.color.colorSnackBarError));
                        snack.show();
                    }
                }
            }
        });

        btnsalir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }


    public void showAlertDialogInternet(Context context, String title, String message, Boolean status) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Login.this);
        alertDialog.setIcon(R.drawable.atencion);
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        alertDialog.show();
    }
    //Termina la conexion a internet

    //Dialogo Exit
    @Override
    public void onBackPressed() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setMessage("¿Estás seguro de no iniciar sesión? ");
        builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        android.app.AlertDialog dialog = builder.create();
        dialog.show();
    }
    // Termina Dialogo Exit

    private void checkLogin(final String usuario, final String tipoUsuario, final String password) {

        // Tag used to cancel the request
        String tag_string_req = "req_login";

        pDialog.setMessage("Iniciando sesión ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                ConfiguracionCifrado.URL_LOGIN, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                hideDialog();
                //SHARE PEREFERENCES
                final String MyPREFERENCES = "MisPrefencias" ;

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    // Check for error node in json
                    if (!error) {
                        //OBTENEMOS VALORES DEL JSON
                        String str_value=jObj.getString("user");

                        JSONObject usuarioData = new JSONObject(str_value);
                        idUsuario=usuarioData.getString("id");

                        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
                        SharedPreferences.Editor editor = pref.edit();

                        editor.putString("usuario", usuario);
                        editor.putString("pass", password);
                        editor.putString("tipo", tipoUsuario);
                        editor.putString("id", idUsuario);
                        editor.commit();

                        Intent intent = new Intent(Login.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.putExtra("usuarioLogin", idUsuario);
                        startActivity(intent);
                        finish();

                    } else {
                        // Error in login. Get the error message
                        String errorMsg = jObj.getString("error_msg");
                        //Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();
                        View view = findViewById(android.R.id.content);
                        Snackbar snack = Snackbar.make(view, errorMsg, Snackbar.LENGTH_LONG)
                                .setAction("Action", null);
                        ViewGroup group = (ViewGroup) snack.getView();
                        group.setBackgroundColor(ContextCompat.getColor(Login.this, R.color.colorSnackBarError));
                        snack.show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    View view = findViewById(android.R.id.content);
                    Snackbar snack = Snackbar.make(view, "Error fatal, no se puede conectar con la base de datos", Snackbar.LENGTH_LONG)
                            .setAction("Action", null);
                    ViewGroup group = (ViewGroup) snack.getView();
                    group.setBackgroundColor(ContextCompat.getColor(Login.this, R.color.colorSnackBarError));
                    snack.show();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                //Si no hay conexion con el servidor
                View view = findViewById(android.R.id.content);
                Snackbar snack = Snackbar.make(view, "El servidor no esta disponible en este momento, intenta mas tarde! ", Snackbar.LENGTH_LONG)
                        .setAction("Action", null);
                ViewGroup group = (ViewGroup) snack.getView();
                group.setBackgroundColor(ContextCompat.getColor(Login.this, R.color.colorSnackBarError));
                snack.show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("group_user", tipoUsuario);
                params.put("nombre", usuario);
                params.put("password", password);
                return params;
            }
        };
        // Adding request to request queue
        GlobalesCifrado.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }


    // Procesar respuesta de usuario
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //Si el requestCode corresponde al que usamos para solicitar el permiso y
        //la respuesta del usuario fue positiva

        if (requestCode == MY_WRITE_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else {
                showSnackBarWriteExternaStorage();
            }
        }
        if (requestCode == MY_CAMERA ) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else {
                showSnackBarCamera();
            }
        }
    }

    private void requestPermissionWriteExternaStorage() {
        //shouldShowRequestPermissionRationale es verdadero solamente si ya se había mostrado
        //anteriormente el dialogo de permisos y el usuario lo negó
        if (ActivityCompat.shouldShowRequestPermissionRationale(Login.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            showSnackBarWriteExternaStorage();
        } else {
            //si es la primera vez se solicita el permiso directamente
            ActivityCompat.requestPermissions(Login.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_WRITE_EXTERNAL_STORAGE);
        }
    }
    private void requestPermissionCamera() {
        //shouldShowRequestPermissionRationale es verdadero solamente si ya se había mostrado
        //anteriormente el dialogo de permisos y el usuario lo negó
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
            showSnackBarCamera();
        } else {
            //si es la primera vez se solicita el permiso directamente
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                    MY_CAMERA);
        }
    }

    private void showSnackBarWriteExternaStorage() {
        Snackbar.make(mLayout, R.string.permission_write_storage,
                Snackbar.LENGTH_LONG)
                .setAction(R.string.settings, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        openSettings();
                    }
                })
                .show();
    }
    private void showSnackBarCamera() {
        Snackbar.make(mLayout, R.string.permission_camera,
                Snackbar.LENGTH_LONG)
                .setAction(R.string.settings, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        openSettings();
                    }
                })
                .show();
    }

    public void openSettings() {
        Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivity(intent);
    }

}
