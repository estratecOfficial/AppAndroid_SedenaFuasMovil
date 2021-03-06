package com.codinginflow.datastore.issste;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.codinginflow.datastore.R;
import com.codinginflow.datastore.sedena.EditarDatos;
import com.codinginflow.datastore.sedena.cargaEvidencias;

import java.util.ArrayList;

public class ListAdapterISSSTE extends BaseAdapter implements Filterable {


    //ARRAY DONDE SE OBTIENEN LOS DATOS DE SOWDATAACTIVITY
    Context context;
    public ArrayList<seriesISSSTE> seriesList = null;
    public ArrayList<seriesISSSTE> orig;



    public ListAdapterISSSTE(Context context, ArrayList<seriesISSSTE> seriesList) {
        super();
        this.context = context;
        this.seriesList = seriesList;
    }

    public class Holder {
        TextView Subject_TextView;
        TextView SubjectFullFormTextView;
        Button BotonEditar;
        Button btn_generar;
        Button btn_evidencias;
    }

    public Filter getFilter() {
        return new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                final FilterResults oReturn = new FilterResults();
                final ArrayList<seriesISSSTE> results = new ArrayList<>();
                if (orig == null)
                    orig = seriesList;
                if (constraint != null) {
                    if (orig != null && orig.size() > 0) {
                        for (final seriesISSSTE g : orig) {
                            if (g.getSerie().toLowerCase().contains(constraint.toString()) || g.getSerie().contains(constraint.toString()) || g.getModelo().toLowerCase().contains(constraint.toString()) || g.getModelo().contains(constraint.toString()))
                            {
                                results.add(g);
                            }

                        }
                    }
                    oReturn.values = results;
                }
                return oReturn;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint,
                                          FilterResults results) {
                seriesList = (ArrayList<seriesISSSTE>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    public int getCount() {
        // TODO Auto-generated method stub
        return seriesList.size();
    }
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return seriesList.get(position);
    }
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }


    public View getView(final int position, View child, ViewGroup parent) {
        ListAdapterISSSTE.Holder holder;
        if (child == null) {

            child = LayoutInflater.from(context).inflate(R.layout.items_issste, parent, false);

            holder = new ListAdapterISSSTE.Holder();
            holder.Subject_TextView = (TextView) child.findViewById(R.id.textViewSubjectISSSTE);
            holder.SubjectFullFormTextView = (TextView) child.findViewById(R.id.textViewSubjectFullFormISSSTE);
            holder.BotonEditar = (Button) child.findViewById(R.id.botonEditarDatosISSSTE);
            holder.btn_generar = (Button) child.findViewById(R.id.botonFUAISSSTE);
            holder.btn_evidencias = (Button) child.findViewById(R.id.botonEvidenciasISSSTE);

            child.setTag(holder);
        } else {
            holder = (ListAdapterISSSTE.Holder) child.getTag();
        }



        //INSERCION DE LOS DATOS EN LOS OBJETOS DEL XML ITEMS.XML
        holder.Subject_TextView.setText(String.valueOf(seriesList.get(position).getSerie()));
        holder.SubjectFullFormTextView.setText(String.valueOf(seriesList.get(position).getModelo()));
        final String EstatusSerie = String.valueOf(seriesList.get(position).getEstatus());

        holder.BotonEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent (v.getContext(), EditarDatosISSSTE.class);
                intent.putExtra("SERIE_BD", String.valueOf(seriesList.get(position).getSerie()));
                intent.putExtra("MODELO", String.valueOf(seriesList.get(position).getModelo()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                context.startActivity(intent);

            }
        });

        holder.btn_generar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (EstatusSerie.matches("Editar")){
                Intent intent = new Intent (v.getContext(), crearFormatoISSSTE.class);
                intent.putExtra("SERIE_BD", String.valueOf(seriesList.get(position).getSerie()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                context.startActivity(intent);
               }else{
                    showAlertDialogInternet(context, "NO HAS ACTUALIZADO LA INFORMACIÓN",
                            "Para generar el PDF es necesario que captures la información del equipo y usuario asignado", true);
                }

            }
        });

        holder.btn_evidencias.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (String.valueOf(seriesList.get(position).getEstatus()).matches("Editar")){
                    Intent intent = new Intent (v.getContext(), cargaEvidenciasISSSTE.class);
                    intent.putExtra("SERIE_BD", String.valueOf(seriesList.get(position).getSerie()));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    context.startActivity(intent);
                }
                else{
                    showAlertDialogInternet(context, "NO HAS INGRESADO LOS DATOS DEL FUA",
                            "Para poder cargar las evidencias es necesario que hayas capturado los datos del FUA", true);
                }


            }
        });




        return child;
    }

    public void showAlertDialogInternet(Context context, String title, String message, Boolean status) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setIcon(R.drawable.atencion);
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        alertDialog.show();
    }

}
