package ar.edu.undec.integrador.dto;

import jakarta.validation.constraints.NotBlank;

public record EstadoRequest(@NotBlank(message = "no debe estar vacío") String estado) {
}
