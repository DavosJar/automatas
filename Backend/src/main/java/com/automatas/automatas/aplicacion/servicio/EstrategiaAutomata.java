package com.automatas.automatas.aplicacion.servicio;

import com.automatas.automatas.aplicacion.dto.AutomataResponse;

public interface EstrategiaAutomata {
    AutomataResponse obtenerAFN();
    AutomataResponse obtenerAFDTransformado();
    AutomataResponse obtenerAFDMinimizado();
}
