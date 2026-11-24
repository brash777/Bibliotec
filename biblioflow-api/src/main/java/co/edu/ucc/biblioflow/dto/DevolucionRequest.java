package co.edu.ucc.biblioflow.dto;

import java.time.Instant;

/** fechaDevolucion es opcional: si no se envía se usa la fecha actual. */
public record DevolucionRequest(Instant fechaDevolucion) {
}
