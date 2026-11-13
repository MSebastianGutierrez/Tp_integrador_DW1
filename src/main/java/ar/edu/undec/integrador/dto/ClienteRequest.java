package ar.edu.undec.integrador.dto;

import jakarta.validation.constraints.NotBlank;

public record ClienteRequest(
        @NotBlank(message = "no debe estar vacío") String nombre,
        @NotBlank(message = "no debe estar vacío") String telefono) {
}
