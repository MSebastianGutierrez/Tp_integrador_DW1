package ar.edu.undec.integrador.dto;

import ar.edu.undec.integrador.model.OrdenRepuesto;
import java.math.BigDecimal;

public record RepuestoLineaResponse(
        Long id,
        Long repuestoId,
        String nombre,
        int cantidad,
        BigDecimal precioUnitario,
        BigDecimal subtotal) {

    public static RepuestoLineaResponse from(OrdenRepuesto linea) {
        return new RepuestoLineaResponse(
                linea.getId(),
                linea.getRepuesto().getId(),
                linea.getRepuesto().getNombre(),
                linea.getCantidad(),
                linea.getPrecioUnitario(),
                linea.subtotal());
    }
}
