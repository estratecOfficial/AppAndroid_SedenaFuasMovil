package com.codinginflow.datastore;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

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

import com.codinginflow.datastore.Globales.ConfiguracionCifrado;
import com.codinginflow.datastore.sedena.SQLiteHelper;
import com.codinginflow.datastore.sedena.ShowDataActivity;
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

public class MainActivity extends AppCompatActivity {

    String tecnicoReceibe,ResultadoLinea = null,ProgressMessage,NombrePackage,idSerie,SerieBD,TipoBD,ModeloBD,RegionMilitarBD,ClienteBD,NombreRangoBD,MatriculaBD,DomicilioBD,ZonaMilitarBD,BatallonBD,AreaBD,PisoBD,TelefonoBD,ExtensionBD,ExtensionSatelitalBD,FechaBD,FolioBD,ContadorBD,SerieSupresorBD,TonerBD,ObservacionesBD,URLFormatoBD,URLContadoresBD,URLConfiguracionBD,EstatusBD;
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
    StoreJSonDataInToSQLiteClass task;
    int contadorAsync = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("SEDENA");

        Bundle extra = this.getIntent().getExtras();
        tecnicoReceibe = extra.getString("usuarioLogin");

        identificador = findViewById(R.id.idTecnico);
        identificador.setText(tecnicoReceibe);

        //BOTONES DE LA INTERFAZ PRINCIPAL
        btnSeriesPendientes = findViewById(R.id.seriesPendientes);
        btnCompletos = findViewById(R.id.historialCompletos);
        btnsincronizar = findViewById(R.id.btnSincronizar);
        btnGuardarGodaddy = findViewById(R.id.btnGuardarGodaddy);

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

                    task=new StoreJSonDataInToSQLiteClass(MainActivity.this);

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


