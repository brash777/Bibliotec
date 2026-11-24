package co.edu.ucc.biblioflow.dto;

import co.edu.ucc.biblioflow.model.Libro;
import co.edu.ucc.biblioflow.model.Prestamo;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

/** Préstamo + datos del libro y del socio (incluye el historial embebido). */
public class PrestamoDetalle {

    private final Prestamo prestamo;
    private final Libro libro;
    private final SocioResumen socio;

    public PrestamoDetalle(Prestamo prestamo, Libro libro, SocioResumen socio) {
        this.prestamo = prestamo;
        this.libro = libro;
        this.socio = socio;
    }

    @JsonUnwrapped
    public Prestamo getPrestamo() {
        return prestamo;
    }

    public Libro getLibro() {
        return libro;
    }

    public SocioResumen getSocio() {
        return socio;
    }
}
