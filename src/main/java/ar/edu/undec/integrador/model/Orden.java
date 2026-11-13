package ar.edu.undec.integrador.model;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ordenes")
public class Orden {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "vehiculo_id", nullable = false)
    private Vehiculo vehiculo;

    @Column(nullable = false, length = 500)
    private String descripcion;

    @Column(name = "costo_final", precision = 14, scale = 2)
    private BigDecimal costoFinal;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoOrden estado;

    @Column(name = "fecha_ingreso", nullable = false)
    private LocalDateTime fechaIngreso;

    @Column(name = "fecha_en_reparacion")
    private LocalDateTime fechaEnReparacion;

    @Column(name = "fecha_terminado")
    private LocalDateTime fechaTerminado;

    @Column(name = "fecha_entregado")
    private LocalDateTime fechaEntregado;

    @Column(name = "fecha_cancelado")
    private LocalDateTime fechaCancelado;

    @OneToMany(mappedBy = "orden", cascade = jakarta.persistence.CascadeType.ALL, orphanRemoval = true)
    private List<OrdenRepuesto> repuestos = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "orden_cambios", joinColumns = @JoinColumn(name = "orden_id"))
    @OrderBy("fechaHora ASC")
    private List<CambioEstado> cambiosEstado = new ArrayList<>();

    protected Orden() {
    }

    public Orden(Vehiculo vehiculo, String descripcion, LocalDateTime fechaIngreso) {
        this.vehiculo = vehiculo;
        this.descripcion = descripcion;
        this.estado = EstadoOrden.INGRESADO;
        this.fechaIngreso = fechaIngreso;
    }

    public void registrarCambio(EstadoOrden estado, LocalDateTime fechaHora) {
        cambiosEstado.add(new CambioEstado(estado, fechaHora));
    }

    public void agregarRepuesto(OrdenRepuesto repuesto) {
        repuesto.setOrden(this);
        repuestos.add(repuesto);
    }

    public BigDecimal costoRepuestos() {
        return repuestos.stream()
                .map(OrdenRepuesto::subtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Long getId() {
        return id;
    }

    public Vehiculo getVehiculo() {
        return vehiculo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public BigDecimal getCostoFinal() {
        return costoFinal;
    }

    public void setCostoFinal(BigDecimal costoFinal) {
        this.costoFinal = costoFinal;
    }

    public EstadoOrden getEstado() {
        return estado;
    }

    public void setEstado(EstadoOrden estado) {
        this.estado = estado;
    }

    public LocalDateTime getFechaIngreso() {
        return fechaIngreso;
    }

    public LocalDateTime getFechaEnReparacion() {
        return fechaEnReparacion;
    }

    public void setFechaEnReparacion(LocalDateTime fechaEnReparacion) {
        this.fechaEnReparacion = fechaEnReparacion;
    }

    public LocalDateTime getFechaTerminado() {
        return fechaTerminado;
    }

    public void setFechaTerminado(LocalDateTime fechaTerminado) {
        this.fechaTerminado = fechaTerminado;
    }

    public LocalDateTime getFechaEntregado() {
        return fechaEntregado;
    }

    public void setFechaEntregado(LocalDateTime fechaEntregado) {
        this.fechaEntregado = fechaEntregado;
    }

    public LocalDateTime getFechaCancelado() {
        return fechaCancelado;
    }

    public void setFechaCancelado(LocalDateTime fechaCancelado) {
        this.fechaCancelado = fechaCancelado;
    }

    public List<OrdenRepuesto> getRepuestos() {
        return repuestos;
    }

    public List<CambioEstado> getCambiosEstado() {
        return cambiosEstado;
    }
}
