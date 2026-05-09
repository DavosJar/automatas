package com.automatas.automatas.dominio.automatas_no_deterministas;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import com.automatas.automatas.dominio.AFN;

public class LecturasIoT extends AFN {

    public LecturasIoT(
        String nombre,
        Set<String> alfabeto,
        Set<String> estados,
        String estadoInicial,
        Set<String> estadosAceptacion,
        Map<String, Map<String, Set<String>>> tablaTransiciones
    ) {
        super(nombre, alfabeto, estados, estadoInicial, estadosAceptacion, tablaTransiciones);
    }

    public static LecturasIoT crear() {
        Set<String> alfabeto = new LinkedHashSet<>(Arrays.asList("HDR", "TEMP", "HUM", "CRC"));
        Set<String> estados = new LinkedHashSet<>(Arrays.asList("q0", "q1", "q2", "q3"));
        Set<String> estadosAceptacion = new LinkedHashSet<>(Arrays.asList("q3"));

        Map<String, Map<String, Set<String>>> tablaTransiciones = new LinkedHashMap<>();

        tablaTransiciones.put("q0", new LinkedHashMap<String, Set<String>>() {{
            put("HDR", new LinkedHashSet<>(Arrays.asList("q1")));
        }});

        tablaTransiciones.put("q1", new LinkedHashMap<String, Set<String>>() {{
            put("TEMP", new LinkedHashSet<>(Arrays.asList("q1", "q2")));
            put("HUM", new LinkedHashSet<>(Arrays.asList("q1", "q2")));
            put("CRC", new LinkedHashSet<>(Arrays.asList("q3")));
        }});

        tablaTransiciones.put("q2", new LinkedHashMap<String, Set<String>>() {{
            put("CRC", new LinkedHashSet<>(Arrays.asList("q3")));
        }});

        tablaTransiciones.put("q3", new LinkedHashMap<String, Set<String>>() {{
        }});


        return new LecturasIoT(
            "LecturasIoT",
            alfabeto,
            estados,
            "q0",
            estadosAceptacion,
            tablaTransiciones
        );
    }
}
