package com.codinginflow.datastore.sedena;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.codinginflow.datastore.BuildConfig;
import com.codinginflow.datastore.R;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorkerHelper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.Date;

public class crearFormato extends AppCompatActivity {
    private static final String GENERADOS = "mis_archivos";

    Button btn_generar;
    PackageManager manager;
    PackageInfo packageInformacion;
    String NombrePackage, serieExtra;
    String tipoSQLite, modeloSQLite, regionMilitarSQLite, clienteSQLite, nombreRangoSQLite, matriculaSQLite, domicilioSQLite, zonaMilitarSQLite, batallonSQLite, areaSQLite, pisoSQLite, telefonoSQLite, extensionSQLite, extensionSatelitalSQLite, folioSQLite, contadorSQLite, serieSupresSQlite, tonerSQLite, observacionesSQLite;
    SQLiteHelper sqLiteHelper;
    SQLiteDatabase sqLiteDatabase;
    Cursor cursor;
    String tonerStock, tonerEquipo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.crear_formato);
        setTitle("SEDENA");

        Bundle extra = this.getIntent().getExtras();
        serieExtra = extra.getString("SERIE_BD");

    }

    @Override
    protected void onResume() {
        //MOSTRAR DATOS DE SQLITE
        ShowSQLiteDBdata();
        try {
            generarPDFOnClick();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        super.onResume();
    }

    private void ShowSQLiteDBdata() {

        if(cursor != null && !cursor.isClosed()){
            cursor.close();
            sqLiteDatabase.close();
        }


        sqLiteHelper = new SQLiteHelper(this);
        sqLiteDatabase = sqLiteHelper.getWritableDatabase();

        cursor = sqLiteDatabase.rawQuery("SELECT * FROM "+SQLiteHelper.TABLE_NAME+" WHERE Serie ='"+serieExtra+"'", null);

        //INSERTAR LOS DATOS OBTENIDOS DE LA CONSULTA SQLITE A LOS ARRAY
        if (cursor.moveToFirst()) {
            do {
                tipoSQLite = cursor.getString(cursor.getColumnIndex(SQLiteHelper.Table_Column_1_Tipo));
                modeloSQLite = cursor.getString(cursor.getColumnIndex(SQLiteHelper.Table_Column_2_Modelo));
                regionMilitarSQLite = cursor.getString(cursor.getColumnIndex(SQLiteHelper.Table_Column_3_RegionMilitar));
                clienteSQLite = cursor.getString(cursor.getColumnIndex(SQLiteHelper.Table_Column_3_Cliente));
                nombreRangoSQLite = cursor.getString(cursor.getColumnIndex(SQLiteHelper.Table_Column_4_NombreRango));
                matriculaSQLite = cursor.getString(cursor.getColumnIndex(SQLiteHelper.Table_Column_5_Matricula));
                domicilioSQLite = cursor.getString(cursor.getColumnIndex(SQLiteHelper.Table_Column_6_Domicilio));
                zonaMilitarSQLite = cursor.getString(cursor.getColumnIndex(SQLiteHelper.Table_Column_7_ZonaMilitar));
                batallonSQLite = cursor.getString(cursor.getColumnIndex(SQLiteHelper.Table_Column_8_Batallon));
                areaSQLite = cursor.getString(cursor.getColumnIndex(SQLiteHelper.Table_Column_9_Area));
                pisoSQLite = cursor.getString(cursor.getColumnIndex(SQLiteHelper.Table_Column_10_Piso));
                telefonoSQLite = cursor.getString(cursor.getColumnIndex(SQLiteHelper.Table_Column_11_Telefono));
                extensionSQLite = cursor.getString(cursor.getColumnIndex(SQLiteHelper.Table_Column_12_Extension));
                extensionSatelitalSQLite = cursor.getString(cursor.getColumnIndex(SQLiteHelper.Table_Column_13_ExtensionSatelital));
                folioSQLite = cursor.getString(cursor.getColumnIndex(SQLiteHelper.Table_Column_15_Folio));
                contadorSQLite = cursor.getString(cursor.getColumnIndex(SQLiteHelper.Table_Column_16_Contador));
                serieSupresSQlite = cursor.getString(cursor.getColumnIndex(SQLiteHelper.Table_Column_17_SerieSupresor));
                tonerSQLite = cursor.getString(cursor.getColumnIndex(SQLiteHelper.Table_Column_18_Toner));
                observacionesSQLite = cursor.getString(cursor.getColumnIndex(SQLiteHelper.Table_Column_19_Observaciones));


            } while (cursor.moveToNext());
        }


        cursor.close();
        sqLiteDatabase.close();

    }

    public void generarPDFOnClick() throws PackageManager.NameNotFoundException {
        //OBTENER DATOS DEL PAQUETE INSTALADO
        manager = getPackageManager();
        NombrePackage = getPackageName();
        packageInformacion = manager.getPackageInfo(NombrePackage, 0);
        NombrePackage = packageInformacion.applicationInfo.dataDir;

        String NOMBRE_ARCHIVO = serieExtra +".pdf";
        Document document = new Document(PageSize.LETTER);
        document.setMargins(23,20,20,20);


        File pdfSubDir = new File(NombrePackage + File.separator + GENERADOS);

        if (!pdfSubDir.exists()){
            pdfSubDir.mkdir();
        }

        String nombre_completo = NombrePackage + File.separator + GENERADOS + File.separator + NOMBRE_ARCHIVO;

        File outputFile = new File(nombre_completo);
        if (outputFile.exists()){
            outputFile.delete();
        }

        try {
            PdfWriter pdfWriter = PdfWriter.getInstance(document, new FileOutputStream(nombre_completo));


            /*CREAR DOCUMENTO PARA ESCRIBIRLO*/
            document.open();
            document.addAuthor("Estratec");
            document.addCreator("Estratec");
            document.addSubject("FUA");
            document.addCreationDate();
            document.addTitle("FUA");

            XMLWorkerHelper worker = XMLWorkerHelper.getInstance();

            //OBENETEMOS EL LOGO SEDENA
            ImageView sedenaLogo = findViewById(R.id.imagenLogoSedena);
            BitmapDrawable draw = (BitmapDrawable) sedenaLogo.getDrawable();
            Bitmap bitmap = draw.getBitmap();

            FileOutputStream outStream = null;
            File dir = new File(NombrePackage + File.separator + "files");
            dir.mkdirs();
            String fileName = String.format("logo_sedena.PNG", System.currentTimeMillis());
            File outFile = new File(dir, fileName);
            outStream = new FileOutputStream(outFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);

            try {
                outStream.flush();
                outStream.close();
            }catch (Exception e){
                e.printStackTrace();
            }

            //OBENETEMOS EL LOGO SEDENA
            ImageView estratecLogo = findViewById(R.id.imagenLogoEstratec);
            BitmapDrawable draw2 = (BitmapDrawable) estratecLogo.getDrawable();
            Bitmap bitmap2 = draw2.getBitmap();

            FileOutputStream outStream2 = null;
            String fileName2 = String.format("logo_estratec.PNG", System.currentTimeMillis());
            File outFile2 = new File(dir, fileName2);
            outStream2 = new FileOutputStream(outFile2);
            bitmap2.compress(Bitmap.CompressFormat.PNG, 100, outStream2);

            try {
                outStream2.flush();
                outStream2.close();
            }catch (Exception e){
                e.printStackTrace();
            }

            String rutaLogo = NombrePackage + File.separator + "files" + File.separator + "logo_sedena.PNG";
            String rutaLogoEstratec = NombrePackage + File.separator + "files" + File.separator + "logo_estratec.PNG";

            //OBTIENE FECHA
            Date d=new Date();
            SimpleDateFormat fecc=new SimpleDateFormat("d-MMMM'-'yyyy");
            String fechacComplString = fecc.format(d);

            String htmlToPDF =
                    "<!DOCTYPE html>\n" +
                            "<html>\n" +
                            "<head>\n" +
                            "\t<title></title>\n" +
                            "\t<meta charset='utf-8'/>\n" +
                            "</head>\n" +
                            "<body>\n" +
                            "\n" +
                            "<style type=\"text/css\">\n" +
                            "\t#fecha{\n" +
                            "\t\tposition: relative; left:40px;\n" +
                            "\t}\n" +
                            "</style>\n" +
                            "\n" +
                            "<div style='border: 1px solid #C00; width: 750px; '>\n" +
                            "\t<table border='1' width='750'>\n" +
                            "\t\t<tr>\n" +
                            "\t\t\t<td>\n" +
                            "\t\n" +
                            "\t\t\t\t<table style='text-align: center;'>\n" +
                            "\t\t\t\t\t<tr>\n" +
                            "\t\t\t\t\t\t<td width='30px'></td>\n" +
                            "\t\t\t\t\t\t<td width='120px'></td>\n" +
                            "\t\t\t\t\t\t<td width='400px' style='font-weight: bold;'>FORMATO UNICO DE ASIGNACIÓN<br/>DE EQUIPO MULTIFUNCIONAL</td>\n" +
                            "\t\t\t\t\t\t<td width='150px'><img src='"+rutaLogoEstratec+"' width='50'/></td>\n" +
                            "\t\t\t\t\t</tr>\n" +
                            "\t\t\t\t</table>\n" +
                            "\n" +
                            "\t\t\t\t<br/>\n" +
                            "\n" +
                            "\t\t\t\t<table  border='0'>\n" +
                            "\t\t\t\t\t<tr>\n" +
                            "\t\t\t\t\t\t<td width='100px' style='font-size: 11; font-weight: bold; text-align: right;'>FECHA:</td>\n" +
                            "\t\t\t\t\t\t<td width='100px' style='border-bottom-width: 1px; border-bottom-style: solid; border-bottom-color: black; text-align: center; font-size: 11;'>"+fechacComplString+"</td>\n" +
                            "\t\t\t\t\t\t<td width='250px'></td>\n" +
                            "\t\t\t\t\t\t<td width='100px' style='font-size: 11; font-weight: bold; text-align: right;'>FOLIO No.</td>\n" +
                            "\t\t\t\t\t\t<td width='100px' style='border-bottom-width: 1px; border-bottom-style: solid; border-bottom-color: black; text-align: center; font-size: 11;'>"+folioSQLite+"</td>\n" +
                            "\t\t\t\t\t\t<td width='50px'></td>\n" +
                            "\t\t\t\t\t</tr>\n" +
                            "\t\t\t\t</table>\n" +
                            "\n" +
                            "\t\t\t\t<br/>\n" +
                            "\n" +
                            "\t\t\t\t<div style='background-color: black; color: white; text-align: center; font-size: 13; padding-top: 5px; padding-bottom: 5px;'>DATOS DEL USUARIO</div>\n" +
                            "\n" +
                            "\t\t\t\t<br/><br/>\n" +
                            "\n" +
                            "\t\t\t\t<table>\n" +
                            "\t\t\t\t\t<tr>\n" +
                            "\t\t\t\t\t\t<td width='100px' style='font-size: 11; font-weight: bold; text-align: right;'>CLIENTE:</td>\n" +
                            "\t\t\t\t\t\t<td width='550px' style='border-bottom-width: 1px; border-bottom-style: solid; border-bottom-color: black; text-align: center; font-size: 11;'>"+clienteSQLite+"</td>\n" +
                            "\t\t\t\t\t\t<td width='50px'></td>\n" +
                            "\t\t\t\t\t</tr>\n" +
                            "\t\t\t\t</table>\n" +
                            "\n" +
                            "\t\t\t\t<br/>\n" +
                            "\n" +
                            "\t\t\t\t<table>\n" +
                            "\t\t\t\t\t<tr>\n" +
                            "\t\t\t\t\t\t<td width='100px' style='font-size: 11; font-weight: bold; text-align: right;'>NOMBRE Y<br/> RANGO:</td>\n" +
                            "\t\t\t\t\t\t<td width='350px' style='border-bottom-width: 1px; border-bottom-style: solid; border-bottom-color: black; text-align: center; font-size: 11;'><br/>"+nombreRangoSQLite+"</td>\n" +
                            "\t\t\t\t\t\t<td width='100px' style='font-size: 11; font-weight: bold; text-align: right;'>MATRÍCULA:</td>\n" +
                            "\t\t\t\t\t\t<td width='100px' style='border-bottom-width: 1px; border-bottom-style: solid; border-bottom-color: black; text-align: center; font-size: 11;'><br/>"+matriculaSQLite+"</td>\n" +
                            "\t\t\t\t\t\t<td width='50px'></td>\n" +
                            "\t\t\t\t\t</tr>\n" +
                            "\t\t\t\t</table>\n" +
                            "\n" +
                            "\t\t\t\t<br/>\n" +
                            "\n" +
                            "\t\t\t\t<table>\n" +
                            "\t\t\t\t\t<tr>\n" +
                            "\t\t\t\t\t\t<td width='100px' style='font-size: 11; font-weight: bold; text-align: right;'>DOMICILIO:</td>\n" +
                            "\t\t\t\t\t\t<td width='550px' style='border-bottom-width: 1px; border-bottom-style: solid; border-bottom-color: black; text-align: center; font-size: 11;'>"+domicilioSQLite+"</td>\n" +
                            "\t\t\t\t\t\t<td width='50px'></td>\n" +
                            "\t\t\t\t\t</tr>\n" +
                            "\t\t\t\t</table>\n" +
                            "\n" +
                            "\t\t\t\t<br/>\n" +
                            "\n" +
                            "\n" +
                            "\t\t\t\t<table>\n" +
                            "\t\t\t\t\t<tr>\n" +
                            "\t\t\t\t\t\t<td width='100px' style='font-size: 11; font-weight: bold; text-align: right;'>REGIÓN<br/> MILITAR:</td>\n" +
                            "\t\t\t\t\t\t<td width='250px' style='border-bottom-width: 1px; border-bottom-style: solid; border-bottom-color: black; text-align: center; font-size: 11;'><br/>"+regionMilitarSQLite+"</td>\n" +
                            "\t\t\t\t\t\t<td width='100px' style='font-size: 11; font-weight: bold; text-align: right;'>ZONA<br/> MILITAR:</td>\n" +
                            "\t\t\t\t\t\t<td width='200px' style='border-bottom-width: 1px; border-bottom-style: solid; border-bottom-color: black; text-align: center; font-size: 11;'><br/>"+zonaMilitarSQLite+"</td>\n" +
                            "\t\t\t\t\t\t<td width='50px'></td>\n" +
                            "\t\t\t\t\t</tr>\n" +
                            "\t\t\t\t</table>\n" +
                            "\n" +
                            "\t\t\t\t<br/>\n" +
                            "\n" +
                            "\n" +
                            "\t\t\t\t<table>\n" +
                            "\t\t\t\t\t<tr>\n" +
                            "\t\t\t\t\t\t<td width='100px' style='font-size: 11; font-weight: bold; text-align: right;'>BATALLÓN:</td>\n" +
                            "\t\t\t\t\t\t<td width='300px' style='border-bottom-width: 1px; border-bottom-style: solid; border-bottom-color: black; text-align: center; font-size: 11;'>"+batallonSQLite+"</td>\n" +
                            "\t\t\t\t\t\t<td width='100px' style='font-size: 11; font-weight: bold; text-align: right;'>ÁREA:</td>\n" +
                            "\t\t\t\t\t\t<td width='150px' style='border-bottom-width: 1px; border-bottom-style: solid; border-bottom-color: black; text-align: center; font-size: 11;'>"+areaSQLite+"</td>\n" +
                            "\t\t\t\t\t\t<td width='50px'></td>\n" +
                            "\t\t\t\t\t</tr>\n" +
                            "\t\t\t\t</table>\n" +
                            "\n" +
                            "\t\t\t\t<br/>\n" +
                            "\n" +
                            "\t\t\t\t<table>\n" +
                            "\t\t\t\t\t<tr>\n" +
                            "\t\t\t\t\t\t<td width='100px' style='font-size: 11; font-weight: bold; text-align: right;'><br/>TELÉFONO:</td>\n" +
                            "\t\t\t\t\t\t<td width='150px' style='border-bottom-width: 1px; border-bottom-style: solid; border-bottom-color: black; text-align: center; font-size: 11;'><br/>"+telefonoSQLite+"</td>\n" +
                            "\t\t\t\t\t\t<td width='100px' style='font-size: 11; font-weight: bold; text-align: right;'><br/>EXTENSIÓN:</td>\n" +
                            "\t\t\t\t\t\t<td width='100px' style='border-bottom-width: 1px; border-bottom-style: solid; border-bottom-color: black; text-align: center; font-size: 11;'><br/>"+extensionSQLite+"</td>\n" +
                            "\t\t\t\t\t\t<td width='100px' style='font-size: 11; font-weight: bold; text-align: right;'><br/>EXT.SATELITAL:</td>\n" +
                            "\t\t\t\t\t\t<td width='100px' style='border-bottom-width: 1px; border-bottom-style: solid; border-bottom-color: black; text-align: center; font-size: 11;'><br/>"+extensionSatelitalSQLite+"</td>\n" +
                            "\t\t\t\t\t\t<td width='50px'></td>\n" +
                            "\t\t\t\t\t</tr>\n" +
                            "\t\t\t\t</table>\n" +
                            "\n" +
                            "\t\t\t\t<br/><br/><br/>\n" +
                            "\n" +
                            "\t\t\t\t<div style='background-color: black; color: white; text-align: center; font-size: 13; padding-top: 5px; padding-bottom: 5px;'>DATOS DEL EQUIPO QUE SE ENTREGA</div>\n" +
                            "\n" +
                            "\t\t\t\t<br/><br/>\n" +
                            "\t\t\t\t<table>\n" +
                            "\t\t\t\t\t<tr>\n" +
                            "\t\t\t\t\t\t<td width='100px' style='font-size: 11; font-weight: bold; text-align: right;'>TIPO:</td>\n" +
                            "\t\t\t\t\t\t<td width='180px' style='border-bottom-width: 1px; border-bottom-style: solid; border-bottom-color: black; text-align: center; font-size: 11;'>"+tipoSQLite+"</td>\n" +
                            "\t\t\t\t\t\t<td width='70px'></td>\n" +
                            "\t\t\t\t\t\t<td width='100px'></td>\n" +
                            "\t\t\t\t\t\t<td width='75px'><br/></td>\n" +
                            "\t\t\t\t\t\t<td width='50px'></td>\n" +
                            "\t\t\t\t\t\t<td width='75px'><br/></td>\n" +
                            "\t\t\t\t\t\t<td width='50px'></td>\n" +
                            "\t\t\t\t\t</tr>\n" +
                            "\t\t\t\t</table>\n" +
                            "\t\t\t\t\n" +
                            "\t\t\t\t<br/><br/><br/>\n" +
                            "\n" +
                            "\t\t\t\t<table>\n" +
                            "\t\t\t\t\t<tr>\n" +
                            "\t\t\t\t\t\t<td width='100px' style='font-size: 11; font-weight: bold; text-align: right;'>MODELO:</td>\n" +
                            "\t\t\t\t\t\t<td width='180px' style='border-bottom-width: 1px; border-bottom-style: solid; border-bottom-color: black; text-align: center; font-size: 11;'>"+modeloSQLite+"</td>\n" +
                            "\t\t\t\t\t\t<td width='60px'></td>\n" +
                            "\t\t\t\t\t\t<td width='90px' style='font-size: 11; font-weight: bold; text-align: right;'>TÓNER:</td>\n" +
                            "\t\t\t\t\t\t<td width='75px' style='border-bottom-width: 1px; border-bottom-style: solid; border-bottom-color: black; text-align: center; font-size: 11;'>"+tonerSQLite+"</td>\n" +
                            "\t\t\t\t\t\t<td width='50px'></td>\n" +
                            "\t\t\t\t\t\t<td width='75px'></td>\n" +
                            "\t\t\t\t\t\t<td width='50px'></td>\n" +
                            "\t\t\t\t\t</tr>\n" +
                            "\t\t\t\t</table>\n" +
                            "\n" +
                            "\t\t\t\t<br/>\n" +
                            "\n" +
                            "\t\t\t\t<table>\n" +
                            "\t\t\t\t\t<tr>\n" +
                            "\t\t\t\t\t\t<td width='390px'></td>\n" +
                            "\t\t\t\t\t\t<td width='100px' style='font-size: 11; font-weight: bold; text-align: right;'>OBSERVACIONES:</td>\n" +
                            "\t\t\t\t\t\t<td width='210px'></td>\n" +
                            "\t\t\t\t\t</tr>\n" +
                            "\t\t\t\t</table>\n" +
                            "\n" +
                            "\n" +
                            "\t\t\t\t<table>\n" +
                            "\t\t\t\t\t<tr>\n" +
                            "\t\t\t\t\t\t<td>\n" +
                            "\t\t\t\t\t\t\t<table>\n" +
                            "\t\t\t\t\t\t\t\t<tr>\n" +
                            "\t\t\t\t\t\t\t\t\t<td width='100px' style='font-size: 11; font-weight: bold; text-align: right;'>SERIE:</td>\n" +
                            "\t\t\t\t\t\t\t\t\t<td width='180px' style='border-bottom-width: 1px; border-bottom-style: solid; border-bottom-color: black; text-align: center; font-size: 11;'>"+serieExtra+"</td>\n" +
                            "\t\t\t\t\t\t\t\t</tr>\n" +
                            "\t\t\t\t\t\t\t\t<tr></tr>\n" +
                            "\t\t\t\t\t\t\t\t<tr>\n" +
                            "\t\t\t\t\t\t\t\t\t<td width='100px' style='font-size: 11; font-weight: bold; text-align: right;'><br/><br/><br/>CONTADOR:</td>\n" +
                            "\t\t\t\t\t\t\t\t\t<td width='180px' style='border-bottom-width: 1px; border-bottom-style: solid; border-bottom-color: black; text-align: center; font-size: 11;'><br/><br/><br/>"+contadorSQLite+"</td>\n" +
                            "\t\t\t\t\t\t\t\t</tr>\n" +
                            "\t\t\t\t\t\t\t\t<tr></tr>\n" +
                            "\t\t\t\t\t\t\t\t<tr>\n" +
                            "\t\t\t\t\t\t\t\t\t<td width='100px' style='font-size: 11; font-weight: bold; text-align: right;'><br/><br/>SERIE SUPRESOR<br/> DE PICOS:</td>\n" +
                            "\t\t\t\t\t\t\t\t\t<td width='180px' style='border-bottom-width: 1px; border-bottom-style: solid; border-bottom-color: black; text-align: center; font-size: 11;'><br/><br/><br/>"+serieSupresSQlite+"</td>\n" +
                            "\t\t\t\t\t\t\t\t</tr>\n" +
                            "\t\t\t\t\t\t\t</table>\n" +
                            "\t\t\t\t\t\t</td>\n" +
                            "\t\t\t\t\t\t<td width='20px'></td>\n" +
                            "\t\t\t\t\t\t<td>\n" +
                            "\t\t\t\t\t\t\t<table>\n" +
                            "\t\t\t\t\t\t\t\t<tr>\n" +
                            "\t\t\t\t\t\t<td width='20px'></td>\n" +
                            "\t\t\t\t\t\t\t\t\t<td width='255px' height='110px' style='border-width: 1px; border-style: solid; border-color: black; text-align: center; font-size: 11;'></td>\n" +
                            "\t\t\t\t\t\t\t\t</tr>\n" +
                            "\t\t\t\t\t\t\t</table>\n" +
                            "\t\t\t\t\t\t</td>\n" +
                            "\t\t\t\t\t</tr>\n" +
                            "\t\t\t\t</table>\n" +
                            "\n" +
                            "\t\t\t\t<br/><br/><br/>\n" +
                            "\n" +
                            "\t\t\t\t<div style='background-color: black; color: white; text-align: center; font-size: 13; padding-top: 5px; padding-bottom: 5px;'>FIRMA DE CONFORMIDAD DE ENTREGA Y RECEPCIÓN DE EQUIPO DE IMPRESIÓN</div>\n" +
                            "\n" +
                            "\t\t\t\t<br/><br/><br/>\n" +
                            "\n" +
                            "\t\t\t\t<table>\n" +
                            "\t\t\t\t\t<tr>\n" +
                            "\t\t\t\t\t\t<td width='50'></td>\n" +
                            "\t\t\t\t\t\t<td width='275' height='140' style='border-width: 1px; border-style: solid; border-color: black; text-align: center; font-size: 11;'>\n" +
                            "\t\t\t\t\t\t\t\n <table>\n" +
                            "\t\t\t\t\t\t\t\t<tr>\n" +
                            "\t\t\t\t\t\t\t\t\t<td height='100'></td>\n" +
                            "\t\t\t\t\t\t\t\t</tr>\n" +
                            "\t\t\t\t\t\t\t</table>\n" +
                            "\t\t\t\t\t\t\t<table>\n" +
                            "\t\t\t\t\t\t\t\t<tr>\n" +
                            "\t\t\t\t\t\t\t\t\t<td height='30' style='font-size: 11; font-weight: bold;'>NOMBRE Y FIRMA DEL USUARIO</td>\n" +
                            "\t\t\t\t\t\t\t\t</tr>\n" +
                            "\t\t\t\t\t\t\t</table>" +
                            "\t\t\t\t\t\t</td>\n" +
                            "\t\t\t\t\t\t<td width='50'></td>\n" +
                            "\t\t\t\t\t\t<td width='275' height='140' style='border-width: 1px; border-style: solid; border-color: black; text-align: center; font-size: 8;'>\n" +
                            "\t\t\t\t\t\t\t\n <table>\n" +
                            "\t\t\t\t\t\t\t\t<tr>\n" +
                            "\t\t\t\t\t\t\t\t\t<td height='100'></td>\n" +
                            "\t\t\t\t\t\t\t\t</tr>\n" +
                            "\t\t\t\t\t\t\t</table>\n" +
                            "\t\t\t\t\t\t\t<table>\n" +
                            "\t\t\t\t\t\t\t\t<tr>\n" +
                            "\t\t\t\t\t\t\t\t\t<td height='30' style='font-size: 11; font-weight: bold;'>SELLO</td>\n" +
                            "\t\t\t\t\t\t\t\t</tr>\n" +
                            "\t\t\t\t\t\t\t</table>" +
                            "\t\t\t\t\t\t</td>\n" +
                            "\t\t\t\t\t\t<td width='50'></td>\n" +
                            "\t\t\t\t\t</tr>\n" +
                            "\t\t\t\t</table>\n" +
                            "\n" +
                            "\t\t\t\t<br/><br/><br/>\n" +
                            "\n" +
                            "\t\t\t\t<div style='text-align: center; font-size: 12; padding-top: 5px; padding-bottom: 5px; font-weight: bold;'>PARA SOLICITAR SOPORTE TÉCNICO Y SUMINISTROS: wwww.estratec.com/soporte o llamando al 800-120-1200 </div>\n" +
                            "\n" +
                            "\t\t\t\t<br/><br/><br/>\n" +
                            "\n" +
                            "\t\t\t\t<div style='text-align: center; font-size: 9; padding-top: 5px; padding-bottom: 5px; font-weight: bold;'>AL FIRMAR ESTE DOCUMENTO ACEPTO LAS POLÍTICAS DE USO DE ESTE EQUIPO Y HABER RECIBIDO LA CAPACITACIÓN SOBRE EL USO DEL MISMO<br/><br/> </div>\n" +
                            "\n" +
                            "\t\t\t</td>\n" +
                            "\t\t</tr>\n" +
                            "\t</table>\n" +
                            "\n" +
                            "</div>\n" +
                            "\n" +
                            "</body>\n" +
                            "</html>";
            try {
                worker.parseXHtml(pdfWriter,document, new StringReader(htmlToPDF));
                document.close();
                Toast.makeText(this,"PDF GENERADO", Toast.LENGTH_LONG).show();
                muestraPDF(NOMBRE_ARCHIVO, NombrePackage, this);


            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    public void muestraPDF (String archivo, String paquete, Context context){
        String CONTENT_PROVIDER_AUTHORITY = BuildConfig.APPLICATION_ID +".provider";

        File documentsPath = new File(NombrePackage + "/", "mis_archivos/");
        File file = new File(documentsPath, archivo);
        file.setReadable(true);

        final Uri data = FileProvider.getUriForFile(context, CONTENT_PROVIDER_AUTHORITY, file);
        context.grantUriPermission(context.getPackageName(), data, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        final Intent intent = new Intent(Intent.ACTION_VIEW)
                .setDataAndType(data, "application/pdf")
                .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        context.startActivity(intent);

        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }
}

