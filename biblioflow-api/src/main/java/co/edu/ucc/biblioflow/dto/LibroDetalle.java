package co.edu.ucc.biblioflow.dto;

import co.edu.ucc.biblioflow.model.Libro;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

/** Libro + cantidad de préstamos que ha tenido. */
public class LibroDetalle {

    private final Libro libro;
    private final long totalPrestamos;

    public LibroDetalle(Libro libro, long totalPrestamos) {
        this.libro = libro;
        this.totalPrestamos = totalPrestamos;
    }

    @JsonUnwrapped
    public Libro getLibro() {
        return libro;
    }

    public long getTotalPrestamos() {
        return totalPrestamos;
    }
}
