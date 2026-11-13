package ar.edu.undec.integrador.controller;

import ar.edu.undec.integrador.dto.RepuestoRequest;
import ar.edu.undec.integrador.dto.RepuestoResponse;
import ar.edu.undec.integrador.service.RepuestoService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/repuestos")
public class RepuestoController {

    private final RepuestoService servicio;

    public RepuestoController(RepuestoService servicio) {
        this.servicio = servicio;
    }

    @GetMapping
    public ResponseEntity<List<RepuestoResponse>> listar() {
        return ResponseEntity.ok(servicio.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RepuestoResponse> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(servicio.buscar(id));
    }

    @PostMapping
    public ResponseEntity<RepuestoResponse> crear(@Valid @RequestBody RepuestoRequest request) {
        RepuestoResponse creado = servicio.crear(request);
        return ResponseEntity.created(URI.create("/api/repuestos/" + creado.id())).body(creado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RepuestoResponse> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody RepuestoRequest request) {
        return ResponseEntity.ok(servicio.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        servicio.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
