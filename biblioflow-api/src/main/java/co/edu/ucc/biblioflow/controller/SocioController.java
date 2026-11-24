package co.edu.ucc.biblioflow.controller;

import co.edu.ucc.biblioflow.dto.PagoMultaRequest;
import co.edu.ucc.biblioflow.dto.SocioDetalle;
import co.edu.ucc.biblioflow.dto.SocioRequest;
import co.edu.ucc.biblioflow.model.Socio;
import co.edu.ucc.biblioflow.service.SocioService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/** Solo atiende HTTP; las reglas de negocio están en SocioService. */
@RestController
@RequestMapping("/api/socios")
public class SocioController {

    private final SocioService socioService;

    public SocioController(SocioService socioService) {
        this.socioService = socioService;
    }

    @GetMapping
    public List<Socio> listar() {
        return socioService.listar();
    }

    @GetMapping("/{id}")
    public SocioDetalle obtener(@PathVariable String id) {
        return socioService.obtener(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Socio registrar(@Valid @RequestBody SocioRequest request) {
        return socioService.registrar(request);
    }

    @PostMapping("/{id}/pagar-multa")
    public Socio pagarMulta(@PathVariable String id, @Valid @RequestBody PagoMultaRequest request) {
        return socioService.pagarMulta(id, request.valor());
    }
}
