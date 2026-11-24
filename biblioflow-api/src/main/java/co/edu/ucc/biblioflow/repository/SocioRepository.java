package co.edu.ucc.biblioflow.repository;

import co.edu.ucc.biblioflow.model.Socio;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SocioRepository extends MongoRepository<Socio, String> {

    boolean existsByDocumento(String documento);
}
