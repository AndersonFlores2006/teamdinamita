package com.example.myapplication;

public class Residuo {
    private int id;
    private String tipo;
    private int cantidad;

    public Residuo(int id, String tipo, int cantidad) {
        this.id = id;
        this.tipo = tipo;
        this.cantidad = cantidad;
    }

    public int getId() {
        return id;
    }

    public String getTipo() {
        return tipo;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }
}
