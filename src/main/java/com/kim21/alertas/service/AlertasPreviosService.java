package com.kim21.alertas.service;

import org.springframework.http.ResponseEntity;

public interface AlertasPreviosService 
{
    
    ResponseEntity<?> findAllPrevios();
    ResponseEntity<?> findPrevioById(Integer id);
}
