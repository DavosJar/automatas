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

@Service
public class AutomataServicio {
    
    private final Map<String, EstrategiaAutomata> estrategias = new HashMap<>();

    public AutomataServicio() {
        estrategias.put("ciberseguridad", new EstrategiaCiberseguridad());
        estrategias.put("iot", new EstrategiaIoT());
        estrategias.put("ecommerce", new EstrategiaEcommerce());
    }

    public ResultadoAutomata validar(String tipo, String[] simbolos) {
        EstrategiaAutomata estrategia = estrategias.get(tipo);
        var afn = crearAFN(tipo);
        var afdTransformado = TransformadorAFN_AFD.transformar(afn);
        var afdMinimizado = MinimizadorAFD.minimizar(afdTransformado);
        
        return new ResultadoAutomata(
            estrategia.obtenerAFN(),
            estrategia.obtenerAFDTransformado(),
            estrategia.obtenerAFDMinimizado(),
            afn.esValida(simbolos),
            afdTransformado.esValida(simbolos),
            afdMinimizado.esValida(simbolos)
        );
    }

    private com.automatas.automatas.dominio.AFN crearAFN(String tipo) {
        return switch (tipo) {
            case "iot" -> LecturasIoT.crear();
            case "ecommerce" -> Ecommerce.crear();
            default -> Ciberseguridad.crear();
        };
    }
}
