package co.edu.ucc.biblioflow.dto;

import co.edu.ucc.biblioflow.model.Prestamo;
import java.util.List;

public record ActualizacionVencidosResponse(int actualizados, List<Prestamo> prestamos) {
}
