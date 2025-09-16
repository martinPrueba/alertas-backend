package com.kim21.alertas.controller;

import com.kim21.alertas.dto.ProcessAssociateIconDTO;
import com.kim21.alertas.model.ProcessAssociateIconModel;
import com.kim21.alertas.repository.ProcessAssociateIconRepository;
import com.kim21.alertas.service.ProcessAssociateIconService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/process-icons")
public class ProcessAssociateIconController 
{

    @Autowired 
    private ProcessAssociateIconService processAssociateIconService;


    // GET: obtener ícono por proceso
    @GetMapping("/get-process/{proceso}")
    public ResponseEntity<?> getIconByProceso(@PathVariable String proceso) 
    {
        return processAssociateIconService.getIconByProceso(proceso);
    }

    // POST: insertar un nuevo proceso con su ícono
    @PostMapping("/create-process-icon")
    public ResponseEntity<?> createProcessIcon(@RequestBody ProcessAssociateIconDTO dto) 
    {
        return processAssociateIconService.createProcessIcon(dto);   
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateIconByProceso(@RequestBody ProcessAssociateIconDTO dto) 
    {
        return processAssociateIconService.updateIconByProceso(dto);
    }
    
    @GetMapping("/get-all")
    public ResponseEntity<?> getAllProcesos() 
    {
        try 
        {
            List<ProcessAssociateIconModel> procesos = processAssociateIconService.getAllProcesos();
            return ResponseEntity.ok(procesos);
        } 
        catch (Exception e) 
        {
            return ResponseEntity.status(500).body("Error al obtener procesos: " + e.getMessage());
        }
    }
}