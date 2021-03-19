package com.codinginflow.datastore.issste;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.codinginflow.datastore.Globales.ConfiguracionCifrado;
import com.codinginflow.datastore.MainActivity;
import com.codinginflow.datastore.R;
import com.codinginflow.datastore.Login;
import com.codinginflow.datastore.sedena.SQLiteHelper;
import com.codinginflow.datastore.sedena.ShowDataActivityCompleto;
import com.codinginflow.datastore.sedena.SubirDatosGodaddy;
import com.google.android.material.snackbar.Snackbar;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.net.ssl.HttpsURLConnection;

public class main_activity_issste extends AppCompatActivity {
    String tecnicoReceibe,ResultadoLinea = null,ProgressMessage,NombrePackage,idSerie,SerieBD,clienteBD,modeloBD,tipoEquipoBD,unidadAdministrativaBD,areaAdscripcionBD,calleNumeroBD,pisoBD,coloniaBD,CPBD,ciudadBD,estadoBD,nombreBD,puestoBD,noEmpleadoBD,noRedBD,emailBD,fechaBD,folioBD,observacionesBD,URLFormatoBD,URLContadoresBD,estatusBD, idUnidadAdmBD, NombreEnlaceBD;
    Boolean verificaConexion;
    TextView identificador;
    Button btnsincronizar, btnSeriesPendientes, btnCompletos, btnGuardarGodaddy;
    SQLiteDatabase sqLiteDatabase;
    ProgressDialog progressDialog;
    URL url,urlSync,UFormato,UContadores,UConfiguracion;
    BufferedReader bufferedReaderDescarga;
    View viewSnack;
    Snackbar snackBar;
    ViewGroup groupSnack;
    PackageManager manager;
    PackageInfo packageInformacion;
    main_activity_issste.StoreJSonDataInToSQLiteClassISSSTE task;
    int contadorAsync = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_issste);
        setTitle("ISSSTE");
        Bundle extra = this.getIntent().getExtras();
        tecnicoReceibe = extra.getString("usuarioLogin");

        identificador = findViewById(R.id.idTecnicoISSSTE);
        identificador.setText(tecnicoReceibe);

        //BOTONES DE LA INTERFAZ PRINCIPAL
        btnSeriesPendientes = findViewById(R.id.seriesPendientesISSSTE);
        btnCompletos = findViewById(R.id.historialCompletosISSSTE);
        btnsincronizar = findViewById(R.id.btnSincronizarISSSTE);
        btnGuardarGodaddy = findViewById(R.id.btnGuardarGodaddyISSSTE);

        //BOTON PARA IMPORTAR DATOS MYSQL
        btnsincronizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //COMPROBAR CONEXXION A INTERNET
                verificaConexion = ConfiguracionCifrado.isOnlineNet();
                if (verificaConexion == true) {

                    //ABRIR BASE DE DATOS SQLITE
                    SQLiteDataBaseBuild();
                    //CREAR TABLAS E INSERCION DE DATOS
                    SQLiteTableBuild();

                    task=new main_activity_issste.StoreJSonDataInToSQLiteClassISSSTE(main_activity_issste.this);

                    if(task.getStatus() == AsyncTask.Status.PENDING){
                        // My AsyncTask has not started yet
                        Log.d(null, "ASYNCTASK PENDIENTE: ");
                        task.execute();
                        contadorAsync ++;
                        Log.d(null, "TASK: "+contadorAsync);
                    }

                    if(task.getStatus() == AsyncTask.Status.RUNNING){
                        // My AsyncTask is currently doing work in doInBackground()
                        Log.d(null, "ASYNCTASK EJECUTANDO: ");
                    }


                }else {

                    showAlertDialogInternet(main_activity_issste.this, "SIN CONEXIÓN",
                            "La aplicación requiere conexión a Internet para sincronizar tus series con la base de datos...", true);

                }
            }
        });



        btnSeriesPendientes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(main_activity_issste.this, ShowDataActivityISSSTE.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra("usuarioLogin", tecnicoReceibe);
                startActivity(intent);

            }
        });

        btnCompletos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(main_activity_issste.this, ShowDataActivityCompletoISSSTE.class);
                startActivity(intent);

            }
        });

        btnGuardarGodaddy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(main_activity_issste.this, SubirDatosGodaddyISSSTE.class);
                startActivity(intent);

            }
        });


    }


    //Dialogo Exit
    @Override
    public void onBackPressed() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setMessage("¿Estás seguro de cerrar la sesión? ");
        builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(main_activity_issste.this, Login.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
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

    private class StoreJSonDataInToSQLiteClassISSSTE extends AsyncTask<Void, Void, String> {

        public Context context;

        public StoreJSonDataInToSQLiteClassISSSTE(Context context) {

            this.context = context;
        }


        @Override
        protected void onPreExecute() {

            super.onPreExecute();

            try{
                if (contadorAsync >= 1){
                    Log.d(null, "ASYNK CANCELADO: ");
                    task.cancel(true);
                    task.isCancelled();
                }else{
                    //PROGRESS DE SINCRONIZACION
                    progressDialog = new ProgressDialog(main_activity_issste.this);
                    progressDialog.setTitle("SINCRONIZANDO");
                    progressDialog.setMessage("Espera a que se sincronize el listado");
                    progressDialog.setCancelable(false);
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();
                }

            }catch(Exception e){
                //something is wrong , async task may crash , show a dialog or error
            }


        }


        @RequiresApi(api = Build.VERSION_CODES.O)
        @SuppressLint("WrongThread")
        @Override
        protected String doInBackground(Void... arg0) {

            //CONEXION POST A BASE DE DATOS
            verificaConexion = ConfiguracionCifrado.isOnlineNet();
            if (verificaConexion == true){

                try {
                    //URLS
                    url = new URL(ConfiguracionCifrado.URL_CONEXION_SYNC_ISSSTE); //URL VERIFICAR SERIES A SINCRONIZAR
                    urlSync = new URL(ConfiguracionCifrado.URL_SYN_SERIES_ISSSTE); //URL CAMBIAR ESTATUS DE SERIES

                    //PETICION POST DESCARGA DE SERIES DISPONIBLES
                    bufferedReaderDescarga = envioPOST(url, tecnicoReceibe, "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "");
                    ResultadoLinea = bufferedReaderDescarga.readLine();

                    if (ResultadoLinea == null)
                    {
                        viewSnack = findViewById(android.R.id.content);
                        snackBar = Snackbar.make(viewSnack, "No hay series a sincronizar", Snackbar.LENGTH_LONG)
                                .setAction("Action", null);
                        groupSnack = (ViewGroup) snackBar.getView();
                        groupSnack.setBackgroundColor(ContextCompat.getColor(main_activity_issste.this, R.color.colorSnackBarError));
                        snackBar.show();
                    }
                    else{

                        try {
                            JSONArray jsonArray = new JSONArray(ResultadoLinea);

                            JSONObject jsonObject;
                            int cantidadRegistros = jsonArray.length();


                            for (int i = 0; i < cantidadRegistros; i++) {

                                ProgressMessage = "SINCRONIZANDO " + (i+1) + " DE " + cantidadRegistros;
                                runOnUiThread(changeMessage);

                                jsonObject = jsonArray.getJSONObject(i);

                                //JSON OBTENIDOS DE BASE DE DATOS
                                idSerie = jsonObject.getString("id");
                                SerieBD = jsonObject.getString("Serie");
                                clienteBD = jsonObject.getString("Cliente");
                                modeloBD = jsonObject.getString("Modelo");
                                tipoEquipoBD = jsonObject.getString("TipoEquipo");
                                unidadAdministrativaBD = jsonObject.getString("UnidadAdministrativa");
                                areaAdscripcionBD = jsonObject.getString("AreaAdscripcion");
                                calleNumeroBD = jsonObject.getString("CalleNumero");
                                pisoBD = jsonObject.getString("Piso");
                                coloniaBD = jsonObject.getString("Colonia");
                                CPBD = jsonObject.getString("CP");
                                ciudadBD = jsonObject.getString("Ciudad");
                                estadoBD = jsonObject.getString("Estado");
                                nombreBD = jsonObject.getString("Nombre");
                                puestoBD = jsonObject.getString("Puesto");
                                noEmpleadoBD = jsonObject.getString("NoEmpleado");
                                noRedBD = jsonObject.getString("NoRed");
                                emailBD = jsonObject.getString("Email");
                                fechaBD = jsonObject.getString("Fecha");
                                folioBD = jsonObject.getString("Folio");
                                observacionesBD = jsonObject.getString("Observaciones");
                                URLFormatoBD = jsonObject.getString("URLFormato");
                                URLContadoresBD = jsonObject.getString("URLContadores");
                                estatusBD = jsonObject.getString("Estatus");
                                idUnidadAdmBD = jsonObject.getString("idUnidad");
                                NombreEnlaceBD = jsonObject.getString("NombreEnlace");


                                //OBTENER DATOS DEL PAQUETE INSTALADO
                                manager = getPackageManager();
                                NombrePackage = getPackageName();
                                packageInformacion = manager.getPackageInfo(NombrePackage, 0);
                                NombrePackage = packageInformacion.applicationInfo.dataDir;

                                if (!modeloBD.equals("") && !estatusBD.equals("Revision")) {
                                    //PETICION POST PARA SINCRONIZAR MANUALES CAMBIO ESTATUS
                                    BufferedReader brSync = envioPOST(urlSync, tecnicoReceibe, idSerie, "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "","", "", "", "", "", "");

                                    Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + SQLiteHelperISSSTE.TABLE_NAME + " WHERE Serie ='" + SerieBD + "'", null);
                                    int contador = 0;

                                    //VERIFICA SI LA SERIE YA EXISTE
                                    if (cursor.moveToFirst()) {
                                        do {
                                            contador++;
                                        } while (cursor.moveToNext());
                                    }

                                    android.util.Log.d(null, "CONTADOR: "+contador);

                                    //ACCION DE SQLITE ANTE LA CONSULTA DE BUSQUEDA
                                    if (contador == 0){
                                        //QUERY INSERCION DE DATOS OBTENIDOS DE MYSQL JSON A SQLITE
                                        String SQLiteDataBaseQueryHolder = "INSERT INTO " + SQLiteHelperISSSTE.TABLE_NAME + " (Serie,Cliente,Modelo,TipoEquipo,UnidadAdministrativa,AreaAdscripcion,CalleNumero,Piso,Colonia,CP,Ciudad,Estado,Nombre,Puesto,NoEmpleado,NoRed,Email,Fecha,Folio,Observaciones,URLFormato,URLContadores,Estatus, idUnidad, NombreEnlace) VALUES('" + SerieBD + "', '" + clienteBD + "','" + modeloBD + "','" + tipoEquipoBD + "','" + unidadAdministrativaBD + "','" + areaAdscripcionBD + "','" + calleNumeroBD + "','" + pisoBD + "','" + coloniaBD + "','"+ CPBD + "','"+ ciudadBD + "','" + estadoBD + "','" + nombreBD + "','" + puestoBD + "','" + noEmpleadoBD + "','" + noRedBD + "','"+ emailBD + "','"+ fechaBD + "','"+ folioBD + "','"+ observacionesBD + "','" + URLFormatoBD + "','" + URLContadoresBD + "','" + estatusBD + "','" + idUnidadAdmBD + "','"+NombreEnlaceBD+"');";
                                        sqLiteDatabase.execSQL(SQLiteDataBaseQueryHolder);
                                    }else{
                                        //QUERY UPDATE DE DATOS OBTENIDOS DE MYSQL JSON A SQLITE
                                        String SQLiteDataBaseQueryHolder = "UPDATE " + SQLiteHelperISSSTE.TABLE_NAME + " SET Serie='"+SerieBD+"', Cliente='"+clienteBD+"', Modelo='"+modeloBD+"', TipoEquipo='"+tipoEquipoBD+"', UnidadAdministrativa='"+unidadAdministrativaBD+"', AreaAdscripcion='"+areaAdscripcionBD+"', CalleNumero='"+calleNumeroBD+"', Piso='"+pisoBD+"', Colonia='"+coloniaBD+"', CP='"+CPBD+"', Ciudad='"+ciudadBD+"', Estado='"+estadoBD+"', Fecha='"+fechaBD+"', Folio='"+folioBD+"', Observaciones='"+observacionesBD+"', idUnidad='"+idUnidadAdmBD+"', Estatus='"+estatusBD+"', NombreEnlace='"+NombreEnlaceBD+"' WHERE Serie = '" + SerieBD + "'";
                                        sqLiteDatabase.execSQL(SQLiteDataBaseQueryHolder);
                                    }

                                    cursor.close();


                                }

                                if (estatusBD.equals("Revision")) {

                                    //CAMBIO TEXTO PROGRESS REVOCADO DE MANUALES
                                    ProgressMessage = "ACTIVANDO SERIE " + idSerie;
                                    runOnUiThread(changeMessage);

                                    BufferedReader brSync = envioPOST(urlSync, tecnicoReceibe, idSerie, "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "","", "", "", "", "", "");

                                    Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + SQLiteHelperISSSTE.TABLE_NAME + " WHERE Serie ='" + SerieBD + "'", null);
                                    int contador = 0;

                                    //VERIFICA SI LA SERIE YA EXISTE
                                    if (cursor.moveToFirst()) {
                                        do {
                                            contador++;
                                        } while (cursor.moveToNext());
                                    }

                                    //ACCION DE SQLITE ANTE LA CONSULTA DE BUSQUEDA
                                    if (contador == 0){
                                        //QUERY INSERCION DE DATOS OBTENIDOS DE MYSQL JSON A SQLITE
                                        String SQLiteDataBaseQueryHolder = "INSERT INTO " + SQLiteHelperISSSTE.TABLE_NAME + " (Serie,Cliente,Modelo,TipoEquipo,UnidadAdministrativa,AreaAdscripcion,CalleNumero,Piso,Colonia,CP,Ciudad,Estado,Nombre,Puesto,NoEmpleado,NoRed,Email,Fecha,Folio,Observaciones,URLFormato,URLContadores,Estatus, idUnidad, NombreEnlace) VALUES('" + SerieBD + "', '" + clienteBD + "','" + modeloBD + "','" + tipoEquipoBD + "','" + unidadAdministrativaBD + "','" + areaAdscripcionBD + "','" + calleNumeroBD + "','" + pisoBD + "','" + coloniaBD + "','"+ CPBD + "','"+ ciudadBD + "','" + estadoBD + "','" + nombreBD + "','" + puestoBD + "','" + noEmpleadoBD + "','" + noRedBD + "','"+ emailBD + "','"+ fechaBD + "','"+ folioBD + "','"+ observacionesBD + "','" + URLFormatoBD + "','" + URLContadoresBD + "','" + estatusBD + "','" + idUnidadAdmBD + "','"+NombreEnlaceBD+"');";
                                        sqLiteDatabase.execSQL(SQLiteDataBaseQueryHolder);
                                    }else{
                                        //QUERY UPDATE DE DATOS OBTENIDOS DE MYSQL JSON A SQLITE
                                        String SQLiteDataBaseQueryHolder = "UPDATE " + SQLiteHelperISSSTE.TABLE_NAME + " SET Serie='"+SerieBD+"', Cliente='"+clienteBD+"', Modelo='"+modeloBD+"', TipoEquipo='"+tipoEquipoBD+"', UnidadAdministrativa='"+unidadAdministrativaBD+"', AreaAdscripcion='"+areaAdscripcionBD+"', CalleNumero='"+calleNumeroBD+"', Piso='"+pisoBD+"', Colonia='"+coloniaBD+"', CP='"+CPBD+"', Ciudad='"+ciudadBD+"', Estado='"+estadoBD+"', Fecha='"+fechaBD+"', Folio='"+folioBD+"', Observaciones='"+observacionesBD+"', idUnidad='"+idUnidadAdmBD+"', Estatus='', URLFormato='', URLContadores='', NombreEnlace='"+NombreEnlaceBD+"' WHERE Serie = '" + SerieBD + "' AND Estatus='Completo'";
                                        sqLiteDatabase.execSQL(SQLiteDataBaseQueryHolder);
                                    }

                                    cursor.close();


                                }


                            }//END FOR JSON

                            String rutaLogo = NombrePackage + File.separator + "mis_archivos_issste";
                            File directorioFUAS = new File(rutaLogo);
                            if (directorioFUAS.exists()){
                                EliminarFUAS(directorioFUAS);
                            }


                            sendNotification("Sincronizacion completa");



                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                    }




                } catch (Exception e) {
                    ResultadoLinea = "Error Captch";
                    e.printStackTrace();
                }
                return ResultadoLinea;

            }//END IF VERIFICA CONEXION
            else{
                ResultadoLinea = "Sin conexion";
                progressDialog.dismiss();

                return ResultadoLinea;
            }

        }

        @Override
        protected void onPostExecute(String result)
        {

            String resultadoResultDoInBack;

            if (result == null){
                resultadoResultDoInBack = "Vacio";
            }
            else{
                resultadoResultDoInBack = result;
            }


            switch (resultadoResultDoInBack){
                case "Vacio" :
                    sqLiteDatabase.close();
                    progressDialog.dismiss();
                    contadorAsync = 0;
                    viewSnack = findViewById(android.R.id.content);
                    snackBar = Snackbar.make(viewSnack, "No tienes series asignadas", Snackbar.LENGTH_LONG)
                            .setAction("Action", null);
                    groupSnack = (ViewGroup) snackBar.getView();
                    groupSnack.setBackgroundColor(ContextCompat.getColor(main_activity_issste.this, R.color.colorSnackBarError));
                    snackBar.show();
                    break;
                case "Sin conexion" :
                    sqLiteDatabase.close();
                    progressDialog.dismiss();
                    contadorAsync = 0;
                    viewSnack = findViewById(android.R.id.content);
                    snackBar = Snackbar.make(viewSnack, "La conexión a internet es lenta o inestable", Snackbar.LENGTH_LONG)
                            .setAction("Action", null);
                    groupSnack = (ViewGroup) snackBar.getView();
                    groupSnack.setBackgroundColor(ContextCompat.getColor(main_activity_issste.this, R.color.colorSnackBar));
                    snackBar.show();
                    break;
                case "Error Captch" :
                    sqLiteDatabase.close();
                    progressDialog.dismiss();
                    contadorAsync = 0;
                    viewSnack = findViewById(android.R.id.content);
                    snackBar = Snackbar.make(viewSnack, "Error en el sistema", Snackbar.LENGTH_LONG)
                            .setAction("Action", null);
                    groupSnack = (ViewGroup) snackBar.getView();
                    groupSnack.setBackgroundColor(ContextCompat.getColor(main_activity_issste.this, R.color.colorSnackBar));
                    snackBar.show();
                    break;
                default:
                    sqLiteDatabase.close();
                    progressDialog.dismiss();
                    contadorAsync = 0;
                    viewSnack = findViewById(android.R.id.content);
                    snackBar = Snackbar.make(viewSnack, "Se sincronizaron correctamente tus series asignadas", Snackbar.LENGTH_LONG)
                            .setAction("Action", null);
                    groupSnack = (ViewGroup) snackBar.getView();
                    groupSnack.setBackgroundColor(ContextCompat.getColor(main_activity_issste.this, R.color.colorSnackBarError));
                    snackBar.show();
                    break;
            }


        }
    }

    //PROCESAR PARAMETROS DE LA PETICION POST A MYSQL
    private static String getQuery(List<NameValuePair> params)  throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        for (NameValuePair pair : params)
        {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(pair.getName(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
        }

        return result.toString();
    }

    public void SQLiteDataBaseBuild(){
        //ABRIR BASE DE DATOS SQLITE
        sqLiteDatabase = openOrCreateDatabase(SQLiteHelperISSSTE.DATABASE_NAME, Context.MODE_PRIVATE, null);

    }

    public void SQLiteTableBuild(){
        //CREAR EN CASO DE NO EXISTIT LA TABLA MANUALES
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS "+SQLiteHelperISSSTE.TABLE_NAME+"("+SQLiteHelperISSSTE.Table_Column_ID+" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "+SQLiteHelperISSSTE.Table_Column_1_Serie+" TEXT, "+SQLiteHelperISSSTE.Table_Column_2_Cliente+" TEXT, "+SQLiteHelperISSSTE.Table_Column_3_Modelo+" TEXT, "+SQLiteHelperISSSTE.Table_Column_4_TipoEquipo+" TEXT, "+SQLiteHelperISSSTE.Table_Column_5_UnidadAdministrativa+" TEXT, "+SQLiteHelperISSSTE.Table_Column_6_AreaAdscripcion+" TEXT, "+SQLiteHelperISSSTE.Table_Column_7_CalleNumero+" TEXT, "+SQLiteHelperISSSTE.Table_Column_8_Piso+" TEXT, "+SQLiteHelperISSSTE.Table_Column_9_Colonia+" TEXT, "+SQLiteHelperISSSTE.Table_Column_10_CP+" TEXT, "+SQLiteHelperISSSTE.Table_Column_11_Ciudad+" TEXT, "+SQLiteHelperISSSTE.Table_Column_12_Estado+" TEXT, "+SQLiteHelperISSSTE.Table_Column_13_Nombre+" TEXT, " +SQLiteHelperISSSTE.Table_Column_13_Puesto+" TEXT, " +SQLiteHelperISSSTE.Table_Column_14_NoEmpleado+" TEXT, "+SQLiteHelperISSSTE.Table_Column_15_NoRed+" TEXT, "+SQLiteHelperISSSTE.Table_Column_16_Email+" TEXT, "+SQLiteHelperISSSTE.Table_Column_17_Fecha+" TEXT, "+SQLiteHelperISSSTE.Table_Column_18_Folio+" TEXT, "+SQLiteHelperISSSTE.Table_Column_19_Observaciones+" TEXT, "+SQLiteHelperISSSTE.Table_Column_20_URLFormato+" TEXT, "+SQLiteHelperISSSTE.Table_Column_21_URLContadores+" TEXT, "+SQLiteHelperISSSTE.Table_Column_22_Estatus+" TEXT, "+SQLiteHelperISSSTE.Table_Column_23_idUnidad+" TEXT, "+SQLiteHelperISSSTE.Table_Column_24_NombreEnlace+" TEXT);");
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS "+SQLiteHelperISSSTE.TABLE_NAME_HISTORIAL+"("+SQLiteHelperISSSTE.Table_Column_ID+" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "+SQLiteHelperISSSTE.Table_Column_1_Serie+" TEXT, "+SQLiteHelperISSSTE.Table_Column_2_Cliente+" TEXT, "+SQLiteHelperISSSTE.Table_Column_3_Modelo+" TEXT, "+SQLiteHelperISSSTE.Table_Column_4_TipoEquipo+" TEXT, "+SQLiteHelperISSSTE.Table_Column_5_UnidadAdministrativa+" TEXT, "+SQLiteHelperISSSTE.Table_Column_6_AreaAdscripcion+" TEXT, "+SQLiteHelperISSSTE.Table_Column_7_CalleNumero+" TEXT, "+SQLiteHelperISSSTE.Table_Column_8_Piso+" TEXT, "+SQLiteHelperISSSTE.Table_Column_9_Colonia+" TEXT, "+SQLiteHelperISSSTE.Table_Column_10_CP+" TEXT, "+SQLiteHelperISSSTE.Table_Column_11_Ciudad+" TEXT, "+SQLiteHelperISSSTE.Table_Column_12_Estado+" TEXT, "+SQLiteHelperISSSTE.Table_Column_13_Nombre+" TEXT, " +SQLiteHelperISSSTE.Table_Column_13_Puesto+" TEXT, "+SQLiteHelperISSSTE.Table_Column_14_NoEmpleado+" TEXT, "+SQLiteHelperISSSTE.Table_Column_15_NoRed+" TEXT, "+SQLiteHelperISSSTE.Table_Column_16_Email+" TEXT, "+SQLiteHelperISSSTE.Table_Column_17_Fecha+" TEXT, "+SQLiteHelperISSSTE.Table_Column_18_Folio+" TEXT, "+SQLiteHelperISSSTE.Table_Column_19_Observaciones+" TEXT, "+SQLiteHelperISSSTE.Table_Column_20_URLFormato+" TEXT, "+SQLiteHelperISSSTE.Table_Column_21_URLContadores+" TEXT, "+SQLiteHelperISSSTE.Table_Column_22_Estatus+" TEXT," +SQLiteHelperISSSTE.Table_Column_23_idUnidad+" TEXT, "+SQLiteHelperISSSTE.Table_Column_24_NombreEnlace+" TEXT);");
    }

    private Runnable changeMessage = new Runnable() {
        @Override
        public void run() {

            progressDialog.setMessage(ProgressMessage);
        }
    };

    public static BufferedReader envioPOST(URL url, String tecnicoID, String Serie, String Cliente, String Modelo, String TipoEquipo, String UnidadAdmin, String AreaAdsc, String CalleNumero, String Piso, String Colonia, String CP, String Ciudad, String Estado, String NombreUser, String Puesto, String NoEmpleado, String NoRed, String Email, String Fecha, String Folio, String Observaciones, String UrlFua, String UrlContador, String Estatus, String NombreEnlace) throws IOException {

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestProperty("Content-Encoding", "gzip"); //COMPRESION GZIP
        conn.setRequestMethod("POST");
        conn.setDoInput(true);
        conn.setDoOutput(true);
        conn.connect();
        //AGREGAR DATOS DE LA CONSULTA POST
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tecnicoID", tecnicoID));
        params.add(new BasicNameValuePair("Serie", Serie));
        params.add(new BasicNameValuePair("Cliente", Cliente));
        params.add(new BasicNameValuePair("Modelo", Modelo));
        params.add(new BasicNameValuePair("TipoEquipo", TipoEquipo));
        params.add(new BasicNameValuePair("UnidadAdmin", UnidadAdmin));
        params.add(new BasicNameValuePair("AreaAdsc", AreaAdsc));
        params.add(new BasicNameValuePair("CalleNumero", CalleNumero));
        params.add(new BasicNameValuePair("Piso", Piso));
        params.add(new BasicNameValuePair("Colonia", Colonia));
        params.add(new BasicNameValuePair("CP", CP));
        params.add(new BasicNameValuePair("Ciudad", Ciudad));
        params.add(new BasicNameValuePair("Estado", Estado));
        params.add(new BasicNameValuePair("NombreUser", NombreUser));
        params.add(new BasicNameValuePair("Puesto", Puesto));
        params.add(new BasicNameValuePair("NoEmpleado", NoEmpleado));
        params.add(new BasicNameValuePair("NoRed", NoRed));
        params.add(new BasicNameValuePair("Email", Email));
        params.add(new BasicNameValuePair("Fecha", Fecha));
        params.add(new BasicNameValuePair("Folio", Folio));
        params.add(new BasicNameValuePair("Observaciones", Observaciones));
        params.add(new BasicNameValuePair("UrlFua", UrlFua));
        params.add(new BasicNameValuePair("UrlContador", UrlContador));
        params.add(new BasicNameValuePair("Estatus", Estatus));
        params.add(new BasicNameValuePair("NombreEnlace", NombreEnlace));


        OutputStream os = conn.getOutputStream();
        BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(os, "UTF-8"));
        writer.write(getQuery(params));
        writer.flush();
        writer.close();
        os.close();

        try{
            int respuesta = conn.getResponseCode();
            if (respuesta == HttpsURLConnection.HTTP_OK) {
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                conn.disconnect();
                return br;
            }
            else{
                conn.disconnect();
                return null;
            }
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }


    }



    public void showAlertDialogInternet(Context context, String title, String message, Boolean status) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(main_activity_issste.this);
        alertDialog.setIcon(R.drawable.atencion);
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        alertDialog.show();
    }

    private void sendNotification(String message) {

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("default","Channel name", NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("Channel description");
            notificationManager.createNotificationChannel(channel);
        }
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(), "default");

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.putExtra("usuarioLogin", tecnicoReceibe);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder.setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Estratec Data Store")
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setContentText(message)
                .setContentIntent(pendingIntent);
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(getRandomNumber(0,1000), mBuilder.build());

    }

    private int getRandomNumber(int min,int max) {
        return (new Random()).nextInt((max - min) + 1) + min;
    }

    void EliminarFUAS(File ArchivoDirectorio) {
        if (ArchivoDirectorio.isDirectory()) {
            for (File hijo : ArchivoDirectorio.listFiles())
                EliminarFUAS(hijo);
        } else
            ArchivoDirectorio.delete();
    }

}
