package com.automatas.automatas.dominio.automatas;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import com.automatas.automatas.dominio.AFN;

public class Ciberseguridad extends AFN {

    public Ciberseguridad(
        String nombre,
        Set<String> alfabeto,
        Set<String> estados,
        String estadoInicial,
        Set<String> estadosAceptacion,
        Map<String, Map<String, Set<String>>> tablaTransiciones
    ) {
        super(nombre, alfabeto, estados, estadoInicial, estadosAceptacion, tablaTransiciones);
    }

    public static Ciberseguridad crear() {
        Set<String> alfabeto = new LinkedHashSet<>(Arrays.asList("ACK", "SYN", "DATA", "RST"));
        Set<String> estados = new LinkedHashSet<>(Arrays.asList("q0", "q1", "q2", "q3", "q_fallo"));
        Set<String> estadosAceptacion = new LinkedHashSet<>(Arrays.asList("q3"));

        Map<String, Map<String, Set<String>>> tablaTransiciones = new LinkedHashMap<>();

        tablaTransiciones.put("q0", new LinkedHashMap<String, Set<String>>() {{
            put("SYN", new LinkedHashSet<>(Arrays.asList("q1")));
            put("ACK", new LinkedHashSet<>(Arrays.asList("q_fallo")));
            put("DATA", new LinkedHashSet<>(Arrays.asList("q_fallo")));
            put("RST", new LinkedHashSet<>(Arrays.asList("q_fallo")));
        }});

        tablaTransiciones.put("q1", new LinkedHashMap<String, Set<String>>() {{
            put("SYN", new LinkedHashSet<>(Arrays.asList("q_fallo")));
            put("ACK", new LinkedHashSet<>(Arrays.asList("q1", "q2")));
            put("DATA", new LinkedHashSet<>(Arrays.asList("q_fallo")));
            put("RST", new LinkedHashSet<>(Arrays.asList("q_fallo")));
        }});

        tablaTransiciones.put("q2", new LinkedHashMap<String, Set<String>>() {{
            put("SYN", new LinkedHashSet<>(Arrays.asList("q_fallo")));
            put("ACK", new LinkedHashSet<>(Arrays.asList("q_fallo")));
            put("DATA", new LinkedHashSet<>(Arrays.asList("q_fallo")));
            put("RST", new LinkedHashSet<>(Arrays.asList("q3")));
        }});

        tablaTransiciones.put("q3", new LinkedHashMap<String, Set<String>>() {{
            put("SYN", new LinkedHashSet<>(Arrays.asList("q_fallo")));
            put("ACK", new LinkedHashSet<>(Arrays.asList("q_fallo")));
            put("DATA", new LinkedHashSet<>(Arrays.asList("q_fallo")));
            put("RST", new LinkedHashSet<>(Arrays.asList("q_fallo")));
        }});

        tablaTransiciones.put("q_fallo", new LinkedHashMap<String, Set<String>>() {{
            put("SYN", new LinkedHashSet<>(Arrays.asList("q_fallo")));
            put("ACK", new LinkedHashSet<>(Arrays.asList("q_fallo")));
            put("DATA", new LinkedHashSet<>(Arrays.asList("q_fallo")));
            put("RST", new LinkedHashSet<>(Arrays.asList("q_fallo")));
        }});

        return new Ciberseguridad(
            "Ciberseguridad",
            alfabeto,
            estados,
            "q0",
            estadosAceptacion,
            tablaTransiciones
        );
    }
}