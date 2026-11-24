package co.edu.ucc.biblioflow.dto;

import co.edu.ucc.biblioflow.model.Prestamo;
import co.edu.ucc.biblioflow.model.Socio;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import java.util.List;

/** Socio + sus préstamos y su cupo. */
public class SocioDetalle {

    private final Socio socio;
    private final List<Prestamo> prestamos;
    private final int prestamosAbiertos;

    public SocioDetalle(Socio socio, List<Prestamo> prestamos, int prestamosAbiertos) {
        this.socio = socio;
        this.prestamos = prestamos;
        this.prestamosAbiertos = prestamosAbiertos;
    }

    @JsonUnwrapped
    public Socio getSocio() {
        return socio;
    }

    public List<Prestamo> getPrestamos() {
        return prestamos;
    }

    public int getPrestamosAbiertos() {
        return prestamosAbiertos;
    }

    public int getMaxPrestamos() {
        return socio.getTipo().getMaxPrestamos();
    }
}
