package com.codinginflow.sedena;

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

import java.util.ArrayList;

public class ListAdapterCompleto extends BaseAdapter implements Filterable {

    //ARRAY DONDE SE OBTIENEN LOS DATOS DE SOWDATAACTIVITY
    Context context;
    public ArrayList<Series> seriesList = null;
    public ArrayList<Series> orig;



    public ListAdapterCompleto(Context context, ArrayList<Series> seriesList) {
        super();
        this.context = context;
        this.seriesList = seriesList;
    }

    public ListAdapterCompleto(){}

    public class Holder {
        TextView Subject_TextView;
        TextView SubjectFullFormTextView;
        TextView RegionMilitar;
    }

    public Filter getFilter() {
        return new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                final FilterResults oReturn = new FilterResults();
                final ArrayList<Series> results = new ArrayList<>();
                if (orig == null)
                    orig = seriesList;
                if (constraint != null) {
                    if (orig != null && orig.size() > 0) {
                        for (final Series g : orig) {
                            if (g.getSerie().toLowerCase().contains(constraint.toString()) || g.getModelo().toLowerCase().contains(constraint.toString()))
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
                seriesList = (ArrayList<Series>) results.values;
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
        ListAdapterCompleto.Holder holder;
        if (child == null) {

            child = LayoutInflater.from(context).inflate(R.layout.items_completo, parent, false);

            holder = new ListAdapterCompleto.Holder();
            holder.Subject_TextView = (TextView) child.findViewById(R.id.textViewSubjectCompleto);
            holder.SubjectFullFormTextView = (TextView) child.findViewById(R.id.textViewSubjectFullFormCompleto);
            holder.RegionMilitar = (TextView) child.findViewById(R.id.RegionMilitarCompleto);

            child.setTag(holder);
        } else {
            holder = (ListAdapterCompleto.Holder) child.getTag();
        }



        //INSERCION DE LOS DATOS EN LOS OBJETOS DEL XML ITEMS.XML
        holder.Subject_TextView.setText(String.valueOf(seriesList.get(position).getSerie()));
        holder.SubjectFullFormTextView.setText(String.valueOf(seriesList.get(position).getModelo()));
        holder.RegionMilitar.setText(String.valueOf(seriesList.get(position).getRegionMilitar()));

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
