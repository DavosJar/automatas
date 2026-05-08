package com.automatas.automatas.dominio.automata_deterministas;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import com.automatas.automatas.dominio.AFD;

public class CiberseguridadDeteminista extends AFD {

    public CiberseguridadDeteminista(
        String nombre,
        Set<String> alfabeto,
        Set<String> estados,
        String estadoInicial,
        Set<String> estadosAceptacion,
        Map<String, Map<String, String>> tablaTransiciones
    ) {
        super(nombre, alfabeto, estados, estadoInicial, estadosAceptacion, tablaTransiciones);
    }

    public static CiberseguridadDeteminista crear() {
        Set<String> alfabeto = new LinkedHashSet<>(Arrays.asList("SYN", "ACK", "RST"));
        Set<String> estados = new LinkedHashSet<>(Arrays.asList("q0", "q1", "q12", "q3"));
        Set<String> estadosAceptacion = new LinkedHashSet<>(Arrays.asList("q3"));

        Map<String, Map<String, String>> tablaTransiciones = new LinkedHashMap<>();

        tablaTransiciones.put("q0", new LinkedHashMap<String, String>() {{
            put("SYN", "q1");
        }});

        tablaTransiciones.put("q1", new LinkedHashMap<String, String>() {{
            put("ACK", "q12");
        }});

        tablaTransiciones.put("q12", new LinkedHashMap<String, String>() {{
            put("ACK", "q12");
            put("RST", "q3");
        }});

        tablaTransiciones.put("q3", new LinkedHashMap<>());

        return new CiberseguridadDeteminista(
            "CiberseguridadDeterminista",
            alfabeto,
            estados,
            "q0",
            estadosAceptacion,
            tablaTransiciones
        );
    }
}
