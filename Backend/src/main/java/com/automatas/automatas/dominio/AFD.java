package com.automatas.automatas.dominio;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Autómata Finito Determinista (AFD)
 * 
 * Esta clase modela un AFD donde cada transición es única: desde un estado
 * y símbolo existe a lo más un estado destino. Es el resultado de transformar
 * un AFN o puede ser creado directamente.
 * 
 * Características:
 * - Tabla de transiciones: estado → símbolo → estado destino único
 * - Validación determinística (un solo camino)
 * - Estado trampa para rechazos rápidos
 * - Inmutable (thread-safe)
 * 
 * @author Proyecto Autómatas
 * @version 1.0
 */
public class AFD {

    /** Estado especial que representa rechazo o falta de transición */
    public static final String ESTADO_TRAMPA = "∞";

    /** Identificador único del autómata */
    private final String nombre;
    /** Conjunto de símbolos del alfabeto */
    private final Set<String> alfabeto;
    /** Conjunto de todos los estados del autómata */
    private final Set<String> estados;
    /** Estado inicial del autómata */
    private final String estadoInicial;
    /** Estados de aceptación o finales */
    private final Set<String> estadosAceptacion;
    /** Tabla de transiciones: estado → (símbolo → estado_destino) */
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

    /**
     * Valida si una secuencia de símbolos es aceptada por este AFD.
     * 
     * Algoritmo:
     * 1. Comienza en el estado inicial
     * 2. Para cada símbolo:
     *    - Busca la transición desde el estado actual
     *    - Si no existe, va al estado trampa
     *    - Si está en estado trampa, rechaza inmediatamente
     * 3. Acepta si el estado final es de aceptación
     * 
     * @param simbolos Array de símbolos a validar
     * @return true si la cadena es aceptada, false en caso contrario
     * @throws NullPointerException si simbolos es null
     */
    public boolean esValida(String[] simbolos) {
        Objects.requireNonNull(simbolos, "simbolos no puede ser nulo");

        // Inicializar en estado inicial
        String estadoActual = estadoInicial;

        // Procesar cada símbolo de la entrada (determinístico)
        for (String simbolo : simbolos) {
            Map<String, String> transiciones = tablaTransiciones.get(estadoActual);

            // Si no hay transiciones desde el estado actual
            if (transiciones == null) {
                estadoActual = ESTADO_TRAMPA;
            } else {
                // Buscar transición con el símbolo
                String estadoSiguiente = transiciones.get(simbolo);
                if (estadoSiguiente == null) {
                    // No existe transición, ir al estado trampa
                    estadoActual = ESTADO_TRAMPA;
                } else {
                    // Ejecutar transición
                    estadoActual = estadoSiguiente;
                }
            }

            // Si estamos en estado trampa, rechazar inmediatamente
            if (estadoActual.equals(ESTADO_TRAMPA)) {
                return false;
            }
        }

        // Aceptar si el estado final es de aceptación
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
