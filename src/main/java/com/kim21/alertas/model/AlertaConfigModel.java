package com.kim21.alertas.model;

import java.time.OffsetDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "alertas_config")
@Data
public class AlertaConfigModel {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(nullable = false, unique = true, name = "clave")
  private String clave;

  @Column(nullable = false,name = "valor")
  private String valor;

  @Column(name = "actualizado_en")
  private OffsetDateTime actualizadoEn;
}