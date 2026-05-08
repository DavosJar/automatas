package com.automatas.automatas.dominio;

import com.automatas.automatas.dominio.automata_deterministas.CiberseguridadDeteminista;
import com.automatas.automatas.dominio.automatas_no_deterministas.Ciberseguridad;
import com.automatas.automatas.dominio.operaciones.MinimizadorAFD;
import com.automatas.automatas.dominio.operaciones.TransformadorAFN_AFD;

public class Main {

    public static void main(String[] args) {
        System.out.println("=== COMPARATIVA CUÁDRUPLE: AFN vs AFD QUEMADO vs AFD TRANSFORMADO vs AFD MINIMIZADO ===\n");
        comparativaCuadruple();
    }

    private static void comparativaCuadruple() {
        Ciberseguridad afn = Ciberseguridad.crear();
        CiberseguridadDeteminista afdQuemado = CiberseguridadDeteminista.crear();
        AFD afdTransformado = TransformadorAFN_AFD.transformar(afn);
        AFD afdMinimizado = MinimizadorAFD.minimizar(afdTransformado);

        String[][] cadenas = {
            {"SYN", "ACK", "RST"},
            {"SYN", "ACK", "ACK", "RST"},
            {"SYN", "DATA", "RST"},
            {"ACK", "RST"}
        };

        String[] descripciones = {
            "Three-way handshake básico",
            "Múltiples ACKs (no determinismo)",
            "DATA no permitido",
            "Sin SYN inicial"
        };

        System.out.println("Información de autómatas:");
        System.out.println("  AFN: " + afn.getNombre() + " (" + afn.getEstados().size() + " estados)");
        System.out.println("  AFD Quemado: " + afdQuemado.getNombre() + " (" + afdQuemado.getEstados().size() + " estados)");
        System.out.println("  AFD Transformado: " + afdTransformado.getNombre() + " (" + afdTransformado.getEstados().size() + " estados)");
        System.out.println("  AFD Minimizado: " + afdMinimizado.getNombre() + " (" + afdMinimizado.getEstados().size() + " estados)");
        System.out.println();

        for (int i = 0; i < cadenas.length; i++) {
            boolean afnResult = afn.esValida(cadenas[i]);
            boolean afdQuemadoResult = afdQuemado.esValida(cadenas[i]);
            boolean afdTransformadoResult = afdTransformado.esValida(cadenas[i]);
            boolean afdMinimizadoResult = afdMinimizado.esValida(cadenas[i]);

            boolean todasIguales = (afnResult == afdQuemadoResult) && (afdQuemadoResult == afdTransformadoResult) && (afdTransformadoResult == afdMinimizadoResult);
            String match = todasIguales ? "✓" : "✗";

            System.out.println(match + " | " + descripciones[i]);
            System.out.println("  Cadena: " + java.util.Arrays.toString(cadenas[i]));
            System.out.println("  AFN: " + afnResult + " | AFD Quemado: " + afdQuemadoResult + " | AFD Transformado: " + afdTransformadoResult + " | AFD Minimizado: " + afdMinimizadoResult);
            System.out.println();
        }
    }
}

