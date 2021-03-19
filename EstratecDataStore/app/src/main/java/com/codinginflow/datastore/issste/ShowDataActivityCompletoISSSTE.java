package com.codinginflow.datastore.issste;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.codinginflow.datastore.Login;
import com.codinginflow.datastore.R;
import com.codinginflow.datastore.sedena.SQLiteHelper;

import java.util.ArrayList;

public class ShowDataActivityCompletoISSSTE extends AppCompatActivity implements SearchView.OnQueryTextListener {
    SQLiteHelperISSSTE sqLiteHelperISSSTE;
    SQLiteDatabase sqLiteDatabase;
    Cursor cursor;
    ListAdapterCompletoISSSTE listAdapterCompletoISSSTE;
    ListView LISTVIEW;
    SearchView searchView;
    ArrayList<seriesISSSTE> seriesList =  new ArrayList<>();
    ArrayList<String> ListViewClickItemArray = new ArrayList<String>();
    String estatusCompleto = "En cola";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_show_data_completo_issste);
        setTitle("ISSSTE");

        searchView=(SearchView) findViewById(R.id.searchViewCompletoISSSTE);
        LISTVIEW = (ListView) findViewById(R.id.listViewCompletoISSSTE);
        //ARRAY LIST VARIABLES

        sqLiteHelperISSSTE = new SQLiteHelperISSSTE(this);
        LISTVIEW.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub
                Toast.makeText(ShowDataActivityCompletoISSSTE.this, ListViewClickItemArray.get(position).toString(), Toast.LENGTH_LONG).show();
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
        sqLiteDatabase = sqLiteHelperISSSTE.getWritableDatabase();
        cursor = sqLiteDatabase.rawQuery("SELECT * FROM "+SQLiteHelperISSSTE.TABLE_NAME+" WHERE Estatus ='" + estatusCompleto +"'" , null);
        int contador = 0;


        //INSERTAR LOS DATOS OBTENIDOS DE LA CONSULTA SQLITE A LOS ARRAY
        if (cursor.moveToFirst()) {
            do {
                contador++;
                seriesList.add(new seriesISSSTE(cursor.getString(cursor.getColumnIndex(SQLiteHelperISSSTE.Table_Column_ID)),cursor.getString(cursor.getColumnIndex(SQLiteHelperISSSTE.Table_Column_1_Serie)), cursor.getString(cursor.getColumnIndex(SQLiteHelperISSSTE.Table_Column_3_Modelo)),cursor.getString(cursor.getColumnIndex(SQLiteHelperISSSTE.Table_Column_5_UnidadAdministrativa)),cursor.getString(cursor.getColumnIndex(SQLiteHelperISSSTE.Table_Column_22_Estatus))));

            } while (cursor.moveToNext());
        }

        if (contador == 0){
            showAlertDialogInternet(this, "NO HAY SERIES EN ESPERA",
                    "No hay series pendientes por subir al servidor o aún no has completado ninguna", true);
        }
        else {
            listAdapterCompletoISSSTE = new ListAdapterCompletoISSSTE(ShowDataActivityCompletoISSSTE.this, seriesList);
            LISTVIEW.setAdapter(listAdapterCompletoISSSTE);
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
                Intent intent = new Intent(ShowDataActivityCompletoISSSTE.this, main_activity_issste.class);
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
