package pe.edu.reparaya.company.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.edu.reparaya.company.domain.model.CategoriaEnum;
import pe.edu.reparaya.company.infrastructure.persistence.entity.EmpresaEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EmpresaJpaRepository extends JpaRepository<EmpresaEntity, UUID> {

    Optional<EmpresaEntity> findByRuc(String ruc);

    boolean existsByRuc(String ruc);

    List<EmpresaEntity> findByEstado(String estado);

    /**
     * Busca empresas activas que tienen capacidad disponible
     * y están especializadas en la categoría indicada.
     */
    @Query(value = """
     SELECT e.* FROM companies.empresa_servicio e
     INNER JOIN companies.empresa_especialidad es ON es.empresa_id = e.id
     WHERE e.estado = 'ACTIVA'
       AND es.categoria = CAST(:categoria AS companies.categoria_enum)
       AND e.trabajos_hoy < e.capacidad_diaria_max
       AND (e.vigencia_contrato IS NULL OR e.vigencia_contrato >= CURRENT_DATE)
     ORDER BY e.trabajos_hoy ASC
     """, nativeQuery = true)
    List<EmpresaEntity> findDisponiblesPorCategoria(@Param("categoria") String categoria);
}
