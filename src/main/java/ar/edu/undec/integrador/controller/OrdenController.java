package ar.edu.undec.integrador.controller;

import ar.edu.undec.integrador.dto.AgregarRepuestoRequest;
import ar.edu.undec.integrador.dto.EstadoRequest;
import ar.edu.undec.integrador.dto.FacturacionResponse;
import ar.edu.undec.integrador.dto.OrdenRequest;
import ar.edu.undec.integrador.dto.OrdenResponse;
import ar.edu.undec.integrador.dto.OrdenUpdateRequest;
import ar.edu.undec.integrador.service.OrdenService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ordenes")
public class OrdenController {

    private final OrdenService servicio;

    public OrdenController(OrdenService servicio) {
        this.servicio = servicio;
    }

    @GetMapping
    public ResponseEntity<List<OrdenResponse>> listar(
            @RequestParam(required = false) String estado) {
        return ResponseEntity.ok(servicio.listar(estado));
    }

    @GetMapping("/facturacion")
    public ResponseEntity<FacturacionResponse> facturacion(@RequestParam String mes) {
        return ResponseEntity.ok(servicio.facturacion(mes));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrdenResponse> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(servicio.buscar(id));
    }

    @PostMapping
    public ResponseEntity<OrdenResponse> crear(@Valid @RequestBody OrdenRequest request) {
        OrdenResponse creada = servicio.crear(request);
        return ResponseEntity.created(URI.create("/api/ordenes/" + creada.id())).body(creada);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrdenResponse> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody OrdenUpdateRequest request) {
        return ResponseEntity.ok(servicio.actualizar(id, request));
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<OrdenResponse> cambiarEstado(
            @PathVariable Long id,
            @Valid @RequestBody EstadoRequest request) {
        return ResponseEntity.ok(servicio.cambiarEstado(id, request.estado()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        servicio.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/repuestos")
    public ResponseEntity<OrdenResponse> agregarRepuesto(
            @PathVariable Long id,
            @Valid @RequestBody AgregarRepuestoRequest request) {
        OrdenResponse actualizada = servicio.agregarRepuesto(id, request.repuestoId(), request.cantidad());
        return ResponseEntity.created(URI.create("/api/ordenes/" + id)).body(actualizada);
    }
}
