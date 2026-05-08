package com.automatas.automatas.aplicacion.dto;

public class ResultadoAutomata {
    public AutomataResponse afn;
    public AutomataResponse afdTransformado;
    public AutomataResponse afdMinimizado;
    public boolean validoAFN;
    public boolean validoAFDTransformado;
    public boolean validoAFDMinimizado;

    public ResultadoAutomata(AutomataResponse afn, AutomataResponse afdTransformado, 
                            AutomataResponse afdMinimizado, boolean validoAFN, 
                            boolean validoAFDTransformado, boolean validoAFDMinimizado) {
        this.afn = afn;
        this.afdTransformado = afdTransformado;
        this.afdMinimizado = afdMinimizado;
        this.validoAFN = validoAFN;
        this.validoAFDTransformado = validoAFDTransformado;
        this.validoAFDMinimizado = validoAFDMinimizado;
    }
}
