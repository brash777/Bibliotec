package co.edu.ucc.biblioflow.model;

import java.time.Instant;

/** Registro de un cambio de estado. Va embebido dentro del documento Prestamo (composición). */
public class HistorialPrestamo {

    private EstadoPrestamo estadoAnterior;
    private EstadoPrestamo estadoNuevo;
    private Instant fecha;
    private String observacion;

    public HistorialPrestamo() {
    }

    public HistorialPrestamo(EstadoPrestamo estadoAnterior, EstadoPrestamo estadoNuevo, Instant fecha, String observacion) {
        this.estadoAnterior = estadoAnterior;
        this.estadoNuevo = estadoNuevo;
        this.fecha = fecha;
        this.observacion = observacion;
    }

    public EstadoPrestamo getEstadoAnterior() {
        return estadoAnterior;
    }

    public void setEstadoAnterior(EstadoPrestamo estadoAnterior) {
        this.estadoAnterior = estadoAnterior;
    }

    public EstadoPrestamo getEstadoNuevo() {
        return estadoNuevo;
    }

    public void setEstadoNuevo(EstadoPrestamo estadoNuevo) {
        this.estadoNuevo = estadoNuevo;
    }

    public Instant getFecha() {
        return fecha;
    }

    public void setFecha(Instant fecha) {
        this.fecha = fecha;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }
}
