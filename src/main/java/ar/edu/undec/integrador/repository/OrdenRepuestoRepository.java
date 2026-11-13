package ar.edu.undec.integrador.repository;

import ar.edu.undec.integrador.model.OrdenRepuesto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrdenRepuestoRepository extends JpaRepository<OrdenRepuesto, Long> {

    boolean existsByRepuestoId(Long repuestoId);
}
