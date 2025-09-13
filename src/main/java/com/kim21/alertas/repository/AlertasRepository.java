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


    @Query("SELECT a FROM AlertasModel a WHERE a.grupoLocal IN :grupos AND a.fechaReconocimiento IS NULL")
    List<AlertasModel> findAllAlertsByGroupUser(@Param("grupos") List<String> grupos);


        @Query("SELECT a FROM AlertasModel a " +
        "WHERE (:proceso IS NULL OR a.proceso = :proceso) " +
        "AND (:activo IS NULL OR a.nombreActivo = :activo) " +
        "AND (:grupos IS NULL OR a.grupoLocal IN :grupos) " +
        "AND a.fechaReconocimiento IS NOT NULL " +
        "AND (:initDate IS NULL OR a.inicioevento >= :initDate) " +
        "AND (:endDate IS NULL OR a.inicioevento <= :endDate)")
        List<AlertasModel> findByProcesoAndGruposAndDateRangeLeidas(
                @Param("proceso") String proceso,
                @Param("activo") String activo,
                @Param("grupos") List<String> grupos,
                @Param("initDate") OffsetDateTime initDate,
                @Param("endDate") OffsetDateTime endDate
        );



        @Query("SELECT a FROM AlertasModel a " +
        "WHERE (:proceso IS NULL OR a.proceso = :proceso) " +
        "AND (:activo IS NULL OR a.nombreActivo = :activo) " +
        "AND (:grupos IS NULL OR a.grupoLocal IN :grupos) " +
        "AND a.fechaReconocimiento IS NULL " +
        "AND (:initDate IS NULL OR a.inicioevento >= :initDate) " +
        "AND (:endDate IS NULL OR a.inicioevento <= :endDate)")
        List<AlertasModel> findByProcesoAndGruposAndDateRange(
                @Param("proceso") String proceso,
                @Param("activo") String activo,
                @Param("grupos") List<String> grupos,
                @Param("initDate") OffsetDateTime initDate,
                @Param("endDate") OffsetDateTime endDate
        );



    @Query("SELECT a FROM AlertasModel a WHERE a.fechaReconocimiento IS NOT NULL")
    List<AlertasModel> findAllReadAlerts();



        // 1. Cantidad de alertas activas
    @Query("SELECT COUNT(a) FROM AlertasModel a WHERE a.fechaReconocimiento IS NULL")
    Long countAlertasActivas();

    // 2. Cantidad de alertas agrupadas por proceso
    @Query("SELECT a.proceso, COUNT(a) FROM AlertasModel a GROUP BY a.proceso")
    List<Object[]> countAlertasPorProceso();

    // 3. Cantidad de alertas agrupadas por servicio
    @Query("SELECT a.tipoServicio, COUNT(a) FROM AlertasModel a GROUP BY a.tipoServicio")
    List<Object[]> countAlertasPorServicio();

    // 4. Cantidad de alertas agrupadas por criticidad (severidad)
    @Query("SELECT a.severidad, COUNT(a) FROM AlertasModel a GROUP BY a.severidad")
    List<Object[]> countAlertasPorCriticidad();
    

}