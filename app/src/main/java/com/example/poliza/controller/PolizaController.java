package com.example.poliza.controller;

public class PolizaController {

    public static String calcularPoliza(String nombre, double valorAuto,
                                        int accidentes, String modelo, String edad) {
        //Calculo de  cargo por valor del auto
        double cargoxvalor = valorAuto * 0.035;
        //Calculo del cargo por modelo
        double cargoxmodelo = 0;

        if(modelo.equals("Modelo A")){
            cargoxmodelo = valorAuto * 0.011;
        } else if (modelo.equals("Modelo B")) {
            cargoxmodelo = valorAuto * 0.012;
        } else if (modelo.equals("Modelo C")) {
            cargoxmodelo = valorAuto * 0.015;
        } else {
            cargoxmodelo = 0; //valor por defecto
        }

        //Calculo por edad
        double cargoxedad = 0;

        if (edad.equals("18-23")){
            cargoxedad = 360;
        } else if (edad.equals("24-55")){
            cargoxedad = 240;
        } else if (edad.equals("Mayor de 35")) {
            cargoxedad = 430;
        }

        //cargo por accidente
        double cargoxacc=0;

        if (accidentes <=3){
            cargoxacc = 17*accidentes;
        } else {
            cargoxacc = (accidentes-3)*21+17;
        }

        //costo total de la poliza
        double costoTotal = cargoxvalor + cargoxmodelo + cargoxedad + cargoxacc;
        return String.format("%.2f", costoTotal);
    }
}
