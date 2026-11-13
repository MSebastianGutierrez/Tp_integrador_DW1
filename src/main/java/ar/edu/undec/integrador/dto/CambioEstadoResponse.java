package ar.edu.undec.integrador.dto;

import ar.edu.undec.integrador.model.CambioEstado;
import ar.edu.undec.integrador.model.EstadoOrden;
import java.time.LocalDateTime;

public record CambioEstadoResponse(EstadoOrden estado, LocalDateTime fechaHora) {

    public static CambioEstadoResponse from(CambioEstado cambio) {
        return new CambioEstadoResponse(cambio.getEstado(), cambio.getFechaHora());
    }
}
