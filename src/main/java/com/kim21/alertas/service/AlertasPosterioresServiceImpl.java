package com.kim21.alertas.service;

import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.kim21.alertas.model.AlertasPosterioresModel;
import com.kim21.alertas.repository.AlertasPosterioresRepository;


@Service
public class AlertasPosterioresServiceImpl implements AlertasPosterioresService 
{
        
    @Autowired
    private AlertasPosterioresRepository posterioresRepository;
    
    // POSTERIORES
    @Override
    public ResponseEntity<?> findAllPosteriores() {
        return ResponseEntity.ok(posterioresRepository.findAll());
    }

    @Override
    public ResponseEntity<?> findPosteriorById(Integer id) 
    {

        try 
        {
            if (id == null) 
            {
                return ResponseEntity.badRequest().body(Map.of("message", "El id no puede ser nulo"));
            }

            Optional<AlertasPosterioresModel> posterior = posterioresRepository.findById(id);

            if (posterior.isPresent()) 
            {
                return ResponseEntity.ok(posterior.get());
            } 
            else 
            {
                return ResponseEntity.status(404).body(Map.of("message", "No se encontró la alerta posterior con el id " + id));
            }

        } 
        catch (Exception e) 
        {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("message", "Error interno al buscar alerta posterior"));
        }

    }
}
