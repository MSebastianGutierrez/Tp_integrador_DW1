package ar.edu.undec.integrador.dto;

import ar.edu.undec.integrador.model.EstadoOrden;
import ar.edu.undec.integrador.model.Orden;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public record OrdenResponse(
        Long id,
        Long vehiculoId,
        String patente,
        String descripcion,
        EstadoOrden estado,
        BigDecimal costoFinal,
        BigDecimal costoRepuestos,
        LocalDateTime fechaIngreso,
        LocalDateTime fechaEnReparacion,
        LocalDateTime fechaTerminado,
        LocalDateTime fechaEntregado,
        LocalDateTime fechaCancelado,
        Double horasEnReparacion,
        List<RepuestoLineaResponse> repuestos,
        List<CambioEstadoResponse> cambiosEstado) {

    public static OrdenResponse from(Orden orden) {
        Double horas = null;
        if (orden.getFechaEnReparacion() != null && orden.getFechaTerminado() != null) {
            long segundos = Duration.between(orden.getFechaEnReparacion(), orden.getFechaTerminado()).getSeconds();
            horas = Math.round((segundos / 3600.0) * 100.0) / 100.0;
        }
        return new OrdenResponse(
                orden.getId(),
                orden.getVehiculo().getId(),
                orden.getVehiculo().getPatente(),
                orden.getDescripcion(),
                orden.getEstado(),
                orden.getCostoFinal(),
                orden.costoRepuestos(),
                orden.getFechaIngreso(),
                orden.getFechaEnReparacion(),
                orden.getFechaTerminado(),
                orden.getFechaEntregado(),
                orden.getFechaCancelado(),
                horas,
                orden.getRepuestos().stream().map(RepuestoLineaResponse::from).toList(),
                orden.getCambiosEstado().stream().map(CambioEstadoResponse::from).toList());
    }
}
