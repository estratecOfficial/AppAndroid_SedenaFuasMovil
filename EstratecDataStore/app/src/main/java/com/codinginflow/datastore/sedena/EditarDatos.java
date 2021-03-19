package com.codinginflow.datastore.sedena;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.codinginflow.datastore.R;
import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.Date;

public class EditarDatos extends AppCompatActivity {
    String modeloExtra, serieExtra, clienteSQLite, regionMilitarSQLite, tipoSQLite, nombreRangoSQLite, matriculaSQLite, domicilioSQLite, zonaMilitarSQLite, batallonSQLite, areaSQLite, pisoSQLite, telefonoSQLite, extensionSQLite, extensionSatelitalSQLite, contadorSQLite, serieSupresSQlite, tonerSQLite, observacionesSQLite, tonerResultado;
    TextView tonerEquipoXM, modeloXM, serieXM, clienteXML, regionXM, tipoXML, nombreRangoXM, matriculaXM, domicilioXM, zonaMilitarXM, batallonXM, areaXM, pisoXM, telefonoXM, extensionXM, extensionSatXM, contadorXM, serieSupXM, observacionesXM;
    Button botonGuardar;
    SQLiteHelper sqLiteHelper;
    SQLiteDatabase sqLiteDatabase;
    Cursor cursor;
    View viewSnack;
    Snackbar snackBar;
    ViewGroup groupSnack;
    String estatusEditar = "Editar";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editar_datos);
        setTitle("SEDENA");

        Bundle serieextra = this.getIntent().getExtras();
        serieExtra = serieextra.getString("SERIE_BD");
        Bundle modeloextra = this.getIntent().getExtras();
        modeloExtra = modeloextra.getString("MODELO");

        //OBJETOS XML
        clienteXML = findViewById(R.id.ClienteNombre);
        modeloXM = findViewById(R.id.ModeloActual);
        serieXM = findViewById(R.id.SerieActual);
        regionXM = findViewById(R.id.RegionMilitar);
        tipoXML = findViewById(R.id.tipoEquipoLetra);
        nombreRangoXM = findViewById(R.id.NombreRango);
        matriculaXM = findViewById(R.id.Matricula);
        domicilioXM = findViewById(R.id.Domicilio);
        zonaMilitarXM = findViewById(R.id.ZonaMiliar);
        batallonXM = findViewById(R.id.Batallon);
        areaXM = findViewById(R.id.Area);
        pisoXM = findViewById(R.id.Piso);
        telefonoXM = findViewById(R.id.Telefono);
        extensionXM = findViewById(R.id.Extension);
        extensionSatXM = findViewById(R.id.ExtensionSatelital);
        contadorXM = findViewById(R.id.Contador);
        serieSupXM = findViewById(R.id.SerieSupresor);
        tonerEquipoXM = findViewById(R.id.toner_actual);
        observacionesXM = findViewById(R.id.Observaciones);
        botonGuardar = findViewById(R.id.BotonGuardarCambios);

        //REGISTRO DE DATOS
        serieXM.setText(serieExtra);
        modeloXM.setText(modeloExtra);

        sqLiteHelper = new SQLiteHelper(this);
        sqLiteDatabase = sqLiteHelper.getWritableDatabase();
        ShowSQLiteDBdata();

    }

    @Override
    protected void onResume() {
        //MOSTRAR DATOS DE SQLITE

        super.onResume();
    }

    private void ShowSQLiteDBdata() {

        cursor = sqLiteDatabase.rawQuery("SELECT * FROM "+SQLiteHelper.TABLE_NAME+" WHERE Serie ='"+serieExtra+"'", null);


        //INSERTAR LOS DATOS OBTENIDOS DE LA CONSULTA SQLITE A LOS ARRAY
        if (cursor.moveToFirst()) {
            do {
                tipoSQLite = cursor.getString(cursor.getColumnIndex(SQLiteHelper.Table_Column_1_Tipo));
                clienteSQLite = cursor.getString(cursor.getColumnIndex(SQLiteHelper.Table_Column_3_Cliente));
                regionMilitarSQLite = cursor.getString(cursor.getColumnIndex(SQLiteHelper.Table_Column_3_RegionMilitar));
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
                contadorSQLite = cursor.getString(cursor.getColumnIndex(SQLiteHelper.Table_Column_16_Contador));
                serieSupresSQlite = cursor.getString(cursor.getColumnIndex(SQLiteHelper.Table_Column_17_SerieSupresor));
                tonerSQLite = cursor.getString(cursor.getColumnIndex(SQLiteHelper.Table_Column_18_Toner));
                observacionesSQLite = cursor.getString(cursor.getColumnIndex(SQLiteHelper.Table_Column_19_Observaciones));

                //REGISTRO DE DATOS
                clienteXML.setText(clienteSQLite);
                regionXM.setText(regionMilitarSQLite);
                nombreRangoXM.setText(nombreRangoSQLite);
                matriculaXM.setText(matriculaSQLite);
                domicilioXM.setText(domicilioSQLite);
                zonaMilitarXM.setText(zonaMilitarSQLite);
                batallonXM.setText(batallonSQLite);
                areaXM.setText(areaSQLite);
                pisoXM.setText(pisoSQLite);
                telefonoXM.setText(telefonoSQLite);
                extensionXM.setText(extensionSQLite);
                extensionSatXM.setText(extensionSatelitalSQLite);
                tipoXML.setText(tipoSQLite);
                contadorXM.setText(contadorSQLite);
                serieSupXM.setText(serieSupresSQlite);
                tonerEquipoXM.setText(tonerSQLite);
                observacionesXM.setText(observacionesSQLite);


            } while (cursor.moveToNext());
        }

        botonGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(EditarDatos.this);
                builder.setMessage("¿Estás seguro de guardar los datos? ");
                builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if ( regionXM.getText().toString().matches("") ){
                            viewSnack = findViewById(android.R.id.content);
                            snackBar = Snackbar.make(viewSnack, "Región militar vacía", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null);
                            groupSnack = (ViewGroup) snackBar.getView();
                            groupSnack.setBackgroundColor(ContextCompat.getColor(EditarDatos.this, R.color.colorSnackBar));
                            snackBar.show();
                        }
                        else if ( nombreRangoXM.getText().toString().matches("") ){
                            viewSnack = findViewById(android.R.id.content);
                            snackBar = Snackbar.make(viewSnack, "Nombre y Rango vacíos", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null);
                            groupSnack = (ViewGroup) snackBar.getView();
                            groupSnack.setBackgroundColor(ContextCompat.getColor(EditarDatos.this, R.color.colorSnackBar));
                            snackBar.show();
                        }
                        else if ( matriculaXM.getText().toString().matches("") ){
                            viewSnack = findViewById(android.R.id.content);
                            snackBar = Snackbar.make(viewSnack, "Matricula vacía", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null);
                            groupSnack = (ViewGroup) snackBar.getView();
                            groupSnack.setBackgroundColor(ContextCompat.getColor(EditarDatos.this, R.color.colorSnackBar));
                            snackBar.show();
                        }
                        else if ( domicilioXM.getText().toString().matches("") ){
                            viewSnack = findViewById(android.R.id.content);
                            snackBar = Snackbar.make(viewSnack, "Domicilio vacío", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null);
                            groupSnack = (ViewGroup) snackBar.getView();
                            groupSnack.setBackgroundColor(ContextCompat.getColor(EditarDatos.this, R.color.colorSnackBar));
                            snackBar.show();
                        }
                        else if ( zonaMilitarXM.getText().toString().matches("") ){
                            viewSnack = findViewById(android.R.id.content);
                            snackBar = Snackbar.make(viewSnack, "Zona Militar vacía", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null);
                            groupSnack = (ViewGroup) snackBar.getView();
                            groupSnack.setBackgroundColor(ContextCompat.getColor(EditarDatos.this, R.color.colorSnackBar));
                            snackBar.show();
                        }
                        else if ( batallonXM.getText().toString().matches("") ){
                            viewSnack = findViewById(android.R.id.content);
                            snackBar = Snackbar.make(viewSnack, "Batallón vacío", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null);
                            groupSnack = (ViewGroup) snackBar.getView();
                            groupSnack.setBackgroundColor(ContextCompat.getColor(EditarDatos.this, R.color.colorSnackBar));
                            snackBar.show();
                        }
                        else if ( areaXM.getText().toString().matches("") ){
                            viewSnack = findViewById(android.R.id.content);
                            snackBar = Snackbar.make(viewSnack, "Área vacío", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null);
                            groupSnack = (ViewGroup) snackBar.getView();
                            groupSnack.setBackgroundColor(ContextCompat.getColor(EditarDatos.this, R.color.colorSnackBar));
                            snackBar.show();
                        }
                        else if ( pisoXM.getText().toString().matches("") ){
                            viewSnack = findViewById(android.R.id.content);
                            snackBar = Snackbar.make(viewSnack, "Piso vacío", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null);
                            groupSnack = (ViewGroup) snackBar.getView();
                            groupSnack.setBackgroundColor(ContextCompat.getColor(EditarDatos.this, R.color.colorSnackBar));
                            snackBar.show();
                        }
                        else if ( telefonoXM.getText().toString().matches("") ){
                            viewSnack = findViewById(android.R.id.content);
                            snackBar = Snackbar.make(viewSnack, "Teléfono vacío", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null);
                            groupSnack = (ViewGroup) snackBar.getView();
                            groupSnack.setBackgroundColor(ContextCompat.getColor(EditarDatos.this, R.color.colorSnackBar));
                            snackBar.show();
                        }
                        else if ( contadorXM.getText().toString().matches("") ){
                            viewSnack = findViewById(android.R.id.content);
                            snackBar = Snackbar.make(viewSnack, "Contador vacío", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null);
                            groupSnack = (ViewGroup) snackBar.getView();
                            groupSnack.setBackgroundColor(ContextCompat.getColor(EditarDatos.this, R.color.colorSnackBar));
                            snackBar.show();
                        }
                        else if ( serieSupXM.getText().toString().matches("") ){
                            viewSnack = findViewById(android.R.id.content);
                            snackBar = Snackbar.make(viewSnack, "Serie supresor vacío", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null);
                            groupSnack = (ViewGroup) snackBar.getView();
                            groupSnack.setBackgroundColor(ContextCompat.getColor(EditarDatos.this, R.color.colorSnackBar));
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

        sqLiteHelper = new SQLiteHelper(this);
        sqLiteDatabase = sqLiteHelper.getWritableDatabase();
        sqLiteDatabase.execSQL("UPDATE " + SQLiteHelper.TABLE_NAME + " SET RegionMilitar='"+ regionXM.getText() +"',"+  " NombreRango='"+ nombreRangoXM.getText() + "'," + " Matricula='"+ matriculaXM.getText() + "'," + " Domicilio='"+ domicilioXM.getText() + "'," + " ZonaMilitar='"+ zonaMilitarXM.getText() + "'," + " Batallon='"+ batallonXM.getText() + "'," + " Area='"+ areaXM.getText() + "'," + " Piso='"+ pisoXM.getText() + "'," + " Telefono='"+ telefonoXM.getText() + "'," + " Extension='"+ extensionXM.getText() + "'," + " ExtensionSatelital='"+ extensionSatXM.getText() + "'," + " Fecha='"+ fechacComplString + "'," + " Contador='"+ contadorXM.getText() + "'," +  " SerieSupresor='"+ serieSupXM.getText() + "'," + " Observaciones='"+ observacionesXM.getText() + "'," + " Estatus='"+ estatusEditar + "'" +  " WHERE Serie='"+ serieExtra +"'");
        sqLiteDatabase.close();


        Intent intent = new Intent(EditarDatos.this, ShowDataActivity.class);
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
