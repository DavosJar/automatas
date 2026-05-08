package com.automatas.automatas.dominio;

import com.automatas.automatas.dominio.automatas.Ciberseguridad;
import com.automatas.automatas.dominio.automatas.Ecommerce;
import com.automatas.automatas.dominio.automatas.LecturasIoT;

public class Main {

    public static void main(String[] args) {
        System.out.println("=== AUTÓMATA FINITO NO DETERMINISTA: CIBERSEGURIDAD ===\n");
        pruebasCiberseguridad();
        
        System.out.println("\n=== AUTÓMATA FINITO NO DETERMINISTA: ECOMMERCE ===\n");
        pruebasEcommerce();
        
        System.out.println("\n=== AUTÓMATA FINITO NO DETERMINISTA: LECTURAS IOT ===\n");
        pruebasLecturasIoT();
    }

    private static void pruebasCiberseguridad() {
        Ciberseguridad ciberseguridad = Ciberseguridad.crear();
        
        prueba(ciberseguridad, new String[]{"SYN", "ACK", "RST"}, true, "Three-way handshake válido");
        prueba(ciberseguridad, new String[]{"SYN", "DATA", "RST"}, false, "DATA no permitido después de SYN");
        prueba(ciberseguridad, new String[]{"SYN", "ACK", "ACK", "RST"}, true, "No determinismo: múltiples ACKs");
    }

    private static void pruebasEcommerce() {
        Ecommerce ecommerce = Ecommerce.crear();
        
        prueba(ecommerce, new String[]{"HOME", "SEARCH", "CART"}, true, "Flujo de compra completo");
        prueba(ecommerce, new String[]{"HOME", "CART"}, false, "No se puede ir al carrito sin buscar");
        prueba(ecommerce, new String[]{"HOME", "SEARCH", "SEARCH", "CART"}, true, "Búsquedas múltiples permitidas");
    }

    private static void pruebasLecturasIoT() {
        LecturasIoT iot = LecturasIoT.crear();
        
        prueba(iot, new String[]{"HDR", "TEMP", "HUM", "CRC"}, true, "Patrón válido: HDR TEMP HUM CRC");
        prueba(iot, new String[]{"HDR", "TEMP", "TEMP", "HUM", "CRC"}, true, "Múltiples TEMP y HUM permitidos");
        prueba(iot, new String[]{"HDR", "CRC"}, false, "CRC sin datos intermedios rechazado");
        prueba(iot, new String[]{"TEMP", "HUM", "CRC"}, false, "Sin HDR inicial rechazado");
        prueba(iot, new String[]{"HDR", "HUM", "HUM", "CRC"}, true, "Patrón válido: HDR HUM HUM CRC");
    }

    private static void prueba(AFN automata, String[] cadena, boolean esperado, String descripcion) {
        boolean resultado = automata.esValida(cadena);
        String estado = resultado == esperado ? "✓ CORRECTO" : "✗ ERROR";
        
        System.out.println(estado + " | " + descripcion);
        System.out.println("  Entrada: " + java.util.Arrays.toString(cadena));
        System.out.println("  Esperado: " + esperado + " | Obtenido: " + resultado);
        System.out.println();
    }
}

