package com.automatas.automatas.aplicacion.dto;

public class AutomataConValidacion {
    public AutomataResponse automata;
    public boolean valido;

    public AutomataConValidacion(AutomataResponse automata, boolean valido) {
        this.automata = automata;
        this.valido = valido;
    }
}
