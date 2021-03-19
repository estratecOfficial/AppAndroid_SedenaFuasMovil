package com.codinginflow.datastore.issste;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import com.codinginflow.datastore.R;


import java.util.ArrayList;

public class ListAdapterCompletoISSSTE extends BaseAdapter implements Filterable {

    //ARRAY DONDE SE OBTIENEN LOS DATOS DE SOWDATAACTIVITY
    Context context;
    public ArrayList<seriesISSSTE> seriesList = null;
    public ArrayList<seriesISSSTE> orig;



    public ListAdapterCompletoISSSTE(Context context, ArrayList<seriesISSSTE> seriesList) {
        super();
        this.context = context;
        this.seriesList = seriesList;
    }


    public class Holder {
        TextView Subject_TextView;
        TextView SubjectFullFormTextView;
        TextView UnidadAdmon;
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
        ListAdapterCompletoISSSTE.Holder holder;
        if (child == null) {

            child = LayoutInflater.from(context).inflate(R.layout.items_completo_issste, parent, false);

            holder = new ListAdapterCompletoISSSTE.Holder();
            holder.Subject_TextView = (TextView) child.findViewById(R.id.textViewSubjectCompletoISSSTE);
            holder.SubjectFullFormTextView = (TextView) child.findViewById(R.id.textViewSubjectFullFormCompletoISSSTE);
            holder.UnidadAdmon = (TextView) child.findViewById(R.id.UnidadAdminCompletoISSSTE);

            child.setTag(holder);
        } else {
            holder = (ListAdapterCompletoISSSTE.Holder) child.getTag();
        }



        //INSERCION DE LOS DATOS EN LOS OBJETOS DEL XML ITEMS.XML
        holder.Subject_TextView.setText(String.valueOf(seriesList.get(position).getSerie()));
        holder.SubjectFullFormTextView.setText(String.valueOf(seriesList.get(position).getModelo()));
        holder.UnidadAdmon.setText(String.valueOf(seriesList.get(position).getUnidad()));

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
