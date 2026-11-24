package co.edu.ucc.biblioflow.dto;

import java.time.Instant;

/** Formato único de error para toda la API. */
public record ErrorResponse(int statusCode, String error, String codigo, String message, Instant timestamp) {
}
