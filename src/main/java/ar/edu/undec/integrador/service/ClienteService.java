package ar.edu.undec.integrador.service;

import ar.edu.undec.integrador.dto.ClienteRequest;
import ar.edu.undec.integrador.dto.ClienteResponse;
import ar.edu.undec.integrador.dto.VehiculoResponse;
import ar.edu.undec.integrador.exception.RecursoNoEncontradoException;
import ar.edu.undec.integrador.exception.ReglaDeNegocioException;
import ar.edu.undec.integrador.model.Cliente;
import ar.edu.undec.integrador.repository.ClienteRepository;
import ar.edu.undec.integrador.repository.VehiculoRepository;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ClienteService {

    private final ClienteRepository clientes;
    private final VehiculoRepository vehiculos;

    public ClienteService(ClienteRepository clientes, VehiculoRepository vehiculos) {
        this.clientes = clientes;
        this.vehiculos = vehiculos;
    }

    @Transactional(readOnly = true)
    public List<ClienteResponse> listar() {
        return clientes.findAll(Sort.by(Sort.Direction.ASC, "id")).stream()
                .map(ClienteResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public ClienteResponse buscar(Long id) {
        return ClienteResponse.from(obtener(id));
    }

    @Transactional
    public ClienteResponse crear(ClienteRequest request) {
        Cliente cliente = new Cliente(request.nombre().trim(), request.telefono().trim());
        return ClienteResponse.from(clientes.save(cliente));
    }

    @Transactional
    public ClienteResponse actualizar(Long id, ClienteRequest request) {
        Cliente cliente = obtener(id);
        cliente.setNombre(request.nombre().trim());
        cliente.setTelefono(request.telefono().trim());
        return ClienteResponse.from(clientes.save(cliente));
    }

    @Transactional
    public void eliminar(Long id) {
        Cliente cliente = obtener(id);
        if (vehiculos.existsByClienteId(id)) {
            throw new ReglaDeNegocioException("No se puede eliminar un cliente que tiene vehículos");
        }
        clientes.delete(cliente);
    }

    @Transactional(readOnly = true)
    public List<VehiculoResponse> listarVehiculos(Long clienteId) {
        obtener(clienteId);
        return vehiculos.findByClienteIdOrderByIdAsc(clienteId).stream()
                .map(VehiculoResponse::from)
                .toList();
    }

    private Cliente obtener(Long id) {
        return clientes.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el cliente con id " + id));
    }
}
