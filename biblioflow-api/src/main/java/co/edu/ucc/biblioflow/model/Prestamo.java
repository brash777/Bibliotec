package co.edu.ucc.biblioflow.model;

import co.edu.ucc.biblioflow.exception.NegocioException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "prestamos")
public class Prestamo {

    /** R4: valor de la multa por cada día de retraso (COP). */
    public static final int MULTA_POR_DIA = 1000;

    private static final long MS_DIA = Duration.ofDays(1).toMillis();

    @Id
    private String id;

    private String libroId;
    private String socioId;
    private Instant fechaPrestamo;
    private Instant fechaLimite;
    private Instant fechaDevolucion;
    private int diasRetraso;
    private int multa;
    private EstadoPrestamo estado;
    private List<HistorialPrestamo> historial = new ArrayList<>();

    public Prestamo() {
    }

    /** Crea un préstamo ACTIVO con fecha límite según el tipo de socio (R2). */
    public static Prestamo crear(Socio socio, Libro libro, Instant fechaPrestamo) {
        Prestamo p = new Prestamo();
        p.libroId = libro.getId();
        p.socioId = socio.getId();
        p.fechaPrestamo = fechaPrestamo;
        p.fechaLimite = fechaPrestamo.plus(Duration.ofDays(socio.getTipo().getDiasPlazo()));
        p.estado = EstadoPrestamo.ACTIVO;
        p.historial.add(new HistorialPrestamo(null, EstadoPrestamo.ACTIVO, fechaPrestamo,
                "Préstamo de \"" + libro.getTitulo() + "\" a " + socio.getNombre()));
        return p;
    }

    /** R3: un préstamo ACTIVO está vencido cuando la fecha de corte supera la fecha límite. */
    public boolean estaVencido(Instant fechaCorte) {
        return estado == EstadoPrestamo.ACTIVO && fechaCorte.isAfter(fechaLimite);
    }

    /** Días de retraso redondeando hacia arriba; nunca negativo. */
    public int calcularDiasRetraso(Instant fecha) {
        long diferencia = fecha.toEpochMilli() - fechaLimite.toEpochMilli();
        if (diferencia <= 0) {
            return 0;
        }
        return (int) ((diferencia + MS_DIA - 1) / MS_DIA);
    }

    /** R4: multa = días de retraso × $1.000. */
    public int calcularMulta(Instant fecha) {
        return calcularDiasRetraso(fecha) * MULTA_POR_DIA;
    }

    public void marcarVencido(Instant fechaCorte) {
        cambiarEstado(EstadoPrestamo.VENCIDO, fechaCorte, "Fecha límite superada");
    }

    /** Registra la devolución: calcula retraso y multa y pasa a DEVUELTO. */
    public void devolver(Instant fecha) {
        if (!estado.puedePasarA(EstadoPrestamo.DEVUELTO)) {
            throw transicionInvalida(EstadoPrestamo.DEVUELTO);
        }
        if (fecha.isBefore(fechaPrestamo)) {
            throw NegocioException.validacion("La fecha de devolución no puede ser anterior a la fecha del préstamo");
        }
        this.fechaDevolucion = fecha;
        this.diasRetraso = calcularDiasRetraso(fecha);
        this.multa = diasRetraso * MULTA_POR_DIA;
        String observacion = multa > 0
                ? "Devuelto con " + diasRetraso + " día(s) de retraso. Multa $" + multa
                : "Devuelto a tiempo";
        cambiarEstado(EstadoPrestamo.DEVUELTO, fecha, observacion);
    }

    /** R7: solo se permiten las transiciones definidas en EstadoPrestamo. */
    private void cambiarEstado(EstadoPrestamo nuevo, Instant fecha, String observacion) {
        if (!estado.puedePasarA(nuevo)) {
            throw transicionInvalida(nuevo);
        }
        historial.add(new HistorialPrestamo(estado, nuevo, fecha, observacion));
        estado = nuevo;
    }

    private NegocioException transicionInvalida(EstadoPrestamo nuevo) {
        return NegocioException.conflicto("TRANSICION_INVALIDA", "El préstamo no puede pasar de " + estado + " a " + nuevo);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLibroId() {
        return libroId;
    }

    public void setLibroId(String libroId) {
        this.libroId = libroId;
    }

    public String getSocioId() {
        return socioId;
    }

    public void setSocioId(String socioId) {
        this.socioId = socioId;
    }

    public Instant getFechaPrestamo() {
        return fechaPrestamo;
    }

    public void setFechaPrestamo(Instant fechaPrestamo) {
        this.fechaPrestamo = fechaPrestamo;
    }

    public Instant getFechaLimite() {
        return fechaLimite;
    }

    public void setFechaLimite(Instant fechaLimite) {
        this.fechaLimite = fechaLimite;
    }

    public Instant getFechaDevolucion() {
        return fechaDevolucion;
    }

    public void setFechaDevolucion(Instant fechaDevolucion) {
        this.fechaDevolucion = fechaDevolucion;
    }

    public int getDiasRetraso() {
        return diasRetraso;
    }

    public void setDiasRetraso(int diasRetraso) {
        this.diasRetraso = diasRetraso;
    }

    public int getMulta() {
        return multa;
    }

    public void setMulta(int multa) {
        this.multa = multa;
    }

    public EstadoPrestamo getEstado() {
        return estado;
    }

    public void setEstado(EstadoPrestamo estado) {
        this.estado = estado;
    }

    public List<HistorialPrestamo> getHistorial() {
        return historial;
    }

    public void setHistorial(List<HistorialPrestamo> historial) {
        this.historial = historial;
    }
}
