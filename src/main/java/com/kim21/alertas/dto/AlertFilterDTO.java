package com.kim21.alertas.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Data
public class AlertFilterDTO 
{
    private String proceso;
    private OffsetDateTime initDate;
    private OffsetDateTime endDate;
}