package co.edu.ucc.biblioflow.dto;

import co.edu.ucc.biblioflow.model.Socio;
import co.edu.ucc.biblioflow.model.TipoSocio;

public record SocioResumen(String id, String documento, String nombre, TipoSocio tipo) {

    public static SocioResumen de(Socio s) {
        return new SocioResumen(s.getId(), s.getDocumento(), s.getNombre(), s.getTipo());
    }
}
