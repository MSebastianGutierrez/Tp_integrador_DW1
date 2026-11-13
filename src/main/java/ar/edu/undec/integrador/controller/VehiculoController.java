package ar.edu.undec.integrador.controller;

import ar.edu.undec.integrador.dto.OrdenResponse;
import ar.edu.undec.integrador.dto.VehiculoRequest;
import ar.edu.undec.integrador.dto.VehiculoResponse;
import ar.edu.undec.integrador.service.OrdenService;
import ar.edu.undec.integrador.service.VehiculoService;
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
@RequestMapping("/api/vehiculos")
public class VehiculoController {

    private final VehiculoService servicio;
    private final OrdenService ordenes;

    public VehiculoController(VehiculoService servicio, OrdenService ordenes) {
        this.servicio = servicio;
        this.ordenes = ordenes;
    }

    @GetMapping
    public ResponseEntity<List<VehiculoResponse>> listar() {
        return ResponseEntity.ok(servicio.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<VehiculoResponse> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(servicio.buscar(id));
    }

    @PostMapping
    public ResponseEntity<VehiculoResponse> crear(@Valid @RequestBody VehiculoRequest request) {
        VehiculoResponse creado = servicio.crear(request);
        return ResponseEntity.created(URI.create("/api/vehiculos/" + creado.id())).body(creado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<VehiculoResponse> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody VehiculoRequest request) {
        return ResponseEntity.ok(servicio.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        servicio.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/patente/{patente}/historial")
    public ResponseEntity<List<OrdenResponse>> historial(@PathVariable String patente) {
        return ResponseEntity.ok(ordenes.historialPorPatente(patente));
    }
}
