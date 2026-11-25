package com.uptc.denticare.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 1. Validaciones de negocio o datos inválidos -> 400 Bad Request
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> manejarArgumentoInvalido(IllegalArgumentException ex) {
        return construirRespuesta(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    // 2. JSON mal formado o tipos de parámetro incorrectos -> 400 Bad Request
    @ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class})
    public ResponseEntity<Object> manejarErrorFormato(Exception ex) {
        return construirRespuesta(HttpStatus.BAD_REQUEST, "Formato de petición o parámetro inválido.");
    }

    // 3. Verbo HTTP equivocado -> 405 Method Not Allowed
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Object> manejarMetodoNoSoportado(HttpRequestMethodNotSupportedException ex) {
        return construirRespuesta(HttpStatus.METHOD_NOT_ALLOWED, "Método HTTP no soportado para esta ruta.");
    }

    // 4. Excepciones de negocio en tus Services -> 404 Not Found o 409 Conflict
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Object> manejarRuntimeException(RuntimeException ex) {
        String mensaje = ex.getMessage() != null ? ex.getMessage() : "Error inesperado en la operación.";
        String lower = mensaje.toLowerCase();

        if (lower.contains("no existe") || lower.contains("no encontrad")) {
            return construirRespuesta(HttpStatus.NOT_FOUND, mensaje);
        }
        if (lower.contains("ya está") || lower.contains("ya fue") || lower.contains("anulada") || lower.contains("pagada")) {
            return construirRespuesta(HttpStatus.CONFLICT, mensaje);
        }

        return construirRespuesta(HttpStatus.BAD_REQUEST, mensaje);
    }

    // 5. Fallback general -> 500 Internal Server Error
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> manejarErrorGeneral(Exception ex) {
        return construirRespuesta(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno del servidor: " + ex.getMessage());
    }

    private ResponseEntity<Object> construirRespuesta(HttpStatus status, String mensaje) {
        LinkedHashMap<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("mensaje", mensaje);
        return ResponseEntity.status(status).body(body);
    }
}