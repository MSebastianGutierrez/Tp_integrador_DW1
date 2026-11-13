package ar.edu.undec.integrador.service;

import ar.edu.undec.integrador.dto.FacturacionResponse;
import ar.edu.undec.integrador.dto.OrdenRequest;
import ar.edu.undec.integrador.dto.OrdenResponse;
import ar.edu.undec.integrador.dto.OrdenUpdateRequest;
import ar.edu.undec.integrador.exception.DatosInvalidosException;
import ar.edu.undec.integrador.exception.RecursoNoEncontradoException;
import ar.edu.undec.integrador.exception.ReglaDeNegocioException;
import ar.edu.undec.integrador.model.EstadoOrden;
import ar.edu.undec.integrador.model.Orden;
import ar.edu.undec.integrador.model.OrdenRepuesto;
import ar.edu.undec.integrador.model.Repuesto;
import ar.edu.undec.integrador.model.Vehiculo;
import ar.edu.undec.integrador.repository.OrdenRepository;
import ar.edu.undec.integrador.repository.RepuestoRepository;
import ar.edu.undec.integrador.repository.VehiculoRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrdenService {

    private final OrdenRepository ordenes;
    private final VehiculoRepository vehiculos;
    private final RepuestoRepository repuestos;

    public OrdenService(
            OrdenRepository ordenes,
            VehiculoRepository vehiculos,
            RepuestoRepository repuestos) {
        this.ordenes = ordenes;
        this.vehiculos = vehiculos;
        this.repuestos = repuestos;
    }

    @Transactional(readOnly = true)
    public List<OrdenResponse> listar(String estado) {
        List<Orden> resultado;
        if (estado == null || estado.isBlank()) {
            resultado = ordenes.findAllByOrderByIdDesc();
        } else {
            resultado = ordenes.findByEstadoOrderByIdDesc(parseEstado(estado));
        }
        return resultado.stream().map(OrdenResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public OrdenResponse buscar(Long id) {
        return OrdenResponse.from(obtener(id));
    }

    @Transactional
    public OrdenResponse crear(OrdenRequest request) {
        Vehiculo vehiculo = vehiculos.findById(request.vehiculoId())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe el vehículo con id " + request.vehiculoId()));
        if (ordenes.existsByVehiculoIdAndEstadoNotIn(
                vehiculo.getId(), List.of(EstadoOrden.ENTREGADO, EstadoOrden.CANCELADO))) {
            throw new ReglaDeNegocioException("El vehículo ya tiene una orden abierta");
        }
        LocalDateTime ahora = LocalDateTime.now();
        Orden orden = new Orden(vehiculo, request.descripcion().trim(), ahora);
        orden.registrarCambio(EstadoOrden.INGRESADO, ahora);
        return OrdenResponse.from(ordenes.save(orden));
    }

    @Transactional
    public OrdenResponse actualizar(Long id, OrdenUpdateRequest request) {
        Orden orden = obtener(id);
        if (request.descripcion() != null) {
            if (request.descripcion().isBlank()) {
                throw new DatosInvalidosException(
                        "La descripción no puede estar vacía",
                        Map.of("descripcion", "no debe estar vacío"));
            }
            orden.setDescripcion(request.descripcion().trim());
        }
        if (request.costoFinal() != null) {
            if (request.costoFinal().signum() < 0) {
                throw new DatosInvalidosException(
                        "El costo final no puede ser negativo",
                        Map.of("costoFinal", "no puede ser negativo"));
            }
            BigDecimal costoRepuestos = orden.costoRepuestos();
            if (request.costoFinal().compareTo(costoRepuestos) < 0) {
                throw new DatosInvalidosException(
                        "El costo final no puede ser menor al costo de repuestos",
                        Map.of("costoFinal", "no puede ser menor a costoRepuestos"));
            }
            orden.setCostoFinal(request.costoFinal());
        }
        return OrdenResponse.from(ordenes.save(orden));
    }

    @Transactional
    public OrdenResponse cambiarEstado(Long id, String estadoSolicitado) {
        Orden orden = obtener(id);
        EstadoOrden destino = parseEstado(estadoSolicitado);
        if (destino == orden.getEstado()) {
            throw new ReglaDeNegocioException("La orden ya está en estado " + destino);
        }
        if (!orden.getEstado().puedeTransicionarA(destino)) {
            throw new ReglaDeNegocioException(
                    "No se puede pasar de " + orden.getEstado() + " a " + destino);
        }
        if (destino == EstadoOrden.ENTREGADO) {
            if (orden.getCostoFinal() == null) {
                throw new ReglaDeNegocioException("No se puede entregar una orden sin costo final");
            }
            if (orden.getCostoFinal().compareTo(orden.costoRepuestos()) < 0) {
                throw new DatosInvalidosException(
                        "El costo final no puede ser menor al costo de repuestos",
                        Map.of("costoFinal", "no puede ser menor a costoRepuestos"));
            }
        }
        LocalDateTime ahora = LocalDateTime.now();
        orden.setEstado(destino);
        switch (destino) {
            case EN_REPARACION -> orden.setFechaEnReparacion(ahora);
            case TERMINADO -> orden.setFechaTerminado(ahora);
            case ENTREGADO -> orden.setFechaEntregado(ahora);
            case CANCELADO -> orden.setFechaCancelado(ahora);
            default -> {
            }
        }
        orden.registrarCambio(destino, ahora);
        return OrdenResponse.from(ordenes.save(orden));
    }

    @Transactional
    public OrdenResponse agregarRepuesto(Long ordenId, Long repuestoId, int cantidad) {
        Orden orden = obtener(ordenId);
        if (orden.getEstado() == EstadoOrden.ENTREGADO || orden.getEstado() == EstadoOrden.CANCELADO) {
            throw new ReglaDeNegocioException("No se pueden agregar repuestos a una orden cerrada");
        }
        Repuesto repuesto = repuestos.findById(repuestoId)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe el repuesto con id " + repuestoId));
        if (repuesto.getStock() < cantidad) {
            throw new ReglaDeNegocioException(
                    "Stock insuficiente para el repuesto " + repuesto.getNombre());
        }
        BigDecimal nuevoCosto = orden.costoRepuestos()
                .add(repuesto.getPrecio().multiply(BigDecimal.valueOf(cantidad)));
        if (orden.getCostoFinal() != null && nuevoCosto.compareTo(orden.getCostoFinal()) > 0) {
            throw new DatosInvalidosException(
                    "El costo final no puede ser menor al costo de repuestos",
                    Map.of("costoFinal", "no puede ser menor a costoRepuestos"));
        }
        repuesto.setStock(repuesto.getStock() - cantidad);
        repuestos.save(repuesto);
        orden.agregarRepuesto(new OrdenRepuesto(orden, repuesto, cantidad, repuesto.getPrecio()));
        return OrdenResponse.from(ordenes.save(orden));
    }

    @Transactional
    public void eliminar(Long id) {
        Orden orden = obtener(id);
        ordenes.delete(orden);
    }

    @Transactional(readOnly = true)
    public List<OrdenResponse> historialPorPatente(String patente) {
        Vehiculo vehiculo = vehiculos.findByPatenteIgnoreCase(patente)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe un vehículo con la patente " + patente));
        return ordenes.findByVehiculoIdOrderByFechaIngresoDescIdDesc(vehiculo.getId()).stream()
                .map(OrdenResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public FacturacionResponse facturacion(String mes) {
        YearMonth periodo;
        try {
            periodo = YearMonth.parse(mes);
        } catch (DateTimeParseException ex) {
            throw new DatosInvalidosException(
                    "El mes debe tener formato YYYY-MM",
                    Map.of("mes", "formato inválido"));
        }
        LocalDateTime desde = periodo.atDay(1).atStartOfDay();
        LocalDateTime hasta = periodo.plusMonths(1).atDay(1).atStartOfDay();
        List<Orden> entregadas = ordenes
                .findByEstadoAndFechaEntregadoGreaterThanEqualAndFechaEntregadoLessThan(
                        EstadoOrden.ENTREGADO, desde, hasta);
        BigDecimal total = entregadas.stream()
                .map(Orden::getCostoFinal)
                .filter(costo -> costo != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return new FacturacionResponse(periodo.toString(), entregadas.size(), total);
    }

    private Orden obtener(Long id) {
        return ordenes.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe la orden con id " + id));
    }

    private EstadoOrden parseEstado(String valor) {
        if (valor == null || valor.isBlank()) {
            throw new DatosInvalidosException(
                    "El estado es obligatorio",
                    Map.of("estado", "no debe estar vacío"));
        }
        try {
            return EstadoOrden.valueOf(valor.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            throw new DatosInvalidosException(
                    "Estado inválido: " + valor,
                    Map.of("estado", "valor inválido"));
        }
    }
}
