package com.automatas.automatas.presentacion.controlador;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.automatas.automatas.aplicacion.dto.ResultadoAutomata;
import com.automatas.automatas.aplicacion.servicio.AutomataServicio;

/**
 * Controlador REST de Autómatas
 * 
 * Proporciona tres endpoints para validar cadenas en los autómatas disponibles.
 * Cada endpoint recibe un array de símbolos y retorna los resultados de
 * validación en AFN, AFD y AFD minimizado.
 * 
 * Base de ruta: /automatas
 * 
 * @author Proyecto Autómatas
 * @version 1.0
 */
@RestController
@RequestMapping("/automatas")
public class AutomataControlador {

    /** Servicio de autómatas inyectado */
    @Autowired
    private AutomataServicio servicio;

    /**
     * Valida una cadena en el autómata de ciberseguridad.
     * 
     * @param simbolos Array de símbolos: ["SYN", "ACK", "RST", ...]
     * @return Resultado con validación en AFN, AFD y AFD minimizado
     */
    @PostMapping("/ciberseguridad")
    public ResultadoAutomata ciberseguridad(@RequestBody String[] simbolos) {
        return servicio.validar("ciberseguridad", simbolos);
    }

    /**
     * Valida una cadena en el autómata de Lecturas IoT.
     * 
     * @param simbolos Array de símbolos: ["HDR", "TEMP", "HUM", "CRC", ...]
     * @return Resultado con validación en AFN, AFD y AFD minimizado
     */
    @PostMapping("/iot")
    public ResultadoAutomata lecturasIoT(@RequestBody String[] simbolos) {
        return servicio.validar("iot", simbolos);
    }

    /**
     * Valida una cadena en el autómata de e-commerce.
     * 
     * @param simbolos Array de símbolos: ["HOME", "SEARCH", "CART", ...]
     * @return Resultado con validación en AFN, AFD y AFD minimizado
     */
    @PostMapping("/ecommerce")
    public ResultadoAutomata ecommerce(@RequestBody String[] simbolos) {
        return servicio.validar("ecommerce", simbolos);
    }
}
