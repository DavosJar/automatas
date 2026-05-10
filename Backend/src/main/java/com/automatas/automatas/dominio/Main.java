package com.automatas.automatas.dominio;

import java.util.Arrays;
import java.util.List;

import com.automatas.automatas.dominio.automatas_no_deterministas.Ciberseguridad;
import com.automatas.automatas.dominio.automatas_no_deterministas.Ecommerce;
import com.automatas.automatas.dominio.automatas_no_deterministas.LecturasIoT;
import com.automatas.automatas.dominio.operaciones.MinimizadorAFD;
import com.automatas.automatas.dominio.operaciones.TransformadorAFN_AFD;

public class Main {

    public static void main(String[] args) {
        System.out.println("=== COMPARATIVA: AFN vs AFD vs AFD MINIMIZADO ===\n");

        List<CasoComparacion> casos = Arrays.asList(
            new CasoComparacion("Ciberseguridad", Ciberseguridad.crear(), cadenasCiberseguridad()),
            new CasoComparacion("Ecommerce", Ecommerce.crear(), cadenasEcommerce()),
            new CasoComparacion("Lecturas IoT", LecturasIoT.crear(), cadenasIoT())
        );

        for (CasoComparacion caso : casos) {
            imprimirComparativa(caso);
        }
    }

    private static void imprimirComparativa(CasoComparacion caso) {
        AFD afd = TransformadorAFN_AFD.transformar(caso.afn);
        AFD afdMinimizado = MinimizadorAFD.minimizar(afd);

        int[] anchos = {4, 40, 8, 8, 14};

        System.out.println("Caso: " + caso.nombre);
        System.out.println("AFN: " + caso.afn.getNombre() + " | AFD: " + afd.getNombre() + " | AFD minimizado: " + afdMinimizado.getNombre());
        System.out.println(renderBorder(anchos));
        System.out.println(renderRow(anchos, "#", "Cadena", "AFN", "AFD", "AFD minimizado"));
        System.out.println(renderBorder(anchos));

        int coincidencias = 0;

        for (int i = 0; i < caso.cadenas.size(); i++) {
            String[] cadena = caso.cadenas.get(i);
            boolean resultadoAfn = caso.afn.esValida(cadena);
            boolean resultadoAfd = afd.esValida(cadena);
            boolean resultadoAfdMin = afdMinimizado.esValida(cadena);
            boolean iguales = resultadoAfn == resultadoAfd && resultadoAfd == resultadoAfdMin;

            if (iguales) {
                coincidencias++;
            }

            System.out.println(renderRow(
                anchos,
                String.valueOf(i + 1),
                formatearCadena(cadena),
                formatearResultado(resultadoAfn),
                formatearResultado(resultadoAfd),
                formatearResultado(resultadoAfdMin)
            ));
        }

        System.out.println(renderBorder(anchos));
        System.out.println("Coincidencias: " + coincidencias + " / " + caso.cadenas.size() + (coincidencias == caso.cadenas.size() ? " (idéntico)" : " (revisar diferencias)"));
        System.out.println();
    }

    private static String formatearResultado(boolean valor) {
        return valor ? "ACEPTA" : "RECHAZA";
    }

    private static String formatearCadena(String[] cadena) {
        if (cadena.length == 0) {
            return "ε";
        }

        return String.join(" ", cadena);
    }

    private static String renderBorder(int[] anchos) {
        StringBuilder borde = new StringBuilder();
        borde.append('+');

        for (int ancho : anchos) {
            borde.append(repetir('-', ancho + 2)).append('+');
        }

        return borde.toString();
    }

    private static String renderRow(int[] anchos, String... valores) {
        StringBuilder fila = new StringBuilder();
        fila.append('|');

        for (int i = 0; i < valores.length; i++) {
            fila.append(' ').append(ajustar(valores[i], anchos[i])).append(' ').append('|');
        }

        return fila.toString();
    }

    private static String ajustar(String valor, int ancho) {
        if (valor.length() >= ancho) {
            return valor.substring(0, ancho);
        }

        StringBuilder ajustado = new StringBuilder(valor);
        while (ajustado.length() < ancho) {
            ajustado.append(' ');
        }

        return ajustado.toString();
    }

    private static String repetir(char caracter, int cantidad) {
        StringBuilder repetido = new StringBuilder();
        for (int i = 0; i < cantidad; i++) {
            repetido.append(caracter);
        }

        return repetido.toString();
    }

