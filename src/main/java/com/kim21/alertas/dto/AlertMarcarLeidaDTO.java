package com.kim21.alertas.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlertMarcarLeidaDTO 
{
    private Integer idAlerta;
    private boolean valida;
    private String comentario;
}