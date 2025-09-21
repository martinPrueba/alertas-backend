package com.kim21.alertas.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kim21.alertas.model.AlertaConfigModel;

public interface AlertaConfigRepository extends JpaRepository<AlertaConfigModel, Integer> {
  Optional<AlertaConfigModel> findByClave(String clave);
}