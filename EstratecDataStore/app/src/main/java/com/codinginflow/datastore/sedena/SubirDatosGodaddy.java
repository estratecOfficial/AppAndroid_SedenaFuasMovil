package com.codinginflow.datastore.sedena;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.codinginflow.datastore.Globales.ConfiguracionCifrado;
import com.codinginflow.datastore.Login;
import com.codinginflow.datastore.R;
import com.google.android.material.snackbar.Snackbar;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

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
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import cz.msebera.android.httpclient.Header;

public class SubirDatosGodaddy extends AppCompatActivity {
    SQLiteHelper sqLiteHelper;
    ProgressDialog pDialog;
    SQLiteDatabase sqLiteDatabase;
    Cursor cursor;
    String ProgressMessage, estatusCompleto = "En cola", idSQLite, serieSQLite, modeloSQLite, regionSQLite, nombreRangoSQLite, matriculaSQLite, domicilioSQLite, zonaMSQLite, batallonSQLite, areaSQLite, pisoSQLite, telefonoSQLite, extensionSQLite, extensionSatSQLite, fechaSQLite, contadorSQLite, serieSupSQLite, tonerSQLite, observacionesSQLite, urlFormatoSQLite, urlContadorSQLite, estatusSQLite;
    ProgressDialog progressDialog;
    Boolean verificaConexion;
    URL url,urlSync,UFormato,UContadores,UConfiguracion;
    String ResultadoLinea = null;
    View viewSnack;
    Snackbar snackBar;
    ViewGroup groupSnack;
    RequestHandle post;
    AsyncHttpClient client;
    String estatusUploadFUA, estatusUploadContador, estatusUploadSQL;
    int contadorErrores = 0;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();

        sqLiteHelper = new SQLiteHelper(this);
        //COMPROBAR CONEXXION A INTERNET
        verificaConexion = ConfiguracionCifrado.isOnlineNet();
        if (verificaConexion == true) {

            new StoreJSonDataInToSQLiteClass(SubirDatosGodaddy.this).execute();


        }else {

            showAlertDialogInternet(SubirDatosGodaddy.this, "SIN CONEXIÓN",
                    "La aplicación requiere conexión a Internet para sincronizar tus series/evidencias con el servidor", true);

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        /*if (progressDialog.isShowing()){
            progressDialog.dismiss();
        }*/

    }


    private class StoreJSonDataInToSQLiteClass extends AsyncTask<Void, Void, String> {

        public Context context;

        public StoreJSonDataInToSQLiteClass(Context context) {

            this.context = context;
        }


        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            //PROGRESS DE SINCRONIZACION
            progressDialog = new ProgressDialog(SubirDatosGodaddy.this);
            progressDialog.setTitle("SINCRONIZANDO");
            progressDialog.setMessage("Espera a que se sincronizen las series completas");
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

        }


