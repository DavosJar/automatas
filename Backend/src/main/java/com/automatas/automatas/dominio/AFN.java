package com.automatas.automatas.dominio;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
//automata inito No determinista
public class AFN {

    public static final String EPSILON = "ε";

    private final String nombre;
    private final Set<String> alfabeto;
    private final Set<String> estados;
    private final String estadoInicial;
    private final Set<String> estadosAceptacion;
    private final Map<String, Map<String, Set<String>>> tablaTransiciones;

    public AFN(
        String nombre,
        Set<String> alfabeto,
        Set<String> estados,
        String estadoInicial,
        Set<String> estadosAceptacion,
        Map<String, Map<String, Set<String>>> tablaTransiciones
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

    public Map<String, Map<String, Set<String>>> getTablaTransiciones() {
        return tablaTransiciones;
    }

    private Map<String, Map<String, Set<String>>> copiarTablaTransiciones(Map<String, Map<String, Set<String>>> tablaTransiciones) {
        Objects.requireNonNull(tablaTransiciones, "tablaTransiciones no puede ser nulo");
        Map<String, Map<String, Set<String>>> copia = new LinkedHashMap<>();

        for (Map.Entry<String, Map<String, Set<String>>> fila : tablaTransiciones.entrySet()) {
            Map<String, Set<String>> transicionesPorSimbolo = new LinkedHashMap<>();
            Map<String, Set<String>> filaOriginal = Objects.requireNonNull(fila.getValue(), "La fila de transiciones no puede ser nula");

            for (Map.Entry<String, Set<String>> transicion : filaOriginal.entrySet()) {
                transicionesPorSimbolo.put(
                    transicion.getKey(),
                    Collections.unmodifiableSet(new LinkedHashSet<>(Objects.requireNonNull(transicion.getValue(), "El conjunto de estados destino no puede ser nulo")))
                );
            }

            copia.put(fila.getKey(), Collections.unmodifiableMap(transicionesPorSimbolo));
        }

        return copia;
    }

    public boolean esValida(String[] simbolos) {
        Objects.requireNonNull(simbolos, "simbolos no puede ser nulo");

        Set<String> estadosActuales = new LinkedHashSet<>();
        estadosActuales.add(estadoInicial);

        for (String simbolo : simbolos) {
            Set<String> siguientes = new LinkedHashSet<>();

            for (String estado : estadosActuales) {
                Map<String, Set<String>> transiciones = tablaTransiciones.get(estado);
                if (transiciones == null) {
                    continue;
                }

                Set<String> destinos = transiciones.get(simbolo);
                if (destinos != null) {
                    siguientes.addAll(destinos);
                }
            }

            estadosActuales = siguientes;

            if (estadosActuales.isEmpty()) {
                return false;
            }
        }

        for (String estado : estadosActuales) {
            if (estadosAceptacion.contains(estado)) {
                return true;
            }
        }

        return false;
    }
}