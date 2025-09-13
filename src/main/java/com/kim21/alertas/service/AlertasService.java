package com.kim21.alertas.service;


import java.io.IOException;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;

import com.kim21.alertas.dto.AlertFilterDTO;
import com.kim21.alertas.dto.AlertMarcarLeidaDTO;

public interface AlertasService {

    ResponseEntity<?> findAllAlertas();
    ResponseEntity<?> findAlertaById(Integer id);

    /**
     * Obtiene los nombres de los grupos de Active Directory
     * a los que pertenece el usuario actual ejecutando "whoami /groups".
     * @return Lista de nombres de grupos en mayúsculas.
     */
    List<String> obtenerGruposDesdeCmd() throws IOException;

    /**
     * Obtiene solo los grupos que coinciden con los valores de la columna grupo_local
     * de la tabla alertas.
     * @return Lista de grupos coincidentes en mayúscula.
     */
    List<String> obtenerGruposCoincidentesConAlertas() throws IOException;

    /*
     * 
     * se obtienen alertas filtradas por procesos y grupolocal y rango de fechas de initDate a endDate, 
     */
    ResponseEntity<?> getAlertsByProcesoAndGrupoLocalAndInitAndEndDate(String proceso,String activo,OffsetDateTime initDate,OffsetDateTime endDate);


    ResponseEntity<?> marcarAlertaComoLeida(AlertMarcarLeidaDTO dto);
    ResponseEntity<?> reportAlerts();


}