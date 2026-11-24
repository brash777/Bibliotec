# Diagramas: las 5 vistas de arquitectura de BiblioFlow

- El proyecto editable de Visual Paradigm es `BiblioFlow.vpp`.
- Los PNG de esta carpeta son de referencia.
- Las fuentes de los PNG están en `fuentes/`.

Todas las vistas usan los mismos nombres de actores, componentes, clases y nodos.

## Mapa de integración entre vistas

| Concepto | Contexto | Funcional | Lógica | Desarrollo | Despliegue |
|---|---|---|---|---|---|
| Socio | Actor **Socio** | Usa **Consulta del socio** | Clase `Socio` | `SocioController` (`GET /socios/{id}`) | «device» Dispositivo del socio |
| Bibliotecario, administrador del catálogo, cajero | Actores **Bibliotecario**, **Administrador del catálogo** y **Cajero** | Usan **Estación de biblioteca** | — | `LibroController`, `PrestamoController`, `SocioController` | «device» PC del bibliotecario |
| Libros | Flujos "Libros nuevos y bajas" y "Catálogo y disponibilidad" | **Gestión de catálogo** (`ICatalogo`) | `Libro`, `EstadoLibro`, `CategoriaLibro` | `LibroService`, `LibroRepository` | Dentro de `biblioflow-api.jar` |
| Socios y multas | Flujos "Pago de multa" y "Multas pendientes" | **Gestión de socios** (`ISocios`) | `Socio`, `TipoSocio` | `SocioService`, `SocioRepository` | Dentro de `biblioflow-api.jar` |
| Préstamos | Flujos "Préstamos y devoluciones" y "Fecha límite, retraso y multa" | **Gestión de préstamos** (`IPrestamos`) | `Prestamo`, `HistorialPrestamo`, `EstadoPrestamo` | `PrestamoService`, `PrestamoRepository`, `PrestamoRequest` | Dentro de `biblioflow-api.jar` |
| Persistencia | — | **Base de datos** (`IDatos`) | — | `*Repository` extienden `MongoRepository<T, String>` | «node» Servidor BD → MongoDB → `biblioflow` |
| Sistema completo | Círculo **Sistema de préstamos BiblioFlow** | Conjunto de componentes | — | Proyecto Spring Boot | «device» Servidor central → JVM → `biblioflow-api.jar` |

## Vista 1 · Contexto (`1_contexto.png`)

El sistema va en un círculo al centro. Cada actor tiene una flecha de entrada y otra de salida.

| Actor | → hacia el sistema | ← desde el sistema |
|---|---|---|
| **Socio** (arriba) | Solicitud de préstamo y consulta | Estado de préstamos y multa |
| **Bibliotecario** (izquierda) | Préstamos y devoluciones | Fecha límite, retraso y multa |
| **Administrador del catálogo** (derecha) | Libros nuevos y bajas | Catálogo y disponibilidad |
| **Cajero** (abajo) | Pago de multa | Multas pendientes |

## Vista 2 · Funcional: componentes (`2_funcional.png`)

| Interfaz | La ofrece (○) | La requiere (◐) | Para qué |
|---|---|---|---|
| `ICatalogo` | Gestión de catálogo | Estación de biblioteca | Registrar, consultar y dar de baja libros (R6, R8) |
| `IPrestamos` | Gestión de préstamos | Estación de biblioteca | Prestar, devolver y marcar vencidos (R1–R5, R7) |
| `ISocios` | Gestión de socios | Estación de biblioteca, Consulta del socio | Registrar socios, consultar su estado y pagar multas (R9) |
| `IDatos` | Base de datos | Gestión de catálogo, de préstamos y de socios | Persistencia: un solo socket al que llegan las tres líneas |

## Vista 3 · Lógica: clases del dominio (`3_logica.png`)

| Desde | Tipo | Hacia | Multiplicidad | Etiqueta | Justificación |
|---|---|---|---|---|---|
| Socio | Asociación | Prestamo | 1 — 0..* | solicita | Un socio puede tener muchos préstamos |
| Libro | Asociación | Prestamo | 1 — 0..* | es prestado en | Un libro se presta muchas veces en el tiempo |
| Prestamo | **Composición ◆** (rombo en Prestamo) | HistorialPrestamo | 1 — 1..* | registra | El historial no existe sin su préstamo; va embebido en el documento |
| Socio / Libro / Prestamo | Dependencia | Enumeraciones | — | — | `TipoSocio`, `EstadoLibro`, `CategoriaLibro`, `EstadoPrestamo` |

## Vista 4 · Desarrollo: clases por capas (`4_desarrollo.png`)

La vista tiene 4 franjas: **Controller**, **Service**, **Repository** y **Model**.

- Línea continua con flecha (Controller → Service y Service → Repository): la referencia se guarda como atributo y se inyecta por el constructor.
- Línea punteada «use»: uso puntual. Por ejemplo, `PrestamoController` recibe un `PrestamoRequest` y cada Repository maneja su documento.
- Los Repositories **extienden** `MongoRepository<T, String>` (herencia △).
- Ninguna flecha va de un Controller a un Repository.

## Vista 5 · Despliegue (`5_despliegue.png`)

| Nodo | Estereotipo | Entorno de ejecución | Artefacto |
|---|---|---|---|
| PC del bibliotecario | «device» | Postman | `BiblioFlow.postman_collection.json` |
| Dispositivo del socio | «device» | Navegador | Consulta del socio |
| Servidor central | «device» | JVM (Java 17) | `biblioflow-api.jar` (Controller · Service · Repository) |
| Servidor BD | «node» | MongoDB | `biblioflow` (libros · socios · prestamos) |

| Ruta de comunicación | Protocolo / puerto | Multiplicidad |
|---|---|---|
| PC del bibliotecario — Servidor central | HTTP :8080 (LAN) | 1..* — 1 |
| Dispositivo del socio — Servidor central | HTTP :8080 (red UCC) | 0..* — 1 |
| Servidor central — Servidor BD | TCP :27017 (MongoDB) | 1 — 1 |

Las capas no son servidores separados: las tres van dentro del mismo `biblioflow-api.jar`.
