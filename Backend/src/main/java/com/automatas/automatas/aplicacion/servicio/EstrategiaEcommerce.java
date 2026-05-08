package com.automatas.automatas.aplicacion.servicio;

import com.automatas.automatas.aplicacion.dto.AutomataResponse;
import com.automatas.automatas.dominio.automatas_no_deterministas.Ecommerce;
import com.automatas.automatas.dominio.operaciones.MinimizadorAFD;
import com.automatas.automatas.dominio.operaciones.TransformadorAFN_AFD;
import java.util.Map;

public class EstrategiaEcommerce implements EstrategiaAutomata {

    @Override
    public AutomataResponse obtenerAFN() {
        var afn = Ecommerce.crear();
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
        var afn = Ecommerce.crear();
        var afd = TransformadorAFN_AFD.transformar(afn);
        @SuppressWarnings("unchecked")
        Map<String, Map<String, Object>> tabla = (Map<String, Map<String, Object>>) (Map<?, ?>) afd.getTablaTransiciones();
        return new AutomataResponse(
            afd.getNombre(),
            afd.getAlfabeto(),
            afd.getEstados(),
            afd.getEstadoInicial(),
            afd.getEstadosAceptacion(),
            tabla
        );
    }

    @Override
    public AutomataResponse obtenerAFDMinimizado() {
        var afn = Ecommerce.crear();
        var afd = TransformadorAFN_AFD.transformar(afn);
        var afdMin = MinimizadorAFD.minimizar(afd);
        @SuppressWarnings("unchecked")
        Map<String, Map<String, Object>> tabla = (Map<String, Map<String, Object>>) (Map<?, ?>) afdMin.getTablaTransiciones();
        return new AutomataResponse(
            afdMin.getNombre(),
            afdMin.getAlfabeto(),
            afdMin.getEstados(),
            afdMin.getEstadoInicial(),
            afdMin.getEstadosAceptacion(),
            tabla
        );
    }
}