        @SuppressLint("WrongThread")
        @Override
        protected String doInBackground(Void... arg0) {

            //CONEXION POST A BASE DE DATOS
            verificaConexion = ConfiguracionCifrado.isOnlineNet();
            if (verificaConexion == true){


                try {
                    urlSync = new URL(ConfiguracionCifrado.URL_UPDATE_GODADDY); //URL ACTUALIZAR SERIES

                    sqLiteDatabase = sqLiteHelper.getWritableDatabase();
                    cursor = sqLiteDatabase.rawQuery("SELECT * FROM "+SQLiteHelper.TABLE_NAME+" WHERE Estatus ='" + estatusCompleto +"'" , null);


                    //INSERTAR LOS DATOS OBTENIDOS DE LA CONSULTA SQLITE A LOS ARRAY
                    if (cursor.moveToFirst()) {
                        do {

                            idSQLite = cursor.getString(cursor.getColumnIndex(SQLiteHelper.Table_Column_ID));
                            serieSQLite = cursor.getString(cursor.getColumnIndex(SQLiteHelper.Table_Column_1_Serie));
                            modeloSQLite = cursor.getString(cursor.getColumnIndex(SQLiteHelper.Table_Column_2_Modelo));
                            regionSQLite = cursor.getString(cursor.getColumnIndex(SQLiteHelper.Table_Column_3_RegionMilitar));
                            nombreRangoSQLite = cursor.getString(cursor.getColumnIndex(SQLiteHelper.Table_Column_4_NombreRango));
                            matriculaSQLite = cursor.getString(cursor.getColumnIndex(SQLiteHelper.Table_Column_5_Matricula));
                            domicilioSQLite = cursor.getString(cursor.getColumnIndex(SQLiteHelper.Table_Column_6_Domicilio));
                            zonaMSQLite = cursor.getString(cursor.getColumnIndex(SQLiteHelper.Table_Column_7_ZonaMilitar));
                            batallonSQLite = cursor.getString(cursor.getColumnIndex(SQLiteHelper.Table_Column_8_Batallon));
                            areaSQLite = cursor.getString(cursor.getColumnIndex(SQLiteHelper.Table_Column_9_Area));
                            pisoSQLite = cursor.getString(cursor.getColumnIndex(SQLiteHelper.Table_Column_10_Piso));
                            telefonoSQLite = cursor.getString(cursor.getColumnIndex(SQLiteHelper.Table_Column_11_Telefono));
                            extensionSQLite = cursor.getString(cursor.getColumnIndex(SQLiteHelper.Table_Column_12_Extension));
                            extensionSatSQLite = cursor.getString(cursor.getColumnIndex(SQLiteHelper.Table_Column_13_ExtensionSatelital));
                            fechaSQLite = cursor.getString(cursor.getColumnIndex(SQLiteHelper.Table_Column_14_Fecha));
                            contadorSQLite = cursor.getString(cursor.getColumnIndex(SQLiteHelper.Table_Column_16_Contador));
                            serieSupSQLite = cursor.getString(cursor.getColumnIndex(SQLiteHelper.Table_Column_17_SerieSupresor));
                            tonerSQLite = cursor.getString(cursor.getColumnIndex(SQLiteHelper.Table_Column_18_Toner));
                            observacionesSQLite = cursor.getString(cursor.getColumnIndex(SQLiteHelper.Table_Column_19_Observaciones));
                            urlFormatoSQLite = cursor.getString(cursor.getColumnIndex(SQLiteHelper.Table_Column_20_URLFormato));
                            urlContadorSQLite = cursor.getString(cursor.getColumnIndex(SQLiteHelper.Table_Column_21_URLContadores));
                            estatusSQLite = cursor.getString(cursor.getColumnIndex(SQLiteHelper.Table_Column_23_Estatus));


                            //VERIFICA CONEXION
                            verificaConexion = ConfiguracionCifrado.isOnlineNet();

                            if (verificaConexion == true) {
                                //ACTUALIZACION SQLITE
                                String SQLiteDataBaseQueryHolder = "DELETE FROM " + SQLiteHelper.TABLE_NAME + " WHERE Serie = '" + serieSQLite + "' AND Estatus='En cola'";
                                sqLiteDatabase.execSQL(SQLiteDataBaseQueryHolder);

                                //ACTUALIZACION GODADDY
                                BufferedReader brSync = null;
                                brSync = envioPOST(urlSync, Login.idUsuario, serieSQLite, modeloSQLite, regionSQLite, nombreRangoSQLite, matriculaSQLite, domicilioSQLite, zonaMSQLite, batallonSQLite, areaSQLite, pisoSQLite, telefonoSQLite, extensionSQLite, extensionSatSQLite, fechaSQLite, "", contadorSQLite, serieSupSQLite, tonerSQLite, observacionesSQLite, urlFormatoSQLite, urlContadorSQLite, "", "Completo");
                                ResultadoLinea = brSync.readLine();
                            }


                            //ARCHIVOS DE EVIDENCIA
                            final File rutaFUA = new File(urlFormatoSQLite);
                            final File rutaContador = new File(urlContadorSQLite);

                            if (rutaFUA.exists()){

                                try {
                                    Thread.sleep(4500);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }

                                //SUBIR EVIDENCIA INDIVIDUAL
                                client = new AsyncHttpClient();
                                client.setConnectTimeout(70 * 1000);
                                RequestParams requestParams = new RequestParams();
                                try {
                                    requestParams.put("fotosFUA", rutaFUA);
                                } catch (IOException e) {
                                    Toast.makeText(SubirDatosGodaddy.this, "Se produjo el siguiente error: " + e.getMessage().toString(), Toast.LENGTH_LONG).show();
                                    e.printStackTrace();

                                }


                                if (verificaConexion == true) {

                                        post = client.post(ConfiguracionCifrado.URLServer.URL_GUARDAR_FOTO_FUA, requestParams, new AsyncHttpResponseHandler() {
                                        @Override
                                        public void onStart() {
                                            //PROGRESS DE SINCRONIZACION

                                        }

                                        @Override
                                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                                            if (statusCode == 200) {
                                                //Eliminamos el archivo de la carpeta
                                                rutaFUA.delete();
                                                estatusUploadFUA = "OK";
                                                Log.d(null, "EXITO FUA: " + serieSQLite);
                                            } else {

                                                Toast.makeText(SubirDatosGodaddy.this, "Lo sentimos...,Tuvimos problemas al subir los FUAS al server.", Toast.LENGTH_LONG).show();
                                                estatusUploadFUA = "Error";
                                            }
                                        }

                                        @Override
                                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                                            if (statusCode == 0) {
                                                //Toast.makeText(SubirDatosGodaddy.this, "Uno o más FUAS se guardaron correctamente en el server alterno.", Toast.LENGTH_LONG).show();
                                                //Eliminamos el archivo de la carpeta
                                                //files[finalI].delete();
                                                Log.d(null, "ERROR FAILURE FUA" + serieSQLite);
                                                estatusUploadFUA = "Error";

                                            } else {
                                                Toast.makeText(SubirDatosGodaddy.this, "Lo sentimos...,Tuvimos problemas al subir los FUAS al server.", Toast.LENGTH_LONG).show();
                                                estatusUploadFUA = "Error";
                                            }
                                        }

                                        @Override
                                        public void onFinish() {
                                            Log.d(null, "ON FINISH FUA: " + serieSQLite);
                                        }

                                        @Override
                                        public boolean getUseSynchronousMode() {
                                            return false;
                                        }

                                    });

                                }else{
                                    ResultadoLinea = "Sin conexion";
                                }

                            }//FIN SUBIR FUA INDIVIDUAL



                            if (rutaContador.exists()){

                                try {
                                    Thread.sleep(4500);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }

                                //SUBIR CONTADOR
                                client = new AsyncHttpClient();
                                client.setConnectTimeout(70 * 1000);
                                RequestParams requestParams = new RequestParams();
                                try {
                                    requestParams.put("fotosContador", rutaContador);
                                } catch (IOException e) {
                                    Toast.makeText(SubirDatosGodaddy.this, "Se produjo el siguiente error: " + e.getMessage().toString(), Toast.LENGTH_LONG).show();
                                    e.printStackTrace();
                                }

                                verificaConexion = ConfiguracionCifrado.isOnlineNet();
                                if (verificaConexion == true) {

                                        post = client.post(ConfiguracionCifrado.URLServer.URL_GUARDAR_FOTO_CONTADOR, requestParams, new AsyncHttpResponseHandler() {
                                        @Override
                                        public void onStart() {

                                        }

                                        @Override
                                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                                            if (statusCode == 200) {
                                                //Toast.makeText(SubirDatosGodaddy.this, "Uno o más Contadores se guardaron correctamente en el server.", Toast.LENGTH_LONG).show();
                                                //Eliminamos el archivo de la carpeta
                                                rutaContador.delete();
                                                estatusUploadContador = "OK";


                                                Log.d(null, "EXITO CONTADOR: " + serieSQLite);

                                            } else {
                                                Toast.makeText(SubirDatosGodaddy.this, "Lo sentimos...,Tuvimos problemas al subir los Contadores al server.", Toast.LENGTH_LONG).show();
                                                estatusUploadContador = "Error";
                                            }
                                        }

                                        @Override
                                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                                            if (statusCode == 0) {
                                                //Toast.makeText(SubirDatosGodaddy.this, "Uno o más Contadores se guardaron correctamente en el server alterno.", Toast.LENGTH_LONG).show();
                                                //Eliminamos el archivo de la carpeta
                                                estatusUploadContador = "Error";

                                                Log.d(null, "ERROR FAILURE CONTADOR" + serieSQLite);
                                            } else {
                                                Toast.makeText(SubirDatosGodaddy.this, "Lo sentimos...,Tuvimos problemas al subir los Contadores al server.", Toast.LENGTH_LONG).show();
                                                estatusUploadContador = "Error";
                                            }
                                        }

                                        @Override
                                        public void onFinish() {
                                            Log.d(null, "ON FINISH CONTADOR: " + serieSQLite);

                                        }

                                        @Override
                                        public boolean getUseSynchronousMode() {
                                            return false;
                                        }
                                    });
                                }else{
                                    ResultadoLinea = "Sin conexion";
                                }

                            }
                            //FIN SUBIR CONTADOR



                        } while (cursor.moveToNext());
                    }


                    cursor.close();
                    sqLiteDatabase.close();

                } catch (Exception e) {
                    ResultadoLinea = "Error Captch";
                    progressDialog.dismiss();
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
                    viewSnack = findViewById(android.R.id.content);
                    snackBar = Snackbar.make(viewSnack, "No hay series ha sincronizar", Snackbar.LENGTH_LONG)
                            .setAction("Action", null);
                    groupSnack = (ViewGroup) snackBar.getView();
                    groupSnack.setBackgroundColor(ContextCompat.getColor(SubirDatosGodaddy.this, R.color.colorSnackBarError));
                    snackBar.show();
                    new StoreJSonDataInToSQLiteClass(SubirDatosGodaddy.this).cancel(true);
                    break;
                case "Sin conexion" :
                    progressDialog.dismiss();
                    viewSnack = findViewById(android.R.id.content);
                    snackBar = Snackbar.make(viewSnack, "La conexión a internet es lenta o inestable", Snackbar.LENGTH_LONG)
                            .setAction("Action", null);
                    groupSnack = (ViewGroup) snackBar.getView();
                    groupSnack.setBackgroundColor(ContextCompat.getColor(SubirDatosGodaddy.this, R.color.colorSnackBar));
                    snackBar.show();
                    new StoreJSonDataInToSQLiteClass(SubirDatosGodaddy.this).cancel(true);
                    break;
                case "Error Captch" :
                    sqLiteDatabase.close();
                    progressDialog.dismiss();
                    viewSnack = findViewById(android.R.id.content);
                    snackBar = Snackbar.make(viewSnack, "Ocurrio un error en tu conexión", Snackbar.LENGTH_LONG)
                            .setAction("Action", null);
                    groupSnack = (ViewGroup) snackBar.getView();
                    groupSnack.setBackgroundColor(ContextCompat.getColor(SubirDatosGodaddy.this, R.color.colorSnackBar));
                    snackBar.show();
                    new StoreJSonDataInToSQLiteClass(SubirDatosGodaddy.this).cancel(true);
                    break;
                default:
                    sqLiteDatabase.close();
                    progressDialog.dismiss();
                    viewSnack = findViewById(android.R.id.content);
                    snackBar = Snackbar.make(viewSnack, "Se ha actualizado la base de datos", Snackbar.LENGTH_LONG)
                            .setAction("Action", null);
                    groupSnack = (ViewGroup) snackBar.getView();
                    groupSnack.setBackgroundColor(ContextCompat.getColor(SubirDatosGodaddy.this, R.color.colorSnackBarError));
                    snackBar.show();
                    new StoreJSonDataInToSQLiteClass(SubirDatosGodaddy.this).cancel(true);

                    break;
            }

            finish();

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
            return null;
        }


    }



    public void showAlertDialogInternet(Context context, String title, String message, Boolean status) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(SubirDatosGodaddy.this);
        alertDialog.setIcon(R.drawable.atencion);
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        alertDialog.show();
    }
}
