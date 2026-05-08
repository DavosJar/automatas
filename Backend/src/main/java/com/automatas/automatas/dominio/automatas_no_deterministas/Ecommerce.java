package com.automatas.automatas.dominio.automatas_no_deterministas;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import com.automatas.automatas.dominio.AFN;

public class Ecommerce extends AFN {

    public Ecommerce(
        String nombre,
        Set<String> alfabeto,
        Set<String> estados,
        String estadoInicial,
        Set<String> estadosAceptacion,
        Map<String, Map<String, Set<String>>> tablaTransiciones
    ) {
        super(nombre, alfabeto, estados, estadoInicial, estadosAceptacion, tablaTransiciones);
    }

    public static Ecommerce crear() {
        Set<String> alfabeto = new LinkedHashSet<>(Arrays.asList("HOME", "SEARCH", "CART"));
        Set<String> estados = new LinkedHashSet<>(Arrays.asList("q0", "q1", "q2", "q3", "q_error"));
        Set<String> estadosAceptacion = new LinkedHashSet<>(Arrays.asList("q3"));

        Map<String, Map<String, Set<String>>> tablaTransiciones = new LinkedHashMap<>();

        tablaTransiciones.put("q0", new LinkedHashMap<String, Set<String>>() {{
            put("HOME", new LinkedHashSet<>(Arrays.asList("q1")));
            put("SEARCH", new LinkedHashSet<>(Arrays.asList("q_error")));
            put("CART", new LinkedHashSet<>(Arrays.asList("q_error")));
        }});

        tablaTransiciones.put("q1", new LinkedHashMap<String, Set<String>>() {{
            put("HOME", new LinkedHashSet<>(Arrays.asList("q_error")));
            put("SEARCH", new LinkedHashSet<>(Arrays.asList("q2")));
            put("CART", new LinkedHashSet<>(Arrays.asList("q_error")));
        }});

        tablaTransiciones.put("q2", new LinkedHashMap<String, Set<String>>() {{
            put("HOME", new LinkedHashSet<>(Arrays.asList("q_error")));
            put("SEARCH", new LinkedHashSet<>(Arrays.asList("q2")));
            put("CART", new LinkedHashSet<>(Arrays.asList("q3")));
        }});

        tablaTransiciones.put("q3", new LinkedHashMap<String, Set<String>>() {{
            put("HOME", new LinkedHashSet<>(Arrays.asList("q_error")));
            put("SEARCH", new LinkedHashSet<>(Arrays.asList("q_error")));
            put("CART", new LinkedHashSet<>(Arrays.asList("q_error")));
        }});

        tablaTransiciones.put("q_error", new LinkedHashMap<String, Set<String>>() {{
            put("HOME", new LinkedHashSet<>(Arrays.asList("q_error")));
            put("SEARCH", new LinkedHashSet<>(Arrays.asList("q_error")));
            put("CART", new LinkedHashSet<>(Arrays.asList("q_error")));
        }});

        return new Ecommerce(
            "Ecommerce",
            alfabeto,
            estados,
            "q0",
            estadosAceptacion,
            tablaTransiciones
        );
    }
}
