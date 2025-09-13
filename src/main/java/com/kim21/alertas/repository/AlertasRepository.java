package com.kim21.alertas.repository;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.kim21.alertas.model.AlertasModel;

@Repository
public interface AlertasRepository extends JpaRepository<AlertasModel, Integer> 
{
        
    @Query("SELECT DISTINCT a.grupoLocal FROM AlertasModel a WHERE a.grupoLocal IS NOT NULL")
    List<String> obtenerGruposLocalesUnicos();


    @Query("SELECT a FROM AlertasModel a WHERE a.grupoLocal IN :grupos")
    List<AlertasModel> findAllAlertsByGroupUser(@Param("grupos") List<String> grupos);


    @Query("SELECT a FROM AlertasModel a " +
           "WHERE a.proceso = :proceso " +
           "AND a.grupoLocal IN :grupos " +
            "AND a.nombreActivo = :activo " +
            "AND a.fechaReconocimiento IS NOT NULL " +
           "AND a.inicioevento BETWEEN :initDate AND :endDate")
    List<AlertasModel> findByProcesoAndGruposAndDateRange(
            @Param("proceso") String proceso,
            @Param("activo") String activo,
            @Param("grupos") List<String> grupos,
            @Param("initDate") OffsetDateTime initDate,
            @Param("endDate") OffsetDateTime endDate
    );


    @Query("SELECT a FROM AlertasModel a WHERE a.fechaReconocimiento IS NOT NULL")
    List<AlertasModel> findAllReadAlerts();

}