package ar.edu.undec.integrador.dto;

import java.math.BigDecimal;

public record FacturacionResponse(String mes, long cantidad, BigDecimal totalFacturado) {
}
