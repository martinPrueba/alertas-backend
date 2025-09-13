package com.kim21.alertas.controller;

import com.kim21.alertas.dto.AlertFilterDTO;
import com.kim21.alertas.service.AlertasService;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/alertas")
@RequiredArgsConstructor
public class AlertasController 
{

    private final AlertasService alertasService;

    @GetMapping("/get-all-alerts")
    public ResponseEntity<?> getAllAlertas() 
    {
        return ResponseEntity.ok(alertasService.findAllAlertas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getAlertaById(@PathVariable Integer id) 
    {
        return alertasService.findAlertaById(id);
    }

    @GetMapping("/filter")
    public ResponseEntity<?> getAlertsByProcesoAndGrupoLocalAndInitAndEndDate(
            @RequestParam String proceso,
            @RequestParam String activo,
            @RequestParam OffsetDateTime initDate,
            @RequestParam OffsetDateTime endDate
    ) 
    {
        return alertasService.getAlertsByProcesoAndGrupoLocalAndInitAndEndDate(proceso,activo, initDate, endDate);
    }

}