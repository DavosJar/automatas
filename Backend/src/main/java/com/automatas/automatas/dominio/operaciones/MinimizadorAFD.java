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

public class MinimizadorAFD {

    public static AFD minimizar(AFD afd) {
        // 1. Calcular estados alcanzables
        Set<String> alcanzables = calcularAlcanzables(afd);
        List<String> estados = new ArrayList<>(alcanzables);
        Collections.sort(estados);

        // 2. Tabla de distinción
        Map<String, Boolean> tabla = tablaDistincion(afd, estados);

        // 3. Agrupar estados indistinguibles
        List<Set<String>> grupos = agruparEstados(estados, tabla);

        // 4. Construir representantes
        Map<String, String> representante = new HashMap<>();
        for (Set<String> grupo : grupos) {
            String rep = grupo.stream().min(String::compareTo).orElse("");
            for (String estado : grupo) {
                representante.put(estado, rep);
            }
        }

        // 5. Construir tabla minimizada
        Map<String, Map<String, String>> tablaMin = new LinkedHashMap<>();
        for (Set<String> grupo : grupos) {
            String rep = grupo.stream().min(String::compareTo).orElse("");
            tablaMin.put(rep, new LinkedHashMap<>());

            Map<String, String> transiciones = afd.getTablaTransiciones().get(rep);
            if (transiciones != null) {
                for (String simbolo : afd.getAlfabeto()) {
                    String destino = transiciones.get(simbolo);
                    if (destino != null && !destino.equals(AFD.ESTADO_TRAMPA)) {
                        tablaMin.get(rep).put(simbolo, representante.get(destino));
                    }
                }
            }
        }

        // 6. Estados aceptación minimizados
        Set<String> aceptacionMin = new HashSet<>();
        for (String estado : afd.getEstadosAceptacion()) {
            if (alcanzables.contains(estado)) {
                aceptacionMin.add(representante.get(estado));
            }
        }

        // 7. Estado inicial minimizado
        String inicialMin = representante.get(afd.getEstadoInicial());

        // 8. Estados minimizados (excluir trampa)
        Set<String> estadosMin = new HashSet<>();
        for (Set<String> grupo : grupos) {
            String rep = grupo.stream().min(String::compareTo).orElse("");
            if (!rep.equals(AFD.ESTADO_TRAMPA)) {
                estadosMin.add(rep);
            }
        }

        return new AFD(
            afd.getNombre() + "_min",
            afd.getAlfabeto(),
            estadosMin,
            inicialMin,
            aceptacionMin,
            tablaMin
        );
    }

    private static Set<String> calcularAlcanzables(AFD afd) {
        Set<String> visitados = new HashSet<>();
        Stack<String> pila = new Stack<>();
        pila.push(afd.getEstadoInicial());

        while (!pila.isEmpty()) {
            String actual = pila.pop();
            if (visitados.contains(actual)) {
                continue;
            }
            visitados.add(actual);

            Map<String, String> transiciones = afd.getTablaTransiciones().get(actual);
            if (transiciones != null) {
                for (String destino : transiciones.values()) {
                    if (!destino.equals(AFD.ESTADO_TRAMPA) && !visitados.contains(destino)) {
                        pila.push(destino);
                    }
                }
            }
        }

        return visitados;
    }

    private static Map<String, Boolean> tablaDistincion(AFD afd, List<String> estados) {
        Map<String, Boolean> tabla = new HashMap<>();

        // Inicializar: distinguir aceptación vs no aceptación
        for (int i = 0; i < estados.size(); i++) {
            for (int j = i + 1; j < estados.size(); j++) {
                String a = estados.get(i);
                String b = estados.get(j);
                boolean esAceptacionA = afd.getEstadosAceptacion().contains(a);
                boolean esAceptacionB = afd.getEstadosAceptacion().contains(b);
                tabla.put(clave(a, b), esAceptacionA != esAceptacionB);
            }
        }

        // Iteración: propagar distinciones
        boolean cambio = true;
        while (cambio) {
            cambio = false;
            for (String clavePar : new HashSet<>(tabla.keySet())) {
                if (tabla.get(clavePar)) {
                    continue;
                }

                String[] partes = clavePar.split("\\|");
                String a = partes[0];
                String b = partes[1];

                Map<String, String> transicionesA = afd.getTablaTransiciones().get(a);
                Map<String, String> transicionesB = afd.getTablaTransiciones().get(b);

                for (String simbolo : afd.getAlfabeto()) {
                    String destinoA = transicionesA != null ? transicionesA.get(simbolo) : null;
                    String destinoB = transicionesB != null ? transicionesB.get(simbolo) : null;

                    if (destinoA == null || destinoB == null) {
                        destinoA = AFD.ESTADO_TRAMPA;
                        destinoB = AFD.ESTADO_TRAMPA;
                    }

                    if (destinoA.equals(destinoB)) {
                        continue;
                    }

                    if (destinoA.equals(AFD.ESTADO_TRAMPA) || destinoB.equals(AFD.ESTADO_TRAMPA)) {
                        tabla.put(clavePar, true);
                        cambio = true;
                        break;
                    }

                    String clave = clave(destinoA, destinoB);
                    if (tabla.getOrDefault(clave, false)) {
                        tabla.put(clavePar, true);
                        cambio = true;
                        break;
                    }
                }
            }
        }

        return tabla;
    }

    private static List<Set<String>> agruparEstados(List<String> estados, Map<String, Boolean> tabla) {
        Map<String, String> representante = new HashMap<>();
        for (String e : estados) {
            representante.put(e, e);
        }

        // Union-find
        for (Map.Entry<String, Boolean> entry : tabla.entrySet()) {
            if (entry.getValue()) {
                continue;
            }

            String[] partes = entry.getKey().split("\\|");
            String a = partes[0];
            String b = partes[1];

            String ra = encontrar(a, representante);
            String rb = encontrar(b, representante);

            if (!ra.equals(rb)) {
                representante.put(rb, ra);
            }
        }

        // Agrupar
        Map<String, Set<String>> grupos = new HashMap<>();
        for (String e : estados) {
            String rep = encontrar(e, representante);
            grupos.computeIfAbsent(rep, k -> new HashSet<>()).add(e);
        }

        return new ArrayList<>(grupos.values());
    }

    private static String encontrar(String e, Map<String, String> representante) {
        if (!representante.get(e).equals(e)) {
            representante.put(e, encontrar(representante.get(e), representante));
        }
        return representante.get(e);
    }

    private static String clave(String a, String b) {
        if (a.compareTo(b) < 0) {
            return a + "|" + b;
        }
        return b + "|" + a;
    }
}
