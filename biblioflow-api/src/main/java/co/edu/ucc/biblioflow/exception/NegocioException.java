package co.edu.ucc.biblioflow.exception;

/**
 * Error de negocio. No conoce HTTP: el GlobalExceptionHandler lo traduce a 400/404/409/422.
 */
public class NegocioException extends RuntimeException {

    public enum Tipo {
        VALIDACION(400), NO_ENCONTRADO(404), CONFLICTO(409), REGLA_NEGOCIO(422);

        private final int status;

        Tipo(int status) {
            this.status = status;
        }

        public int getStatus() {
            return status;
        }
    }

    private final Tipo tipo;
    private final String codigo;

    public NegocioException(Tipo tipo, String codigo, String mensaje) {
        super(mensaje);
        this.tipo = tipo;
        this.codigo = codigo;
    }

    public static NegocioException validacion(String mensaje) {
        return new NegocioException(Tipo.VALIDACION, "DATOS_INVALIDOS", mensaje);
    }

    public static NegocioException noEncontrado(String recurso, String id) {
        return new NegocioException(Tipo.NO_ENCONTRADO, recurso.toUpperCase() + "_NO_ENCONTRADO",
                recurso + " con id " + id + " no existe");
    }

    public static NegocioException conflicto(String codigo, String mensaje) {
        return new NegocioException(Tipo.CONFLICTO, codigo, mensaje);
    }

    public static NegocioException regla(String codigo, String mensaje) {
        return new NegocioException(Tipo.REGLA_NEGOCIO, codigo, mensaje);
    }

    public Tipo getTipo() {
        return tipo;
    }

    public String getCodigo() {
        return codigo;
    }
}
