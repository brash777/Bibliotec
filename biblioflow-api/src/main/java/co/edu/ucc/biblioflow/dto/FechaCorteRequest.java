package co.edu.ucc.biblioflow.dto;

import java.time.Instant;

/** fechaCorte es opcional: si no se envía se usa la fecha actual. */
public record FechaCorteRequest(Instant fechaCorte) {
}
