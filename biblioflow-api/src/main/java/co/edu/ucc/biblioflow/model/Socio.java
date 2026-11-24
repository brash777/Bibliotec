package co.edu.ucc.biblioflow.model;

import co.edu.ucc.biblioflow.exception.NegocioException;
import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "socios")
public class Socio {

    @Id
    private String id;

    @Indexed(unique = true)
    private String documento;

    private String nombre;
    private String correo;
    private TipoSocio tipo;
    private int multaPendiente;
    private Instant creadoEn;

    public Socio() {
    }

    public Socio(String documento, String nombre, String correo, TipoSocio tipo) {
        this.documento = documento;
        this.nombre = nombre;
        this.correo = correo;
        this.tipo = tipo;
        this.multaPendiente = 0;
        this.creadoEn = Instant.now();
    }

    public void agregarMulta(int valor) {
        multaPendiente += valor;
    }

    /** R9: el pago debe ser exactamente el valor de la multa pendiente. */
    public void pagarMulta(int valor) {
        if (multaPendiente == 0) {
            throw NegocioException.conflicto("SIN_MULTA", "El socio no tiene multas pendientes");
        }
        if (valor != multaPendiente) {
            throw NegocioException.regla("VALOR_INCORRECTO", "El valor a pagar debe ser $" + multaPendiente);
        }
        multaPendiente = 0;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public TipoSocio getTipo() {
        return tipo;
    }

    public void setTipo(TipoSocio tipo) {
        this.tipo = tipo;
    }

    public int getMultaPendiente() {
        return multaPendiente;
    }

    public void setMultaPendiente(int multaPendiente) {
        this.multaPendiente = multaPendiente;
    }

    public Instant getCreadoEn() {
        return creadoEn;
    }

    public void setCreadoEn(Instant creadoEn) {
        this.creadoEn = creadoEn;
    }
}
