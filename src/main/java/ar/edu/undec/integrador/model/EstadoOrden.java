package ar.edu.undec.integrador.model;

public enum EstadoOrden {
    INGRESADO,
    EN_REPARACION,
    TERMINADO,
    ENTREGADO,
    CANCELADO;

    public boolean puedeTransicionarA(EstadoOrden destino) {
        return switch (this) {
            case INGRESADO -> destino == EN_REPARACION || destino == CANCELADO;
            case EN_REPARACION -> destino == TERMINADO;
            case TERMINADO -> destino == ENTREGADO;
            case ENTREGADO, CANCELADO -> false;
        };
    }
}
