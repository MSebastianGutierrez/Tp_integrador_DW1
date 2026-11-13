package ar.edu.undec.integrador.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record AgregarRepuestoRequest(
        @NotNull(message = "no debe estar vacío") Long repuestoId,
        @NotNull(message = "no debe estar vacío")
        @Min(value = 1, message = "debe ser mayor a 0")
        Integer cantidad) {
}
