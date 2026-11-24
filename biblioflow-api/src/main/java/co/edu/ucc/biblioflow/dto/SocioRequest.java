package co.edu.ucc.biblioflow.dto;

import co.edu.ucc.biblioflow.model.TipoSocio;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record SocioRequest(
        @NotBlank(message = "es obligatorio") @Pattern(regexp = "^\\d{6,12}$", message = "debe tener de 6 a 12 dígitos") String documento,
        @NotBlank(message = "es obligatorio") @Size(min = 3, message = "mínimo 3 caracteres") String nombre,
        @NotBlank(message = "es obligatorio") @Email(message = "debe tener formato válido") String correo,
        @NotNull(message = "es obligatorio (ESTUDIANTE o DOCENTE)") TipoSocio tipo) {
}
