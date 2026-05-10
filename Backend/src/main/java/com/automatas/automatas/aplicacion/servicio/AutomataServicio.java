package com.automatas.automatas.aplicacion.servicio;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.automatas.automatas.aplicacion.dto.ResultadoAutomata;
import com.automatas.automatas.dominio.automatas_no_deterministas.Ciberseguridad;
import com.automatas.automatas.dominio.automatas_no_deterministas.Ecommerce;
import com.automatas.automatas.dominio.automatas_no_deterministas.LecturasIoT;
import com.automatas.automatas.dominio.operaciones.MinimizadorAFD;
import com.automatas.automatas.dominio.operaciones.TransformadorAFN_AFD;

/**
 * Servicio de Autómatas
 * 
 * Orquesta el pipeline completo de validación de cadenas:
 * 1. Crea el AFN según el tipo
 * 2. Transforma AFN a AFD
 * 3. Minimiza el AFD
 * 4. Valida la cadena en los 3 autómatas
 * 5. Retorna los resultados y las representaciones
 * 
 * @author Proyecto Autómatas
 * @version 1.0
 */
@Service
public class AutomataServicio {
    
    /** Registro de estrategias por tipo de autómata */
    private final Map<String, EstrategiaAutomata> estrategias = new HashMap<>();

    /**
     * Constructor que inicializa las estrategias disponibles.
     * Registra implementaciones para: ciberseguridad, iot, ecommerce
     */
    public AutomataServicio() {
        estrategias.put("ciberseguridad", new EstrategiaCiberseguridad());
        estrategias.put("iot", new EstrategiaIoT());
        estrategias.put("ecommerce", new EstrategiaEcommerce());
    }

    /**
     * Valida una secuencia de símbolos en un autómata.
     * 
     * Pipeline:
     * 1. Crea el AFN según tipo
     * 2. Transforma a AFD (subset construction)
     * 3. Minimiza el AFD (tabla de distinción)
     * 4. Valida en los 3 autómatas
     * 5. Retorna resultados completos
     * 
     * @param tipo Tipo de autómata: "ciberseguridad", "iot", "ecommerce"
     * @param simbolos Array de símbolos a validar
     * @return ResultadoAutomata con los 3 autómatas y resultados de validación
     */
    public ResultadoAutomata validar(String tipo, String[] simbolos) {
        // Obtener estrategia para el tipo
        EstrategiaAutomata estrategia = estrategias.get(tipo);
        // Crear AFN
        var afn = crearAFN(tipo);
        // Transformar a AFD
        var afdTransformado = TransformadorAFN_AFD.transformar(afn);
        // Minimizar AFD
        var afdMinimizado = MinimizadorAFD.minimizar(afdTransformado);
        
        // Validar en los 3 autómatas
        return new ResultadoAutomata(
            estrategia.obtenerAFN(),
            estrategia.obtenerAFDTransformado(),
            estrategia.obtenerAFDMinimizado(),
            afn.esValida(simbolos),
            afdTransformado.esValida(simbolos),
            afdMinimizado.esValida(simbolos)
        );
    }

    /**
     * Factory method que crea el AFN adecuado según el tipo.
     * 
     * @param tipo Tipo de autómata
     * @return Nuevo AFN creado
     */
    private com.automatas.automatas.dominio.AFN crearAFN(String tipo) {
        return switch (tipo) {
            case "iot" -> LecturasIoT.crear();
            case "ecommerce" -> Ecommerce.crear();
            default -> Ciberseguridad.crear();
        };
    }
}
