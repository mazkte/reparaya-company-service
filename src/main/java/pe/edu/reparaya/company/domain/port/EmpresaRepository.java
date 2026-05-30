package pe.edu.reparaya.company.domain.port;

import pe.edu.reparaya.company.domain.model.CategoriaEnum;
import pe.edu.reparaya.company.domain.model.EmpresaServicio;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Puerto de salida (output port) — interfaz que define cómo el dominio
 * accede a la persistencia. La implementación está en infrastructure.
 */
public interface EmpresaRepository {

    EmpresaServicio guardar(EmpresaServicio empresa);

    Optional<EmpresaServicio> buscarPorId(UUID id);

    Optional<EmpresaServicio> buscarPorRuc(String ruc);

    List<EmpresaServicio> buscarTodas();

    List<EmpresaServicio> buscarDisponiblesPorCategoria(CategoriaEnum categoria);

    List<EmpresaServicio> buscarPorEstado(String estado);

    boolean existePorRuc(String ruc);

    void guardarTodas(List<EmpresaServicio> empresas);
}
