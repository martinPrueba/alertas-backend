package com.kim21.alertas.controller;

import com.kim21.alertas.service.AlertasPreviosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/alertas-previos")
public class AlertasPreviosController 
{

    @Autowired
    private AlertasPreviosService alertasPreviosService;

    @GetMapping("/{id}")
    public ResponseEntity<?> getPrevioById(@PathVariable("id") Integer id) 
    {
        return alertasPreviosService.findPrevioById(id);
    }
}