package ar.edu.undec.integrador.dto;

import jakarta.validation.constraints.DecimalMin;
import java.math.BigDecimal;

public record OrdenUpdateRequest(
        String descripcion,
        @DecimalMin(value = "0.0", message = "no puede ser negativo") BigDecimal costoFinal) {
}
