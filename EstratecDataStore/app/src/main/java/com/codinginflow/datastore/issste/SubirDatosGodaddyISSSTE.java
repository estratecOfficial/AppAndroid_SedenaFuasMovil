package com.codinginflow.datastore.issste;

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

public class SubirDatosGodaddyISSSTE extends AppCompatActivity {
    SQLiteHelperISSSTE sqLiteHelperISSSTE;
    ProgressDialog pDialog;
    SQLiteDatabase sqLiteDatabase;
    Cursor cursor;
    String ProgressMessage, estatusCompleto = "En cola", idSQLite, serieSQLite, clienteSQLite, modeloSQLite, tipoEquipoSQLite, unidadSQLite, areaAdsSQlite, calleSQLite, pisoSQLite, coloniaSQLite, CPSQLite, ciudadSQLite, estadoSQLite, nombreSQLite, puestoSQLite, noEmpleadoSQLite, noRedSQLite, emailSQLite, fechaSQLite, folioSQLite, estatusSQLite, ObservacionesSQLite, URLFormatoSQLite, URLContadoresSQLite, idUnidadSQLite, NombreEnlaceSQLite;
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

        sqLiteHelperISSSTE = new SQLiteHelperISSSTE(this);
        //COMPROBAR CONEXXION A INTERNET
        verificaConexion = ConfiguracionCifrado.isOnlineNet();
        if (verificaConexion == true) {

            new SubirDatosGodaddyISSSTE.StoreJSonDataInToSQLiteClassGodaddy(SubirDatosGodaddyISSSTE.this).execute();


        }else {

            showAlertDialogInternet(SubirDatosGodaddyISSSTE.this, "SIN CONEXIÓN",
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


    private class StoreJSonDataInToSQLiteClassGodaddy extends AsyncTask<Void, Void, String> {

        public Context context;

        public StoreJSonDataInToSQLiteClassGodaddy(Context context) {

            this.context = context;
        }


        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            //PROGRESS DE SINCRONIZACION
            progressDialog = new ProgressDialog(SubirDatosGodaddyISSSTE.this);
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
                    urlSync = new URL(ConfiguracionCifrado.URL_UPDATE_GODADDY_ISSSTE); //URL ACTUALIZAR SERIES

                    sqLiteDatabase = sqLiteHelperISSSTE.getWritableDatabase();
                    cursor = sqLiteDatabase.rawQuery("SELECT * FROM "+SQLiteHelperISSSTE.TABLE_NAME+" WHERE Estatus ='" + estatusCompleto +"'" , null);


                    //INSERTAR LOS DATOS OBTENIDOS DE LA CONSULTA SQLITE A LOS ARRAY
                    if (cursor.moveToFirst()) {
                        do {

                            idSQLite = cursor.getString(cursor.getColumnIndex(SQLiteHelperISSSTE.Table_Column_ID));
                            serieSQLite = cursor.getString(cursor.getColumnIndex(SQLiteHelperISSSTE.Table_Column_1_Serie));
                            clienteSQLite = cursor.getString(cursor.getColumnIndex(SQLiteHelperISSSTE.Table_Column_2_Cliente));
                            modeloSQLite = cursor.getString(cursor.getColumnIndex(SQLiteHelperISSSTE.Table_Column_3_Modelo));
                            tipoEquipoSQLite = cursor.getString(cursor.getColumnIndex(SQLiteHelperISSSTE.Table_Column_4_TipoEquipo));
                            unidadSQLite = cursor.getString(cursor.getColumnIndex(SQLiteHelperISSSTE.Table_Column_5_UnidadAdministrativa));
                            areaAdsSQlite = cursor.getString(cursor.getColumnIndex(SQLiteHelperISSSTE.Table_Column_6_AreaAdscripcion));
                            calleSQLite = cursor.getString(cursor.getColumnIndex(SQLiteHelperISSSTE.Table_Column_7_CalleNumero));
                            pisoSQLite = cursor.getString(cursor.getColumnIndex(SQLiteHelperISSSTE.Table_Column_8_Piso));
                            coloniaSQLite = cursor.getString(cursor.getColumnIndex(SQLiteHelperISSSTE.Table_Column_9_Colonia));
                            CPSQLite = cursor.getString(cursor.getColumnIndex(SQLiteHelperISSSTE.Table_Column_10_CP));
                            ciudadSQLite = cursor.getString(cursor.getColumnIndex(SQLiteHelperISSSTE.Table_Column_11_Ciudad));
                            estadoSQLite = cursor.getString(cursor.getColumnIndex(SQLiteHelperISSSTE.Table_Column_12_Estado));
                            nombreSQLite = cursor.getString(cursor.getColumnIndex(SQLiteHelperISSSTE.Table_Column_13_Nombre));
                            puestoSQLite = cursor.getString(cursor.getColumnIndex(SQLiteHelperISSSTE.Table_Column_13_Puesto));
                            noEmpleadoSQLite = cursor.getString(cursor.getColumnIndex(SQLiteHelperISSSTE.Table_Column_14_NoEmpleado));
                            noRedSQLite = cursor.getString(cursor.getColumnIndex(SQLiteHelperISSSTE.Table_Column_15_NoRed));
                            emailSQLite = cursor.getString(cursor.getColumnIndex(SQLiteHelperISSSTE.Table_Column_16_Email));
                            fechaSQLite = cursor.getString(cursor.getColumnIndex(SQLiteHelperISSSTE.Table_Column_17_Fecha));
                            folioSQLite = cursor.getString(cursor.getColumnIndex(SQLiteHelperISSSTE.Table_Column_18_Folio));
                            ObservacionesSQLite = cursor.getString(cursor.getColumnIndex(SQLiteHelperISSSTE.Table_Column_19_Observaciones));
                            URLFormatoSQLite = cursor.getString(cursor.getColumnIndex(SQLiteHelperISSSTE.Table_Column_20_URLFormato));
                            URLContadoresSQLite = cursor.getString(cursor.getColumnIndex(SQLiteHelperISSSTE.Table_Column_21_URLContadores));
                            estatusSQLite = cursor.getString(cursor.getColumnIndex(SQLiteHelperISSSTE.Table_Column_22_Estatus));
                            idUnidadSQLite = cursor.getString(cursor.getColumnIndex(SQLiteHelperISSSTE.Table_Column_23_idUnidad));
                            NombreEnlaceSQLite = cursor.getString(cursor.getColumnIndex(SQLiteHelperISSSTE.Table_Column_24_NombreEnlace));

                            //VERIFICA CONEXION
                            verificaConexion = ConfiguracionCifrado.isOnlineNet();

                            if (verificaConexion == true) {
                                //ACTUALIZACION SQLITE
                                String SQLiteDataBaseQueryHolder = "DELETE FROM " + SQLiteHelperISSSTE.TABLE_NAME + " WHERE Serie = '" + serieSQLite + "' AND Estatus='En cola'";
                                sqLiteDatabase.execSQL(SQLiteDataBaseQueryHolder);

                                //ACTUALIZACION GODADDY
                                BufferedReader brSync = null;
                                brSync = envioPOST(urlSync, Login.idUsuario, serieSQLite, clienteSQLite, modeloSQLite, tipoEquipoSQLite, unidadSQLite, areaAdsSQlite, calleSQLite, pisoSQLite, coloniaSQLite, CPSQLite, ciudadSQLite, estadoSQLite, nombreSQLite, puestoSQLite, noEmpleadoSQLite, noRedSQLite, emailSQLite, fechaSQLite, folioSQLite, ObservacionesSQLite, URLFormatoSQLite, URLContadoresSQLite, "Completo", idUnidadSQLite,NombreEnlaceSQLite);
                                ResultadoLinea = brSync.readLine();
                            }


                            //ARCHIVOS DE EVIDENCIA
                            final File rutaFUA = new File(URLFormatoSQLite);
                            final File rutaContador = new File(URLContadoresSQLite);

                            if (rutaFUA.exists()){

                                try {
                                    Thread.sleep(6500);
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
                                    Toast.makeText(SubirDatosGodaddyISSSTE.this, "Se produjo el siguiente error: " + e.getMessage().toString(), Toast.LENGTH_LONG).show();
                                    e.printStackTrace();

                                }


                                if (verificaConexion == true) {

                                    post = client.post(ConfiguracionCifrado.URLServerISSSTE.URL_GUARDAR_FOTO_FUA, requestParams, new AsyncHttpResponseHandler() {
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

                                                Toast.makeText(SubirDatosGodaddyISSSTE.this, "Lo sentimos...,Tuvimos problemas al subir los FUAS al server.", Toast.LENGTH_LONG).show();
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
                                                Toast.makeText(SubirDatosGodaddyISSSTE.this, "Lo sentimos...,Tuvimos problemas al subir los FUAS al server.", Toast.LENGTH_LONG).show();
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
                                    Thread.sleep(5500);
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
                                    Toast.makeText(SubirDatosGodaddyISSSTE.this, "Se produjo el siguiente error: " + e.getMessage().toString(), Toast.LENGTH_LONG).show();
                                    e.printStackTrace();
                                }

                                verificaConexion = ConfiguracionCifrado.isOnlineNet();
                                if (verificaConexion == true) {

                                    post = client.post(ConfiguracionCifrado.URLServerISSSTE.URL_GUARDAR_FOTO_CONTADOR, requestParams, new AsyncHttpResponseHandler() {
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
                                                Toast.makeText(SubirDatosGodaddyISSSTE.this, "Lo sentimos...,Tuvimos problemas al subir los Contadores al server.", Toast.LENGTH_LONG).show();
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
                                                Toast.makeText(SubirDatosGodaddyISSSTE.this, "Lo sentimos...,Tuvimos problemas al subir los Contadores al server.", Toast.LENGTH_LONG).show();
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
                    groupSnack.setBackgroundColor(ContextCompat.getColor(SubirDatosGodaddyISSSTE.this, R.color.colorSnackBarError));
                    snackBar.show();
                    new SubirDatosGodaddyISSSTE.StoreJSonDataInToSQLiteClassGodaddy(SubirDatosGodaddyISSSTE.this).cancel(true);
                    break;
                case "Sin conexion" :
                    progressDialog.dismiss();
                    viewSnack = findViewById(android.R.id.content);
                    snackBar = Snackbar.make(viewSnack, "La conexión a internet es lenta o inestable", Snackbar.LENGTH_LONG)
                            .setAction("Action", null);
                    groupSnack = (ViewGroup) snackBar.getView();
                    groupSnack.setBackgroundColor(ContextCompat.getColor(SubirDatosGodaddyISSSTE.this, R.color.colorSnackBar));
                    snackBar.show();
                    new SubirDatosGodaddyISSSTE.StoreJSonDataInToSQLiteClassGodaddy(SubirDatosGodaddyISSSTE.this).cancel(true);
                    break;
                case "Error Captch" :
                    sqLiteDatabase.close();
                    progressDialog.dismiss();
                    viewSnack = findViewById(android.R.id.content);
                    snackBar = Snackbar.make(viewSnack, "Ocurrio un error en tu conexión", Snackbar.LENGTH_LONG)
                            .setAction("Action", null);
                    groupSnack = (ViewGroup) snackBar.getView();
                    groupSnack.setBackgroundColor(ContextCompat.getColor(SubirDatosGodaddyISSSTE.this, R.color.colorSnackBar));
                    snackBar.show();
                    new SubirDatosGodaddyISSSTE.StoreJSonDataInToSQLiteClassGodaddy(SubirDatosGodaddyISSSTE.this).cancel(true);
                    break;
                default:
                    sqLiteDatabase.close();
                    progressDialog.dismiss();
                    viewSnack = findViewById(android.R.id.content);
                    snackBar = Snackbar.make(viewSnack, "Se ha actualizado la base de datos", Snackbar.LENGTH_LONG)
                            .setAction("Action", null);
                    groupSnack = (ViewGroup) snackBar.getView();
                    groupSnack.setBackgroundColor(ContextCompat.getColor(SubirDatosGodaddyISSSTE.this, R.color.colorSnackBarError));
                    snackBar.show();
                    new SubirDatosGodaddyISSSTE.StoreJSonDataInToSQLiteClassGodaddy(SubirDatosGodaddyISSSTE.this).cancel(true);

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

    public static BufferedReader envioPOST(URL url, String tecnicoID, String Serie, String Cliente, String Modelo, String TipoEquipo, String UnidadAdmin, String AreaAdsc, String CalleNumero, String Piso, String Colonia, String CP, String Ciudad, String Estado, String NombreUser, String Puesto, String NoEmpleado, String NoRed, String Email, String Fecha, String Folio, String Observaciones, String UrlFua, String UrlContador, String Estatus, String idUnidadAdm, String NombreEnlace) throws IOException {

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
        params.add(new BasicNameValuePair("idUnidad", idUnidadAdm));
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
            return null;
        }


    }



    public void showAlertDialogInternet(Context context, String title, String message, Boolean status) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(SubirDatosGodaddyISSSTE.this);
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
