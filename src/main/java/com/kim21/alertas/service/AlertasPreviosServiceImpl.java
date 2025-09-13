package com.kim21.alertas.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import com.kim21.alertas.model.AlertasPreviosModel;
import com.kim21.alertas.repository.AlertasPreviosRepository;

public class AlertasPreviosServiceImpl implements AlertasPreviosService
{
    @Autowired
    private AlertasPreviosRepository previosRepository;

    // PREVIOS
    @Override
    public ResponseEntity<?> findAllPrevios() {
        return ResponseEntity.ok(previosRepository.findAll());
    }

    @Override
    public ResponseEntity<?> findPrevioById(Integer id) {
        Optional<AlertasPreviosModel> previo = previosRepository.findById(id);
        return previo.map(ResponseEntity::ok)
                     .orElse(ResponseEntity.notFound().build());
    }
}
