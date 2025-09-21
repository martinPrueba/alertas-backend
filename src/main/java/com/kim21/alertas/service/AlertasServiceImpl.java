package com.kim21.alertas.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.kim21.alertas.dto.AlertMarcarLeidaDTO;
import com.kim21.alertas.model.AlertasModel;
import com.kim21.alertas.model.ProcessAssociateIconModel;
import com.kim21.alertas.model.VisibleFieldConfigModel;
import com.kim21.alertas.repository.AlertasRepository;
import com.kim21.alertas.repository.ProcessAssociateIconRepository;
import com.kim21.alertas.repository.VisibleFieldConfigRepository;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
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

    private static final Map<String, String> COLUMN_TO_FIELD = Map.of(
    "fecha_reconocimiento", "fechaReconocimiento",
    "tiempo_reconocimiento", "tiempoReconocimiento",
    "grupo_local", "grupoLocal"
    );

    @Autowired
    private ProcessAssociateIconRepository processAssociateIconRepository;

    // 👉 Define el formato
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

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

                        String fieldName = COLUMN_TO_FIELD.getOrDefault(campo, campo);
                        String getterName = "get" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);


                        // Obtiene el método de la clase
                        Method getter = AlertasModel.class.getMethod(getterName);

                        // Invoca el getter sobre la instancia actual
                        Object valor = getter.invoke(alerta);

                        System.out.println("CAMPO: " + campo + "  VALOR: " + valor );
                        // Añade al map el nombre del campo y el valor
                        visibleData.put(campo, valor);

                        //System.out.println(campo + " y el valor: " + valor);

                    } catch (Exception e) 
                    {
                        // Log opcional si un campo no se puede acceder
                        System.err.println("Error accediendo al campo: " + campo + " → " + e.getMessage());
                    }
                }


                //agregamos campo de iconAssocieteFromProceso en ProcessAssociateIconModel
                Optional<ProcessAssociateIconModel> findIconUrl = processAssociateIconRepository.findByProceso(alerta.getProceso());
                if(!findIconUrl.isPresent())
                {
                    //alerta.setIconAssocieteFromProceso("No existe un icono asociado al proceso.");
                    visibleData.put("IconAssocieteFromProceso", "No existe un icono asociado al proceso.");
                }
                else
                {
                    //buscar la url asociada al proceso en 
                    //alerta.setIconAssocieteFromProceso(findIconUrl.get().getIconUrl());

                    visibleData.put("IconAssocieteFromProceso", findIconUrl.get().getIconUrl());
                }    
            
                

                alertasVisiblesNormales.add(visibleData);
            }


            Map<String, Object> response = new HashMap<>();
            response.put("alertas", alertasVisiblesNormales); // las visibles
            response.put("alertasLeidas", getAlertasLeidas());

            resultado.add(response);

            return ResponseEntity.ok(resultado);

        } 
        catch (Exception e) 
        {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Error al obtener alertas filtradas", "details", e.getMessage()));
        }

    }

    @Override
    public ResponseEntity<?> findAlertaById(Integer id) 
    {
        try 
        {
            List<AlertasModel> alertas = alertasRepository.findAllByAlertaid(id);

            List<String> gruposCoincidentesParaBuscar =  obtenerGruposCoincidentesConAlertas();

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

                        String fieldName = COLUMN_TO_FIELD.getOrDefault(campo, campo);
                        String getterName = "get" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);


                        // Obtiene el método de la clase
                        Method getter = AlertasModel.class.getMethod(getterName);

                        // Invoca el getter sobre la instancia actual
                        Object valor = getter.invoke(alerta);

                        // 👀 Sanitizar fecha_reconocimiento
                        if ("fecha_reconocimiento".equalsIgnoreCase(campo) && valor != null) 
                        {
                            OffsetDateTime fecha = (OffsetDateTime) valor;
                            valor = fecha.format(FORMATTER);
                        }

                        // 👀 Sanitizar fecha_reconocimiento
                        if ("tiempo_reconocimiento".equalsIgnoreCase(campo) && valor != null) 
                        {
                            
                            valor = valor + " Minutos";
                        }

                        // Añade al map el nombre del campo y el valor
                        visibleData.put(campo, valor);

                        //System.out.println(campo + " y el valor: " + valor);

                    } catch (Exception e) 
                    {
                        // Log opcional si un campo no se puede acceder
                        System.err.println("Error accediendo al campo: " + campo + " → " + e.getMessage());
                    }
                }


                //agregamos campo de iconAssocieteFromProceso en ProcessAssociateIconModel
                Optional<ProcessAssociateIconModel> findIconUrl = processAssociateIconRepository.findByProceso(alerta.getProceso());
                if(!findIconUrl.isPresent())
                {
                    //alerta.setIconAssocieteFromProceso("No existe un icono asociado al proceso.");
                    visibleData.put("IconAssocieteFromProceso", "No existe un icono asociado al proceso.");
                }
                else
                {
                    //buscar la url asociada al proceso en 
                    //alerta.setIconAssocieteFromProceso(findIconUrl.get().getIconUrl());

                    visibleData.put("IconAssocieteFromProceso", findIconUrl.get().getIconUrl());
                }    
            
                

                alertasVisiblesNormales.add(visibleData);
            }


            Map<String, Object> response = new HashMap<>();
            response.put("alertas", alertasVisiblesNormales); // las visibles
            resultado.add(response);

            return ResponseEntity.ok(resultado);

        } 
        catch (Exception e) 
        {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Error al obtener alertas filtradas", "details", e.getMessage()));
        }
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

    // 1) Ambas fechas son obligatorias
    if (initDate == null || endDate == null) 
    {
        return ResponseEntity
            .badRequest()
            .body(Map.of("message", "Debes enviar ambas fechas: fecha inicio y fecha fin."));
    }

    // 2) Coherencia de rango
    if (endDate.isBefore(initDate)) 
    {
        return ResponseEntity
            .badRequest()
            .body(Map.of("message", "La fecha fin no puede ser anterior a la inicial."));
    }


        try 
        {
            List<String> gruposCoincidentesParaBuscar =  obtenerGruposCoincidentesConAlertas();
            if(gruposCoincidentesParaBuscar.isEmpty())
            {
                return ResponseEntity.status(404).body(Map.of("message","No existen grupos asociados al usuario en las alertas."));
            }   

            List<Map<String, Object>> alertasLeidas = getAlertasLeidasByFilter(proceso,
                activo,
                gruposCoincidentesParaBuscar,
                initDate,
                endDate
            );


            //crear map para almacenar alertas y alertasLeidas
            Map<String,Object> mapGeneral = new HashMap<>();

            mapGeneral.put("alertasLeidas", alertasLeidas);

            //obtener grupos locales que puede tener el usuario
            List<Map<String, Object>> alertas = getAlertasByFilter(
                proceso,
                activo,
                gruposCoincidentesParaBuscar,
                initDate,
                endDate
            );


            
            //logica para filtrar alertas no leidas
            mapGeneral.put("alertas", alertas);

            return ResponseEntity.ok(mapGeneral);

        } 
        catch (Exception e)
        {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("message","Ha ocurrido un error interno."));
        }
    }

    @Override
    public ResponseEntity<?> marcarAlertaComoLeida(AlertMarcarLeidaDTO dto) 
    {

        // 1. Validar que el ID de la alerta no sea nulo
        if (dto.getIdAlerta() == null || dto.getIdAlerta() <= 0) 
        {
            return ResponseEntity.badRequest().body(Map.of("message", "El campo 'alertaid' es obligatorio y debe ser mayor a 0"));
        }
        
        try 
        {

            // 1: verificar que existe la alerta con el id del dto
            Optional<AlertasModel> alertaOpt = alertasRepository.findById(dto.getIdAlerta());
            if (alertaOpt.isEmpty()) 
            {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "No existe una alerta con el ID especificado."));
            }

            AlertasModel alerta = alertaOpt.get();

            // 3. Verificar si ya fue marcada como leída
            if (alerta.getFechaReconocimiento() != null) 
            { 
                return ResponseEntity.status(HttpStatus.CONFLICT).body( Map.of("message", "La alerta ya fue marcada como leída anteriormente"));
            }

            // 2: obtener el usuario del sistema
            String usuario = System.getProperty("user.name");
            if (usuario == null || usuario.isBlank()) 
            {
                usuario = "desconocido";
            }

            // 3: verificar que el comentario no exceda 80 caracteres
            if (dto.getComentario() != null && dto.getComentario().length() > 80) 
            {
                return ResponseEntity.badRequest() .body(Map.of("message", "El comentario no puede exceder los 80 caracteres."));
            }

            // 4: calcular tiempo transcurrido entre inicioevento y ahora
            OffsetDateTime fechaReconocimiento = OffsetDateTime.now();
            long tiempoReconocimiento = Duration.between(alerta.getInicioevento(), fechaReconocimiento).toMinutes();

            // 5: hacer el update final de la alerta
            alerta.setUserid(usuario);
            alerta.setComentario(dto.getComentario());
            alerta.setValida(dto.isValida());
            alerta.setFechaReconocimiento(fechaReconocimiento);
            alerta.setTiempoReconocimiento(tiempoReconocimiento);

            alertasRepository.save(alerta);

            return ResponseEntity.ok(Map.of(
                    "message", "La alerta fue marcada como leída correctamente.",
                    "alertaid", alerta.getAlertaid(),
                    "usuario", usuario,
                    "fecha_reconocimiento", fechaReconocimiento,
                    "tiempo_reconocimiento_min", tiempoReconocimiento
            ));

        } 
        catch (Exception e) 
        {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Error al marcar la alerta como leída", "details", e.getMessage()));
        }

    }

    @Override
    public ResponseEntity<?> reportAlerts() 
    {

        try 
        {
            Map<String, Object> report = new HashMap<>();

            // 1. Cantidad de alertas activas (no leídas)
            Long cantidadActivas = alertasRepository.countAlertasActivas();
            report.put("cantidadActivas", cantidadActivas);

            // 2. Cantidad por proceso
            List<Object[]> porProceso = alertasRepository.countAlertasPorProceso();
            Map<String, Long> procesoMap = new HashMap<>();
            for (Object[] row : porProceso) 
            {
                String proceso = (String) row[0];
                if(proceso == null )
                {
                    proceso = "NULO";
                }
                
                Long total = (Long) row[1];
                procesoMap.put(proceso, total);
            }
            report.put("alertasPorProceso", procesoMap);

            // 3. Cantidad por servicio
            List<Object[]> porServicio = alertasRepository.countAlertasPorServicio();
            Map<String, Long> servicioMap = new HashMap<>();
            for (Object[] row : porServicio) 
            {
                String servicio = (String) row[0];
                if(servicio == null )
                {
                    servicio = "NULO";
                }

                Long total = (Long) row[1];
                servicioMap.put(servicio, total);
            }
            report.put("alertasPorServicio", servicioMap);

            // 4. Cantidad por criticidad (severidad)
            List<Object[]> porCriticidad = alertasRepository.countAlertasPorCriticidad();
            Map<String, Long> criticidadMap = new HashMap<>();
            for (Object[] row : porCriticidad) 
            {
                String criticidad = (String) row[0];
                if(criticidad == null )
                {
                    criticidad = "NULO";
                }

                Long total = (Long) row[1];
                criticidadMap.put(criticidad, total);
            }
            
            report.put("alertasPorCriticidad", criticidadMap);

            return ResponseEntity.ok(report);

        } 
        catch (Exception e) 
        {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("message", "Error interno al generar el reporte de alertas", "error", e.getMessage()));
        }
    }




    public List<Map<String, Object>> getAlertasLeidas()
    {

        try 
        {
            List<String> gruposCoincidentesParaBuscar =  obtenerGruposCoincidentesConAlertas();

            List<AlertasModel> alertas = alertasRepository.findAllAlertsByGroupUserLeidas(gruposCoincidentesParaBuscar);
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

                        String fieldName = COLUMN_TO_FIELD.getOrDefault(campo, campo);
                        String getterName = "get" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);


                        // Obtiene el método de la clase
                        Method getter = AlertasModel.class.getMethod(getterName);

                        // Invoca el getter sobre la instancia actual
                        Object valor = getter.invoke(alerta);

                        // 👀 Sanitizar fecha_reconocimiento
                        if ("fecha_reconocimiento".equalsIgnoreCase(campo) && valor != null) 
                        {
                            OffsetDateTime fecha = (OffsetDateTime) valor;
                            valor = fecha.format(FORMATTER);
                        }

                        // Añade al map el nombre del campo y el valor
                        visibleData.put(campo, valor);

                        //System.out.println(campo + " y el valor: " + valor);

                    } catch (Exception e) 
                    {
                        // Log opcional si un campo no se puede acceder
                        System.err.println("Error accediendo al campo: " + campo + " → " + e.getMessage());
                    }
                }



                //agregamos campo de iconAssocieteFromProceso en ProcessAssociateIconModel
                Optional<ProcessAssociateIconModel> findIconUrl = processAssociateIconRepository.findByProceso(alerta.getProceso());
                if(!findIconUrl.isPresent())
                {
                    visibleData.put("IconAssocieteFromProceso", "No existe un icono asociado al proceso.");
                }
                else
                {
                    visibleData.put("IconAssocieteFromProceso", findIconUrl.get().getIconUrl());
                } 



                alertasVisiblesNormales.add(visibleData);
            }

            return alertasVisiblesNormales ;

        } 
        catch (Exception e) 
        {
            // TODO: handle exception
            e.printStackTrace();
            return Collections.emptyList(); 
        }

    }


    public List<Map<String, Object>> getAlertasLeidasByFilter(String proceso, String activo, List<String> grupos, OffsetDateTime initDate,OffsetDateTime endDate)
    {
        try 
        {
            List<String> gruposCoincidentesParaBuscar =  obtenerGruposCoincidentesConAlertas();

            List<AlertasModel> alertas = alertasRepository.findByProcesoAndGruposAndDateRangeLeidas
            (   proceso,
                activo,
                gruposCoincidentesParaBuscar,
                initDate,
                endDate
            );
            
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

                        String fieldName = COLUMN_TO_FIELD.getOrDefault(campo, campo);
                        String getterName = "get" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);


                        // Obtiene el método de la clase
                        Method getter = AlertasModel.class.getMethod(getterName);

                        // Invoca el getter sobre la instancia actual
                        Object valor = getter.invoke(alerta);

                        // 👀 Sanitizar fecha_reconocimiento
                        if ("fecha_reconocimiento".equalsIgnoreCase(campo) && valor != null) 
                        {
                            OffsetDateTime fecha = (OffsetDateTime) valor;
                            valor = fecha.format(FORMATTER);
                        }

                        // Añade al map el nombre del campo y el valor
                        visibleData.put(campo, valor);

                        //System.out.println(campo + " y el valor: " + valor);

                    } catch (Exception e) 
                    {
                        // Log opcional si un campo no se puede acceder
                        System.err.println("Error accediendo al campo: " + campo + " → " + e.getMessage());
                    }
                }



                //agregamos campo de iconAssocieteFromProceso en ProcessAssociateIconModel
                Optional<ProcessAssociateIconModel> findIconUrl = processAssociateIconRepository.findByProceso(alerta.getProceso());
                if(!findIconUrl.isPresent())
                {
                    visibleData.put("IconAssocieteFromProceso", "No existe un icono asociado al proceso.");
                }
                else
                {
                    visibleData.put("IconAssocieteFromProceso", findIconUrl.get().getIconUrl());
                } 



                alertasVisiblesNormales.add(visibleData);
            }

            return alertasVisiblesNormales ;

        } 
        catch (Exception e) 
        {
            // TODO: handle exception
            e.printStackTrace();
            return Collections.emptyList(); 
        }
    }



    public List<Map<String, Object>> getAlertasByFilter(String proceso, String activo, List<String> grupos, OffsetDateTime initDate,OffsetDateTime endDate)
    {
        try 
        {
            List<String> gruposCoincidentesParaBuscar =  obtenerGruposCoincidentesConAlertas();

            List<AlertasModel> alertas = alertasRepository.findByProcesoAndGruposAndDateRange
            (   proceso,
                activo,
                gruposCoincidentesParaBuscar,
                initDate,
                endDate
            );
            
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

                        String fieldName = COLUMN_TO_FIELD.getOrDefault(campo, campo);
                        String getterName = "get" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);


                        // Obtiene el método de la clase
                        Method getter = AlertasModel.class.getMethod(getterName);

                        // Invoca el getter sobre la instancia actual
                        Object valor = getter.invoke(alerta);


                        System.out.println("CAMPO: " + campo + "  VALOR: " + valor );
                        // Añade al map el nombre del campo y el valor
                        visibleData.put(campo, valor);

                        //System.out.println(campo + " y el valor: " + valor);

                    } catch (Exception e) 
                    {
                        // Log opcional si un campo no se puede acceder
                        System.err.println("Error accediendo al campo: " + campo + " → " + e.getMessage());
                    }
                }



                //agregamos campo de iconAssocieteFromProceso en ProcessAssociateIconModel
                Optional<ProcessAssociateIconModel> findIconUrl = processAssociateIconRepository.findByProceso(alerta.getProceso());
                if(!findIconUrl.isPresent())
                {
                    visibleData.put("IconAssocieteFromProceso", "No existe un icono asociado al proceso.");
                }
                else
                {
                    visibleData.put("IconAssocieteFromProceso", findIconUrl.get().getIconUrl());
                } 



                alertasVisiblesNormales.add(visibleData);
            }

            return alertasVisiblesNormales ;

        } 
        catch (Exception e) 
        {
            // TODO: handle exception
            e.printStackTrace();
            return Collections.emptyList(); 
        }
    }

    @Override
    public ResponseEntity<?> getProcesos() 
    {       
        try 
        {
            List<String> gruposCoincidentesParaBuscar =  obtenerGruposCoincidentesConAlertas();
            if(gruposCoincidentesParaBuscar.isEmpty())
            {
                return ResponseEntity.status(404).body(Map.of("message","No existen grupos asociados al usuario."));
            }
            
            List<String> procesos = alertasRepository.findDistinctProcesosByGrupoLocal(gruposCoincidentesParaBuscar);

            if(procesos.isEmpty())
            {
                return ResponseEntity.status(404).body(Map.of("message","No hay procesos asociados al usuario."));
            }

            return ResponseEntity.ok(procesos);
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
            return ResponseEntity.status(404).body(Map.of("error","Ha ocurrido un error interno"));
        }

    }

    @Override
    public ResponseEntity<?> getActivos() 
    {
        try 
        {
            List<String> gruposCoincidentesParaBuscar =  obtenerGruposCoincidentesConAlertas();
            if(gruposCoincidentesParaBuscar.isEmpty())
            {
                return ResponseEntity.status(404).body(Map.of("message","No existen grupos asociados al usuario."));
            }

            List<String> activos = alertasRepository.findAllDistintActivosAndGrupoLocal(gruposCoincidentesParaBuscar);

            if(activos.isEmpty())
            {
                return ResponseEntity.status(404).body(Map.of("message","No hay activos asociados al usuario."));
            }
            return ResponseEntity.ok(activos);
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
            return ResponseEntity.status(404).body(Map.of("error","Ha ocurrido un error interno"));
        }

    }



}