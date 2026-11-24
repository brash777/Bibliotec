package co.edu.ucc.biblioflow.controller;

import co.edu.ucc.biblioflow.dto.ErrorResponse;
import co.edu.ucc.biblioflow.exception.NegocioException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import java.time.Instant;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/** Traduce las excepciones a HTTP con un formato JSON único. */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NegocioException.class)
    public ResponseEntity<ErrorResponse> negocio(NegocioException ex) {
        return responder(ex.getTipo().getStatus(), ex.getCodigo(), ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> validacion(MethodArgumentNotValidException ex) {
        String mensaje = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .sorted()
                .collect(Collectors.joining("; "));
        return responder(400, "DATOS_INVALIDOS", mensaje);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> jsonInvalido(HttpMessageNotReadableException ex) {
        if (ex.getCause() instanceof InvalidFormatException ife) {
            String campo = ife.getPath().stream()
                    .map(JsonMappingException.Reference::getFieldName)
                    .collect(Collectors.joining("."));
            return responder(400, "DATOS_INVALIDOS", campo + ": valor no válido (" + ife.getValue() + ")");
        }
        return responder(400, "JSON_INVALIDO", "El cuerpo de la petición no es un JSON válido");
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> parametroInvalido(MethodArgumentTypeMismatchException ex) {
        return responder(400, "DATOS_INVALIDOS", ex.getName() + ": valor no válido (" + ex.getValue() + ")");
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> rutaNoEncontrada(NoResourceFoundException ex) {
        return responder(404, "RUTA_NO_ENCONTRADA", "No existe la ruta " + ex.getResourcePath());
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> metodoNoPermitido(HttpRequestMethodNotSupportedException ex) {
        return responder(405, "METODO_NO_PERMITIDO", ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> inesperado(Exception ex) {
        ex.printStackTrace();
        return responder(500, "ERROR_INTERNO", "Ocurrió un error inesperado");
    }

    private ResponseEntity<ErrorResponse> responder(int status, String codigo, String mensaje) {
        String nombre = HttpStatus.valueOf(status).getReasonPhrase();
        return ResponseEntity.status(status).body(new ErrorResponse(status, nombre, codigo, mensaje, Instant.now()));
    }
}
