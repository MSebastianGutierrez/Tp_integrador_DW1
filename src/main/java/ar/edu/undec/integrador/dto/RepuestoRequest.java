package ar.edu.undec.integrador.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record RepuestoRequest(
        @NotBlank(message = "no debe estar vacío") String nombre,
        @NotNull(message = "no debe estar vacío")
        @DecimalMin(value = "0.0", message = "no puede ser negativo")
        BigDecimal precio,
        @NotNull(message = "no debe estar vacío")
        @Min(value = 0, message = "no puede ser negativo")
        Integer stock) {
}
