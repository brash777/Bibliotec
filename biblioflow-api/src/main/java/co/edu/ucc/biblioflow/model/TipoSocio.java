package co.edu.ucc.biblioflow.model;

/** Política de préstamo según el tipo de socio (R1 y R2). */
public enum TipoSocio {
    ESTUDIANTE(3, 14),
    DOCENTE(5, 21);

    private final int maxPrestamos;
    private final int diasPlazo;

    TipoSocio(int maxPrestamos, int diasPlazo) {
        this.maxPrestamos = maxPrestamos;
        this.diasPlazo = diasPlazo;
    }

    public int getMaxPrestamos() {
        return maxPrestamos;
    }

    public int getDiasPlazo() {
        return diasPlazo;
    }
}
