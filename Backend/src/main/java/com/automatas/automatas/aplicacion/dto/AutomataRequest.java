package com.automatas.automatas.aplicacion.dto;

public class AutomataRequest {
    public String tipo;
    public String[] simbolos;

    public AutomataRequest() {}

    public AutomataRequest(String tipo, String[] simbolos) {
        this.tipo = tipo;
        this.simbolos = simbolos;
    }
}
