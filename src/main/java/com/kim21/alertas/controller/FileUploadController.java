package com.kim21.alertas.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/files")
public class FileUploadController 
{

    @Value("${media.location}")
    private String mediaLocation;

    @PostMapping("/icon")
    public ResponseEntity<?> uploadIcon(@RequestParam("file") MultipartFile file,HttpServletRequest request) 
    {
        try 
        {
            // Crear carpeta si no existe
            Path uploadPath = Paths.get(mediaLocation);
            if (!Files.exists(uploadPath)) 
            {
                Files.createDirectories(uploadPath);
            }

            // Nombre único para el archivo
            String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path destination = uploadPath.resolve(filename);

            // Guardar archivo en disco
            Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);

            // Construir URL pública usando ServletUriComponentsBuilder
            String url = ServletUriComponentsBuilder
                    .fromRequestUri(request) // obtiene http://localhost:8080/api/files/icon
                    .replacePath(null)       // limpia la ruta para dejar solo http://localhost:8080
                    .path("/media/")         // tu endpoint público para recursos estáticos
                    .path(filename)          // el archivo subido
                    .toUriString();

            return ResponseEntity.ok(Map.of(
                    "message", "Archivo subido correctamente",
                    "fileUrl", url
            ));
        } catch (IOException e) 
        {
            return ResponseEntity.status(500).body(Map.of("error","No se pudo guardar el archivo"));
        }
    }

}