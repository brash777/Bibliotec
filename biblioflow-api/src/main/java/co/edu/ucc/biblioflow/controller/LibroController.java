package co.edu.ucc.biblioflow.controller;

import co.edu.ucc.biblioflow.dto.LibroDetalle;
import co.edu.ucc.biblioflow.dto.LibroRequest;
import co.edu.ucc.biblioflow.model.CategoriaLibro;
import co.edu.ucc.biblioflow.model.EstadoLibro;
import co.edu.ucc.biblioflow.model.Libro;
import co.edu.ucc.biblioflow.service.LibroService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/** Solo atiende HTTP; las reglas de negocio están en LibroService. */
@RestController
@RequestMapping("/api/libros")
public class LibroController {

    private final LibroService libroService;

    public LibroController(LibroService libroService) {
        this.libroService = libroService;
    }

    @GetMapping
    public List<Libro> listar(@RequestParam(required = false) EstadoLibro estado,
                              @RequestParam(required = false) CategoriaLibro categoria,
                              @RequestParam(required = false) String q) {
        return libroService.listar(estado, categoria, q);
    }

    @GetMapping("/{id}")
    public LibroDetalle obtener(@PathVariable String id) {
        return libroService.obtener(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Libro registrar(@Valid @RequestBody LibroRequest request) {
        return libroService.registrar(request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable String id) {
        libroService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
