package com.codinginflow.datastore.issste;

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
import com.codinginflow.datastore.sedena.SQLiteHelper;
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

public class crearFormatoISSSTE extends AppCompatActivity {
    private static final String GENERADOS = "mis_archivos_issste";

    Button btn_generar;
    PackageManager manager;
    PackageInfo packageInformacion;
    String NombrePackage, serieExtra;
    String ClienteSQLite, ModeloSQLite, TipoEquipoSQLite, UnidadAdministrativaSQLite, AreaAdscripcionSQLite, CalleNumeroSQLite, PisoSQLite, ColoniaSQLite, CPSQLite, CiudadSQLite, EstadoSQLite, NombreSQLite, PuestoSQLite, NoEmpleadoSQLite, NoRedSQLite, EmailSQLite, FechaSQLite, FolioSQLite, ObservacionesSQLite, EstatusSQLite, idUnidadSQLite, NombreEnlaceSQLite;
    SQLiteHelperISSSTE sqLiteHelperISSSTE;
    SQLiteDatabase sqLiteDatabase;
    Cursor cursor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.crear_formato_issste);
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


        sqLiteHelperISSSTE = new SQLiteHelperISSSTE(this);
        sqLiteDatabase = sqLiteHelperISSSTE.getWritableDatabase();

        cursor = sqLiteDatabase.rawQuery("SELECT * FROM "+SQLiteHelperISSSTE.TABLE_NAME+" WHERE Serie ='"+serieExtra+"'", null);

        //INSERTAR LOS DATOS OBTENIDOS DE LA CONSULTA SQLITE A LOS ARRAY
        if (cursor.moveToFirst()) {
            do {
                ClienteSQLite = cursor.getString(cursor.getColumnIndex(SQLiteHelperISSSTE.Table_Column_2_Cliente));
                ModeloSQLite = cursor.getString(cursor.getColumnIndex(SQLiteHelperISSSTE.Table_Column_3_Modelo));
                TipoEquipoSQLite = cursor.getString(cursor.getColumnIndex(SQLiteHelperISSSTE.Table_Column_4_TipoEquipo));
                UnidadAdministrativaSQLite = cursor.getString(cursor.getColumnIndex(SQLiteHelperISSSTE.Table_Column_5_UnidadAdministrativa));
                AreaAdscripcionSQLite = cursor.getString(cursor.getColumnIndex(SQLiteHelperISSSTE.Table_Column_6_AreaAdscripcion));
                CalleNumeroSQLite = cursor.getString(cursor.getColumnIndex(SQLiteHelperISSSTE.Table_Column_7_CalleNumero));
                PisoSQLite = cursor.getString(cursor.getColumnIndex(SQLiteHelperISSSTE.Table_Column_8_Piso));
                ColoniaSQLite = cursor.getString(cursor.getColumnIndex(SQLiteHelperISSSTE.Table_Column_9_Colonia));
                CPSQLite = cursor.getString(cursor.getColumnIndex(SQLiteHelperISSSTE.Table_Column_10_CP));
                CiudadSQLite = cursor.getString(cursor.getColumnIndex(SQLiteHelperISSSTE.Table_Column_11_Ciudad));
                EstadoSQLite = cursor.getString(cursor.getColumnIndex(SQLiteHelperISSSTE.Table_Column_12_Estado));
                NombreSQLite = cursor.getString(cursor.getColumnIndex(SQLiteHelperISSSTE.Table_Column_13_Nombre));
                PuestoSQLite = cursor.getString(cursor.getColumnIndex(SQLiteHelperISSSTE.Table_Column_13_Puesto));
                NoEmpleadoSQLite = cursor.getString(cursor.getColumnIndex(SQLiteHelperISSSTE.Table_Column_14_NoEmpleado));
                NoRedSQLite = cursor.getString(cursor.getColumnIndex(SQLiteHelperISSSTE.Table_Column_15_NoRed));
                EmailSQLite = cursor.getString(cursor.getColumnIndex(SQLiteHelperISSSTE.Table_Column_16_Email));
                FechaSQLite = cursor.getString(cursor.getColumnIndex(SQLiteHelperISSSTE.Table_Column_17_Fecha));
                FolioSQLite = cursor.getString(cursor.getColumnIndex(SQLiteHelperISSSTE.Table_Column_18_Folio));
                ObservacionesSQLite = cursor.getString(cursor.getColumnIndex(SQLiteHelperISSSTE.Table_Column_19_Observaciones));
                EstatusSQLite = cursor.getString(cursor.getColumnIndex(SQLiteHelperISSSTE.Table_Column_22_Estatus));
                idUnidadSQLite = cursor.getString(cursor.getColumnIndex(SQLiteHelperISSSTE.Table_Column_23_idUnidad));
                NombreEnlaceSQLite = cursor.getString(cursor.getColumnIndex(SQLiteHelperISSSTE.Table_Column_24_NombreEnlace));

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
            ImageView sedenaLogo = findViewById(R.id.imagenLogoIssste);
            BitmapDrawable draw = (BitmapDrawable) sedenaLogo.getDrawable();
            Bitmap bitmap = draw.getBitmap();

            FileOutputStream outStream = null;
            File dir = new File(NombrePackage + File.separator + "files");
            dir.mkdirs();
            String fileName = String.format("logo_issste.PNG", System.currentTimeMillis());
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

            String rutaLogo = NombrePackage + File.separator + "files" + File.separator + "logo_issste.PNG";
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
                            "\t<table border='0' width='750'>\n" +
                            "\t\t<tr>\n" +
                            "\t\t\t<td>\n" +
                            "\t\n" +
                            "\t\t\t\t<table style='text-align: center;'>\n" +
                            "\t\t\t\t\t<tr>\n" +
                            "\t\t\t\t\t\t<td width='30px'></td>\n" +
                            "\t\t\t\t\t\t<td width='170px'><img src='"+rutaLogo+"' width='90'/></td>\n" +
                            "\t\t\t\t\t\t<td width='330px'></td>\n" +
                            "\t\t\t\t\t\t<td width='170px'><img src='"+rutaLogoEstratec+"' width='70'/></td>\n" +
                            "\t\t\t\t\t</tr>\n" +
                            "\t\t\t\t</table>\n" +
                            "\n" +
                            "\n" +
                            "\t\t\t\t<table style='text-align: center;'>\n" +
                            "\t\t\t\t\t<tr>\n" +
                            "\t\t\t\t\t\t<td width='30px'></td>\n" +
                            "\t\t\t\t\t\t<td width='640px' style='font-weight: bold;'>FORMATO ÚNICO DE ASIGNACIÓN<br/>DE EQUIPO DE IMPRESIÓN v.21 </td>\n" +
                            "\t\t\t\t\t\t<td width='30px'></td>\n" +
                            "\t\t\t\t\t</tr>\n" +
                            "\t\t\t\t\t<tr>\n" +
                            "\t\t\t\t\t\t<td width='30px'></td>\n" +
                            "\t\t\t\t\t\t<td width='640px' style='font-weight: bold;'><div style='background-color: black; font-size: 1; padding-top: 1px; padding-bottom: 1px; width:640px;'></div></td>\n" +
                            "\t\t\t\t\t\t<td width='30px'></td>\n" +
                            "\t\t\t\t\t</tr>\n" +
                            "\t\t\t\t\t<tr>\n" +
                            "\t\t\t\t\t\t<td width='30px'></td>\n" +
                            "\t\t\t\t\t\t<td width='640px' style='font-size: 11;'>ENTREGA | INSTALACIÓN | CONFIGURACIÓN | CAPACITACIÓN</td>\n" +
                            "\t\t\t\t\t\t<td width='30px'></td>\n" +
                            "\t\t\t\t\t</tr>\n" +
                            "\t\t\t\t</table>\n" +
                            "\t\t\t\t<br/>\n" +
                            "\n" +
                            "\n" +
                            "\t\t\t\t<table  border='0'>\n" +
                            "\t\t\t\t\t<tr>\n" +
                            "\t\t\t\t\t\t<td width='50px'></td>\n" +
                            "\t\t\t\t\t\t<td width='50px' style='font-size: 11; font-weight: bold; text-align: left;'>FECHA:</td>\n" +
                            "\t\t\t\t\t\t<td width='100px' style='border-bottom-width: 1px; border-bottom-style: solid; border-bottom-color: black; text-align: center; font-size: 11;'>"+fechacComplString+"</td>\n" +
                            "\t\t\t\t\t\t<td width='250px'></td>\n" +
                            "\t\t\t\t\t\t<td width='100px' style='font-size: 11; font-weight: bold; text-align: right;'>FOLIO ÚNICO:</td>\n" +
                            "\t\t\t\t\t\t<td width='100px' style='border-bottom-width: 1px; border-bottom-style: solid; border-bottom-color: black; text-align: center; font-size: 11;'>"+FolioSQLite+"</td>\n" +
                            "\t\t\t\t\t\t<td width='50px'></td>\n" +
                            "\t\t\t\t\t</tr>\n" +
                            "\t\t\t\t</table>\n" +
                            "\n" +
                            "\n" +
                            "\t\t\t\t<br/>\n" +
                            "\n" +
                            "\t\t\t\t<table style='text-align: center;'>\n" +
                            "\t\t\t\t\t<tr>\n" +
                            "\t\t\t\t\t\t<td width='30px'></td>\n" +
                            "\t\t\t\t\t\t<td width='640px' style='font-weight: bold; font-size: 12;'>DATOS DEL USUARIO</td>\n" +
                            "\t\t\t\t\t\t<td width='30px'></td>\n" +
                            "\t\t\t\t\t</tr>\n" +
                            "\t\t\t\t</table>\n" +
                            "\n" +
                            "\t\t\t\t<br/><br/><br/>\n" +
                            "\n" +
                            "\t\t\t\t<table>\n" +
                            "\t\t\t\t\t<tr>\n" +
                            "\t\t\t\t\t\t<td width='50px'></td>\n" +
                            "\t\t\t\t\t\t<td width='60px' style='font-size: 11; font-weight: bold; text-align: left;'>NOMBRE:</td>\n" +
                            "\t\t\t\t\t\t<td width='340px' style='border-bottom-width: 1px; border-bottom-style: solid; border-bottom-color: black; text-align: center; font-size: 11;'>"+NombreSQLite+"</td>\n" +
                            "\t\t\t\t\t\t<td width='100px' style='font-size: 11; font-weight: bold; text-align: right;'>No EMPLEADO:</td>\n" +
                            "\t\t\t\t\t\t<td width='100px' style='border-bottom-width: 1px; border-bottom-style: solid; border-bottom-color: black; text-align: center; font-size: 11;'>"+NoEmpleadoSQLite+"</td>\n" +
                            "\t\t\t\t\t\t<td width='50px'></td>\n" +
                            "\t\t\t\t\t</tr>\n" +
                            "\t\t\t\t</table>\n" +
                            "\n" +
                            "\t\t\t\t<br/>\n" +
                            "\n" +
                            "\t\t\t\t<table>\n" +
                            "\t\t\t\t\t<tr>\n" +
                            "\t\t\t\t\t\t<td width='50px'></td>\n" +
                            "\t\t\t\t\t\t<td width='60px' style='font-size: 11; font-weight: bold; text-align: left;'>PUESTO:</td>\n" +
                            "\t\t\t\t\t\t<td width='540px' style='border-bottom-width: 1px; border-bottom-style: solid; border-bottom-color: black; text-align: center; font-size: 11;'>"+PuestoSQLite+"</td>\n" +
                            "\t\t\t\t\t\t<td width='50px'></td>\n" +
                            "\t\t\t\t\t</tr>\n" +
                            "\t\t\t\t</table>\n" +
                            "\n" +
                            "\t\t\t\t<br/>\n" +
                            "\n" +
                            "\n" +
                            "\t\t\t\t<table>\n" +
                            "\t\t\t\t\t<tr>\n" +
                            "\t\t\t\t\t\t<td width='50px'></td>\n" +
                            "\t\t\t\t\t\t<td width='80px' style='font-size: 11; font-weight: bold; text-align: left;'>No. DE RED:</td>\n" +
                            "\t\t\t\t\t\t<td width='160px' style='border-bottom-width: 1px; border-bottom-style: solid; border-bottom-color: black; text-align: center; font-size: 11;'>"+NoRedSQLite+"</td>\n" +
                            "\t\t\t\t\t\t<td width='60px' style='font-size: 11; font-weight: bold; text-align: right;'>E-MAIL:</td>\n" +
                            "\t\t\t\t\t\t<td width='300px' style='border-bottom-width: 1px; border-bottom-style: solid; border-bottom-color: black; text-align: center; font-size: 11;'>"+EmailSQLite+"</td>\n" +
                            "\t\t\t\t\t\t<td width='50px'></td>\n" +
                            "\t\t\t\t\t</tr>\n" +
                            "\t\t\t\t</table>\n" +
                            "\n" +
                            "\t\t\t\t<br/>\n" +
                            "\n" +
                            "\n" +
                            "\t\t\t\t<table>\n" +
                            "\t\t\t\t\t<tr>\n" +
                            "\t\t\t\t\t\t<td width='50px'></td>\n" +
                            "\t\t\t\t\t\t<td width='120px' style='font-size: 11; font-weight: bold; text-align: left;'>CALLE Y NÚMERO:</td>\n" +
                            "\t\t\t\t\t\t<td width='480px' style='border-bottom-width: 1px; border-bottom-style: solid; border-bottom-color: black; text-align: center; font-size: 11;'>"+CalleNumeroSQLite+"</td>\n" +
                            "\t\t\t\t\t\t<td width='50px'></td>\n" +
                            "\t\t\t\t\t</tr>\n" +
                            "\t\t\t\t</table>\n" +
                            "\n" +
                            "\t\t\t\t<br/>\n" +
                            "\n" +
                            "\t\t\t\t<table>\n" +
                            "\t\t\t\t\t<tr>\n" +
                            "\t\t\t\t\t\t<td width='50px'></td>\n" +
                            "\t\t\t\t\t\t<td width='40px' style='font-size: 11; font-weight: bold; text-align: left;'>PISO:</td>\n" +
                            "\t\t\t\t\t\t<td width='60px' style='border-bottom-width: 1px; border-bottom-style: solid; border-bottom-color: black; text-align: center; font-size: 11;'>"+PisoSQLite+"</td>\n" +
                            "\t\t\t\t\t\t<td width='60px' style='font-size: 11; font-weight: bold; text-align: right;'>COLONIA:</td>\n" +
                            "\t\t\t\t\t\t<td width='320px' style='border-bottom-width: 1px; border-bottom-style: solid; border-bottom-color: black; text-align: center; font-size: 11;'>"+ColoniaSQLite+"</td>\n" +
                            "\t\t\t\t\t\t<td width='30px' style='font-size: 11; font-weight: bold; text-align: right;'>CP:</td>\n" +
                            "\t\t\t\t\t\t<td width='50px' style='border-bottom-width: 1px; border-bottom-style: solid; border-bottom-color: black; text-align: center; font-size: 11;'>"+CPSQLite+"</td>\n" +
                            "\t\t\t\t\t\t<td width='50px'></td>\n" +
                            "\t\t\t\t\t</tr>\n" +
                            "\t\t\t\t</table>\n" +
                            "\n" +
                            "\t\t\t\t<br/>\n" +
                            "\n" +
                            "\n" +
                            "\t\t\t\t<table>\n" +
                            "\t\t\t\t\t<tr>\n" +
                            "\t\t\t\t\t\t<td width='50px'></td>\n" +
                            "\t\t\t\t\t\t<td width='60px' style='font-size: 11; font-weight: bold; text-align: left;'>CIUDAD:</td>\n" +
                            "\t\t\t\t\t\t<td width='290px' style='border-bottom-width: 1px; border-bottom-style: solid; border-bottom-color: black; text-align: center; font-size: 11;'>"+CiudadSQLite+"</td>\n" +
                            "\t\t\t\t\t\t<td width='60px' style='font-size: 11; font-weight: bold; text-align: right;'>ESTADO:</td>\n" +
                            "\t\t\t\t\t\t<td width='190px' style='border-bottom-width: 1px; border-bottom-style: solid; border-bottom-color: black; text-align: center; font-size: 11;'>"+EstadoSQLite+"</td>\n" +
                            "\t\t\t\t\t\t<td width='50px'></td>\n" +
                            "\t\t\t\t\t</tr>\n" +
                            "\t\t\t\t</table>\n" +
                            "\n" +
                            "\t\t\t\t<br/>\n" +
                            "\n" +
                            "\t\t\t\t<table>\n" +
                            "\t\t\t\t\t<tr>\n" +
                            "\t\t\t\t\t\t<td width='50px'></td>\n" +
                            "\t\t\t\t\t\t<td width='230px' style='font-size: 11; font-weight: bold; text-align: left;'>UNIDAD ADMINISTRATIVA / HOSPITALARIA:</td>\n" +
                            "\t\t\t\t\t\t<td width='370px' style='border-bottom-width: 1px; border-bottom-style: solid; border-bottom-color: black; text-align: center; font-size: 11;'>"+UnidadAdministrativaSQLite+"</td>\n" +
                            "\t\t\t\t\t\t<td width='50px'></td>\n" +
                            "\t\t\t\t\t</tr>\n" +
                            "\t\t\t\t</table>\n" +
                            "\n" +
                            "\t\t\t\t<br/>\n" +
                            "\n" +
                            "\n" +
                            "\t\t\t\t<table>\n" +
                            "\t\t\t\t\t<tr>\n" +
                            "\t\t\t\t\t\t<td width='50px'></td>\n" +
                            "\t\t\t\t\t\t<td width='30px' style='font-size: 11; font-weight: bold; text-align: left;'>ID:</td>\n" +
                            "\t\t\t\t\t\t<td width='120px' style='border-bottom-width: 1px; border-bottom-style: solid; border-bottom-color: black; text-align: center; font-size: 11;'>"+idUnidadSQLite+"</td>\n" +
                            "\t\t\t\t\t\t<td width='140px' style='font-size: 11; font-weight: bold; text-align: right;'>ÁREA DE ADSCRIPCIÓN:</td>\n" +
                            "\t\t\t\t\t\t<td width='310px' style='border-bottom-width: 1px; border-bottom-style: solid; border-bottom-color: black; text-align: center; font-size: 11;'>"+AreaAdscripcionSQLite+"</td>\n" +
                            "\t\t\t\t\t\t<td width='50px'></td>\n" +
                            "\t\t\t\t\t</tr>\n" +
                            "\t\t\t\t</table>\n" +
                            "\n" +
                            "\n" +
                            "\t\t\t\t<br/><br/><br/>\n" +
                            "\n" +
                            "\t\t\t\t<table style='text-align: center;'>\n" +
                            "\t\t\t\t\t<tr>\n" +
                            "\t\t\t\t\t\t<td width='30px'></td>\n" +
                            "\t\t\t\t\t\t<td width='640px' style='font-weight: bold; font-size: 12;'>DESCRIPCIÓN DEL EQUIPO ASIGNADO</td>\n" +
                            "\t\t\t\t\t\t<td width='30px'></td>\n" +
                            "\t\t\t\t\t</tr>\n" +
                            "\t\t\t\t</table>\n" +
                            "\n" +
                            "\t\t\t\t\n" +
                            "\t\t\t\t<br/>\n" +
                            "\n" +
                            "\t\t\t\t<table border='0' style='border-collapse:collapse;'>\n" +
                            "\t\t<tr>\n" +
                            "\t\t\t<td width='50px'></td>\n" +
                            "\t\t\t<td width='100px' style='font-size: 11; font-weight: bold; text-align: left;'><br/><br/>TIPO DE EQUIPO:</td>\n" +
                            "\t\t\t<td width='250px' style='border-bottom-width: 1px; border-bottom-style: solid; border-bottom-color: black; text-align: center; font-size: 11;'><br/><br/>"+TipoEquipoSQLite+"</td>\n" +
                            "\t\t\t<td width='250px' style='font-size: 11; font-weight: bold; text-align: center; border-bottom-width: 1px; border-bottom-style: solid; border-bottom-color: black;'><br/><br/>OBSERVACIONES</td>\n" +
                            "\t\t\t<td width='50px'></td>\n" +
                            "\t\t</tr>\n" +
                            "\t\t<tr>\n" +
                            "\t\t\t<td width='50px'></td>\n" +
                            "\t\t\t<td width='100px' style='font-size: 11; font-weight: bold; text-align: left;'><br/><br/>MODELO:</td>\n" +
                            "\t\t\t<td width='250px' style='border-bottom-width: 1px; border-bottom-style: solid; border-bottom-color: black; text-align: center; font-size: 11;'><br/><br/><br/>"+ModeloSQLite+"</td>\n" +
                            "\t\t\t<td width='250px' style='border-right-width: 1px; border-left-width: 1px; border-right-style: solid; border-right-color: black; border-left-style: solid; border-left-color: black; text-align: center; font-size: 11;'>"+ObservacionesSQLite+"</td>\n" +
                            "\t\t\t<td width='50px'></td>\n" +
                            "\t\t</tr>\n" +
                            "\t\t<tr>\n" +
                            "\t\t\t<td width='50px'></td>\n" +
                            "\t\t\t<td width='100px' style='font-size: 11; font-weight: bold; text-align: left;'><br/><br/>No. DE SERIE:</td>\n" +
                            "\t\t\t<td width='250px' style='border-bottom-width: 1px; border-bottom-style: solid; border-bottom-color: black; text-align: center; font-size: 11;'><br/><br/><br/>"+serieExtra+"</td>\n" +
                            "\t\t\t<td width='250px' style='border-right-width: 1px; border-left-width: 1px; border-right-style: solid; border-right-color: black; border-left-style: solid; border-left-color: black;'></td>\n" +
                            "\t\t\t<td width='50px'></td>\n" +
                            "\t\t</tr>\n" +
                            "\t\t<tr>\n" +
                            "\t\t\t<td width='50px'></td>\n" +
                            "\t\t\t<td width='100px' style='font-size: 11; font-weight: bold; text-align: left;'></td>\n" +
                            "\t\t\t<td width='250px'></td>\n" +
                            "\t\t\t<td width='250px' style='border-bottom-width: 1px; border-right-width: 1px; border-left-width: 1px; border-right-style: solid; border-right-color: black; border-left-style: solid; border-left-color: black; border-bottom-style: solid; border-bottom-color: black;'></td>\n" +
                            "\t\t\t<td width='50px'></td>\n" +
                            "\t\t</tr>\n" +
                            "\t</table>\n" +
                            "\n" +
                            "\n" +
                            "\t\t\t\t<br/><br/><br/>\n" +
                            "\n" +
                            "\t\t\t\t<table border='0' style='border-collapse:collapse;'>\n" +
                            "\t\t<tr>\n" +
                            "\t\t\t<td width='50px'></td>\n" +
                            "\t\t\t<td width='200px' style='border-top-width: 1px; border-bottom-width: 1px; border-right-width: 1px; border-left-width: 1px; border-right-style: solid; border-right-color: black; border-left-style: solid; border-left-color: black; border-bottom-style: solid; border-bottom-color: black; border-top-style: solid; border-top-color: black; text-align: center; font-size: 11;'><br/><br/><br/><br/><br/><br/><br/>"+NombreSQLite+"<br/></td>\n" +
                            "\t\t\t<td width='200px' style='border-top-width: 1px; border-bottom-width: 1px; border-right-width: 1px; border-left-width: 1px; border-right-style: solid; border-right-color: black; border-left-style: solid; border-left-color: black; border-bottom-style: solid; border-bottom-color: black; border-top-style: solid; border-top-color: black; text-align: center; font-size: 11;'><br/><br/><br/><br/><br/><br/><br/>"+NombreEnlaceSQLite+"<br/></td>\n" +
                            "\t\t\t<td width='200px' style='border-top-width: 1px; border-bottom-width: 1px; border-right-width: 1px; border-left-width: 1px; border-right-style: solid; border-right-color: black; border-left-style: solid; border-left-color: black; border-bottom-style: solid; border-bottom-color: black; border-top-style: solid; border-top-color: black;'><br/><br/><br/><br/><br/><br/><br/><br/></td>\n" +
                            "\t\t\t<td width='50px'></td>\n" +
                            "\t\t</tr>\n" +
                            "\t\t<tr>\n" +
                            "\t\t\t<td width='50px'></td>\n" +
                            "\t\t\t<td width='200px' style='font-size: 8; font-weight: bold; text-align: center; border-left-width: 1px; border-bottom-width: 1px; border-left-style: solid; border-left-color: black; border-bottom-style: solid; border-bottom-color: black; padding-bottom: 10px;'><br/>NOMBRE Y FIRMA DEL USUARIO</td>\n" +
                            "\t\t\t<td width='200px' style='font-size: 8; font-weight: bold; text-align: center; border-bottom-width: 1px; border-bottom-style: solid; border-bottom-color: black; padding-bottom: 10px;'><br/>NOMBRE Y FIRMA DEL ENLACE INFORMATICO</td>\n" +
                            "\t\t\t<td width='200px' style='font-size: 8; font-weight: bold; text-align: center; border-bottom-width: 1px; border-right-width: 1px; border-right-style: solid; border-right-color: black; border-bottom-style: solid; border-bottom-color: black; padding-bottom: 10px;'><br/>SELLO DE LA UNIDAD ADMINISTRATIVA</td>\n" +
                            "\t\t\t<td width='50px'></td>\n" +
                            "\t\t</tr>\n" +
                            "\t</table>\n" +
                            "\n" +
                            "\t\t\t\t<br/><br/><br/>\n" +
                            "\n" +
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

        File documentsPath = new File(NombrePackage + "/", "mis_archivos_issste/");
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
