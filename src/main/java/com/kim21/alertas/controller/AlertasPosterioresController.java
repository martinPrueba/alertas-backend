package com.kim21.alertas.controller;

import com.kim21.alertas.service.AlertasPosterioresService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/alertas-posteriores")
public class AlertasPosterioresController 
{

    @Autowired
    private AlertasPosterioresService posterioresService;

    // Obtener todas las alertas posteriores
    @GetMapping
    public ResponseEntity<?> getAllPosteriores() 
    {
        return posterioresService.findAllPosteriores();
    }

    // Obtener una alerta posterior por su ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getPosteriorById(@PathVariable Integer id) 
    {
        return posterioresService.findPosteriorById(id);
    }
}