package com.automatas.automatas.dominio.operaciones;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;

import com.automatas.automatas.dominio.AFD;
import com.automatas.automatas.dominio.AFN;

/**
 * Transformador de AFN a AFD
 * 
 * Esta clase implementa el algoritmo de construcción de subconjuntos (subset construction)
 * para convertir un Autómata Finito No Determinista (AFN) en un Autómata Finito
 * Determinista (AFD) equivalente.
 * 
 * Algoritmo:
 * 1. El estado inicial del AFD es {q0} (el estado inicial del AFN)
 * 2. Para cada superestado (conjunto de estados del AFN):
 *    - Calcular el "mover" para cada símbolo del alfabeto
 *    - Crear nuevos superestados si es necesario
 * 3. Los estados de aceptación del AFD son aquellos superestados
 *    que contienen al menos un estado de aceptación del AFN
 * 
 * @author Proyecto Autómatas
 * @version 1.0
 */
public class TransformadorAFN_AFD {

    public static AFD transformar(AFN afn) {
        String nombreAFD = afn.getNombre() + "_afd";
        Set<String> alfabeto = afn.getAlfabeto();
        Map<String, Map<String, Set<String>>> tablaAFN = afn.getTablaTransiciones();

        // Superestado inicial: {q0}
        Set<String> inicial = new HashSet<>();
        inicial.add(afn.getEstadoInicial());

        Map<String, Map<String, String>> tablaAFD = new LinkedHashMap<>();
        Set<String> estadosAFD = new HashSet<>();
        Queue<Set<String>> cola = new LinkedList<>();
        Set<Set<String>> visitados = new HashSet<>();

        cola.add(inicial);
        visitados.add(inicial);

        while (!cola.isEmpty()) {
            Set<String> superestado = cola.poll();
            String nombreSuperestado = nombreSuperestado(superestado);
            estadosAFD.add(nombreSuperestado);
            tablaAFD.put(nombreSuperestado, new LinkedHashMap<>());

            for (String simbolo : new TreeSet<>(alfabeto)) {
                Set<String> destino = mover(superestado, simbolo, tablaAFN);

                if (destino.isEmpty()) {
                    continue;
                }

                String nombreDestino = nombreSuperestado(destino);
                tablaAFD.get(nombreSuperestado).put(simbolo, nombreDestino);

                if (!visitados.contains(destino)) {
                    visitados.add(destino);
                    cola.add(destino);
                }
            }
        }

        Set<String> estadosAceptacionAFD = new HashSet<>();
        Set<String> estadosAceptacionAFN = afn.getEstadosAceptacion();

        for (Set<String> superestado : visitados) {
            for (String estado : superestado) {
                if (estadosAceptacionAFN.contains(estado)) {
                    estadosAceptacionAFD.add(nombreSuperestado(superestado));
                    break;
                }
            }
        }

        return new AFD(
            nombreAFD,
            alfabeto,
            estadosAFD,
            nombreSuperestado(inicial),
            estadosAceptacionAFD,
            tablaAFD
        );
    }

    private static Set<String> mover(Set<String> superestado, String simbolo, Map<String, Map<String, Set<String>>> tablaAFN) {
        Set<String> destino = new HashSet<>();

        for (String estado : superestado) {
            Map<String, Set<String>> transiciones = tablaAFN.get(estado);
            if (transiciones != null) {
                Set<String> destinos = transiciones.get(simbolo);
                if (destinos != null) {
                    destino.addAll(destinos);
                }
            }
        }

        return destino;
    }

    private static String nombreSuperestado(Set<String> superestado) {
        if (superestado.isEmpty()) {
            return "∅";
        }
        return "{" + String.join(",", new TreeSet<>(superestado)) + "}";
    }
}
