package co.edu.ucc.biblioflow.dto;

import jakarta.validation.constraints.NotBlank;
import java.time.Instant;

/** fechaPrestamo es opcional: si no se envía se usa la fecha actual. */
public record PrestamoRequest(
        @NotBlank(message = "es obligatorio") String socioId,
        @NotBlank(message = "es obligatorio") String libroId,
        Instant fechaPrestamo) {
}
