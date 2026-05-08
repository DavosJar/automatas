package com.automatas.automatas.aplicacion.servicio;

import java.util.LinkedHashMap;
import java.util.Map;

import com.automatas.automatas.aplicacion.dto.AutomataResponse;
import com.automatas.automatas.dominio.AFD;
import com.automatas.automatas.dominio.automatas_no_deterministas.Ciberseguridad;
import com.automatas.automatas.dominio.operaciones.MinimizadorAFD;
import com.automatas.automatas.dominio.operaciones.TransformadorAFN_AFD;

public class EstrategiaCiberseguridad implements EstrategiaAutomata {

    @Override
    public AutomataResponse obtenerAFN() {
        var afn = Ciberseguridad.crear();
        @SuppressWarnings("unchecked")
        Map<String, Map<String, Object>> tabla = (Map<String, Map<String, Object>>) (Map<?, ?>) afn.getTablaTransiciones();
        return new AutomataResponse(
            afn.getNombre(),
            afn.getAlfabeto(),
            afn.getEstados(),
            afn.getEstadoInicial(),
            afn.getEstadosAceptacion(),
            tabla
        );
    }

    @Override
    public AutomataResponse obtenerAFDTransformado() {
        var afn = Ciberseguridad.crear();
        var afd = TransformadorAFN_AFD.transformar(afn);
        Map<String, Map<String, Object>> tablaFiltrada = filtrarTransicionesAlTrampa(afd.getTablaTransiciones());
        return new AutomataResponse(
            afd.getNombre(),
            afd.getAlfabeto(),
            afd.getEstados(),
            afd.getEstadoInicial(),
            afd.getEstadosAceptacion(),
            tablaFiltrada
        );
    }

    @Override
    public AutomataResponse obtenerAFDMinimizado() {
        var afn = Ciberseguridad.crear();
        var afd = TransformadorAFN_AFD.transformar(afn);
        var afdMin = MinimizadorAFD.minimizar(afd);
        Map<String, Map<String, Object>> tablaFiltrada = filtrarTransicionesAlTrampa(afdMin.getTablaTransiciones());
        return new AutomataResponse(
            afdMin.getNombre(),
            afdMin.getAlfabeto(),
            afdMin.getEstados(),
            afdMin.getEstadoInicial(),
            afdMin.getEstadosAceptacion(),
            tablaFiltrada
        );
    }

    private Map<String, Map<String, Object>> filtrarTransicionesAlTrampa(Map<String, Map<String, String>> tabla) {
        Map<String, Map<String, Object>> resultado = new LinkedHashMap<>();
        for (String estado : tabla.keySet()) {
            Map<String, String> transiciones = tabla.get(estado);
            Map<String, Object> transicionesFiltradas = new LinkedHashMap<>();
            for (String simbolo : transiciones.keySet()) {
                String destino = transiciones.get(simbolo);
                if (!destino.equals(AFD.ESTADO_TRAMPA)) {
                    transicionesFiltradas.put(simbolo, destino);
                }
            }
            resultado.put(estado, transicionesFiltradas);
        }
        return resultado;
    }
}
