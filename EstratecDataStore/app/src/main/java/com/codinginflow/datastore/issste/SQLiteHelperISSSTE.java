package com.codinginflow.datastore.issste;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.codinginflow.datastore.sedena.SQLiteHelper;

public class SQLiteHelperISSSTE extends SQLiteOpenHelper {

    //VARIABLES DE INFORMACION DE BASE DE DATOS MYSQL
    public static final String DATABASE_NAME="bd_series_issste";

    public static final String TABLE_NAME="series";
    public static final String TABLE_NAME_HISTORIAL="historial";
    public static final String Table_Column_ID="id";
    public static final String Table_Column_1_Serie="Serie";
    public static final String Table_Column_2_Cliente="Cliente";
    public static final String Table_Column_3_Modelo="Modelo";
    public static final String Table_Column_4_TipoEquipo="TipoEquipo";
    public static final String Table_Column_5_UnidadAdministrativa="UnidadAdministrativa";
    public static final String Table_Column_6_AreaAdscripcion="AreaAdscripcion";
    public static final String Table_Column_7_CalleNumero="CalleNumero";
    public static final String Table_Column_8_Piso="Piso";
    public static final String Table_Column_9_Colonia="Colonia";
    public static final String Table_Column_10_CP="CP";
    public static final String Table_Column_11_Ciudad="Ciudad";
    public static final String Table_Column_12_Estado="Estado";
    public static final String Table_Column_13_Nombre="Nombre";
    public static final String Table_Column_13_Puesto="Puesto";
    public static final String Table_Column_14_NoEmpleado="NoEmpleado";
    public static final String Table_Column_15_NoRed="NoRed";
    public static final String Table_Column_16_Email="Email";
    public static final String Table_Column_17_Fecha="Fecha";
    public static final String Table_Column_18_Folio="Folio";
    public static final String Table_Column_19_Observaciones="Observaciones";
    public static final String Table_Column_20_URLFormato="URLFormato";
    public static final String Table_Column_21_URLContadores="URLContadores";
    public static final String Table_Column_22_Estatus="Estatus";
    public static final String Table_Column_23_idUnidad="idUnidad";
    public static final String Table_Column_24_NombreEnlace="NombreEnlace";

    public SQLiteHelperISSSTE(Context context) {

        super(context, DATABASE_NAME, null, 1);

    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        //STRING PARA CREACION DE SQLITE
        String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS "+ SQLiteHelperISSSTE.TABLE_NAME+"("+SQLiteHelperISSSTE.Table_Column_ID+" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "+SQLiteHelperISSSTE.Table_Column_1_Serie+" TEXT, "+SQLiteHelperISSSTE.Table_Column_2_Cliente+" TEXT, "+SQLiteHelperISSSTE.Table_Column_3_Modelo+" TEXT, "+SQLiteHelperISSSTE.Table_Column_4_TipoEquipo+" TEXT, "+SQLiteHelperISSSTE.Table_Column_5_UnidadAdministrativa+" TEXT, "+SQLiteHelperISSSTE.Table_Column_6_AreaAdscripcion+" TEXT, "+SQLiteHelperISSSTE.Table_Column_7_CalleNumero+" TEXT, "+SQLiteHelperISSSTE.Table_Column_8_Piso+" TEXT, "+SQLiteHelperISSSTE.Table_Column_9_Colonia+" TEXT, "+SQLiteHelperISSSTE.Table_Column_10_CP+" TEXT, "+SQLiteHelperISSSTE.Table_Column_11_Ciudad+" TEXT, "+SQLiteHelperISSSTE.Table_Column_12_Estado+" TEXT, "+SQLiteHelperISSSTE.Table_Column_13_Nombre+" TEXT, " +SQLiteHelperISSSTE.Table_Column_13_Puesto+" TEXT, "+SQLiteHelperISSSTE.Table_Column_14_NoEmpleado+" TEXT, "+SQLiteHelperISSSTE.Table_Column_15_NoRed+" TEXT, "+SQLiteHelperISSSTE.Table_Column_16_Email+" TEXT, "+SQLiteHelperISSSTE.Table_Column_17_Fecha+" TEXT, "+SQLiteHelperISSSTE.Table_Column_18_Folio+" TEXT, "+SQLiteHelperISSSTE.Table_Column_19_Observaciones+" TEXT, "+SQLiteHelperISSSTE.Table_Column_20_URLFormato+" TEXT, "+SQLiteHelperISSSTE.Table_Column_21_URLContadores+" TEXT, "+SQLiteHelperISSSTE.Table_Column_22_Estatus+" TEXT,"+SQLiteHelperISSSTE.Table_Column_23_idUnidad+" TEXT, "+SQLiteHelperISSSTE.Table_Column_24_NombreEnlace+" TEXT);";
        database.execSQL(CREATE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //STRING PARA ELIMINAR DE SQLITE
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);
    }

}
