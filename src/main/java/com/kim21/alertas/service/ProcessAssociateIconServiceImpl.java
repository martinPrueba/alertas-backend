package com.kim21.alertas.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.kim21.alertas.dto.ProcessAssociateIconDTO;
import com.kim21.alertas.model.AlertasModel;
import com.kim21.alertas.model.ProcessAssociateIconModel;
import com.kim21.alertas.repository.AlertasRepository;
import com.kim21.alertas.repository.ProcessAssociateIconRepository;

@Service
public class ProcessAssociateIconServiceImpl implements ProcessAssociateIconService
{

        
    @Autowired
    private ProcessAssociateIconRepository repository;

    @Autowired
    private AlertasRepository alertasRepository;

    @Override
    public ResponseEntity<?> getIconByProceso(String proceso) 
    {
        try 
        {
            Optional<ProcessAssociateIconModel> result = repository.findByProceso(proceso);

            if (result.isEmpty()) 
            {
                return ResponseEntity.status(404).body(Map.of("error", "No se encontró ícono para el proceso: " + proceso));
            }

            return ResponseEntity.ok(result.get());

        } 
        catch (Exception e) 
        {
            e.printStackTrace();
            // TODO: handle exception
            return ResponseEntity.status(500).body(Map.of("error","Ha ocurrido un error interno."));
        }



    }

    @Override
    public ResponseEntity<?> createProcessIcon(ProcessAssociateIconDTO dto) 
    {


        try 
        {
            // Validación básica
            if (dto.getProceso() == null || dto.getProceso().isBlank() ||
                dto.getIconUrl() == null || dto.getIconUrl().isBlank()) 
            {
                return ResponseEntity.badRequest().body(Map.of("error", "Los campos 'proceso' e 'iconUrl' son obligatorios."));
            }

            // Evitar duplicados por proceso
            if (repository.findByProceso(dto.getProceso()).isPresent()) 
            {
                return ResponseEntity.status(409).body(Map.of("error", "El proceso ya tiene un ícono asociado."));
            }

            // Mapear DTO a entidad
            ProcessAssociateIconModel entity = ProcessAssociateIconModel.builder()
                    .proceso(dto.getProceso())
                    .iconUrl(dto.getIconUrl())
                    .build();

            // Guardar en BD
            ProcessAssociateIconModel saved = repository.save(entity);

            // Convertir de nuevo a DTO para la respuesta
            ProcessAssociateIconDTO responseDto = ProcessAssociateIconDTO.builder()
                    .proceso(saved.getProceso())
                    .iconUrl(saved.getIconUrl())
                    .build();

            return ResponseEntity.ok(responseDto);

        } 
        catch (Exception e) 
        {
            return ResponseEntity.status(500).body(Map.of("error", "Error interno al guardar el ícono", "details", e.getMessage()));
        }

    }

    @Override
    public ResponseEntity<?> updateIconByProceso(ProcessAssociateIconDTO dto) 
    {
        try    
        {
            // Validar campos
            if (dto.getProceso() == null || dto.getProceso().isBlank() ||
                dto.getIconUrl() == null || dto.getIconUrl().isBlank()) 
            {
                return ResponseEntity.badRequest().body(Map.of("error", "Los campos 'proceso' e 'iconUrl' son obligatorios."));
            }

            // Buscar si el proceso existe
            Optional<ProcessAssociateIconModel> existingOpt = repository.findByProceso(dto.getProceso());
            if (existingOpt.isEmpty()) 
            {
                return ResponseEntity.status(404).body(Map.of("error", "No existe un ícono asociado al proceso: " + dto.getProceso()));
            }

            // Actualizar entidad
            ProcessAssociateIconModel existing = existingOpt.get();
            existing.setIconUrl(dto.getIconUrl());

            ProcessAssociateIconModel updated = repository.save(existing);

            // Convertir a DTO para la respuesta
            ProcessAssociateIconDTO responseDto = ProcessAssociateIconDTO.builder()
                    .proceso(updated.getProceso())
                    .iconUrl(updated.getIconUrl())
                    .build();

            return ResponseEntity.ok(responseDto);

        } 
        catch (Exception e) 
        {
            return ResponseEntity.status(500) .body(Map.of("error", "Error interno al actualizar el ícono", "details", e.getMessage()));
        }

    }

    @Override
    public ResponseEntity<?> getAllProcesos() 
    {
        List<ProcessAssociateIconModel> listaProcesos= new ArrayList<>(repository.findAll());
        
        List<AlertasModel> allAlerts = alertasRepository.findAll();

        Map<String,String> returnMap = new HashMap<>();
        

        for (ProcessAssociateIconModel processAsocciete : listaProcesos) 
        {
            if(processAsocciete.getProceso() != null)
            {
                returnMap.put(processAsocciete.getProceso(), processAsocciete.getIconUrl());
            }
        }


        // ahora debemos verificar los procesos que tengamos en alertas que no existan en returnMap para asociarlos

            for (AlertasModel alertasModel : allAlerts) 
            {
                if(alertasModel.getProceso() != null)
                {
                    if(!returnMap.containsKey(alertasModel.getProceso()))
                    {
                        //verificar si se encuentra en la tabla de ProccessAssocieteIcon para agregarlo
                        Optional<ProcessAssociateIconModel> verify = repository.findByProceso(alertasModel.getProceso());
                        if(verify.isEmpty())
                        {
                            ProcessAssociateIconModel entity = ProcessAssociateIconModel.builder()
                                .proceso(alertasModel.getProceso())
                                .iconUrl("")
                                .build();
                            repository.save(entity);
                        }
                        returnMap.put(alertasModel.getProceso(), "null");
                    }
                }
            }
        
        return ResponseEntity.ok(returnMap);
    }
    
}
