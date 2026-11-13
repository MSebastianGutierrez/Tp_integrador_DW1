package ar.edu.undec.integrador.repository;

import ar.edu.undec.integrador.model.Vehiculo;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VehiculoRepository extends JpaRepository<Vehiculo, Long> {

    boolean existsByPatente(String patente);

    boolean existsByPatenteAndIdNot(String patente, Long id);

    boolean existsByClienteId(Long clienteId);

    List<Vehiculo> findByClienteIdOrderByIdAsc(Long clienteId);

    Optional<Vehiculo> findByPatenteIgnoreCase(String patente);
}
