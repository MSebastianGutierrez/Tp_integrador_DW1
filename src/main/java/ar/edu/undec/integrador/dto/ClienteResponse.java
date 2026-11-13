package ar.edu.undec.integrador.dto;

import ar.edu.undec.integrador.model.Cliente;

public record ClienteResponse(Long id, String nombre, String telefono) {

    public static ClienteResponse from(Cliente cliente) {
        return new ClienteResponse(cliente.getId(), cliente.getNombre(), cliente.getTelefono());
    }
}
