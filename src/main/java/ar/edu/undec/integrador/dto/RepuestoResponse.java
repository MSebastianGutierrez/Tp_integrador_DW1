package ar.edu.undec.integrador.dto;

import ar.edu.undec.integrador.model.Repuesto;
import java.math.BigDecimal;

public record RepuestoResponse(Long id, String nombre, BigDecimal precio, int stock) {

    public static RepuestoResponse from(Repuesto repuesto) {
        return new RepuestoResponse(
                repuesto.getId(),
                repuesto.getNombre(),
                repuesto.getPrecio(),
                repuesto.getStock());
    }
}
