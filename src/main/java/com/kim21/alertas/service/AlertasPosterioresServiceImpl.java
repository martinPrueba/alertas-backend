package com.kim21.alertas.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import com.kim21.alertas.model.AlertasPosterioresModel;
import com.kim21.alertas.repository.AlertasPosterioresRepository;

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
    public ResponseEntity<?> findPosteriorById(Integer id) {
        Optional<AlertasPosterioresModel> posterior = posterioresRepository.findById(id);
        return posterior.map(ResponseEntity::ok)
                        .orElse(ResponseEntity.notFound().build());
    }
}
