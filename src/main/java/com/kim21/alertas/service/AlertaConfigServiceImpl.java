package com.kim21.alertas.service;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.kim21.alertas.model.AlertaConfigModel;
import com.kim21.alertas.repository.AlertaConfigRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AlertaConfigServiceImpl 
{

  private final AlertaConfigRepository repo;

  public ResponseEntity<?> getRefreshSeconds() 
  {
    try 
    {
      int seconds = repo.findByClave("refresh_interval_seconds")
          .map(cfg -> Integer.parseInt(cfg.getValor()))
          .orElse(30); // valor por defecto

      return ResponseEntity.ok(Map.of("seconds", seconds));
    } 
    catch (Exception e) 
    {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(Map.of(
              "error", "No se pudo obtener el intervalo de refresco",
              "details", e.getMessage()
          ));
    }
  }

  public ResponseEntity<?> setRefreshSeconds(Integer seconds) 
  {
    try 
    {
      if (seconds == null) 
      {
        return ResponseEntity.badRequest().body(Map.of("message", "'seconds' es requerido"));
      }
      if (seconds < 5) 
      {
        return ResponseEntity.badRequest().body(Map.of("message", "'seconds' debe ser >= 5"));
      }
      // (Opcional) límite superior de seguridad
      if (seconds > 3600) 
      {
        return ResponseEntity.badRequest().body(Map.of("message", "'seconds' no debe superar 3600"));
      }

      AlertaConfigModel cfg = repo.findByClave("refresh_interval_seconds")
          .orElseGet(() -> {
            AlertaConfigModel c = new AlertaConfigModel();
            c.setClave("refresh_interval_seconds");
            return c;
          });

      cfg.setValor(Integer.toString(seconds));
      repo.save(cfg);

      return ResponseEntity.ok(Map.of(
          "message", "Actualizado",
          "seconds", seconds
      ));
    } 
    catch (Exception e) 
    {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(Map.of(
              "error", "No se pudo actualizar el intervalo de refresco",
              "details", e.getMessage()
          ));
    }
  }
}
