package co.edu.ucc.biblioflow.service;

import co.edu.ucc.biblioflow.dto.SocioDetalle;
import co.edu.ucc.biblioflow.dto.SocioRequest;
import co.edu.ucc.biblioflow.exception.NegocioException;
import co.edu.ucc.biblioflow.model.EstadoPrestamo;
import co.edu.ucc.biblioflow.model.Socio;
import co.edu.ucc.biblioflow.repository.PrestamoRepository;
import co.edu.ucc.biblioflow.repository.SocioRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class SocioService {

    private final SocioRepository socioRepository;
    private final PrestamoRepository prestamoRepository;

    public SocioService(SocioRepository socioRepository, PrestamoRepository prestamoRepository) {
        this.socioRepository = socioRepository;
        this.prestamoRepository = prestamoRepository;
    }

    public Socio registrar(SocioRequest req) {
        if (socioRepository.existsByDocumento(req.documento())) {
            throw NegocioException.conflicto("DOCUMENTO_DUPLICADO", "Ya existe un socio con documento " + req.documento());
        }
        Socio socio = new Socio(req.documento(), req.nombre().trim(), req.correo().trim().toLowerCase(), req.tipo());
        return socioRepository.save(socio);
    }

    public List<Socio> listar() {
        return socioRepository.findAll();
    }

    public Socio buscar(String id) {
        return socioRepository.findById(id).orElseThrow(() -> NegocioException.noEncontrado("Socio", id));
    }

    public SocioDetalle obtener(String id) {
        Socio socio = buscar(id);
        int abiertos = prestamoRepository
                .findBySocioIdAndEstadoIn(id, List.of(EstadoPrestamo.ACTIVO, EstadoPrestamo.VENCIDO)).size();
        return new SocioDetalle(socio, prestamoRepository.findBySocioId(id), abiertos);
    }

    public Socio pagarMulta(String id, int valor) {
        Socio socio = buscar(id);
        socio.pagarMulta(valor);
        return socioRepository.save(socio);
    }
}
