package ar.edu.undec.integrador.dto;

import ar.edu.undec.integrador.model.Vehiculo;

public record VehiculoResponse(Long id, String patente, String marca, String modelo, Long clienteId) {

    public static VehiculoResponse from(Vehiculo vehiculo) {
        return new VehiculoResponse(
                vehiculo.getId(),
                vehiculo.getPatente(),
                vehiculo.getMarca(),
                vehiculo.getModelo(),
                vehiculo.getCliente().getId());
    }
}
