package co.edu.ucc.biblioflow.controller;

import co.edu.ucc.biblioflow.dto.ActualizacionVencidosResponse;
import co.edu.ucc.biblioflow.dto.DevolucionRequest;
import co.edu.ucc.biblioflow.dto.FechaCorteRequest;
import co.edu.ucc.biblioflow.dto.PrestamoDetalle;
import co.edu.ucc.biblioflow.dto.PrestamoRequest;
import co.edu.ucc.biblioflow.model.EstadoPrestamo;
import co.edu.ucc.biblioflow.model.Prestamo;
import co.edu.ucc.biblioflow.service.PrestamoService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/** Solo atiende HTTP; las reglas de negocio están en PrestamoService. */
@RestController
@RequestMapping("/api/prestamos")
public class PrestamoController {

    private final PrestamoService prestamoService;

    public PrestamoController(PrestamoService prestamoService) {
        this.prestamoService = prestamoService;
    }

    @GetMapping
    public List<Prestamo> listar(@RequestParam(required = false) EstadoPrestamo estado,
                                 @RequestParam(required = false) String socioId,
                                 @RequestParam(required = false) String libroId) {
        return prestamoService.listar(estado, socioId, libroId);
    }

    @GetMapping("/{id}")
    public PrestamoDetalle obtener(@PathVariable String id) {
        return prestamoService.obtener(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PrestamoDetalle prestar(@Valid @RequestBody PrestamoRequest request) {
        return prestamoService.prestar(request.socioId(), request.libroId(), request.fechaPrestamo());
    }

    @PatchMapping("/{id}/devolver")
    public PrestamoDetalle devolver(@PathVariable String id, @RequestBody(required = false) DevolucionRequest request) {
        return prestamoService.devolver(id, request == null ? null : request.fechaDevolucion());
    }

    @PostMapping("/actualizar-vencidos")
    public ActualizacionVencidosResponse actualizarVencidos(@RequestBody(required = false) FechaCorteRequest request) {
        return prestamoService.actualizarVencidos(request == null ? null : request.fechaCorte());
    }
}
