package com.codinginflow.sedena;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class ShowDataActivityCompleto extends AppCompatActivity implements SearchView.OnQueryTextListener {
    SQLiteHelper sqLiteHelper;
    SQLiteDatabase sqLiteDatabase;
    Cursor cursor;
    android.widget.ListAdapter listAdapter ;
    ListView LISTVIEW;
    SearchView searchView;
    ArrayList<Series> seriesList =  new ArrayList<>();
    ArrayList<String> ListViewClickItemArray = new ArrayList<String>();
    String estatusCompleto = "En cola";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_show_data_completo);

        searchView=(SearchView) findViewById(R.id.searchViewCompleto);
        LISTVIEW = (ListView) findViewById(R.id.listViewCompleto);
        //ARRAY LIST VARIABLES

        sqLiteHelper = new SQLiteHelper(this);
        LISTVIEW.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub
                Toast.makeText(ShowDataActivityCompleto.this, ListViewClickItemArray.get(position).toString(), Toast.LENGTH_LONG).show();
            }
        });
        ShowSQLiteDBdata();
    }
    @Override
    protected void onResume() {
        //MOSTRAR DATOS DE SQLITE

        super.onResume();
    }

    private void ShowSQLiteDBdata() {
        sqLiteDatabase = sqLiteHelper.getWritableDatabase();
        cursor = sqLiteDatabase.rawQuery("SELECT * FROM "+SQLiteHelper.TABLE_NAME+" WHERE Estatus ='" + estatusCompleto +"'" , null);
        int contador = 0;


        //INSERTAR LOS DATOS OBTENIDOS DE LA CONSULTA SQLITE A LOS ARRAY
        if (cursor.moveToFirst()) {
            do {
                contador++;
                seriesList.add(new Series(cursor.getString(cursor.getColumnIndex(SQLiteHelper.Table_Column_ID)),cursor.getString(cursor.getColumnIndex(SQLiteHelper.Table_Column_1_Serie)), cursor.getString(cursor.getColumnIndex(SQLiteHelper.Table_Column_2_Modelo)),cursor.getString(cursor.getColumnIndex(SQLiteHelper.Table_Column_3_RegionMilitar)),cursor.getString(cursor.getColumnIndex(SQLiteHelper.Table_Column_23_Estatus))));

            } while (cursor.moveToNext());
        }

        if (contador == 0){
            showAlertDialogInternet(this, "NO HAS COMPLETADO SERIES",
                    "Para ver el listado de series completas, tienes que capturar la información y guardar las evidencias localmente", true);
        }
        else {
            listAdapter = new com.codinginflow.sedena.ListAdapterCompleto(ShowDataActivityCompleto.this, seriesList);
            LISTVIEW.setAdapter(listAdapter);
            LISTVIEW.setTextFilterEnabled(true);
            setupSearchView();
        }

        cursor.close();
        sqLiteDatabase.close();
    }

    private void setupSearchView()
    {
        searchView.setIconifiedByDefault(false);
        searchView.setOnQueryTextListener(this);
        searchView.setSubmitButtonEnabled(true);
        searchView.setQueryHint("Buscar serie");
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (TextUtils.isEmpty(newText)) {
            LISTVIEW.clearTextFilter();
        } else {
            LISTVIEW.setFilterText(newText);
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setMessage("¿Estás seguro de regresar al inicio? ");
        builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(ShowDataActivityCompleto.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra("usuarioLogin", Login.idUsuario);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }

    public void showAlertDialogInternet(Context context, String title, String message, Boolean status) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
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
