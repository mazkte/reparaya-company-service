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
    @Query("""
        SELECT e FROM EmpresaEntity e
        WHERE e.estado = 'ACTIVA'
          AND :categoria MEMBER OF e.especialidades
          AND e.trabajosHoy < e.capacidadDiariaMax
          AND (e.vigenciaContrato IS NULL OR e.vigenciaContrato >= CURRENT_DATE)
        ORDER BY e.trabajosHoy ASC
        """)
    List<EmpresaEntity> findDisponiblesPorCategoria(@Param("categoria") CategoriaEnum categoria);
}
