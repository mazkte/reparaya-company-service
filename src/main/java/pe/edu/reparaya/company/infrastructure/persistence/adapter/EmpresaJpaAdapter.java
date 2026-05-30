package pe.edu.reparaya.company.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pe.edu.reparaya.company.domain.model.CategoriaEnum;
import pe.edu.reparaya.company.domain.model.EmpresaEstadoEnum;
import pe.edu.reparaya.company.domain.model.EmpresaServicio;
import pe.edu.reparaya.company.domain.port.EmpresaRepository;
import pe.edu.reparaya.company.infrastructure.persistence.entity.EmpresaEntity;
import pe.edu.reparaya.company.infrastructure.persistence.repository.EmpresaJpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Adaptador de salida (output adapter) — implementa el puerto EmpresaRepository
 * usando Spring Data JPA. El dominio nunca conoce esta clase directamente.
 */
@Component
@RequiredArgsConstructor
public class EmpresaJpaAdapter implements EmpresaRepository {

    private final EmpresaJpaRepository jpaRepository;

    @Override
    public EmpresaServicio guardar(EmpresaServicio empresa) {
        EmpresaEntity entity = toEntity(empresa);
        EmpresaEntity saved  = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<EmpresaServicio> buscarPorId(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<EmpresaServicio> buscarPorRuc(String ruc) {
        return jpaRepository.findByRuc(ruc).map(this::toDomain);
    }

    @Override
    public List<EmpresaServicio> buscarTodas() {
        return jpaRepository.findAll().stream().map(this::toDomain).toList();
    }

    @Override
    public List<EmpresaServicio> buscarDisponiblesPorCategoria(CategoriaEnum categoria) {
        return jpaRepository.findDisponiblesPorCategoria(categoria)
                .stream().map(this::toDomain).toList();
    }

    @Override
    public List<EmpresaServicio> buscarPorEstado(String estado) {
        return jpaRepository.findByEstado(estado)
                .stream().map(this::toDomain).toList();
    }

    @Override
    public boolean existePorRuc(String ruc) {
        return jpaRepository.existsByRuc(ruc);
    }

    @Override
    public void guardarTodas(List<EmpresaServicio> empresas) {
        List<EmpresaEntity> entities = empresas.stream().map(this::toEntity).toList();
        jpaRepository.saveAll(entities);
    }

    // ── Conversión dominio ↔ entidad ──────────────────────────

    private EmpresaServicio toDomain(EmpresaEntity e) {
        return EmpresaServicio.reconstituir(
                e.getId(), e.getNombre(), e.getRuc(),
                e.getEmailCoordinador(), e.getWhatsappCoordinador(),
                e.getEspecialidades(), e.getCapacidadDiariaMax(),
                e.getTrabajosHoy(), e.getEstado(), e.getVigenciaContrato()
        );
    }

    private EmpresaEntity toEntity(EmpresaServicio d) {
        return EmpresaEntity.builder()
                .id(d.getId())
                .nombre(d.getNombre())
                .ruc(d.getRuc())
                .emailCoordinador(d.getEmailCoordinador())
                .whatsappCoordinador(d.getWhatsappCoordinador())
                .especialidades(d.getEspecialidades())
                .capacidadDiariaMax(d.getCapacidadDiariaMax())
                .trabajosHoy(d.getTrabajosHoy())
                .estado(d.getEstado())
                .vigenciaContrato(d.getVigenciaContrato())
                .build();
    }
}
