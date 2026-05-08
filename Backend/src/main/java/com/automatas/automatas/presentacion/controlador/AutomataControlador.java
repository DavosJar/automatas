package com.automatas.automatas.presentacion.controlador;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.automatas.automatas.aplicacion.dto.ResultadoAutomata;
import com.automatas.automatas.aplicacion.servicio.AutomataServicio;

@RestController
@RequestMapping("/automatas")
public class AutomataControlador {

    @Autowired
    private AutomataServicio servicio;

    @PostMapping("/ciberseguridad")
    public ResultadoAutomata ciberseguridad(@RequestBody String[] simbolos) {
        return servicio.validar("ciberseguridad", simbolos);
    }

    @PostMapping("/iot")
    public ResultadoAutomata lecturasIoT(@RequestBody String[] simbolos) {
        return servicio.validar("iot", simbolos);
    }

    @PostMapping("/ecommerce")
    public ResultadoAutomata ecommerce(@RequestBody String[] simbolos) {
        return servicio.validar("ecommerce", simbolos);
    }
}
