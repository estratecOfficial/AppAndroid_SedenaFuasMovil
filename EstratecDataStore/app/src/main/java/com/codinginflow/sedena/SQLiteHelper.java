package com.codinginflow.sedena;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteHelper extends SQLiteOpenHelper {

    //VARIABLES DE INFORMACION DE BASE DE DATOS MYSQL
    static String DATABASE_NAME="bd_series_sedena";

    public static final String TABLE_NAME="series";
    public static final String TABLE_NAME_HISTORIAL="historial";
    public static final String Table_Column_ID="id";
    public static final String Table_Column_1_Serie="Serie";
    public static final String Table_Column_1_Tipo="Tipo";
    public static final String Table_Column_2_Modelo="Modelo";
    public static final String Table_Column_3_RegionMilitar="RegionMilitar";
    public static final String Table_Column_3_Cliente="Cliente";
    public static final String Table_Column_4_NombreRango="NombreRango";
    public static final String Table_Column_5_Matricula="Matricula";
    public static final String Table_Column_6_Domicilio="Domicilio";
    public static final String Table_Column_7_ZonaMilitar="ZonaMilitar";
    public static final String Table_Column_8_Batallon="Batallon";
    public static final String Table_Column_9_Area="Area";
    public static final String Table_Column_10_Piso="Piso";
    public static final String Table_Column_11_Telefono="Telefono";
    public static final String Table_Column_12_Extension="Extension";
    public static final String Table_Column_13_ExtensionSatelital="ExtensionSatelital";
    public static final String Table_Column_14_Fecha="Fecha";
    public static final String Table_Column_15_Folio="Folio";
    public static final String Table_Column_16_Contador="Contador";
    public static final String Table_Column_17_SerieSupresor="SerieSupresor";
    public static final String Table_Column_18_Toner="Toner";
    public static final String Table_Column_19_Observaciones="Observaciones";
    public static final String Table_Column_20_URLFormato="URLFormato";
    public static final String Table_Column_21_URLContadores="URLContadores";
    public static final String Table_Column_22_URLConfiguracion="URLConfiguracion";
    public static final String Table_Column_23_Estatus="Estatus";

    public SQLiteHelper(Context context) {

        super(context, DATABASE_NAME, null, 1);

    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        //STRING PARA CREACION DE SQLITE
        String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS "+SQLiteHelper.TABLE_NAME+"("+SQLiteHelper.Table_Column_ID+" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "+SQLiteHelper.Table_Column_1_Serie+" TEXT, "+SQLiteHelper.Table_Column_1_Tipo+" TEXT, "+SQLiteHelper.Table_Column_2_Modelo+" TEXT, "+SQLiteHelper.Table_Column_3_RegionMilitar+" TEXT, "+SQLiteHelper.Table_Column_3_Cliente+" TEXT, "+SQLiteHelper.Table_Column_4_NombreRango+" TEXT, "+SQLiteHelper.Table_Column_5_Matricula+" TEXT, "+SQLiteHelper.Table_Column_6_Domicilio+" TEXT, "+SQLiteHelper.Table_Column_7_ZonaMilitar+" TEXT, "+SQLiteHelper.Table_Column_8_Batallon+" TEXT, "+SQLiteHelper.Table_Column_9_Area+" TEXT, "+SQLiteHelper.Table_Column_10_Piso+" TEXT, "+SQLiteHelper.Table_Column_11_Telefono+" TEXT, "+SQLiteHelper.Table_Column_12_Extension+" TEXT, "+SQLiteHelper.Table_Column_13_ExtensionSatelital+" TEXT, "+SQLiteHelper.Table_Column_14_Fecha+" TEXT, "+SQLiteHelper.Table_Column_15_Folio+" TEXT, "+SQLiteHelper.Table_Column_16_Contador+" TEXT, "+SQLiteHelper.Table_Column_17_SerieSupresor+" TEXT, "+SQLiteHelper.Table_Column_18_Toner+" TEXT, "+SQLiteHelper.Table_Column_19_Observaciones+" TEXT, "+SQLiteHelper.Table_Column_20_URLFormato+" TEXT, "+SQLiteHelper.Table_Column_21_URLContadores+" TEXT, "+SQLiteHelper.Table_Column_22_URLConfiguracion+" TEXT, "+SQLiteHelper.Table_Column_23_Estatus+" TEXT );";
        database.execSQL(CREATE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //STRING PARA ELIMINAR DE SQLITE
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);
    }

}
