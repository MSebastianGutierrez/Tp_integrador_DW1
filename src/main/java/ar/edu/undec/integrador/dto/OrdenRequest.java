package ar.edu.undec.integrador.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record OrdenRequest(
        @NotNull(message = "no debe estar vacío") Long vehiculoId,
        @NotBlank(message = "no debe estar vacío") String descripcion) {
}
