package ar.utn.ba.ddsi.dinamica.controller;

import ar.utn.ba.ddsi.dinamica.service.MultimediaStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/multimedia")
@CrossOrigin(origins = "${frontend.url}")
@RequiredArgsConstructor
@Slf4j
public class MultimediaController {

    private final MultimediaStorageService storageService;

    @GetMapping("/{nombreArchivo:.+}")
    public ResponseEntity<Resource> obtenerArchivo(@PathVariable String nombreArchivo) {
        log.info("[MultimediaController] - GET - /api/multimedia/{} - nombreArchivo: {}", nombreArchivo, nombreArchivo);
        try {
            Resource archivo = storageService.cargarArchivo(nombreArchivo);
            String contentType = determinarContentType(nombreArchivo);
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + nombreArchivo + "\"")
                    .body(archivo);
        } catch (IllegalArgumentException | SecurityException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    private String determinarContentType(String nombreArchivo) {
        String extension = "";
        if (nombreArchivo.contains(".")) {
            extension = nombreArchivo.substring(nombreArchivo.lastIndexOf(".")).toLowerCase();
        }
        return switch (extension) {
            case ".jpg", ".jpeg" -> "image/jpeg";
            case ".png" -> "image/png";
            case ".gif" -> "image/gif";
            case ".webp" -> "image/webp";
            case ".mp4" -> "video/mp4";
            case ".webm" -> "video/webm";
            case ".mp3" -> "audio/mpeg";
            case ".wav" -> "audio/wav";
            case ".ogg" -> "audio/ogg";
            default -> "application/octet-stream";
        };
    }
}

