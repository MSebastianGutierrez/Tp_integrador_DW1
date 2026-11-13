package ar.edu.undec.integrador.repository;

import ar.edu.undec.integrador.model.EstadoOrden;
import ar.edu.undec.integrador.model.Orden;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrdenRepository extends JpaRepository<Orden, Long> {

    List<Orden> findAllByOrderByIdDesc();

    List<Orden> findByEstadoOrderByIdDesc(EstadoOrden estado);

    boolean existsByVehiculoIdAndEstadoNotIn(Long vehiculoId, Collection<EstadoOrden> estados);

    boolean existsByVehiculoId(Long vehiculoId);

    List<Orden> findByVehiculoIdOrderByFechaIngresoDescIdDesc(Long vehiculoId);

    List<Orden> findByEstadoAndFechaEntregadoGreaterThanEqualAndFechaEntregadoLessThan(
            EstadoOrden estado,
            LocalDateTime desde,
            LocalDateTime hasta);
}
