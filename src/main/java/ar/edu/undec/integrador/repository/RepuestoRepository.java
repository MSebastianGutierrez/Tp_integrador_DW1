package ar.edu.undec.integrador.repository;

import ar.edu.undec.integrador.model.Repuesto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RepuestoRepository extends JpaRepository<Repuesto, Long> {
}
