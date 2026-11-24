package co.edu.ucc.biblioflow.service;

import co.edu.ucc.biblioflow.dto.ActualizacionVencidosResponse;
import co.edu.ucc.biblioflow.dto.PrestamoDetalle;
import co.edu.ucc.biblioflow.dto.SocioResumen;
import co.edu.ucc.biblioflow.exception.NegocioException;
import co.edu.ucc.biblioflow.model.EstadoLibro;
import co.edu.ucc.biblioflow.model.EstadoPrestamo;
import co.edu.ucc.biblioflow.model.Libro;
import co.edu.ucc.biblioflow.model.Prestamo;
import co.edu.ucc.biblioflow.model.Socio;
import co.edu.ucc.biblioflow.repository.LibroRepository;
import co.edu.ucc.biblioflow.repository.PrestamoRepository;
import co.edu.ucc.biblioflow.repository.SocioRepository;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class PrestamoService {

    private final PrestamoRepository prestamoRepository;
    private final LibroRepository libroRepository;
    private final SocioRepository socioRepository;

    public PrestamoService(PrestamoRepository prestamoRepository, LibroRepository libroRepository,
                           SocioRepository socioRepository) {
        this.prestamoRepository = prestamoRepository;
        this.libroRepository = libroRepository;
        this.socioRepository = socioRepository;
    }

    public List<Prestamo> listar(EstadoPrestamo estado, String socioId, String libroId) {
        return prestamoRepository.findAll().stream()
                .filter(p -> estado == null || p.getEstado() == estado)
                .filter(p -> socioId == null || socioId.equals(p.getSocioId()))
                .filter(p -> libroId == null || libroId.equals(p.getLibroId()))
                .toList();
    }

    public PrestamoDetalle obtener(String id) {
        Prestamo prestamo = buscar(id);
        Libro libro = libroRepository.findById(prestamo.getLibroId()).orElse(null);
        Socio socio = socioRepository.findById(prestamo.getSocioId()).orElse(null);
        return new PrestamoDetalle(prestamo, libro, socio == null ? null : SocioResumen.de(socio));
    }

    /**
     * Presta un libro. Primero valida TODAS las reglas y solo después escribe
     * (préstamo ACTIVO + libro PRESTADO + historial).
     */
    public PrestamoDetalle prestar(String socioId, String libroId, Instant fecha) {
        Socio socio = socioRepository.findById(socioId).orElseThrow(() -> NegocioException.noEncontrado("Socio", socioId));
        Libro libro = libroRepository.findById(libroId).orElseThrow(() -> NegocioException.noEncontrado("Libro", libroId));
        List<Prestamo> abiertos = prestamoRepository
                .findBySocioIdAndEstadoIn(socioId, List.of(EstadoPrestamo.ACTIVO, EstadoPrestamo.VENCIDO));

        validarPuedePrestar(socio, libro, abiertos);

        Prestamo prestamo = Prestamo.crear(socio, libro, fecha == null ? Instant.now() : fecha);
        libro.prestar();
        prestamo = prestamoRepository.save(prestamo);
        libroRepository.save(libro);
        return new PrestamoDetalle(prestamo, libro, SocioResumen.de(socio));
    }

    /**
     * R5: libro DISPONIBLE (409). R3: sin multas (422). Sin préstamos vencidos (422).
     * R1: no superar el máximo de préstamos del tipo de socio (422).
     */
    void validarPuedePrestar(Socio socio, Libro libro, List<Prestamo> abiertos) {
        if (libro.getEstado() != EstadoLibro.DISPONIBLE) {
            throw NegocioException.conflicto("LIBRO_NO_DISPONIBLE",
                    "El libro \"" + libro.getTitulo() + "\" no está disponible (estado " + libro.getEstado() + ")");
        }
        if (socio.getMultaPendiente() > 0) {
            throw NegocioException.regla("SOCIO_CON_MULTA", "El socio tiene una multa pendiente de $" + socio.getMultaPendiente());
        }
        if (abiertos.stream().anyMatch(p -> p.getEstado() == EstadoPrestamo.VENCIDO)) {
            throw NegocioException.regla("SOCIO_CON_VENCIDOS", "El socio tiene préstamos vencidos sin devolver");
        }
        int max = socio.getTipo().getMaxPrestamos();
        if (abiertos.size() >= max) {
            throw NegocioException.regla("LIMITE_PRESTAMOS",
                    "Un socio " + socio.getTipo() + " puede tener máximo " + max + " préstamos activos");
        }
    }

    /** Devolución: calcula retraso y multa, libera el libro y suma la multa al socio. */
    public PrestamoDetalle devolver(String id, Instant fecha) {
        Prestamo prestamo = buscar(id);
        prestamo.devolver(fecha == null ? Instant.now() : fecha);

        Libro libro = libroRepository.findById(prestamo.getLibroId())
                .orElseThrow(() -> NegocioException.noEncontrado("Libro", prestamo.getLibroId()));
        Socio socio = socioRepository.findById(prestamo.getSocioId())
                .orElseThrow(() -> NegocioException.noEncontrado("Socio", prestamo.getSocioId()));
        libro.devolver();
        if (prestamo.getMulta() > 0) {
            socio.agregarMulta(prestamo.getMulta());
            socioRepository.save(socio);
        }
        libroRepository.save(libro);
        prestamoRepository.save(prestamo);
        return new PrestamoDetalle(prestamo, libro, SocioResumen.de(socio));
    }

    /** R3: marca como VENCIDOS los préstamos ACTIVOS cuya fecha límite ya pasó. */
    public ActualizacionVencidosResponse actualizarVencidos(Instant fechaCorte) {
        Instant corte = fechaCorte == null ? Instant.now() : fechaCorte;
        List<Prestamo> vencidos = prestamoRepository.findByEstado(EstadoPrestamo.ACTIVO).stream()
                .filter(p -> p.estaVencido(corte))
                .toList();
        vencidos.forEach(p -> p.marcarVencido(corte));
        prestamoRepository.saveAll(vencidos);
        return new ActualizacionVencidosResponse(vencidos.size(), vencidos);
    }

    private Prestamo buscar(String id) {
        return prestamoRepository.findById(id).orElseThrow(() -> NegocioException.noEncontrado("Prestamo", id));
    }
}
