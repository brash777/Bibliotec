package co.edu.ucc.biblioflow.model;

import co.edu.ucc.biblioflow.exception.NegocioException;
import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "libros")
public class Libro {

    @Id
    private String id;

    @Indexed(unique = true)
    private String isbn;

    private String titulo;
    private String autor;
    private CategoriaLibro categoria;
    private Integer anioPublicacion;
    private EstadoLibro estado;
    private Instant creadoEn;

    public Libro() {
    }

    public Libro(String isbn, String titulo, String autor, CategoriaLibro categoria, Integer anioPublicacion) {
        this.isbn = isbn;
        this.titulo = titulo;
        this.autor = autor;
        this.categoria = categoria;
        this.anioPublicacion = anioPublicacion;
        this.estado = EstadoLibro.DISPONIBLE;
        this.creadoEn = Instant.now();
    }

    /** R5: solo se presta un libro DISPONIBLE. */
    public void prestar() {
        if (estado != EstadoLibro.DISPONIBLE) {
            throw NegocioException.conflicto("LIBRO_NO_DISPONIBLE",
                    "El libro \"" + titulo + "\" no está disponible (estado " + estado + ")");
        }
        estado = EstadoLibro.PRESTADO;
    }

    public void devolver() {
        if (estado != EstadoLibro.PRESTADO) {
            throw NegocioException.conflicto("TRANSICION_INVALIDA", "El libro no puede pasar de " + estado + " a DISPONIBLE");
        }
        estado = EstadoLibro.DISPONIBLE;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public CategoriaLibro getCategoria() {
        return categoria;
    }

    public void setCategoria(CategoriaLibro categoria) {
        this.categoria = categoria;
    }

    public Integer getAnioPublicacion() {
        return anioPublicacion;
    }

    public void setAnioPublicacion(Integer anioPublicacion) {
        this.anioPublicacion = anioPublicacion;
    }

    public EstadoLibro getEstado() {
        return estado;
    }

    public void setEstado(EstadoLibro estado) {
        this.estado = estado;
    }

    public Instant getCreadoEn() {
        return creadoEn;
    }

    public void setCreadoEn(Instant creadoEn) {
        this.creadoEn = creadoEn;
    }
}
