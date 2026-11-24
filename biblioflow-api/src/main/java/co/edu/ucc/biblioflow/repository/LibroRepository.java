package co.edu.ucc.biblioflow.repository;

import co.edu.ucc.biblioflow.model.Libro;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface LibroRepository extends MongoRepository<Libro, String> {

    boolean existsByIsbn(String isbn);
}
