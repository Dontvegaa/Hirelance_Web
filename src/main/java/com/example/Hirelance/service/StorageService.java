package com.example.Hirelance.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class StorageService {

    // Define la carpeta donde se guardarán las imágenes
    // Puedes cambiar esto a una ruta absoluta o configurarla en application.properties
    private final Path rootLocation = Paths.get("uploads");

    public StorageService() {
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new RuntimeException("No se pudo inicializar el almacenamiento de archivos", e);
        }
    }

    /**
     * Guarda un archivo y devuelve su nombre único.
     * @param file El archivo MultipartFile subido.
     * @return El nombre de archivo único generado (o null si el archivo está vacío).
     * @throws IOException Si ocurre un error al guardar.
     */
    public String store(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            return null; // No hay archivo para guardar
        }

        // Generar un nombre de archivo único
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String uniqueFilename = UUID.randomUUID().toString() + extension;

        // Guardar el archivo
        Files.copy(file.getInputStream(), this.rootLocation.resolve(uniqueFilename));

        return uniqueFilename; // Devuelve solo el nombre del archivo
    }
}