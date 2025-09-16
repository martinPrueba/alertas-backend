package com.kim21.alertas.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.kim21.alertas.model.VisibleFieldConfigModel;
import com.kim21.alertas.repository.VisibleFieldConfigRepository;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/visible-fields")
public class VisibleFieldConfigController 
{
    @Autowired
    private VisibleFieldConfigRepository repository;


    @GetMapping("/get-all")
    public ResponseEntity<?> getAllVisibleFields() 
    {
        return ResponseEntity.ok(repository.findAll());
    }

    @PutMapping("/update-all")
    public ResponseEntity<?> updateAll(@RequestBody List<VisibleFieldConfigModel> fields) 
    {
        try 
        {
            for (VisibleFieldConfigModel field : fields) 
            {
                repository.save(field); // como el @Id es fieldName, hará update automático
            }
            return ResponseEntity.ok(Map.of("message", "Campos actualizados correctamente"));
        } 
        catch (Exception e) 
        {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Error al actualizar campos", "error", e.getMessage()));
        }

    }
}