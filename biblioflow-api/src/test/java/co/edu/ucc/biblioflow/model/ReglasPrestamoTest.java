package co.edu.ucc.biblioflow.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import co.edu.ucc.biblioflow.exception.NegocioException;
import java.time.Instant;
import org.junit.jupiter.api.Test;

/** Pruebas unitarias de las reglas de negocio del modelo (no necesitan MongoDB). */
class ReglasPrestamoTest {

    private static final Instant INICIO = Instant.parse("2026-11-01T00:00:00Z");

    private Socio socio(TipoSocio tipo) {
        Socio s = new Socio("1085000001", "Laura", "laura@campusucc.edu.co", tipo);
        s.setId("s1");
        return s;
    }

    private Libro libro() {
        Libro l = new Libro("978-0132350884", "Clean Code", "Robert C. Martin", CategoriaLibro.INGENIERIA, 2008);
        l.setId("l1");
        return l;
    }

    @Test
    void estudianteTiene14DiasYDocente21() {
        assertEquals(Instant.parse("2026-11-15T00:00:00Z"),
                Prestamo.crear(socio(TipoSocio.ESTUDIANTE), libro(), INICIO).getFechaLimite());
        assertEquals(Instant.parse("2026-11-22T00:00:00Z"),
                Prestamo.crear(socio(TipoSocio.DOCENTE), libro(), INICIO).getFechaLimite());
    }

    @Test
    void prestamoNuevoQuedaActivoConHistorial() {
        Prestamo p = Prestamo.crear(socio(TipoSocio.ESTUDIANTE), libro(), INICIO);
        assertEquals(EstadoPrestamo.ACTIVO, p.getEstado());
        assertEquals(1, p.getHistorial().size());
    }

    @Test
    void sinRetrasoSiSeDevuelveATiempo() {
        Prestamo p = Prestamo.crear(socio(TipoSocio.ESTUDIANTE), libro(), INICIO);
        assertEquals(0, p.calcularDiasRetraso(Instant.parse("2026-11-10T00:00:00Z")));
        assertEquals(0, p.calcularDiasRetraso(Instant.parse("2026-11-15T00:00:00Z")));
    }

    @Test
    void retrasoRedondeaHaciaArribaYMultaEsMilPorDia() {
        Prestamo p = Prestamo.crear(socio(TipoSocio.ESTUDIANTE), libro(), INICIO);
        assertEquals(1, p.calcularDiasRetraso(Instant.parse("2026-11-15T01:00:00Z")));
        assertEquals(5, p.calcularDiasRetraso(Instant.parse("2026-11-20T00:00:00Z")));
        assertEquals(5000, p.calcularMulta(Instant.parse("2026-11-20T00:00:00Z")));
    }

    @Test
    void devolverTardeCalculaMultaYCambiaEstado() {
        Prestamo p = Prestamo.crear(socio(TipoSocio.ESTUDIANTE), libro(), INICIO);
        p.devolver(Instant.parse("2026-11-20T00:00:00Z"));
        assertEquals(EstadoPrestamo.DEVUELTO, p.getEstado());
        assertEquals(5, p.getDiasRetraso());
        assertEquals(5000, p.getMulta());
        assertEquals(2, p.getHistorial().size());
    }

    @Test
    void noSePuedeDevolverDosVeces() {
        Prestamo p = Prestamo.crear(socio(TipoSocio.ESTUDIANTE), libro(), INICIO);
        p.devolver(Instant.parse("2026-11-05T00:00:00Z"));
        NegocioException ex = assertThrows(NegocioException.class, () -> p.devolver(Instant.parse("2026-11-06T00:00:00Z")));
        assertEquals("TRANSICION_INVALIDA", ex.getCodigo());
    }

    @Test
    void fechaDeDevolucionAnteriorAlPrestamoEsInvalida() {
        Prestamo p = Prestamo.crear(socio(TipoSocio.ESTUDIANTE), libro(), INICIO);
        NegocioException ex = assertThrows(NegocioException.class, () -> p.devolver(Instant.parse("2020-01-01T00:00:00Z")));
        assertEquals("DATOS_INVALIDOS", ex.getCodigo());
    }

    @Test
    void detectaYMarcaVencidos() {
        Prestamo p = Prestamo.crear(socio(TipoSocio.ESTUDIANTE), libro(), INICIO);
        assertFalse(p.estaVencido(Instant.parse("2026-11-10T00:00:00Z")));
        assertTrue(p.estaVencido(Instant.parse("2026-11-16T00:00:00Z")));
        p.marcarVencido(Instant.parse("2026-11-16T00:00:00Z"));
        assertEquals(EstadoPrestamo.VENCIDO, p.getEstado());
        assertFalse(p.estaVencido(Instant.parse("2026-12-01T00:00:00Z")));
    }

    @Test
    void maquinaDeEstados() {
        assertTrue(EstadoPrestamo.ACTIVO.puedePasarA(EstadoPrestamo.DEVUELTO));
        assertTrue(EstadoPrestamo.ACTIVO.puedePasarA(EstadoPrestamo.VENCIDO));
        assertTrue(EstadoPrestamo.VENCIDO.puedePasarA(EstadoPrestamo.DEVUELTO));
        assertFalse(EstadoPrestamo.VENCIDO.puedePasarA(EstadoPrestamo.ACTIVO));
        assertFalse(EstadoPrestamo.DEVUELTO.puedePasarA(EstadoPrestamo.ACTIVO));
    }

    @Test
    void libroPrestadoNoSePuedeVolverAPrestar() {
        Libro l = libro();
        l.prestar();
        assertEquals(EstadoLibro.PRESTADO, l.getEstado());
        NegocioException ex = assertThrows(NegocioException.class, l::prestar);
        assertEquals("LIBRO_NO_DISPONIBLE", ex.getCodigo());
    }

    @Test
    void pagoDeMultaDebeSerExacto() {
        Socio s = socio(TipoSocio.ESTUDIANTE);
        assertEquals("SIN_MULTA", assertThrows(NegocioException.class, () -> s.pagarMulta(1000)).getCodigo());
        s.agregarMulta(5000);
        assertEquals("VALOR_INCORRECTO", assertThrows(NegocioException.class, () -> s.pagarMulta(1000)).getCodigo());
        s.pagarMulta(5000);
        assertEquals(0, s.getMultaPendiente());
    }

    @Test
    void limitesPorTipoDeSocio() {
        assertEquals(3, TipoSocio.ESTUDIANTE.getMaxPrestamos());
        assertEquals(5, TipoSocio.DOCENTE.getMaxPrestamos());
    }
}
