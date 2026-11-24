package co.edu.ucc.biblioflow.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record PagoMultaRequest(
        @NotNull(message = "es obligatorio") @Positive(message = "debe ser mayor que cero") Integer valor) {
}
