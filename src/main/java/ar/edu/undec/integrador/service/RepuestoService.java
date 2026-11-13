package ar.edu.undec.integrador.service;

import ar.edu.undec.integrador.dto.RepuestoRequest;
import ar.edu.undec.integrador.dto.RepuestoResponse;
import ar.edu.undec.integrador.exception.RecursoNoEncontradoException;
import ar.edu.undec.integrador.exception.ReglaDeNegocioException;
import ar.edu.undec.integrador.model.Repuesto;
import ar.edu.undec.integrador.repository.OrdenRepuestoRepository;
import ar.edu.undec.integrador.repository.RepuestoRepository;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RepuestoService {

    private final RepuestoRepository repuestos;
    private final OrdenRepuestoRepository ordenRepuestos;

    public RepuestoService(RepuestoRepository repuestos, OrdenRepuestoRepository ordenRepuestos) {
        this.repuestos = repuestos;
        this.ordenRepuestos = ordenRepuestos;
    }

    @Transactional(readOnly = true)
    public List<RepuestoResponse> listar() {
        return repuestos.findAll(Sort.by(Sort.Direction.ASC, "id")).stream()
                .map(RepuestoResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public RepuestoResponse buscar(Long id) {
        return RepuestoResponse.from(obtener(id));
    }

    @Transactional
    public RepuestoResponse crear(RepuestoRequest request) {
        Repuesto repuesto = new Repuesto(
                request.nombre().trim(),
                request.precio(),
                request.stock());
        return RepuestoResponse.from(repuestos.save(repuesto));
    }

    @Transactional
    public RepuestoResponse actualizar(Long id, RepuestoRequest request) {
        Repuesto repuesto = obtener(id);
        repuesto.setNombre(request.nombre().trim());
        repuesto.setPrecio(request.precio());
        repuesto.setStock(request.stock());
        return RepuestoResponse.from(repuestos.save(repuesto));
    }

    @Transactional
    public void eliminar(Long id) {
        Repuesto repuesto = obtener(id);
        if (ordenRepuestos.existsByRepuestoId(id)) {
            throw new ReglaDeNegocioException("No se puede eliminar un repuesto usado en una orden");
        }
        repuestos.delete(repuesto);
    }

    private Repuesto obtener(Long id) {
        return repuestos.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el repuesto con id " + id));
    }
}
