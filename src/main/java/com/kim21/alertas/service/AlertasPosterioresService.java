package com.kim21.alertas.service;

import org.springframework.http.ResponseEntity;

public interface AlertasPosterioresService 
{
    ResponseEntity<?> findAllPosteriores();
    ResponseEntity<?> findPosteriorById(Integer id);
}
