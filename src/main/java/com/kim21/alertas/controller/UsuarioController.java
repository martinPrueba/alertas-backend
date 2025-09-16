package com.kim21.alertas.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class UsuarioController 
{

    @GetMapping("/api/usuario/is-admin")
    public ResponseEntity<?> verificarSiEsAdmin() 
    {
        try 
        {
            // Ejecuta el comando para obtener los grupos
            ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c", "whoami /groups");
            builder.redirectErrorStream(true);
            Process process = builder.start();

            // Captura la salida
            List<String> grupos = new BufferedReader(new InputStreamReader(process.getInputStream()))
                    .lines()
                    .collect(Collectors.toList());

            // Normaliza a mayúsculas para evitar problemas de comparación
            boolean esAdmin = grupos.stream()
                    .anyMatch(linea -> linea.toUpperCase().contains("ADMINISTRADORES") 
                                     || linea.toUpperCase().contains("DOMAIN ADMINS"));

            return ResponseEntity.ok(esAdmin);

        } 
        catch (Exception e) 
        {
            e.printStackTrace();
            return ResponseEntity.status(500).body(false); // en caso de error retornamos false
        }
    }
}