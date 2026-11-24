# Pruebas de la API con Postman

1. Arranca la API: `cd biblioflow-api && mvn spring-boot:run`. Necesitas MongoDB corriendo en `localhost:27017`.
2. En Postman, **Import** → selecciona `BiblioFlow.postman_collection.json` y `BiblioFlow.postman_environment.json`.
3. Arriba a la derecha elige el entorno **BiblioFlow - Local** (`baseUrl = http://localhost:8080/api`).
4. Clic derecho en la colección → **Run collection** → **Run BiblioFlow API**, en orden.

| Carpeta | Qué prueba |
|---|---|
| 00 - Salud | Que el servicio responde |
| 01 - Libros | Listar, filtrar, registrar, ISBN duplicado (409), datos inválidos (400), JSON mal formado (400), no encontrado (404) |
| 02 - Socios | Registrar, documento duplicado (409), datos inválidos (400), detalle con cupo |
| 03 - Préstamos | Prestar → libro PRESTADO → devolver con 5 días de retraso → multa de $5.000 → pagar la multa |
| 04 - Reglas de negocio | Límite de 3 préstamos (422), vencidos, devolución de un vencido, eliminar libro prestado o con historial (409), errores 400 y 404 |

En total son 47 peticiones y 126 validaciones. Las variables (`libroA`, `socioId`, `prestamoA`…) se guardan solas con los scripts de *Tests*. Cada corrida crea sus propios datos, así que se puede ejecutar varias veces.

Desde la terminal también se puede correr con Newman:

```bash
npx newman run postman/BiblioFlow.postman_collection.json -e postman/BiblioFlow.postman_environment.json
```
