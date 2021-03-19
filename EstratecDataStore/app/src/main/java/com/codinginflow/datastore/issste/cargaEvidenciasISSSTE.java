package com.codinginflow.datastore.issste;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import com.codinginflow.datastore.R;
import com.google.android.material.snackbar.Snackbar;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class cargaEvidenciasISSSTE extends AppCompatActivity {
    private Button btnTomatFotoFUA;
    private Button btnTomarFotoContador;
    private Button btnGuardarEvidencias;
    private static final int FOTO_FUA = 1;
    private static final int FOTO_CONTADOR = 2;
    private static final int MY_CAMERA = 0;
    private static final int MY_WRITE_EXTERNAL_STORAGE = 0;
    private View mLayout;
    private ProgressDialog pDialog;

    File fotoFUA;
    File fotoContador;
    EditText rutaFua, rutaContador;
    String valorRutaFua, valorRutaContador, serieExtra, estatusCompleto = "En cola";

    SQLiteHelperISSSTE sqLiteHelperISSSTE;
    SQLiteDatabase sqLiteDatabase;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.evidencias_layout_issste);
        setTitle("ISSSTE");

        Bundle extra = this.getIntent().getExtras();
        serieExtra = extra.getString("SERIE_BD");

        mLayout = findViewById(R.id.ConstraintlayoutISSSSTE);
        btnTomatFotoFUA = (Button) findViewById(R.id.btnTomarFotoFUAISSSTE);
        btnTomarFotoContador = (Button) findViewById(R.id.btnTomarFotoContadorISSSTE);
        btnGuardarEvidencias = (Button) findViewById(R.id.btnGuardarEvidenciasSQLISSSTE);
        rutaFua = findViewById(R.id.rutaFUAISSSTE);
        rutaContador = findViewById(R.id.rutaContadorISSSTE);

        //Rutas en blanco
        rutaFua.setText("");
        rutaContador.setText("");

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        btnTomatFotoFUA.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                verifyPermissionCamaraFotoFUA();
            }
        });

        btnTomarFotoContador.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                verifyPermissionCamaraFotoContador();
            }
        });

        btnGuardarEvidencias.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(cargaEvidenciasISSSTE.this);
                builder.setMessage("¿Estás seguro de guardar las evidencias, recuerda ya no podrás modificar la información? ");
                builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        guardaEvidenciasSQLite();
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

    }

    //Verificar permiso para tomar foto del FUA
    private void verifyPermissionCamaraFotoFUA() {

        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permissionCheck2 = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            requestPermissionWriteExternaStorage();
        } else if (permissionCheck2 != PackageManager.PERMISSION_GRANTED) {
            requestPermissionCamera();
        } else {
            //Creamos el Intent para llamar a la Camara
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N){
                File imagesFolder = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "ISSSTE/FUAS");
                imagesFolder.mkdirs();
                fotoFUA = new File(imagesFolder, getCodeFUA() + ".jpg");
                Log.d("Foto FUA!!!", String.valueOf(fotoFUA));
                Uri uriSavedImage = Uri.fromFile(fotoFUA);
                //Le decimos al Intent que queremos grabar la imagen
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriSavedImage);
                //Lanzamos la aplicacion de la camara con retorno (forResult)
                startActivityForResult(cameraIntent, FOTO_FUA);
            } else{
                File imagesFolder = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "ISSSTE/FUAS");
                imagesFolder.mkdirs();
                fotoFUA = new File(imagesFolder, getCodeFUA() + ".jpg");
                Log.d("Foto FUA!!!", String.valueOf(fotoFUA));
                Uri uriSavedImage = FileProvider.getUriForFile(this, getPackageName() + ".provider", fotoFUA);
                //Le decimos al Intent que queremos grabar la imagen
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriSavedImage);
                //Lanzamos la aplicacion de la camara con retorno (forResult)
                startActivityForResult(cameraIntent, FOTO_FUA);

            }
        }
    }

    //Verificar permiso para tomar foto del contador
    private void verifyPermissionCamaraFotoContador() {

        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permissionCheck2 = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            requestPermissionWriteExternaStorage();
        } else if (permissionCheck2 != PackageManager.PERMISSION_GRANTED) {
            requestPermissionCamera();
        } else {
            //Creamos el Intent para llamar a la Camara
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N){
                File imagesFolder = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "ISSSTE/Contador");
                imagesFolder.mkdirs();
                fotoContador = new File(imagesFolder, getCodeContador() + ".jpg");
                Log.d("Foto Contador!!!", String.valueOf(fotoContador));
                Uri uriSavedImage = Uri.fromFile(fotoContador);
                //Le decimos al Intent que queremos grabar la imagen
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriSavedImage);
                //Lanzamos la aplicacion de la camara con retorno (forResult)
                startActivityForResult(cameraIntent, FOTO_CONTADOR);
            } else{
                File imagesFolder = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "ISSSTE/Contador");
                imagesFolder.mkdirs();
                fotoContador = new File(imagesFolder, getCodeContador() + ".jpg");
                Log.d("Foto Contador!!!", String.valueOf(fotoContador));
                Uri uriSavedImage = FileProvider.getUriForFile(this, getPackageName() + ".provider", fotoContador);
                //Le decimos al Intent que queremos grabar la imagen
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriSavedImage);
                //Lanzamos la aplicacion de la camara con retorno (forResult)
                startActivityForResult(cameraIntent, FOTO_CONTADOR);

            }
        }
    }

    private String getCodeFUA() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH_mm_ss");
        String date = dateFormat.format(new Date());
        String photoCode = "FUA_" + date;
        return photoCode;
    }

    private String getCodeContador() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH_mm_ss");
        String date = dateFormat.format(new Date());
        String photoCode = "CONTADOR_" + date;
        return photoCode;
    }


    private void requestPermissionWriteExternaStorage() {
        //shouldShowRequestPermissionRationale es verdadero solamente si ya se había mostrado
        //anteriormente el dialogo de permisos y el usuario lo negó
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            showSnackBarWriteExternaStorage();
        } else {
            //si es la primera vez se solicita el permiso directamente
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_WRITE_EXTERNAL_STORAGE);
        }
    }

    private void requestPermissionCamera() {
        //shouldShowRequestPermissionRationale es verdadero solamente si ya se había mostrado
        //anteriormente el dialogo de permisos y el usuario lo negó
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
            showSnackBarCamera();
        } else {
            //si es la primera vez se solicita el permiso directamente
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                    MY_CAMERA);
        }
    }

    // Procesar respuesta de usuario
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //Si el requestCode corresponde al que usamos para solicitar el permiso y
        //la respuesta del usuario fue positiva

        if (requestCode == MY_WRITE_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else {
                showSnackBarWriteExternaStorage();
            }
        }

        if (requestCode == MY_CAMERA) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else {
                showSnackBarCamera();
            }
        }
    }

    private void showSnackBarWriteExternaStorage() {
        Snackbar.make(mLayout, R.string.permission_write_storage,
                Snackbar.LENGTH_LONG)
                .setAction(R.string.settings, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        openSettings();
                    }
                })
                .show();
    }

    private void showSnackBarCamera() {
        Snackbar.make(mLayout, R.string.permission_camera,
                Snackbar.LENGTH_LONG)
                .setAction(R.string.settings, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        openSettings();
                    }
                })
                .show();
    }

    public void openSettings() {
        Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivity(intent);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FOTO_FUA && resultCode == Activity.RESULT_OK) {

            File image_uris = fotoFUA;
            Bitmap bm = BitmapFactory.decodeFile(String.valueOf(image_uris));
            FileOutputStream fileOutputStream = null;
            try {
                fileOutputStream = new FileOutputStream(image_uris);
                bm.compress(Bitmap.CompressFormat.JPEG, 20, fileOutputStream);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (fileOutputStream != null) {
                        fileOutputStream.flush();
                        fileOutputStream.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            //REGISTRO EN LOS EDITTEXT
            rutaFua.setText(String.valueOf(fotoFUA));

            //CAMBIAR COLOR DE BOTON
            btnTomatFotoFUA.setBackgroundResource(R.drawable.btn_foto_success);
            btnTomatFotoFUA.setEnabled(false);

            View view = findViewById(android.R.id.content);
            Snackbar snack = Snackbar.make(view, "La foto del FUA se guardó correctamente", Snackbar.LENGTH_LONG)
                    .setAction("Action", null);
            ViewGroup group = (ViewGroup) snack.getView();
            group.setBackgroundColor(ContextCompat.getColor(this, R.color.colorSnackBarError));
            snack.show();
        } else if(requestCode == FOTO_CONTADOR && resultCode == Activity.RESULT_OK) {

            File image_uris = fotoContador;
            Bitmap bm = BitmapFactory.decodeFile(String.valueOf(image_uris));
            FileOutputStream fileOutputStream = null;
            try {
                fileOutputStream = new FileOutputStream(image_uris);
                bm.compress(Bitmap.CompressFormat.JPEG, 20, fileOutputStream);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (fileOutputStream != null) {
                        fileOutputStream.flush();
                        fileOutputStream.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            //REGISTRO EN LOS EDITTEXT
            rutaContador.setText(String.valueOf(fotoContador));

            //CAMBIAR COLOR DE BOTON
            btnTomarFotoContador.setBackgroundResource(R.drawable.btn_foto_success);
            btnTomarFotoContador.setEnabled(false);

            View view = findViewById(android.R.id.content);
            Snackbar snack = Snackbar.make(view, "La foto del Contador se guardó correctamente", Snackbar.LENGTH_LONG)
                    .setAction("Action", null);
            ViewGroup group = (ViewGroup) snack.getView();
            group.setBackgroundColor(ContextCompat.getColor(this, R.color.colorSnackBarError));
            snack.show();

        }
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    public void guardaEvidenciasSQLite() {

        valorRutaFua = rutaFua.getText().toString();
        valorRutaContador = rutaContador.getText().toString();

        if (TextUtils.isEmpty(valorRutaFua)){
            View view = findViewById(android.R.id.content);
            Snackbar snack = Snackbar.make(view, "No has guardado una evidencia del FUA", Snackbar.LENGTH_LONG)
                    .setAction("Action", null);
            ViewGroup group = (ViewGroup) snack.getView();
            group.setBackgroundColor(ContextCompat.getColor(this, R.color.colorSnackBar));
            snack.show();
        }
        if (!TextUtils.isEmpty(valorRutaFua )){

            sqLiteHelperISSSTE = new SQLiteHelperISSSTE(this);
            sqLiteDatabase = sqLiteHelperISSSTE.getWritableDatabase();
            sqLiteDatabase.execSQL("UPDATE " + SQLiteHelperISSSTE.TABLE_NAME + " SET URLFormato='"+ valorRutaFua +"',"+  " URLContadores='"+ valorRutaContador + "'," + " Estatus='"+ estatusCompleto + "'" +  " WHERE Serie='"+ serieExtra +"'");
            sqLiteDatabase.close();

            View view = findViewById(android.R.id.content);
            Snackbar snack = Snackbar.make(view, "Evidencias guardadas", Snackbar.LENGTH_LONG)
                    .setAction("Action", null);
            ViewGroup group = (ViewGroup) snack.getView();
            group.setBackgroundColor(ContextCompat.getColor(this, R.color.colorSnackBarError));
            snack.show();

            Intent intent = new Intent(this, ShowDataActivityISSSTE.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            //finish();
        }
    }

    @Override
    public void onBackPressed() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setMessage("¿Estás seguro salir, no se guardarán las evidencias? ");
        builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (!rutaFua.getText().toString().matches("")) {
                    if (fotoFUA.exists()) {
                        fotoFUA.delete();
                    }
                }

                if (!rutaContador.getText().toString().matches("")) {
                    if (fotoContador.exists()) {
                        fotoContador.delete();
                    }
                }

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
