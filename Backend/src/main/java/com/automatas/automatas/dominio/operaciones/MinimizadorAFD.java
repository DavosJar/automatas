package com.automatas.automatas.dominio.operaciones;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import com.automatas.automatas.dominio.AFD;

/**
 * Minimizador de Autómata Finito Determinista (AFD)
 * 
 * Esta clase implementa el algoritmo de minimización de Hopcroft para reducir
 * un AFD a su forma mínima (menos estados) manteniendo el lenguaje aceptado.
 * 
 * Algoritmo (Tabla de Distinción):
 * 1. Elimina estados inalcanzables (desde el estado inicial)
 * 2. Marca pares de estados (aceptador, no-aceptador) como distinguibles
 * 3. Itera propagando marcas hasta que no haya cambios
 * 4. Agrupa estados no distinguibles
 * 5. Construye el AFD minimizado usando representantes de grupos
 * 
 * @author Proyecto Autómatas
 * @version 1.0
 */
public class MinimizadorAFD {

    public static AFD minimizar(AFD afd) {
        // 1. Eliminar inalcanzables
        Set<String> alcanzables = estadosAlcanzables(afd);
        List<String> estados = new ArrayList<>(alcanzables);
        Collections.sort(estados);

        // 2. Tabla de distinción
        Map<String, Boolean> tabla = tablaDistincion(afd, estados);

        // 3. Agrupar indistinguibles
        List<Set<String>> grupos = agrupar(estados, tabla);

        // 4. Representante de cada grupo = primer estado ordenado
        Map<String, String> rep = new HashMap<>();
        for (Set<String> grupo : grupos) {
            String representante = grupo.stream().min(String::compareTo).orElse("");
            for (String e : grupo) {
                rep.put(e, representante);
            }
        }

        // 5. Construir tabla minimizada
        Map<String, Map<String, String>> tablaMin = new LinkedHashMap<>();
        for (Set<String> grupo : grupos) {
            String r = grupo.stream().min(String::compareTo).orElse("");
            tablaMin.put(r, new LinkedHashMap<>());
            
            Map<String, String> transiciones = afd.getTablaTransiciones().get(r);
            if (transiciones != null) {
                for (String simbolo : afd.getAlfabeto()) {
                    String destino = transiciones.get(simbolo);
                    if (destino != null && !destino.equals(AFD.ESTADO_TRAMPA)) {
                        tablaMin.get(r).put(simbolo, rep.get(destino));
                    }
                }
            }
        }

        Set<String> estadosMin = new HashSet<>();
        for (Set<String> grupo : grupos) {
            String r = grupo.stream().min(String::compareTo).orElse("");
            estadosMin.add(r);
        }

        Set<String> aceptacionMin = new HashSet<>();
        for (String e : afd.getEstadosAceptacion()) {
            if (alcanzables.contains(e)) {
                aceptacionMin.add(rep.get(e));
            }
        }

        String inicialMin = rep.get(afd.getEstadoInicial());

        return new AFD(
            afd.getNombre() + "_min",
            afd.getAlfabeto(),
            estadosMin,
            inicialMin,
            aceptacionMin,
            tablaMin
        );
    }

    private static Set<String> estadosAlcanzables(AFD afd) {
        Set<String> visitados = new HashSet<>();
        Stack<String> pila = new Stack<>();
        pila.push(afd.getEstadoInicial());

        while (!pila.isEmpty()) {
            String actual = pila.pop();
            if (visitados.contains(actual)) {
                continue;
            }
            visitados.add(actual);

            for (String simbolo : afd.getAlfabeto()) {
                Map<String, String> transiciones = afd.getTablaTransiciones().get(actual);
                if (transiciones != null) {
                    String destino = transiciones.get(simbolo);
                    if (destino != null && !destino.equals(AFD.ESTADO_TRAMPA) && !visitados.contains(destino)) {
                        pila.push(destino);
                    }
                }
            }
        }

        return visitados;
    }

    private static Map<String, Boolean> tablaDistincion(AFD afd, List<String> estados) {
        Map<String, Boolean> tabla = new HashMap<>();

        // Inicializar: distinguir por aceptación
        for (int i = 0; i < estados.size(); i++) {
            for (int j = i + 1; j < estados.size(); j++) {
                String a = estados.get(i);
                String b = estados.get(j);
                boolean esAceptacionA = afd.getEstadosAceptacion().contains(a);
                boolean esAceptacionB = afd.getEstadosAceptacion().contains(b);
                tabla.put(clave(a, b), esAceptacionA != esAceptacionB);
            }
        }

        // Propagación de distinciones
        boolean cambio = true;
        while (cambio) {
            cambio = false;
            for (String par : new HashSet<>(tabla.keySet())) {
                if (tabla.get(par)) {
                    continue;
                }

                String[] partes = par.split("\\|");
                String a = partes[0];
                String b = partes[1];

                for (String simbolo : afd.getAlfabeto()) {
                    String destinoA = obtenerDestino(afd, a, simbolo);
                    String destinoB = obtenerDestino(afd, b, simbolo);

                    // si van al mismo lado no distingue
                    if (destinoA.equals(destinoB)) {
                        continue;
                    }

                    // si uno va a trampa y el otro no → distinguibles
                    if (destinoA.equals(AFD.ESTADO_TRAMPA) || destinoB.equals(AFD.ESTADO_TRAMPA)) {
                        tabla.put(par, true);
                        cambio = true;
                        break;
                    }

                    String parDestinos = clave(destinoA, destinoB);
                    if (tabla.getOrDefault(parDestinos, false)) {
                        tabla.put(par, true);
                        cambio = true;
                        break;
                    }
                }
            }
        }

        return tabla;
    }

    private static String obtenerDestino(AFD afd, String estado, String simbolo) {
        Map<String, String> transiciones = afd.getTablaTransiciones().get(estado);
        if (transiciones != null) {
            String destino = transiciones.get(simbolo);
            if (destino != null) {
                return destino;
            }
        }
        return AFD.ESTADO_TRAMPA;
    }

    private static List<Set<String>> agrupar(List<String> estados, Map<String, Boolean> tabla) {
        Map<String, String> representante = new HashMap<>();
        for (String e : estados) {
            representante.put(e, e);
        }

        // Función auxiliar para encontrar representante
        java.util.function.Function<String, String> encontrar = new java.util.function.Function<String, String>() {
            @Override
            public String apply(String e) {
                String rep = representante.get(e);
                if (!rep.equals(e)) {
                    representante.put(e, apply(rep));
                }
                return representante.get(e);
            }
        };

        // Union-find: agrupar indistinguibles
        for (Map.Entry<String, Boolean> entry : tabla.entrySet()) {
            if (entry.getValue()) {
                continue;
            }

            String[] partes = entry.getKey().split("\\|");
            String a = partes[0];
            String b = partes[1];

            String ra = encontrar.apply(a);
            String rb = encontrar.apply(b);

            if (!ra.equals(rb)) {
                representante.put(rb, ra);
            }
        }

        // Agrupar por representante
        Map<String, Set<String>> grupos = new HashMap<>();
        for (String e : estados) {
            String r = encontrar.apply(e);
            grupos.computeIfAbsent(r, k -> new HashSet<>()).add(e);
        }

        return new ArrayList<>(grupos.values());
    }

    private static String clave(String a, String b) {
        if (a.compareTo(b) < 0) {
            return a + "|" + b;
        }
        return b + "|" + a;
    }
}
