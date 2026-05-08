package com.automatas.automatas.aplicacion.dto;

import java.util.Map;
import java.util.Set;

public class AutomataResponse {
    public String nombre;
    public Set<String> alfabeto;
    public Set<String> estados;
    public String estadoInicial;
    public Set<String> estadosAceptacion;
    public Map<String, Map<String, Object>> tablaTransiciones;

    public AutomataResponse(String nombre, Set<String> alfabeto, Set<String> estados, 
                           String estadoInicial, Set<String> estadosAceptacion, 
                           Map<String, Map<String, Object>> tablaTransiciones) {
        this.nombre = nombre;
        this.alfabeto = alfabeto;
        this.estados = estados;
        this.estadoInicial = estadoInicial;
        this.estadosAceptacion = estadosAceptacion;
        this.tablaTransiciones = tablaTransiciones;
    }
}
