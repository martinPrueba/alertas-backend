package com.kim21.alertas.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "process_associate_icons_from_alerts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProcessAssociateIconModel 
{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "proceso", nullable = false, unique = true)
    private String proceso;

    @Column(name = "icon_url", nullable = false)
    private String iconUrl;
}