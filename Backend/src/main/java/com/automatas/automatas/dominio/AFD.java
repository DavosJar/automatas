package com.automatas.automatas.dominio;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class AFD {

    public static final String ESTADO_TRAMPA = "∅";

    private final String nombre;
    private final Set<String> alfabeto;
    private final Set<String> estados;
    private final String estadoInicial;
    private final Set<String> estadosAceptacion;
    private final Map<String, Map<String, String>> tablaTransiciones;

    public AFD(
        String nombre,
        Set<String> alfabeto,
        Set<String> estados,
        String estadoInicial,
        Set<String> estadosAceptacion,
        Map<String, Map<String, String>> tablaTransiciones
    ) {
        this.nombre = Objects.requireNonNull(nombre, "nombre no puede ser nulo");
        this.alfabeto = Collections.unmodifiableSet(new LinkedHashSet<>(Objects.requireNonNull(alfabeto, "alfabeto no puede ser nulo")));
        this.estados = Collections.unmodifiableSet(new LinkedHashSet<>(Objects.requireNonNull(estados, "estados no puede ser nulo")));
        this.estadoInicial = Objects.requireNonNull(estadoInicial, "estadoInicial no puede ser nulo");
        this.estadosAceptacion = Collections.unmodifiableSet(new LinkedHashSet<>(Objects.requireNonNull(estadosAceptacion, "estadosAceptacion no puede ser nulo")));
        this.tablaTransiciones = Collections.unmodifiableMap(copiarTablaTransiciones(tablaTransiciones));
    }

    public String getNombre() {
        return nombre;
    }

    public Set<String> getAlfabeto() {
        return alfabeto;
    }

    public Set<String> getEstados() {
        return estados;
    }

    public String getEstadoInicial() {
        return estadoInicial;
    }

    public Set<String> getEstadosAceptacion() {
        return estadosAceptacion;
    }

    public Map<String, Map<String, String>> getTablaTransiciones() {
        return tablaTransiciones;
    }

    public boolean esValida(String[] simbolos) {
        Objects.requireNonNull(simbolos, "simbolos no puede ser nulo");

        String estadoActual = estadoInicial;

        for (String simbolo : simbolos) {
            Map<String, String> transiciones = tablaTransiciones.get(estadoActual);

            if (transiciones == null) {
                estadoActual = ESTADO_TRAMPA;
            } else {
                String estadoSiguiente = transiciones.get(simbolo);
                if (estadoSiguiente == null) {
                    estadoActual = ESTADO_TRAMPA;
                } else {
                    estadoActual = estadoSiguiente;
                }
            }

            if (estadoActual.equals(ESTADO_TRAMPA)) {
                return false;
            }
        }

        return estadosAceptacion.contains(estadoActual);
    }

    private Map<String, Map<String, String>> copiarTablaTransiciones(Map<String, Map<String, String>> tablaTransiciones) {
        Objects.requireNonNull(tablaTransiciones, "tablaTransiciones no puede ser nulo");
        Map<String, Map<String, String>> copia = new LinkedHashMap<>();

        for (Map.Entry<String, Map<String, String>> fila : tablaTransiciones.entrySet()) {
            Map<String, String> transicionesPorSimbolo = new LinkedHashMap<>();
            Map<String, String> filaOriginal = Objects.requireNonNull(fila.getValue(), "La fila de transiciones no puede ser nula");

            for (Map.Entry<String, String> transicion : filaOriginal.entrySet()) {
                transicionesPorSimbolo.put(
                    transicion.getKey(),
                    Objects.requireNonNull(transicion.getValue(), "El estado destino no puede ser nulo")
                );
            }

            copia.put(fila.getKey(), Collections.unmodifiableMap(transicionesPorSimbolo));
        }

        return copia;
    }
}
