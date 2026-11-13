# TP Integrador DAW1 - Taller mecánico

**Alumno/a:** Gutiérrez, Sebastián · **Legajo:** 510127 ·
**Enunciado elegido:** taller

## Descripción

API REST para un taller mecánico que registra clientes, vehículos y órdenes de reparación. Cada orden avanza por estados controlados y permite cargar repuestos, costo final y datos de facturación.

## Cómo ejecutarlo

Requisitos: Java 21. Maven se ejecuta mediante el wrapper incluido.

```bash
./mvnw spring-boot:run
```

En Windows PowerShell:

```powershell
.\mvnw.cmd spring-boot:run
```

La API queda disponible en `http://localhost:8080/api`. La base H2 se guarda en `data/` y no se sube al repositorio. La consola H2 está en `http://localhost:8080/h2-console`.

## Modelo

| Entidad | Atributos principales | Relaciones |
|---|---|---|
| Cliente | id, nombre, telefono | 1 a N con Vehiculo |
| Vehiculo | id, patente, marca, modelo | N a 1 con Cliente; 1 a N con Orden |
| Orden | id, descripcion, estado, costoFinal, fechas de cambio | N a 1 con Vehiculo; 1 a N con OrdenRepuesto |
| Repuesto | id, nombre, precio, stock | 1 a N con OrdenRepuesto |
| OrdenRepuesto | id, cantidad, precioUnitario | N a 1 con Orden y Repuesto |

Estados de la orden: `INGRESADO → EN_REPARACION | CANCELADO`, `EN_REPARACION → TERMINADO`, `TERMINADO → ENTREGADO`. `ENTREGADO` y `CANCELADO` son finales.

## Endpoints

| Método | Ruta | Descripción | Respuestas |
|---|---|---|---|
| GET | /api/clientes | Lista clientes | 200 |
| GET | /api/clientes/{id} | Busca un cliente | 200, 404 |
| POST | /api/clientes | Crea un cliente | 201, 400 |
| PUT | /api/clientes/{id} | Modifica un cliente | 200, 400, 404 |
| DELETE | /api/clientes/{id} | Elimina un cliente sin vehículos | 204, 404, 409 |
| GET | /api/clientes/{id}/vehiculos | Vehículos del cliente | 200, 404 |
| GET | /api/vehiculos | Lista vehículos | 200 |
| GET | /api/vehiculos/{id} | Busca un vehículo | 200, 404 |
| POST | /api/vehiculos | Crea un vehículo | 201, 400, 404, 409 |
| PUT | /api/vehiculos/{id} | Modifica un vehículo | 200, 400, 404, 409 |
| DELETE | /api/vehiculos/{id} | Elimina un vehículo sin órdenes | 204, 404, 409 |
| GET | /api/vehiculos/patente/{patente}/historial | Historial del vehículo | 200, 404 |
| GET | /api/ordenes | Lista órdenes; acepta estado | 200, 400 |
| GET | /api/ordenes/{id} | Busca una orden | 200, 404 |
| POST | /api/ordenes | Crea una orden en INGRESADO | 201, 400, 404, 409 |
| PUT | /api/ordenes/{id} | Carga descripción y costo final | 200, 400, 404 |
| PATCH | /api/ordenes/{id}/estado | Cambia el estado | 200, 400, 404, 409 |
| DELETE | /api/ordenes/{id} | Elimina una orden | 204, 404 |
| GET | /api/ordenes/facturacion?mes=YYYY-MM | Facturación mensual | 200, 400 |
| POST | /api/ordenes/{id}/repuestos | Agrega un repuesto y descuenta stock | 201, 400, 404, 409 |
| GET | /api/repuestos | Lista repuestos | 200 |
| GET | /api/repuestos/{id} | Busca un repuesto | 200, 404 |
| POST | /api/repuestos | Crea un repuesto | 201, 400 |
| PUT | /api/repuestos/{id} | Modifica un repuesto | 200, 400, 404 |
| DELETE | /api/repuestos/{id} | Elimina un repuesto sin uso | 204, 404, 409 |

Los errores usan `ProblemDetail` con `status`, `title` y `detail`. Las validaciones agregan `errores` con el detalle por campo.

## Reglas de negocio

1. Un vehículo con una orden abierta, es decir, no entregada ni cancelada, no puede tener otra orden: `409`.
2. No puede haber dos vehículos con la misma patente: `409`.
3. No se puede pasar una orden a `ENTREGADO` sin `costoFinal`: `409`.
4. Solo se permiten las transiciones de estado indicadas: `409`.
5. El `costoFinal` no puede ser menor al costo de los repuestos: `400`.

## Extras implementados

- Repuestos con stock y descuento transaccional al agregarlos a una orden.
- `costoRepuestos` calculado y validación del costo final.
- Historial de órdenes por patente, de la más nueva a la más vieja.
- Facturación mensual de órdenes entregadas.
- Fechas y horas de cada cambio de estado, con `horasEnReparacion` al terminar.

## Pruebas

En `pruebas.http` están las peticiones de ejemplo para ejecutar desde IntelliJ. La corrección automática se ejecuta con `.tp/correr.sh` y en GitHub Actions.
