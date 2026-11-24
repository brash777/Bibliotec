package co.edu.ucc.biblioflow.dto;

import co.edu.ucc.biblioflow.model.CategoriaLibro;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record LibroRequest(
        @NotBlank(message = "es obligatorio")
        @Pattern(regexp = "^97[89]-?\\d{1,5}-?\\d+-?\\d*-?[\\dX]?$", message = "debe ser un ISBN-13 válido (978-...)")
        String isbn,
        @NotBlank(message = "es obligatorio") @Size(min = 2, message = "mínimo 2 caracteres") String titulo,
        @NotBlank(message = "es obligatorio") @Size(min = 2, message = "mínimo 2 caracteres") String autor,
        @NotNull(message = "es obligatoria (INGENIERIA, CIENCIAS, LITERATURA, HISTORIA, OTRO)") CategoriaLibro categoria,
        @Min(value = 1450, message = "debe ser mayor o igual a 1450") @Max(value = 2100, message = "no es válido") Integer anioPublicacion) {
}
