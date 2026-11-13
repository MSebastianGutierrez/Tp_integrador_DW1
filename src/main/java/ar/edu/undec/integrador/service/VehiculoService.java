package ar.edu.undec.integrador.service;

import ar.edu.undec.integrador.dto.VehiculoRequest;
import ar.edu.undec.integrador.dto.VehiculoResponse;
import ar.edu.undec.integrador.exception.RecursoNoEncontradoException;
import ar.edu.undec.integrador.exception.ReglaDeNegocioException;
import ar.edu.undec.integrador.model.Cliente;
import ar.edu.undec.integrador.model.Vehiculo;
import ar.edu.undec.integrador.repository.ClienteRepository;
import ar.edu.undec.integrador.repository.OrdenRepository;
import ar.edu.undec.integrador.repository.VehiculoRepository;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class VehiculoService {

    private final VehiculoRepository vehiculos;
    private final ClienteRepository clientes;
    private final OrdenRepository ordenes;

    public VehiculoService(
            VehiculoRepository vehiculos,
            ClienteRepository clientes,
            OrdenRepository ordenes) {
        this.vehiculos = vehiculos;
        this.clientes = clientes;
        this.ordenes = ordenes;
    }

    @Transactional(readOnly = true)
    public List<VehiculoResponse> listar() {
        return vehiculos.findAll(Sort.by(Sort.Direction.ASC, "id")).stream()
                .map(VehiculoResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public VehiculoResponse buscar(Long id) {
        return VehiculoResponse.from(obtener(id));
    }

    @Transactional
    public VehiculoResponse crear(VehiculoRequest request) {
        String patente = request.patente().trim();
        Cliente cliente = obtenerCliente(request.clienteId());
        if (vehiculos.existsByPatente(patente)) {
            throw new ReglaDeNegocioException("Ya existe un vehículo con la patente " + patente);
        }
        Vehiculo vehiculo = new Vehiculo(patente, request.marca().trim(), request.modelo().trim(), cliente);
        return VehiculoResponse.from(vehiculos.save(vehiculo));
    }

    @Transactional
    public VehiculoResponse actualizar(Long id, VehiculoRequest request) {
        Vehiculo vehiculo = obtener(id);
        String patente = request.patente().trim();
        if (!vehiculo.getPatente().equals(patente) && vehiculos.existsByPatenteAndIdNot(patente, id)) {
            throw new ReglaDeNegocioException("Ya existe un vehículo con la patente " + patente);
        }
        vehiculo.setPatente(patente);
        vehiculo.setMarca(request.marca().trim());
        vehiculo.setModelo(request.modelo().trim());
        vehiculo.setCliente(obtenerCliente(request.clienteId()));
        return VehiculoResponse.from(vehiculos.save(vehiculo));
    }

    @Transactional
    public void eliminar(Long id) {
        Vehiculo vehiculo = obtener(id);
        if (ordenes.existsByVehiculoId(id)) {
            throw new ReglaDeNegocioException("No se puede eliminar un vehículo que tiene órdenes");
        }
        vehiculos.delete(vehiculo);
    }

    private Vehiculo obtener(Long id) {
        return vehiculos.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el vehículo con id " + id));
    }

    private Cliente obtenerCliente(Long id) {
        return clientes.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el cliente con id " + id));
    }
}
