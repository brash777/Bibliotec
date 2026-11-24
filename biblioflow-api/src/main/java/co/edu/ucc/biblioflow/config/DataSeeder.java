package co.edu.ucc.biblioflow.config;

import co.edu.ucc.biblioflow.model.CategoriaLibro;
import co.edu.ucc.biblioflow.model.Libro;
import co.edu.ucc.biblioflow.model.Socio;
import co.edu.ucc.biblioflow.model.TipoSocio;
import co.edu.ucc.biblioflow.repository.LibroRepository;
import co.edu.ucc.biblioflow.repository.SocioRepository;
import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/** Carga 6 libros y 3 socios de prueba la primera vez que arranca (si la base está vacía). */
@Component
public class DataSeeder implements CommandLineRunner {

    private final LibroRepository libroRepository;
    private final SocioRepository socioRepository;

    public DataSeeder(LibroRepository libroRepository, SocioRepository socioRepository) {
        this.libroRepository = libroRepository;
        this.socioRepository = socioRepository;
    }

    @Override
    public void run(String... args) {
        if (libroRepository.count() == 0) {
            libroRepository.saveAll(List.of(
                    new Libro("978-0132350884", "Clean Code", "Robert C. Martin", CategoriaLibro.INGENIERIA, 2008),
                    new Libro("978-0201633610", "Design Patterns", "Gamma, Helm, Johnson, Vlissides", CategoriaLibro.INGENIERIA, 1994),
                    new Libro("978-0321125217", "Domain-Driven Design", "Eric Evans", CategoriaLibro.INGENIERIA, 2003),
                    new Libro("978-0307474728", "Cien años de soledad", "Gabriel García Márquez", CategoriaLibro.LITERATURA, 1967),
                    new Libro("978-0553380163", "Breve historia del tiempo", "Stephen Hawking", CategoriaLibro.CIENCIAS, 1988),
                    new Libro("978-9584276971", "Historia mínima de Colombia", "Jorge Orlando Melo", CategoriaLibro.HISTORIA, 2017)));
        }
        if (socioRepository.count() == 0) {
            socioRepository.saveAll(List.of(
                    new Socio("1085000001", "Laura Benavides", "laura.benavides@campusucc.edu.co", TipoSocio.ESTUDIANTE),
                    new Socio("1085000002", "Andrés Ortega", "andres.ortega@campusucc.edu.co", TipoSocio.ESTUDIANTE),
                    new Socio("27000003", "Marta Rosero", "marta.rosero@ucc.edu.co", TipoSocio.DOCENTE)));
        }
    }
}
