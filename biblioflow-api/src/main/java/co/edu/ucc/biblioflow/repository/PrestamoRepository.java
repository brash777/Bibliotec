package co.edu.ucc.biblioflow.repository;

import co.edu.ucc.biblioflow.model.EstadoPrestamo;
import co.edu.ucc.biblioflow.model.Prestamo;
import java.util.Collection;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PrestamoRepository extends MongoRepository<Prestamo, String> {

    List<Prestamo> findBySocioId(String socioId);

    List<Prestamo> findBySocioIdAndEstadoIn(String socioId, Collection<EstadoPrestamo> estados);

    List<Prestamo> findByEstado(EstadoPrestamo estado);

    long countByLibroId(String libroId);
}
