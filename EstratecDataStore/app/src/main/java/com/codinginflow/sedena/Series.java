package com.codinginflow.sedena;

public class Series {

    private String id;
    private String serie;
    private String modelo;
    private String regionMilitar;
    private String estatus;

    public  Series(String id, String serie,String modelo,String regionMilitar,String estatus){
        this.id = id;
        this.serie = serie;
        this.modelo = modelo;
        this.regionMilitar = regionMilitar;
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

    public String getRegionMilitar(){
        return regionMilitar;
    }

    public String getEstatus(){
        return estatus;
    }
}
