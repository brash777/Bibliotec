package co.edu.ucc.biblioflow.model;

/** Máquina de estados del préstamo (R7): ACTIVO → VENCIDO → DEVUELTO, o ACTIVO → DEVUELTO. */
public enum EstadoPrestamo {
    ACTIVO,
    VENCIDO,
    DEVUELTO;

    public boolean puedePasarA(EstadoPrestamo nuevo) {
        return switch (this) {
            case ACTIVO -> nuevo == VENCIDO || nuevo == DEVUELTO;
            case VENCIDO -> nuevo == DEVUELTO;
            case DEVUELTO -> false;
        };
    }
}
