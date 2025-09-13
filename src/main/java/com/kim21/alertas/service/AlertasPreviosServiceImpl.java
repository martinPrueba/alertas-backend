package com.kim21.alertas.service;

import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.kim21.alertas.model.AlertasPreviosModel;
import com.kim21.alertas.repository.AlertasPreviosRepository;

@Service
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
    public ResponseEntity<?> findPrevioById(Integer id) 
    {
        try 
        {
            if (id == null) 
            {
                return ResponseEntity.badRequest().body(Map.of("message", "El id no puede ser nulo"));
            }

            Optional<AlertasPreviosModel> previoOpt = previosRepository.findById(id);

            if (previoOpt.isPresent()) 
            {
                return ResponseEntity.ok(previoOpt.get());
            } 
            else 
            {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body( Map.of("message", "No se encontró ninguna alerta previa con id " + id));
            }

        } 
        catch (Exception e) 
        {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Error interno al buscar alerta previa"));
        }
    }
}
