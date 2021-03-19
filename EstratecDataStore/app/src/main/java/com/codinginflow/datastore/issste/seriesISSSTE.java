package com.codinginflow.datastore.issste;

public class seriesISSSTE {
    private String id;
    private String serie;
    private String modelo;
    private String unidadAdm;
    private String estatus;

    public  seriesISSSTE(String id, String serie,String modelo,String unidadAdm,String estatus){
        this.id = id;
        this.serie = serie;
        this.modelo = modelo;
        this.unidadAdm = unidadAdm;
        this.estatus = estatus;
    }

    public String getId(){
        return id;
    }

    public String getSerie(){
        return serie;
    }

    public String getModelo(){
        return modelo;
    }

    public String getUnidad(){
        return unidadAdm;
    }

    public String getEstatus(){
        return estatus;
    }
}
