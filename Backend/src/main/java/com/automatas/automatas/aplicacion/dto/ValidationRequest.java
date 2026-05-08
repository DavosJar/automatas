package com.automatas.automatas.aplicacion.dto;

public class ValidationRequest {
    public String tipo;
    public String[] simbolos;

    public ValidationRequest() {}

    public ValidationRequest(String tipo, String[] simbolos) {
        this.tipo = tipo;
        this.simbolos = simbolos;
    }
}
