package co.edu.ucc.biblioflow.service;

import co.edu.ucc.biblioflow.dto.LibroDetalle;
import co.edu.ucc.biblioflow.dto.LibroRequest;
import co.edu.ucc.biblioflow.exception.NegocioException;
import co.edu.ucc.biblioflow.model.CategoriaLibro;
import co.edu.ucc.biblioflow.model.EstadoLibro;
import co.edu.ucc.biblioflow.model.Libro;
import co.edu.ucc.biblioflow.repository.LibroRepository;
import co.edu.ucc.biblioflow.repository.PrestamoRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class LibroService {

    private final LibroRepository libroRepository;
    private final PrestamoRepository prestamoRepository;

    public LibroService(LibroRepository libroRepository, PrestamoRepository prestamoRepository) {
        this.libroRepository = libroRepository;
        this.prestamoRepository = prestamoRepository;
    }

    /** R6: el ISBN es único. */
    public Libro registrar(LibroRequest req) {
        String isbn = req.isbn().trim();
        if (libroRepository.existsByIsbn(isbn)) {
            throw NegocioException.conflicto("ISBN_DUPLICADO", "Ya existe un libro con ISBN " + isbn);
        }
        Libro libro = new Libro(isbn, req.titulo().trim(), req.autor().trim(), req.categoria(), req.anioPublicacion());
        return libroRepository.save(libro);
    }

    public List<Libro> listar(EstadoLibro estado, CategoriaLibro categoria, String q) {
        String texto = q == null ? null : q.toLowerCase();
        return libroRepository.findAll().stream()
                .filter(l -> estado == null || l.getEstado() == estado)
                .filter(l -> categoria == null || l.getCategoria() == categoria)
                .filter(l -> texto == null || l.getTitulo().toLowerCase().contains(texto)
                        || l.getAutor().toLowerCase().contains(texto))
                .toList();
    }

    public Libro buscar(String id) {
        return libroRepository.findById(id).orElseThrow(() -> NegocioException.noEncontrado("Libro", id));
    }

    public LibroDetalle obtener(String id) {
        return new LibroDetalle(buscar(id), prestamoRepository.countByLibroId(id));
    }

    /** R8: solo se elimina un libro DISPONIBLE y que nunca se haya prestado. */
    public void eliminar(String id) {
        Libro libro = buscar(id);
        if (libro.getEstado() == EstadoLibro.PRESTADO) {
            throw NegocioException.conflicto("LIBRO_PRESTADO", "No se puede eliminar un libro que está prestado");
        }
        if (prestamoRepository.countByLibroId(id) > 0) {
            throw NegocioException.conflicto("LIBRO_CON_HISTORIAL", "No se puede eliminar un libro con historial de préstamos");
        }
        libroRepository.deleteById(id);
    }
}
