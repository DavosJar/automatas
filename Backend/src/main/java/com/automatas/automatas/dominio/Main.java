package com.automatas.automatas.dominio;

import java.util.Arrays;
import java.util.List;

import com.automatas.automatas.dominio.automatas_no_deterministas.Ciberseguridad;
import com.automatas.automatas.dominio.automatas_no_deterministas.Ecommerce;
import com.automatas.automatas.dominio.automatas_no_deterministas.LecturasIoT;
import com.automatas.automatas.dominio.operaciones.MinimizadorAFD;
import com.automatas.automatas.dominio.operaciones.TransformadorAFN_AFD;

/**
 * Clase principal para comparación de autómatas desde línea de comandos
 * 
 * Esta clase ejecuta el pipeline completo para tres casos de uso:
 * 1. Ciberseguridad: Protocolo de handshake (SYN, ACK, DATA, RST)
 * 2. E-commerce: Flujo de compra (HOME, SEARCH, CART)
 * 3. Lecturas IoT: Sensores de temperatura/humedad (HDR, TEMP, HUM, CRC)
 * 
 * Para cada caso:
 * - Crea el AFN
 * - Transforma a AFD
 * - Minimiza el AFD
 * - Valida 20 cadenas frontera
 * - Compara resultados (deben ser idénticos)
 * - Imprime tabla en consola
 * 
 * @author Proyecto Autómatas
 * @version 1.0
 */
public class Main {

    /**
     * Punto de entrada del programa.
     * Ejecuta la comparación de los 3 autómatas.
     * 
     * @param args No se utilizan argumentos
     */
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

    /**
     * Imprime una comparativa de validación para un caso de autómata.
     * 
     * Crea una tabla con:
     * - Cada cadena a probar
     * - Resultado en AFN
     * - Resultado en AFD transformado
     * - Resultado en AFD minimizado
     * - Contador de coincidencias
     * 
     * @param caso El caso de comparación con AFN y cadenas de prueba
     */
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

    /**
     * Convierte un resultado booleano a string legible.
     * 
     * @param valor true o false
     * @return "ACEPTA" si es true, "RECHAZA" si es false
     */
    private static String formatearResultado(boolean valor) {
        return valor ? "ACEPTA" : "RECHAZA";
    }

    /**
     * Convierte un array de símbolos a string para mostrar en tabla.
     * 
     * @param cadena Array de símbolos
     * @return String con símbolos separados por espacios, o "ε" si está vacía
     */
    private static String formatearCadena(String[] cadena) {
        if (cadena.length == 0) {
            return "ε";
        }

        return String.join(" ", cadena);
    }

    /**
     * Dibuja la línea horizontal de borde de la tabla.
     * 
     * @param anchos Array con el ancho de cada columna
     * @return String con el borde (+-+---+-...)
     */
    private static String renderBorder(int[] anchos) {
        StringBuilder borde = new StringBuilder();
        borde.append('+');

        for (int ancho : anchos) {
            borde.append(repetir('-', ancho + 2)).append('+');
        }

        return borde.toString();
    }

    /**
     * Dibuja una fila de la tabla con contenido alineado.
     * 
     * @param anchos Array con el ancho de cada columna
     * @param valores Contenido de cada celda
     * @return String con la fila formateada
     */
    private static String renderRow(int[] anchos, String... valores) {
        StringBuilder fila = new StringBuilder();
        fila.append('|');

        for (int i = 0; i < valores.length; i++) {
            fila.append(' ').append(ajustar(valores[i], anchos[i])).append(' ').append('|');
        }

        return fila.toString();
    }

    /**
     * Ajusta un string a un ancho específoco rellenando con espacios.
     * 
     * @param valor String a ajustar
     * @param ancho Ancho deseado
     * @return String ajustado (truncado o rellenado)
     */
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

    /**
     * Repite un carácter un número determinado de veces.
     * 
     * @param caracter El carácter a repetir
     * @param cantidad Número de repeticiones
     * @return String con los caracteres repetidos
     */
    private static String repetir(char caracter, int cantidad) {
        StringBuilder repetido = new StringBuilder();
        for (int i = 0; i < cantidad; i++) {
            repetido.append(caracter);
        }

        return repetido.toString();
    }

    /**
     * Proporciona 20 cadenas frontera para el autómata de Ciberseguridad.
     * 
     * Incluye:
     * - Cadena vacía (ε)
     * - Prefijos correctos e incorrectos
     * - Secuencias válidas e inválidas
     * 
     * @return Lista de 20 cadenas de prueba
     */
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

    /**
     * Proporciona 20 cadenas frontera para el autómata de E-commerce.
     * 
     * Incluye:
     * - Cadena vacía (ε)
     * - Caminos incompletos
     * - Secuencias válidas e inválidas
     * 
     * @return Lista de 20 cadenas de prueba
     */
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

    /**
     * Proporciona 20 cadenas frontera para el autómata de IoT.
     * 
     * Incluye:
     * - Cadena vacía (ε)
     * - Múltiples lecturas de sensores
     * - Secuencias válidas e inválidas
     * 
     * @return Lista de 20 cadenas de prueba
     */
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

    /**
     * Clase interna que agrupa información de un caso de comparación.
     * 
     * Contiene:
     * - nombre: Descripción del caso
     * - afn: El autómata finito no determinista
     * - cadenas: Las 20 cadenas frontera para probar
     */
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

