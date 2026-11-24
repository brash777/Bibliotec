# BiblioFlow

**Sistema de Gestión de Préstamos de Biblioteca**, con la biblioteca de la Universidad Cooperativa de Colombia, sede Pasto, como caso de referencia.

El sistema cubre todo el ciclo de un préstamo:
1. El bibliotecario presta un libro a un socio, que puede ser estudiante o docente.
2. El sistema valida las reglas y calcula la fecha límite.
3. Al devolver el libro, el sistema calcula los días de retraso y la multa.
4. El cajero recibe el pago de la multa.

Cada cambio de estado queda registrado en el historial del préstamo.

Proyecto académico de Ingeniería de Software.

## Estructura

```
BiblioFlow/
├── biblioflow-api/     Backend Spring Boot (controller, service, repository, model, dto)
├── postman/            Colección y entorno de Postman
├── diagramas/          Las 5 vistas de arquitectura (Visual Paradigm + PNG de referencia)
└── README.md
```

## Tecnologías

| Componente | Tecnología |
|---|---|
| API REST | Java 17, Spring Boot 3.3, Spring Web, Bean Validation |
| Persistencia | MongoDB con Spring Data MongoDB (`MongoRepository`) |
| Pruebas de API | Postman (colección v2.1 + entorno) |
| Pruebas unitarias | JUnit 5 |
| Diagramas | Visual Paradigm |

## Cómo ejecutar

Requisitos: **Java 17 o superior**, **Maven** (o IntelliJ IDEA, que ya lo trae) y **MongoDB** corriendo en `localhost:27017`.

```bash
cd biblioflow-api
mvn spring-boot:run
```

- La API queda en `http://localhost:8080/api`.
- La primera vez que arranca carga 6 libros y 3 socios de prueba (`DataSeeder`).
- Pruebas unitarias: `mvn test`.
- Para usar otra base de datos, cambia `spring.data.mongodb.uri` en `src/main/resources/application.properties`.

## Reglas de negocio

| # | Regla | Dónde está en el código | Error |
|---|---|---|---|
| R1 | Un estudiante puede tener máximo 3 préstamos abiertos y un docente máximo 5 | `TipoSocio`, `PrestamoService.validarPuedePrestar` | 422 `LIMITE_PRESTAMOS` |
| R2 | El plazo es de 14 días para estudiantes y 21 para docentes | `TipoSocio`, `Prestamo.crear` | — |
| R3 | Un préstamo ACTIVO pasa a VENCIDO cuando se supera la fecha límite. Un socio con préstamos vencidos o con multa no puede pedir prestado | `Prestamo.estaVencido`, `PrestamoService` | 422 `SOCIO_CON_VENCIDOS` / `SOCIO_CON_MULTA` |
| R4 | La multa es de $1.000 por cada día de retraso y se suma al saldo del socio | `Prestamo.calcularMulta`, `Socio.agregarMulta` | — |
| R5 | Solo se presta un libro DISPONIBLE | `Libro.prestar` | 409 `LIBRO_NO_DISPONIBLE` |
| R6 | El ISBN del libro y el documento del socio son únicos | `LibroService`, `SocioService` | 409 `ISBN_DUPLICADO` / `DOCUMENTO_DUPLICADO` |
| R7 | Máquina de estados del préstamo: ACTIVO → VENCIDO → DEVUELTO, o ACTIVO → DEVUELTO | `EstadoPrestamo.puedePasarA` | 409 `TRANSICION_INVALIDA` |
| R8 | Solo se elimina un libro que está disponible y nunca se ha prestado | `LibroService.eliminar` | 409 `LIBRO_PRESTADO` / `LIBRO_CON_HISTORIAL` |
| R9 | La multa se paga por el valor exacto | `Socio.pagarMulta` | 422 `VALOR_INCORRECTO` / 409 `SIN_MULTA` |

## Endpoints

URL base: `http://localhost:8080/api`

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/salud` | Estado del servicio |
| GET | `/libros?estado=&categoria=&q=` | Lista libros con filtros |
| GET | `/libros/{id}` | Detalle del libro con su total de préstamos |
| POST | `/libros` | Registra un libro (201) |
| DELETE | `/libros/{id}` | Elimina un libro (204) |
| GET | `/socios` | Lista socios |
| GET | `/socios/{id}` | Detalle del socio con sus préstamos, multa y cupo |
| POST | `/socios` | Registra un socio (201) |
| POST | `/socios/{id}/pagar-multa` | Paga la multa pendiente |
| GET | `/prestamos?estado=&socioId=&libroId=` | Lista préstamos con filtros |
| GET | `/prestamos/{id}` | Detalle del préstamo con libro, socio e historial |
| POST | `/prestamos` | Presta un libro (201) |
| PATCH | `/prestamos/{id}/devolver` | Registra la devolución y calcula la multa |
| POST | `/prestamos/actualizar-vencidos` | Marca como VENCIDOS los préstamos atrasados |

Todos los errores usan el mismo formato JSON:

```json
{ "statusCode": 409, "error": "Conflict", "codigo": "LIBRO_NO_DISPONIBLE", "message": "...", "timestamp": "..." }
```

## Postman

1. Importa `postman/BiblioFlow.postman_collection.json` y `postman/BiblioFlow.postman_environment.json`.
2. Selecciona el entorno **BiblioFlow - Local**.
3. Ejecuta la colección con el **Runner**, en orden.

La colección tiene 47 peticiones y 126 validaciones: flujo principal, casos de error y reglas de negocio. Cada corrida crea sus propios libros y su socio, así que se puede repetir sin borrar la base.

## Arquitectura

La API tiene 3 capas: **Controller → Service → Repository**. El modelo lo usan todas las capas.

- Los **Controllers** solo atienden HTTP y nunca llaman a un Repository.
- Las reglas de negocio viven en los **Services** y en los métodos del **modelo**.
- Los **Repositories** extienden `MongoRepository<T, String>`; Spring Data genera la implementación.
- El historial va **embebido** dentro de cada documento `Prestamo`. Es una composición: el historial no existe sin su préstamo.

Las 5 vistas (contexto, funcional, lógica, desarrollo y despliegue) están en [`diagramas/`](diagramas/README.md).
