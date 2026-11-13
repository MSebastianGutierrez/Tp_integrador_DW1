package ar.edu.undec.integrador.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.time.LocalDateTime;

@Embeddable
public class CambioEstado {

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    private EstadoOrden estado;

    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHora;

    protected CambioEstado() {
    }

    public CambioEstado(EstadoOrden estado, LocalDateTime fechaHora) {
        this.estado = estado;
        this.fechaHora = fechaHora;
    }

    public EstadoOrden getEstado() {
        return estado;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }
}
