package com.kim21.alertas.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Entity
@Table(name = "alertas")
@Data
@AllArgsConstructor
@NoArgsConstructor 
@Builder
@ToString
public class AlertasModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "alertaid")
    private Integer alertaid;

    @Column(name = "codalerta")
    private String codalerta;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "sentenciaId")
    private Integer sentenciaId;

    @Column(name = "inicioevento")
    private OffsetDateTime inicioevento;

    @Column(name = "identificacionalerta")
    private String identificacionalerta;

    @Column(name = "nombreActivo")
    private String nombreActivo;

    @Column(name = "proceso")
    private String proceso;

    @Column(name = "latencia")
    private Double latencia;

    @Column(name = "tipoServicio")
    private String tipoServicio;

    @Column(name = "CI")
    private String CI;

    @Column(name = "Subtiposervicio")
    private String Subtiposervicio;

    @Column(name = "jitter")
    private Double jitter;

    @Column(name = "disponibilidad")
    private Double disponibilidad;

    @Column(name = "packetlost")
    private Double packetlost;

    @Column(name = "rssi")
    private Double rssi;

    @Column(name = "nsr")
    private Double nsr;

    @Column(name = "PLM")
    private String PLM;

    @Column(name = "tipoExWa")
    private String tipoExWa;

    @Column(name = "codigoEvento")
    private String codigoEvento;

    @Column(name = "descripcionevento")
    private String descripcionevento;

    @Column(name = "Origen")
    private String Origen;

    @Column(name = "tipodocumento")
    private String tipodocumento;

    @Column(name = "estado")
    private String estado;

    @Lob
    @Column(name = "resumen")
    private String resumen;

    @Column(name = "titulo")
    private String titulo;

    @Column(name = "numero")
    private String numero;

    @Column(name = "fechaestado")
    private OffsetDateTime fechaestado;

    @Column(name = "razonestado")
    private String razonestado;

    @Column(name = "gpsx")
    private Double gpsx;

    @Column(name = "gpsy")
    private Double gpsy;

    @Column(name = "gpsz")
    private Double gpsz;

    @Column(name = "gpsh")
    private Double gpsh;

    @Column(name = "radio")
    private String radio;

    @Column(name = "severidad")
    private String severidad;

    @Column(name = "userid")
    private String userid;

    @Lob
    @Column(name = "comentario")
    private String comentario;

    @Column(name = "valida")
    private Boolean valida;

    @Column(name = "OT")
    private String OT;

    @Column(name = "ticket")
    private String ticket;

    @Column(name = "fecha_reconocimiento")
    private OffsetDateTime fechaReconocimiento;

    @Column(name = "grupo_local")
    private String grupoLocal;

    @Column(name = "prediccion")
    private String prediccion;
}