package com.codinginflow.datastore.issste;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.codinginflow.datastore.R;
import com.codinginflow.datastore.sedena.EditarDatos;
import com.codinginflow.datastore.sedena.SQLiteHelper;
import com.codinginflow.datastore.sedena.ShowDataActivity;
import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.Date;

public class EditarDatosISSSTE extends AppCompatActivity {
    String modeloExtra, serieExtra, clienteSQLite, tipoEquipoSQLite, unidadSQLIte, areaAdsSQLite, calleNumSQLite, pisoSQLite, coloniaSQLite, CPSQLite, ciudadSQLite, estadoSQLite, nombreSQLite, puestoSQLite, noEmpleadoSQLite, noRedSQLite, emailSQLite, fechaSQLite, folioSQLite, estatusSQLite, ObservacionesSQLite, idUnidadSQLite, NombreEnlaceSQLite;
    TextView modeloXM, serieXM, clienteXML, tipoEquipoXML, unidadXML, areaAdsXML, calleXML, pisoXML, coloniaXML, CPXML, ciudadXML, estadoXML, nombreXML, puestoXML, noEmpleadoXML, noRedXML, emailXML, fechaXML, folioXML, estatusXML, ObservacionesXML, idUnidadXML, NombreEnlaceXML;
    Button botonGuardar;
    SQLiteHelperISSSTE sqLiteHelperISSSTE;
    SQLiteDatabase sqLiteDatabase;
    Cursor cursor;
    View viewSnack;
    Snackbar snackBar;
    ViewGroup groupSnack;
    String estatusEditar = "Editar";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editar_datos_issste);
        setTitle("ISSSTE");

        Bundle serieextra = this.getIntent().getExtras();
        serieExtra = serieextra.getString("SERIE_BD");
        Bundle modeloextra = this.getIntent().getExtras();
        modeloExtra = modeloextra.getString("MODELO");

        //OBJETOS XML
        clienteXML = findViewById(R.id.ClienteNombreISSSTE);
        modeloXM = findViewById(R.id.ModeloActualISSSTE);
        serieXM = findViewById(R.id.SerieActualISSSTE);
        tipoEquipoXML = findViewById(R.id.tipoEquipoLetraISSSTE);
        idUnidadXML = findViewById(R.id.idUnidadISSSTE);
        unidadXML = findViewById(R.id.UnidadAdminISSSTE);
        areaAdsXML = findViewById(R.id.AreaAdsISSSTE);
        calleXML = findViewById(R.id.CalleISSSTE);
        NombreEnlaceXML = findViewById(R.id.NombreEnlaceISSSTE);
        pisoXML = findViewById(R.id.PisoISSSTE);
        coloniaXML = findViewById(R.id.ColoniaISSSTE);
        CPXML = findViewById(R.id.CPISSSTE);
        ciudadXML = findViewById(R.id.CiudadISSSTE);
        estadoXML = findViewById(R.id.EstadoISSSTE);
        nombreXML = findViewById(R.id.NombreUsuarioISSSTE);
        puestoXML = findViewById(R.id.PuestoISSSTE);
        noEmpleadoXML = findViewById(R.id.NoEmpleadoISSSTE);
        noRedXML = findViewById(R.id.NoRedISSSTE);
        emailXML = findViewById(R.id.EmailISSSTE);
        ObservacionesXML = findViewById(R.id.ObservacionesISSSTE);
        botonGuardar = findViewById(R.id.BotonGuardarCambiosISSSTE);

        //REGISTRO DE DATOS
        serieXM.setText(serieExtra);
        modeloXM.setText(modeloExtra);

        sqLiteHelperISSSTE = new SQLiteHelperISSSTE(this);
        sqLiteDatabase = sqLiteHelperISSSTE.getWritableDatabase();
        ShowSQLiteDBdata();

    }

    @Override
    protected void onResume() {
        //MOSTRAR DATOS DE SQLITE

        super.onResume();
    }

    private void ShowSQLiteDBdata() {

        cursor = sqLiteDatabase.rawQuery("SELECT * FROM "+SQLiteHelperISSSTE.TABLE_NAME+" WHERE Serie ='"+serieExtra+"'", null);


        //INSERTAR LOS DATOS OBTENIDOS DE LA CONSULTA SQLITE A LOS ARRAY
        if (cursor.moveToFirst()) {
            do {
                clienteSQLite = cursor.getString(cursor.getColumnIndex(SQLiteHelperISSSTE.Table_Column_2_Cliente));
                tipoEquipoSQLite = cursor.getString(cursor.getColumnIndex(SQLiteHelperISSSTE.Table_Column_4_TipoEquipo));
                unidadSQLIte = cursor.getString(cursor.getColumnIndex(SQLiteHelperISSSTE.Table_Column_5_UnidadAdministrativa));
                areaAdsSQLite = cursor.getString(cursor.getColumnIndex(SQLiteHelperISSSTE.Table_Column_6_AreaAdscripcion));
                calleNumSQLite = cursor.getString(cursor.getColumnIndex(SQLiteHelperISSSTE.Table_Column_7_CalleNumero));
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
                ObservacionesSQLite = cursor.getString(cursor.getColumnIndex(SQLiteHelperISSSTE.Table_Column_19_Observaciones));
                idUnidadSQLite = cursor.getString(cursor.getColumnIndex(SQLiteHelperISSSTE.Table_Column_23_idUnidad));
                NombreEnlaceSQLite = cursor.getString(cursor.getColumnIndex(SQLiteHelperISSSTE.Table_Column_24_NombreEnlace));

                //REGISTRO DE DATOS
                clienteXML.setText(clienteSQLite);
                tipoEquipoXML.setText(tipoEquipoSQLite);
                unidadXML.setText(unidadSQLIte);
                areaAdsXML.setText(areaAdsSQLite);
                calleXML.setText(calleNumSQLite);
                pisoXML.setText(pisoSQLite);
                coloniaXML.setText(coloniaSQLite);
                CPXML.setText(CPSQLite);
                ciudadXML.setText(ciudadSQLite);
                estadoXML.setText(estadoSQLite);
                nombreXML.setText(nombreSQLite);
                puestoXML.setText(puestoSQLite);
                noEmpleadoXML.setText(noEmpleadoSQLite);
                noRedXML.setText(noRedSQLite);
                emailXML.setText(emailSQLite);
                ObservacionesXML.setText(ObservacionesSQLite);
                idUnidadXML.setText(idUnidadSQLite);
                NombreEnlaceXML.setText(NombreEnlaceSQLite);


            } while (cursor.moveToNext());
        }

        botonGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(EditarDatosISSSTE.this);
                builder.setMessage("¿Estás seguro de guardar los datos? ");
                builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if ( nombreXML.getText().toString().matches("") ){
                            viewSnack = findViewById(android.R.id.content);
                            snackBar = Snackbar.make(viewSnack, "El nombre esta vacío", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null);
                            groupSnack = (ViewGroup) snackBar.getView();
                            groupSnack.setBackgroundColor(ContextCompat.getColor(EditarDatosISSSTE.this, R.color.colorSnackBar));
                            snackBar.show();
                        }
                        else if ( puestoXML.getText().toString().matches("") ){
                            viewSnack = findViewById(android.R.id.content);
                            snackBar = Snackbar.make(viewSnack, "El puesto esta vacío", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null);
                            groupSnack = (ViewGroup) snackBar.getView();
                            groupSnack.setBackgroundColor(ContextCompat.getColor(EditarDatosISSSTE.this, R.color.colorSnackBar));
                            snackBar.show();
                        }
                        else if ( noEmpleadoXML.getText().toString().matches("") ){
                            viewSnack = findViewById(android.R.id.content);
                            snackBar = Snackbar.make(viewSnack, "El número de empleado esta vacío", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null);
                            groupSnack = (ViewGroup) snackBar.getView();
                            groupSnack.setBackgroundColor(ContextCompat.getColor(EditarDatosISSSTE.this, R.color.colorSnackBar));
                            snackBar.show();
                        }
                        else if ( noRedXML.getText().toString().matches("") ){
                            viewSnack = findViewById(android.R.id.content);
                            snackBar = Snackbar.make(viewSnack, "El número de red vacío", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null);
                            groupSnack = (ViewGroup) snackBar.getView();
                            groupSnack.setBackgroundColor(ContextCompat.getColor(EditarDatosISSSTE.this, R.color.colorSnackBar));
                            snackBar.show();
                        }
                        else if ( emailXML.getText().toString().matches("") ){
                            viewSnack = findViewById(android.R.id.content);
                            snackBar = Snackbar.make(viewSnack, "El email vacío", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null);
                            groupSnack = (ViewGroup) snackBar.getView();
                            groupSnack.setBackgroundColor(ContextCompat.getColor(EditarDatosISSSTE.this, R.color.colorSnackBar));
                            snackBar.show();
                        }
                        else if (!Patterns.EMAIL_ADDRESS.matcher(emailXML.getText().toString()).matches()){
                            viewSnack = findViewById(android.R.id.content);
                            snackBar = Snackbar.make(viewSnack, "El email no es válido", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null);
                            groupSnack = (ViewGroup) snackBar.getView();
                            groupSnack.setBackgroundColor(ContextCompat.getColor(EditarDatosISSSTE.this, R.color.colorSnackBar));
                            snackBar.show();
                        }
                        else{
                            actualizarDatos();
                        }

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
        });

        cursor.close();
        sqLiteDatabase.close();
    }

    private void actualizarDatos() {

        //OBTIENE FECHA
        Date d=new Date();
        SimpleDateFormat fecc=new SimpleDateFormat("yyyy-MM'-'d");
        String fechacComplString = fecc.format(d);

        sqLiteHelperISSSTE = new SQLiteHelperISSSTE(this);
        sqLiteDatabase = sqLiteHelperISSSTE.getWritableDatabase();
        sqLiteDatabase.execSQL("UPDATE " + SQLiteHelperISSSTE.TABLE_NAME + " SET Nombre='"+ nombreXML.getText() +"',"+  " Puesto='"+ puestoXML.getText() + "'," + " NoEmpleado='"+ noEmpleadoXML.getText() + "'," + " NoRed='"+ noRedXML.getText() + "'," + " Email='"+ emailXML.getText() + "'," + " Fecha='"+ fechacComplString  + "'," + " Observaciones='"+ ObservacionesXML.getText() + "'," + " Estatus='"+ estatusEditar + "'" +  " WHERE Serie='"+ serieExtra +"'");
        sqLiteDatabase.close();


        Intent intent = new Intent(EditarDatosISSSTE.this, ShowDataActivityISSSTE.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);


    }

    @Override
    public void onBackPressed() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setMessage("¿Estás seguro salir, no se guardarán los datos? ");
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
}
