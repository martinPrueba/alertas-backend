package com.kim21.alertas.service;

import org.springframework.http.ResponseEntity;

import com.kim21.alertas.dto.ProcessAssociateIconDTO;

public interface ProcessAssociateIconService 
{
    ResponseEntity<?> getIconByProceso(String proceso);
    ResponseEntity<?> createProcessIcon(ProcessAssociateIconDTO dto);
    ResponseEntity<?> updateIconByProceso(ProcessAssociateIconDTO dto);
}
