package com.kim21.alertas.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.kim21.alertas.dto.AlertFilterDTO;
import com.kim21.alertas.model.AlertasModel;
import com.kim21.alertas.model.VisibleFieldConfigModel;
import com.kim21.alertas.repository.AlertasRepository;
import com.kim21.alertas.repository.VisibleFieldConfigRepository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AlertasServiceImpl implements AlertasService 
{

    @Autowired
    private AlertasRepository alertasRepository;

    @Autowired
    private VisibleFieldConfigRepository visibleFieldConfigRepository;


    // ALERTAS
    @Override
    public ResponseEntity<?> findAllAlertas() 
    {

        try 
        {
            List<String> gruposCoincidentesParaBuscar =  obtenerGruposCoincidentesConAlertas();

            List<AlertasModel> alertas = alertasRepository.findAllAlertsByGroupUser(gruposCoincidentesParaBuscar);

            // Obtener campos visibles desde la configuración
            List<String> camposVisibles = visibleFieldConfigRepository.findAll()
                .stream()
                .filter(VisibleFieldConfigModel::getVisible) // Solo los que están en true
                .map(VisibleFieldConfigModel::getFieldName)
                .collect(Collectors.toList());

            // Convertimos cada alerta a un Map con solo los campos visibles
            List<Map<String, Object>> resultado = new ArrayList<>();

            List<Map<String, Object>> alertasVisiblesNormales = new ArrayList<>();

            for (AlertasModel alerta : alertas) 
            {

                Map<String, Object> visibleData = new HashMap<>();

                for (String campo : camposVisibles) 
                {
                    try 
                    {

                        // Construye el nombre del getter: "get" + nombreCampo con primera letra mayúscula
                        String getterName = "get" + Character.toUpperCase(campo.charAt(0)) + campo.substring(1);

                        // Obtiene el método de la clase
                        Method getter = AlertasModel.class.getMethod(getterName);

                        // Invoca el getter sobre la instancia actual
                        Object valor = getter.invoke(alerta);

                        // Añade al map el nombre del campo y el valor
                        visibleData.put(campo, valor);

                        //System.out.println(campo + " y el valor: " + valor);

                    } catch (Exception e) 
                    {
                        // Log opcional si un campo no se puede acceder
                        System.err.println("Error accediendo al campo: " + campo + " → " + e.getMessage());
                    }
                }

                alertasVisiblesNormales.add(visibleData);
            }


            //buscar alertas que fecha_reconocimiento no sea null, para obtener las leidas
            List<AlertasModel> alertasLeidas = alertasRepository.findAllReadAlerts();

            Map<String, Object> response = new HashMap<>();
            response.put("alertas", alertasVisiblesNormales); // las visibles
            response.put("alertasLeidas", alertasLeidas);


            resultado.add(response);

            return ResponseEntity.ok(resultado);

        } 
        catch (Exception e) 
        {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Error al obtener alertas filtradas", "details", e.getMessage()));
        }

    }

    @Override
    public ResponseEntity<?> findAlertaById(Integer id) {
        Optional<AlertasModel> alerta = alertasRepository.findById(id);
        return alerta.map(ResponseEntity::ok)
                     .orElse(ResponseEntity.notFound().build());
    }

    @Override
    public List<String> obtenerGruposDesdeCmd() throws IOException 
    {
        List<String> grupos = new ArrayList<>();

        ProcessBuilder pb = new ProcessBuilder("cmd.exe", "/c", "whoami /groups");
        pb.redirectErrorStream(true);
        Process process = pb.start();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) 
        {
            String linea;
            while ((linea = reader.readLine()) != null) 
            {
                if (linea.contains("S-1-")) 
                {
                    String[] partes = linea.trim().split("\\s{2,}");
                    if (partes.length > 0) 
                    {
                        grupos.add(partes[0].trim().toUpperCase());
                    }
                }
            }
        }
        return grupos;
    }



    private String normalizeGroup(String s) 
    {
        if (s == null) return "";
        return s.trim()
                .replace("\\\\", "\\")   // dobles barras → una sola
                .toUpperCase(Locale.ROOT);
    }



    @Override
    public List<String> obtenerGruposCoincidentesConAlertas() throws IOException 
    {
        List<String> gruposUsuario = obtenerGruposDesdeCmd();
        if (gruposUsuario.isEmpty()) return Collections.emptyList();

        //System.out.println("grupos de CMD " + gruposUsuario);

        List<String> gruposEnBD = alertasRepository.obtenerGruposLocalesUnicos();
        //System.out.println("GRUPOS EN BD DE METODO " + gruposEnBD);

        // Normalizar BD
        Set<String> gruposBDSet = gruposEnBD.stream()
                .map(this::normalizeGroup)
                .collect(Collectors.toSet());

        //System.out.println("GRUPOS EN BD SET " + gruposBDSet);

        // Normalizar usuario antes de comparar
        return gruposUsuario.stream()
                .map(this::normalizeGroup)
                .filter(gruposBDSet::contains)
                .collect(Collectors.toList());
    }

    @Override
    public ResponseEntity<?> getAlertsByProcesoAndGrupoLocalAndInitAndEndDate(String proceso,String activo, OffsetDateTime initDate,OffsetDateTime endDate)
    {
        if (proceso == null || proceso.isBlank())    
        {
            return ResponseEntity.badRequest().body(Map.of("message","El campo 'proceso' es obligatorio"));
        }

        if (initDate == null || endDate == null) 
        {
            return ResponseEntity.badRequest().body(Map.of("message","Debe enviar fechas válidas (initDate y endDate)"));
        }
        if (endDate.isBefore(initDate)) 
        {
            return ResponseEntity.badRequest().body(Map.of("message","La fecha final no puede ser anterior a la inicial"));
        }

        if (activo == null) 
        {
            return ResponseEntity.badRequest().body(Map.of("message","El activo no puede venir vacio ni nulo."));
        }

        try 
        {
            List<String> gruposCoincidentesParaBuscar =  obtenerGruposCoincidentesConAlertas();
            if(gruposCoincidentesParaBuscar.isEmpty())
            {
                return ResponseEntity.status(404).body(Map.of("message","No existen grupos asociados al usuario en las alertas."));
            }   

            //obtener grupos locales que puede tener el usuario
            List<AlertasModel> alertas = alertasRepository.findByProcesoAndGruposAndDateRange(
                proceso,
                activo,
                gruposCoincidentesParaBuscar,
                initDate,
                endDate
            );

            if (alertas.isEmpty()) 
            {
                return ResponseEntity.status(404).body(Map.of(
                        "message", "No se encontraron alertas para el proceso '" + proceso + 
                                "', activo '" + activo + 
                                "' en los grupos del usuario, dentro del rango de fechas especificado."
                ));
            }

            return ResponseEntity.ok(alertas);

        } 
        catch (Exception e)
        {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("message","Ha ocurrido un error interno."));
        }
    }



}