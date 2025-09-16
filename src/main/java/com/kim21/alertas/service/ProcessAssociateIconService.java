package com.kim21.alertas.service;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.kim21.alertas.dto.ProcessAssociateIconDTO;
import com.kim21.alertas.model.ProcessAssociateIconModel;

public interface ProcessAssociateIconService 
{
    ResponseEntity<?> getIconByProceso(String proceso);
    ResponseEntity<?> createProcessIcon(ProcessAssociateIconDTO dto);
    ResponseEntity<?> updateIconByProceso(ProcessAssociateIconDTO dto);
    List<ProcessAssociateIconModel> getAllProcesos();
}
