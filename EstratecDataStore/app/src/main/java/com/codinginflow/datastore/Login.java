package com.codinginflow.datastore;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.codinginflow.datastore.Globales.ConfiguracionCifrado;
import com.codinginflow.datastore.Globales.GlobalesCifrado;
import com.codinginflow.datastore.issste.main_activity_issste;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class Login extends AppCompatActivity {

    private EditText edtusuario, edtpassword;
    private Button btninicarsesion, btnsalir;
    private ArrayAdapter<CharSequence> adapter, adaptercliente;
    private Spinner spitipoUsuario, spinCliente;
    private ProgressDialog pDialog;
    public static String idUsuario, modoConexion, usuario, password, tipoUsuario, tipoCliente, clienteSQL, estatusSQL;
    private static final int MY_WRITE_EXTERNAL_STORAGE = 0;
    private static final int MY_CAMERA = 0;
    private View mLayout;
    String NombrePackage;
    PackageInfo packageInformacion;
    PackageManager manager;


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

        //Spinner cliente
        spinCliente = (Spinner) findViewById(R.id.spiCliente);
        adaptercliente = ArrayAdapter.createFromResource(this, R.array.ClienteCaptura, android.R.layout.simple_spinner_item);
        adaptercliente.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinCliente.setAdapter(adaptercliente);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        manager = getPackageManager();
        NombrePackage = getPackageName();
        try {
            packageInformacion = manager.getPackageInfo(NombrePackage, 0);
            NombrePackage = packageInformacion.applicationInfo.dataDir;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        btninicarsesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                usuario = edtusuario.getText().toString().trim();
                password = edtpassword.getText().toString().trim();
                tipoUsuario = spitipoUsuario.getSelectedItem().toString().trim();
                tipoCliente = spinCliente.getSelectedItem().toString().trim();

                if (modoConexion == "Online") {
                    showAlertDialogInternet(Login.this, "SIN CONEXIÓN",
                            "La aplicación requiere conexión a Internet para iniciar sesión...", true);
                }else{

                    if(!usuario.isEmpty()  && !password.isEmpty() ) {
                        if(spitipoUsuario.getSelectedItem().toString().trim().equals("Selecciona un tipo de usuario")) {
                            View view = findViewById(android.R.id.content);
                            Snackbar snack = Snackbar.make(view, "Selecciona un tipo de usuario.", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null);
                            ViewGroup group = (ViewGroup) snack.getView();
                            group.setBackgroundColor(ContextCompat.getColor(Login.this, R.color.colorSnackBarError));
                            snack.show();
                        }
                        else if (spinCliente.getSelectedItem().toString().trim().equals("Selecciona un cliente")){
                            View view = findViewById(android.R.id.content);
                            Snackbar snack = Snackbar.make(view, "Selecciona un cliente.", Snackbar.LENGTH_LONG)
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
                                            // SEDENA
                                            if (tipoCliente.equals("SEDENA")){
                                                String userOffline = sh.getString("usuario", "").trim();
                                                String passOffline = sh.getString("pass", "").trim();
                                                String tipoOffline = sh.getString("tipo", "").trim();
                                                String idOffline = sh.getString("id", "").trim();
                                                String clienteOffline = sh.getString("cliente", "").trim();
                                                String estatusOffline = sh.getString("estatus", "").trim();

                                                if (!estatusOffline.equals("Inactivo")){
                                                    if (userOffline.equals(usuario) && passOffline.equals(password) && tipoOffline.equals(tipoUsuario) && clienteOffline.equals("SEDENA")) {


                                                        Intent intent = new Intent(Login.this, MainActivity.class);
                                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                        intent.putExtra("usuarioLogin", idOffline);
                                                        intent.putExtra("clienteLogin", tipoCliente);
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

                                                if (estatusOffline.equals("Inactivo")){
                                                    if (userOffline.equals(usuario) && passOffline.equals(password) && tipoOffline.equals(tipoUsuario) && clienteOffline.equals("SEDENA")) {

                                                        android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(Login.this);
                                                        alertDialog .setCancelable(false);
                                                        alertDialog .setIcon(R.drawable.error);
                                                        alertDialog .setTitle("ERROR DE INICIO DE SESIÓN");
                                                        alertDialog .setMessage("Lo sentimos pero ya no tienes acceso a esta aplicación ya que fue de manera temporal, te recomendamos desinstalar esta aplicación..., Gracias");
                                                        alertDialog .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                finish();
                                                            }
                                                        });
                                                        final android.app.AlertDialog alert = alertDialog .create();
                                                        alert.show();

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



                                            }//END IF SEDENA


                                            if (tipoCliente.equals("ISSSTE")){
                                                // ISSSTE
                                                String userOfflineISSSTE = sh.getString("usuarioISSSTE", "").trim();
                                                String passOfflineISSSTE = sh.getString("passISSSTE", "").trim();
                                                String tipoOfflineISSSTE = sh.getString("tipoISSSTE", "").trim();
                                                String idOfflineISSSTE = sh.getString("idISSSTE", "").trim();
                                                String clienteOfflineISSSTE = sh.getString("clienteISSSTE", "").trim();
                                                String estatusOfflineISSSTE = sh.getString("estatusISSSTE", "").trim();

                                                if (!estatusOfflineISSSTE.equals("Inactivo")){
                                                    if (userOfflineISSSTE.equals(usuario) && passOfflineISSSTE.equals(password) && tipoOfflineISSSTE.equals(tipoUsuario) && clienteOfflineISSSTE.equals("ISSSTE")) {

                                                        Intent intent = new Intent(Login.this, main_activity_issste.class);
                                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                        intent.putExtra("usuarioLogin", idOfflineISSSTE);
                                                        intent.putExtra("clienteLogin", tipoCliente);
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

                                                if (estatusOfflineISSSTE.equals("Inactivo")){
                                                    if (userOfflineISSSTE.equals(usuario) && passOfflineISSSTE.equals(password) && tipoOfflineISSSTE.equals(tipoUsuario) && clienteOfflineISSSTE.equals("ISSSTE")) {

                                                        android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(Login.this);
                                                        alertDialog .setCancelable(false);
                                                        alertDialog .setIcon(R.drawable.error);
                                                        alertDialog .setTitle("ERROR DE INICIO DE SESIÓN");
                                                        alertDialog .setMessage("Lo sentimos pero ya no tienes acceso a esta aplicación ya que fue de manera temporal, te recomendamos desinstalar esta aplicación..., Gracias");
                                                        alertDialog .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                finish();
                                                            }
                                                        });
                                                        final android.app.AlertDialog alert = alertDialog .create();
                                                        alert.show();


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
                        clienteSQL = usuarioData.getString("cliente");
                        estatusSQL = usuarioData.getString("estatus");

                        Log.d(null, "CLIENTE: " + clienteSQL);


                        // VERIFICA SI ESTA ACTIVO EN LA BASE DE DATOS
                        if (!estatusSQL.equals("Inactivo")){

                            if (tipoCliente.matches("SEDENA") && clienteSQL.equals("SEDENA")) {

                                Log.d(null, "IF SEDENA ACTIVO: ");

                                SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
                                SharedPreferences.Editor editor = pref.edit();

                                editor.putString("usuario", usuario);
                                editor.putString("pass", password);
                                editor.putString("tipo", tipoUsuario);
                                editor.putString("id", idUsuario);
                                editor.putString("cliente", tipoCliente);
                                editor.putString("estatus", estatusSQL);
                                editor.commit();


                                Intent intent = new Intent(Login.this, MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                intent.putExtra("usuarioLogin", idUsuario);
                                intent.putExtra("clienteLogin", tipoCliente);
                                startActivity(intent);
                                finish();


                            }else{
                                // Error in login. Get the error message
                                View view = findViewById(android.R.id.content);
                                Snackbar snack = Snackbar.make(view, "No tienes acceso al cliente SEDENA", Snackbar.LENGTH_LONG)
                                        .setAction("Action", null);
                                ViewGroup group = (ViewGroup) snack.getView();
                                group.setBackgroundColor(ContextCompat.getColor(Login.this, R.color.colorSnackBar));
                                snack.show();

                            }

                            if (tipoCliente.matches("ISSSTE") && clienteSQL.matches("ISSSTE")) {
                                Log.d(null, "IF SEDENA ACTIVO: ");

                                SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
                                SharedPreferences.Editor editor = pref.edit();

                                editor.putString("usuarioISSSTE", usuario);
                                editor.putString("passISSSTE", password);
                                editor.putString("tipoISSSTE", tipoUsuario);
                                editor.putString("idISSSTE", idUsuario);
                                editor.putString("clienteISSSTE", tipoCliente);
                                editor.putString("estatusISSSTE", estatusSQL);
                                editor.commit();


                                Intent intent = new Intent(Login.this, main_activity_issste.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                intent.putExtra("usuarioLogin", idUsuario);
                                intent.putExtra("clienteLogin", tipoCliente);
                                startActivity(intent);
                                finish();

                            }else{
                                // Error in login. Get the error message
                                View view = findViewById(android.R.id.content);
                                Snackbar snack = Snackbar.make(view, "No tienes acceso al cliente ISSSTE", Snackbar.LENGTH_LONG)
                                        .setAction("Action", null);
                                ViewGroup group = (ViewGroup) snack.getView();
                                group.setBackgroundColor(ContextCompat.getColor(Login.this, R.color.colorSnackBar));
                                snack.show();

                            }

                            if ((tipoCliente.matches("SEDENA") && clienteSQL.matches("ISSSTE")) || (tipoCliente.matches("ISSSTE") && clienteSQL.matches("SEDENA"))) {
                                // Error in login. Get the error message
                                View view = findViewById(android.R.id.content);
                                Snackbar snack = Snackbar.make(view, "No tienes acceso a este cliente", Snackbar.LENGTH_LONG)
                                        .setAction("Action", null);
                                ViewGroup group = (ViewGroup) snack.getView();
                                group.setBackgroundColor(ContextCompat.getColor(Login.this, R.color.colorSnackBar));
                                snack.show();
                            }

                        }//END IF ACTIVOS

                        if (estatusSQL.equals("Inactivo")){

                            if (tipoCliente.matches("SEDENA") && clienteSQL.equals("SEDENA")) {

                                SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
                                SharedPreferences.Editor editor = pref.edit();

                                editor.putString("usuario", usuario);
                                editor.putString("pass", password);
                                editor.putString("tipo", tipoUsuario);
                                editor.putString("id", idUsuario);
                                editor.putString("cliente", tipoCliente);
                                editor.putString("estatus", estatusSQL);
                                editor.commit();

                                //ELIMINAR BD SEDENA
                                String rutaLogo = NombrePackage + File.separator + "databases/bd_series_sedena";
                                File directorioFUAS = new File(rutaLogo);
                                if (directorioFUAS.exists()){
                                    EliminarBD(directorioFUAS);
                                }

                                // Error in login. Get the error message
                                android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(Login.this);
                                alertDialog .setCancelable(false);
                                alertDialog .setIcon(R.drawable.error);
                                alertDialog .setTitle("ERROR DE INICIO DE SESIÓN");
                                alertDialog .setMessage("Lo sentimos pero ya no tienes acceso a esta aplicación ya que fue de manera temporal, te recomendamos desinstalar esta aplicación..., Gracias");
                                alertDialog .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        finish();
                                    }
                                });
                                final android.app.AlertDialog alert = alertDialog .create();
                                alert.show();
                            }

                            if (tipoCliente.matches("ISSSTE") && clienteSQL.equals("ISSSTE")) {

                                SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
                                SharedPreferences.Editor editor = pref.edit();

                                editor.putString("usuarioISSSTE", usuario);
                                editor.putString("passISSSTE", password);
                                editor.putString("tipoISSSTE", tipoUsuario);
                                editor.putString("idISSSTE", idUsuario);
                                editor.putString("clienteISSSTE", tipoCliente);
                                editor.putString("estatusISSSTE", estatusSQL);
                                editor.commit();

                                //ELIMINA BD ISSSTE
                                String rutaLogo = NombrePackage + File.separator + "databases/bd_series_issste";
                                File directorioFUAS = new File(rutaLogo);
                                if (directorioFUAS.exists()){
                                    EliminarBD(directorioFUAS);
                                }

                                android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(Login.this);
                                alertDialog .setCancelable(false);
                                alertDialog .setIcon(R.drawable.error);
                                alertDialog .setTitle("ERROR DE INICIO DE SESIÓN");
                                alertDialog .setMessage("Lo sentimos pero ya no tienes acceso a esta aplicación ya que fue de manera temporal, te recomendamos desinstalar esta aplicación..., Gracias");
                                alertDialog .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                       finish();
                                    }
                                });
                                final android.app.AlertDialog alert = alertDialog .create();
                                alert.show();
                            }

                            if (!tipoCliente.matches(clienteSQL)){
                                // Error in login. Get the error message
                                View view = findViewById(android.R.id.content);
                                Snackbar snack = Snackbar.make(view, "Verifica los datos ingresados", Snackbar.LENGTH_LONG)
                                        .setAction("Action", null);
                                ViewGroup group = (ViewGroup) snack.getView();
                                group.setBackgroundColor(ContextCompat.getColor(Login.this, R.color.colorSnackBar));
                                snack.show();
                            }

                        }//EN IF INACTIVO


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

    void EliminarBD(File ArchivoDirectorio) {
        if (ArchivoDirectorio.isDirectory()) {
            for (File hijo : ArchivoDirectorio.listFiles())
                EliminarBD(hijo);
        } else
            ArchivoDirectorio.delete();
    }



}
