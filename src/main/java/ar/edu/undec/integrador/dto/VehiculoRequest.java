package ar.edu.undec.integrador.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record VehiculoRequest(
        @NotBlank(message = "no debe estar vacío")
        @Pattern(regexp = "^(?:[A-Z]{3}\\d{3}|[A-Z]{2}\\d{3}[A-Z]{2})$", message = "debe tener formato ABC123 o AB123CD")
        String patente,
        @NotBlank(message = "no debe estar vacío") String marca,
        @NotBlank(message = "no debe estar vacío") String modelo,
        @NotNull(message = "no debe estar vacío") Long clienteId) {
}