                    //new StoreJSonDataInToSQLiteClass(MainActivity.this).execute();

                }else {

                    showAlertDialogInternet(MainActivity.this, "SIN CONEXIÓN",
                            "La aplicación requiere conexión a Internet para sincronizar tus series con la base de datos...", true);

                }
            }
        });



        btnSeriesPendientes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MainActivity.this, ShowDataActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra("usuarioLogin", tecnicoReceibe);
                startActivity(intent);

            }
        });

        btnCompletos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MainActivity.this, ShowDataActivityCompleto.class);
                startActivity(intent);

            }
        });

        btnGuardarGodaddy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MainActivity.this, SubirDatosGodaddy.class);
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
                Intent intent = new Intent(MainActivity.this, Login.class);
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

    private class StoreJSonDataInToSQLiteClass extends AsyncTask<Void, Void, String> {

        public Context context;

        public StoreJSonDataInToSQLiteClass(Context context) {

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
                    progressDialog = new ProgressDialog(MainActivity.this);
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
                    url = new URL(ConfiguracionCifrado.URL_CONEXION_SYNC); //URL VERIFICAR SERIES A SINCRONIZAR
                    urlSync = new URL(ConfiguracionCifrado.URL_SYN_SERIES); //URL CAMBIAR ESTATUS DE SERIES

                    //PETICION POST DESCARGA DE SERIES DISPONIBLES
                    bufferedReaderDescarga = envioPOST(url, tecnicoReceibe, "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "");
                    ResultadoLinea = bufferedReaderDescarga.readLine();

                    if (ResultadoLinea == null)
                    {
                        viewSnack = findViewById(android.R.id.content);
                        snackBar = Snackbar.make(viewSnack, "No hay series a sincronizar", Snackbar.LENGTH_LONG)
                                .setAction("Action", null);
                        groupSnack = (ViewGroup) snackBar.getView();
                        groupSnack.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.colorSnackBarError));
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
                                TipoBD = jsonObject.getString("Tipo");
                                ModeloBD = jsonObject.getString("Modelo");
                                RegionMilitarBD = jsonObject.getString("RegionMilitar");
                                ClienteBD = jsonObject.getString("Cliente");
                                NombreRangoBD = jsonObject.getString("NombreRango");
                                MatriculaBD = jsonObject.getString("Matricula");
                                DomicilioBD = jsonObject.getString("Domicilio");
                                ZonaMilitarBD = jsonObject.getString("ZonaMilitar");
                                BatallonBD = jsonObject.getString("Batallon");
                                AreaBD = jsonObject.getString("Area");
                                PisoBD = jsonObject.getString("Piso");
                                TelefonoBD = jsonObject.getString("Telefono");
                                ExtensionBD = jsonObject.getString("Extension");
                                ExtensionSatelitalBD = jsonObject.getString("ExtensionSatelital");
                                FechaBD = jsonObject.getString("Fecha");
                                FolioBD = jsonObject.getString("Folio");
                                ContadorBD = jsonObject.getString("Contador");
                                SerieSupresorBD = jsonObject.getString("SerieSupresor");
                                TonerBD = jsonObject.getString("Toner");
                                ObservacionesBD = jsonObject.getString("Observaciones");
                                URLFormatoBD = jsonObject.getString("URLFormato");
                                URLContadoresBD = jsonObject.getString("URLContadores");
                                URLConfiguracionBD = jsonObject.getString("URLConfiguracion");
                                EstatusBD = jsonObject.getString("Estatus");

                                

                                //OBTENER DATOS DEL PAQUETE INSTALADO
                                manager = getPackageManager();
                                NombrePackage = getPackageName();
                                packageInformacion = manager.getPackageInfo(NombrePackage, 0);
                                NombrePackage = packageInformacion.applicationInfo.dataDir;

                                if (!ModeloBD.equals("") && !EstatusBD.equals("Revision")) {
                                    //PETICION POST PARA SINCRONIZAR MANUALES CAMBIO ESTATUS
                                    BufferedReader brSync = envioPOST(urlSync, tecnicoReceibe, idSerie, "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "","", "", "", "", "", "");

                                    Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + SQLiteHelper.TABLE_NAME + " WHERE Serie ='" + SerieBD + "'", null);
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
                                        String SQLiteDataBaseQueryHolder = "INSERT INTO " + SQLiteHelper.TABLE_NAME + " (Serie,Tipo,Modelo,RegionMilitar,Cliente,NombreRango,Matricula,Domicilio,ZonaMilitar,Batallon,Area,Piso,Telefono,Extension,ExtensionSatelital,Fecha,Folio,Contador,SerieSupresor,Toner,Observaciones,URLFormato,URLContadores,URLConfiguracion,Estatus) VALUES('" + SerieBD + "', '" + TipoBD + "','" + ModeloBD + "','" + RegionMilitarBD + "','" + ClienteBD + "','" + NombreRangoBD + "','" + MatriculaBD + "','" + DomicilioBD + "','" + ZonaMilitarBD + "','"+ BatallonBD + "','"+ AreaBD + "','" + PisoBD + "','" + TelefonoBD + "','" + ExtensionBD + "','" + ExtensionSatelitalBD + "','"+ FechaBD + "','"+ FolioBD + "','"+ ContadorBD + "','"+ SerieSupresorBD + "','" + TonerBD + "','" + ObservacionesBD + "','" + URLFormatoBD + "','" + URLContadoresBD + "','" + URLConfiguracionBD + "','"+ EstatusBD + "');";
                                        sqLiteDatabase.execSQL(SQLiteDataBaseQueryHolder);
                                    }else{
                                        //QUERY UPDATE DE DATOS OBTENIDOS DE MYSQL JSON A SQLITE
                                        String SQLiteDataBaseQueryHolder = "UPDATE " + SQLiteHelper.TABLE_NAME + " SET Serie='"+SerieBD+"', Tipo='"+TipoBD+"', Modelo='"+ModeloBD+"', RegionMilitar='"+RegionMilitarBD+"', Cliente='"+ClienteBD+"', NombreRango='"+NombreRangoBD+"', Matricula='"+MatriculaBD+"', Domicilio='"+DomicilioBD+"', ZonaMilitar='"+ZonaMilitarBD+"', Batallon='"+BatallonBD+"', Area='"+AreaBD+"', Piso='"+PisoBD+"', Telefono='"+TelefonoBD+"', Extension='"+ExtensionBD+"', ExtensionSatelital='"+ExtensionSatelitalBD+"', Folio='"+FolioBD+"', Contador='"+ContadorBD+"', SerieSupresor='"+SerieSupresorBD+"', Toner='"+TonerBD+"', Observaciones='"+ObservacionesBD+"' WHERE Serie = '" + SerieBD + "'";
                                        sqLiteDatabase.execSQL(SQLiteDataBaseQueryHolder);
                                    }

                                    cursor.close();


                                }

                                if (EstatusBD.equals("Revision")) {
                                    //CAMBIO TEXTO PROGRESS REVOCADO DE MANUALES
                                    ProgressMessage = "ACTIVANDO SERIE " + idSerie;
                                    runOnUiThread(changeMessage);

                                    //PETICION POST PARA SINCRONIZAR MANUALES CAMBIO ESTATUS
                                    BufferedReader brSync = envioPOST(urlSync, tecnicoReceibe, idSerie, "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "","", "", "", "", "", "");

                                    Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + SQLiteHelper.TABLE_NAME + " WHERE Serie ='" + SerieBD + "'", null);
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
                                        String SQLiteDataBaseQueryHolder = "INSERT INTO " + SQLiteHelper.TABLE_NAME + " (Serie,Tipo,Modelo,RegionMilitar,Cliente,NombreRango,Matricula,Domicilio,ZonaMilitar,Batallon,Area,Piso,Telefono,Extension,ExtensionSatelital,Fecha,Folio,Contador,SerieSupresor,Toner,Observaciones,URLFormato,URLContadores,URLConfiguracion,Estatus) VALUES('" + SerieBD + "', '" + TipoBD + "','" + ModeloBD + "','" + RegionMilitarBD + "','" + ClienteBD + "','" + NombreRangoBD + "','" + MatriculaBD + "','" + DomicilioBD + "','" + ZonaMilitarBD + "','"+ BatallonBD + "','"+ AreaBD + "','" + PisoBD + "','" + TelefonoBD + "','" + ExtensionBD + "','" + ExtensionSatelitalBD + "','"+ FechaBD + "','"+ FolioBD + "','"+ ContadorBD + "','"+ SerieSupresorBD + "','" + TonerBD + "','" + ObservacionesBD + "','" + URLFormatoBD + "','" + URLContadoresBD + "','" + URLConfiguracionBD + "','"+ EstatusBD + "');";
                                        sqLiteDatabase.execSQL(SQLiteDataBaseQueryHolder);
                                    }else{
                                        //QUERY UPDATE DE DATOS OBTENIDOS DE MYSQL JSON A SQLITE
                                        String SQLiteDataBaseQueryHolder = "UPDATE " + SQLiteHelper.TABLE_NAME + " SET Serie='"+SerieBD+"', Tipo='"+TipoBD+"', Modelo='"+ModeloBD+"', RegionMilitar='"+RegionMilitarBD+"', Cliente='"+ClienteBD+"', NombreRango='"+NombreRangoBD+"', Matricula='"+MatriculaBD+"', Domicilio='"+DomicilioBD+"', ZonaMilitar='"+ZonaMilitarBD+"', Batallon='"+BatallonBD+"', Area='"+AreaBD+"', Piso='"+PisoBD+"', Telefono='"+TelefonoBD+"', Extension='"+ExtensionBD+"', ExtensionSatelital='"+ExtensionSatelitalBD+"', Folio='"+FolioBD+"', Contador='"+ContadorBD+"', SerieSupresor='"+SerieSupresorBD+"', Toner='"+TonerBD+"', Observaciones='"+ObservacionesBD+"' WHERE Serie = '" + SerieBD + "'";
                                        sqLiteDatabase.execSQL(SQLiteDataBaseQueryHolder);
                                    }

                                    cursor.close();

                                }


                            }//END FOR JSON

                            String rutaLogo = NombrePackage + File.separator + "mis_archivos";
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
                    groupSnack.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.colorSnackBarError));
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
                    groupSnack.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.colorSnackBar));
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
                    groupSnack.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.colorSnackBar));
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
                    groupSnack.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.colorSnackBarError));
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
        sqLiteDatabase = openOrCreateDatabase(SQLiteHelper.DATABASE_NAME, Context.MODE_PRIVATE, null);

    }

    public void SQLiteTableBuild(){
        //CREAR EN CASO DE NO EXISTIT LA TABLA MANUALES
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS "+SQLiteHelper.TABLE_NAME+"("+SQLiteHelper.Table_Column_ID+" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "+SQLiteHelper.Table_Column_1_Serie+" TEXT, "+SQLiteHelper.Table_Column_1_Tipo+" TEXT, "+SQLiteHelper.Table_Column_2_Modelo+" TEXT, "+SQLiteHelper.Table_Column_3_RegionMilitar+" TEXT, "+SQLiteHelper.Table_Column_3_Cliente+" TEXT, "+SQLiteHelper.Table_Column_4_NombreRango+" TEXT, "+SQLiteHelper.Table_Column_5_Matricula+" TEXT, "+SQLiteHelper.Table_Column_6_Domicilio+" TEXT, "+SQLiteHelper.Table_Column_7_ZonaMilitar+" TEXT, "+SQLiteHelper.Table_Column_8_Batallon+" TEXT, "+SQLiteHelper.Table_Column_9_Area+" TEXT, "+SQLiteHelper.Table_Column_10_Piso+" TEXT, "+SQLiteHelper.Table_Column_11_Telefono+" TEXT, "+SQLiteHelper.Table_Column_12_Extension+" TEXT, "+SQLiteHelper.Table_Column_13_ExtensionSatelital+" TEXT, "+SQLiteHelper.Table_Column_14_Fecha+" TEXT, "+SQLiteHelper.Table_Column_15_Folio+" TEXT, "+SQLiteHelper.Table_Column_16_Contador+" TEXT, "+SQLiteHelper.Table_Column_17_SerieSupresor+" TEXT, "+SQLiteHelper.Table_Column_18_Toner+" TEXT, "+SQLiteHelper.Table_Column_19_Observaciones+" TEXT, "+SQLiteHelper.Table_Column_20_URLFormato+" TEXT, "+SQLiteHelper.Table_Column_21_URLContadores+" TEXT, "+SQLiteHelper.Table_Column_22_URLConfiguracion+" TEXT, "+SQLiteHelper.Table_Column_23_Estatus+" TEXT );");
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS "+SQLiteHelper.TABLE_NAME_HISTORIAL+"("+SQLiteHelper.Table_Column_ID+" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "+SQLiteHelper.Table_Column_1_Serie+" TEXT, "+SQLiteHelper.Table_Column_1_Tipo+" TEXT, "+SQLiteHelper.Table_Column_2_Modelo+" TEXT, "+SQLiteHelper.Table_Column_3_RegionMilitar+" TEXT, "+SQLiteHelper.Table_Column_3_Cliente+" TEXT, "+SQLiteHelper.Table_Column_4_NombreRango+" TEXT, "+SQLiteHelper.Table_Column_5_Matricula+" TEXT, "+SQLiteHelper.Table_Column_6_Domicilio+" TEXT, "+SQLiteHelper.Table_Column_7_ZonaMilitar+" TEXT, "+SQLiteHelper.Table_Column_8_Batallon+" TEXT, "+SQLiteHelper.Table_Column_9_Area+" TEXT, "+SQLiteHelper.Table_Column_10_Piso+" TEXT, "+SQLiteHelper.Table_Column_11_Telefono+" TEXT, "+SQLiteHelper.Table_Column_12_Extension+" TEXT, "+SQLiteHelper.Table_Column_13_ExtensionSatelital+" TEXT, "+SQLiteHelper.Table_Column_14_Fecha+" TEXT, "+SQLiteHelper.Table_Column_15_Folio+" TEXT, "+SQLiteHelper.Table_Column_16_Contador+" TEXT, "+SQLiteHelper.Table_Column_17_SerieSupresor+" TEXT, "+SQLiteHelper.Table_Column_18_Toner+" TEXT, "+SQLiteHelper.Table_Column_19_Observaciones+" TEXT, "+SQLiteHelper.Table_Column_20_URLFormato+" TEXT, "+SQLiteHelper.Table_Column_21_URLContadores+" TEXT, "+SQLiteHelper.Table_Column_22_URLConfiguracion+" TEXT, "+SQLiteHelper.Table_Column_23_Estatus+" TEXT );");
    }

    private Runnable changeMessage = new Runnable() {
        @Override
        public void run() {

            progressDialog.setMessage(ProgressMessage);
        }
    };

    public static BufferedReader envioPOST(URL url, String tecnicoID, String Serie, String Modelo, String RegionMilitar, String NombreRango, String Matricula, String Domicilio, String ZonaMilitar, String Batallon, String Area, String Piso, String Telefono, String Extension, String ExtensionSatelital, String Fecha, String Folio, String Contador, String SerieSupresor, String Toner, String Observaciones, String URLFormato, String URLContadores, String URLConfiguracion, String Estatus) throws IOException {

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
        params.add(new BasicNameValuePair("Modelo", Modelo));
        params.add(new BasicNameValuePair("RegionMilitar", RegionMilitar));
        params.add(new BasicNameValuePair("NombreRango", NombreRango));
        params.add(new BasicNameValuePair("Matricula", Matricula));
        params.add(new BasicNameValuePair("Domicilio", Domicilio));
        params.add(new BasicNameValuePair("ZonaMilitar", ZonaMilitar));
        params.add(new BasicNameValuePair("Batallon", Batallon));
        params.add(new BasicNameValuePair("Area", Area));
        params.add(new BasicNameValuePair("Piso", Piso));
        params.add(new BasicNameValuePair("Telefono", Telefono));
        params.add(new BasicNameValuePair("Extension", Extension));
        params.add(new BasicNameValuePair("ExtensionSatelital", ExtensionSatelital));
        params.add(new BasicNameValuePair("Fecha", Fecha));
        params.add(new BasicNameValuePair("Folio", Folio));
        params.add(new BasicNameValuePair("Contador", Contador));
        params.add(new BasicNameValuePair("SerieSupresor", SerieSupresor));
        params.add(new BasicNameValuePair("Toner", Toner));
        params.add(new BasicNameValuePair("Observaciones", Observaciones));
        params.add(new BasicNameValuePair("URLFormato", URLFormato));
        params.add(new BasicNameValuePair("URLContadores", URLContadores));
        params.add(new BasicNameValuePair("URLConfiguracion", URLConfiguracion));
        params.add(new BasicNameValuePair("Estatus", Estatus));

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
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
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