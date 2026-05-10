package com.automatas.automatas.dominio;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Autómata Finito No Determinista (AFN)
 * 
 * Esta clase modela un AFN que permite múltiples transiciones desde un estado
 * para el mismo símbolo. Es utilizada como punto de partida en la transformación
 * hacia un AFD equivalente.
 * 
 * Características:
 * - Tabla de transiciones: estado → símbolo → conjunto de estados destino
 * - Validación mediante exploración de todos los caminos posibles
 * - Inmutable (thread-safe)
 * 
 * @author Proyecto Autómatas
 * @version 1.0
 */
public class AFN {

    /** Símbolo que representa la transición épsilon (sin símbolo) */
    public static final String EPSILON = "ε";

    /** Identificador único del autómata */
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
    /** Tabla de transiciones: estado → (símbolo → conjunto_destino) */
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

    /**
     * Valida si una secuencia de símbolos es aceptada por este AFN.
     * 
     * Algoritmo:
     * 1. Inicializa conjunto de estados actuales con el estado inicial
     * 2. Para cada símbolo en la secuencia:
     *    - Calcula todos los estados destino posibles desde los estados actuales
     *    - Si no hay destinos, la cadena es rechazada
     * 3. Acepta si algún estado final está en estados de aceptación
     * 
     * @param simbolos Array de símbolos a validar
     * @return true si la cadena es aceptada, false en caso contrario
     * @throws NullPointerException si simbolos es null
     */
    public boolean esValida(String[] simbolos) {
        Objects.requireNonNull(simbolos, "simbolos no puede ser nulo");

        // Inicializar con el estado inicial
        Set<String> estadosActuales = new LinkedHashSet<>();
        estadosActuales.add(estadoInicial);

        // Procesar cada símbolo de la entrada
        for (String simbolo : simbolos) {
            Set<String> siguientes = new LinkedHashSet<>();

            // Para cada estado actual, buscar transiciones con el símbolo
            for (String estado : estadosActuales) {
                Map<String, Set<String>> transiciones = tablaTransiciones.get(estado);
                if (transiciones == null) {
                    continue;
                }

                // Obtener todos los destinos posibles (no determinismo)
                Set<String> destinos = transiciones.get(simbolo);
                if (destinos != null) {
                    siguientes.addAll(destinos);
                }
            }

            // Actualizar estados actuales
            estadosActuales = siguientes;

            // Si no hay camino posible, rechazar
            if (estadosActuales.isEmpty()) {
                return false;
            }
        }

        // Verificar si algún estado actual es de aceptación
        for (String estado : estadosActuales) {
            if (estadosAceptacion.contains(estado)) {
                return true;
            }
        }

        return false;
    }
}