    private static List<String[]> cadenasCiberseguridad() {
        return Arrays.asList(
            new String[] {},
            new String[] {"SYN"},
            new String[] {"ACK"},
            new String[] {"RST"},
            new String[] {"SYN", "ACK"},
            new String[] {"SYN", "ACK", "RST"},
            new String[] {"SYN", "ACK", "ACK"},
            new String[] {"SYN", "ACK", "ACK", "RST"},
            new String[] {"SYN", "ACK", "ACK", "ACK", "RST"},
            new String[] {"SYN", "ACK", "RST", "ACK"},
            new String[] {"SYN", "RST"},
            new String[] {"SYN", "DATA"},
            new String[] {"SYN", "DATA", "RST"},
            new String[] {"ACK", "RST"},
            new String[] {"SYN", "SYN", "ACK", "RST"},
            new String[] {"SYN", "ACK", "SYN", "RST"},
            new String[] {"SYN", "ACK", "ACK", "RST", "RST"},
            new String[] {"SYN", "ACK", "ACK", "DATA", "RST"},
            new String[] {"SYN", "ACK", "ACK", "ACK"},
            new String[] {"SYN", "ACK", "ACK", "ACK", "RST", "DATA"}
        );
    }

    private static List<String[]> cadenasEcommerce() {
        return Arrays.asList(
            new String[] {},
            new String[] {"HOME"},
            new String[] {"SEARCH"},
            new String[] {"CART"},
            new String[] {"HOME", "SEARCH"},
            new String[] {"HOME", "CART"},
            new String[] {"HOME", "SEARCH", "CART"},
            new String[] {"HOME", "SEARCH", "SEARCH"},
            new String[] {"HOME", "SEARCH", "SEARCH", "CART"},
            new String[] {"HOME", "SEARCH", "SEARCH", "SEARCH", "CART"},
            new String[] {"HOME", "SEARCH", "SEARCH", "SEARCH"},
            new String[] {"HOME", "SEARCH", "CART", "CART"},
            new String[] {"HOME", "HOME", "SEARCH", "CART"},
            new String[] {"HOME", "SEARCH", "HOME", "CART"},
            new String[] {"SEARCH", "HOME", "SEARCH", "CART"},
            new String[] {"HOME", "SEARCH", "SEARCH", "HOME"},
            new String[] {"HOME", "SEARCH", "SEARCH", "CART", "HOME"},
            new String[] {"HOME", "SEARCH", "SEARCH", "SEARCH", "CART", "CART"},
            new String[] {"HOME", "SEARCH", "SEARCH", "SEARCH", "SEARCH", "CART"},
            new String[] {"HOME", "SEARCH", "SEARCH", "SEARCH", "SEARCH"}
        );
    }

    private static List<String[]> cadenasIoT() {
        return Arrays.asList(
            new String[] {},
            new String[] {"HDR"},
            new String[] {"TEMP"},
            new String[] {"HUM"},
            new String[] {"CRC"},
            new String[] {"HDR", "CRC"},
            new String[] {"HDR", "TEMP"},
            new String[] {"HDR", "HUM"},
            new String[] {"HDR", "TEMP", "CRC"},
            new String[] {"HDR", "HUM", "CRC"},
            new String[] {"HDR", "TEMP", "TEMP", "CRC"},
            new String[] {"HDR", "TEMP", "HUM", "CRC"},
            new String[] {"HDR", "HUM", "TEMP", "CRC"},
            new String[] {"HDR", "TEMP", "HUM", "HUM", "CRC"},
            new String[] {"HDR", "TEMP", "HUM", "TEMP", "CRC"},
            new String[] {"HDR", "TEMP", "HUM", "CRC", "TEMP"},
            new String[] {"HDR", "TEMP", "HUM", "CRC", "CRC"},
            new String[] {"HDR", "TEMP", "CRC", "CRC"},
            new String[] {"HDR", "CRC", "TEMP"},
            new String[] {"TEMP", "CRC"}
        );
    }

    private static final class CasoComparacion {
        private final String nombre;
        private final AFN afn;
        private final List<String[]> cadenas;

        private CasoComparacion(String nombre, AFN afn, List<String[]> cadenas) {
            this.nombre = nombre;
            this.afn = afn;
            this.cadenas = cadenas;
        }
    }
}

