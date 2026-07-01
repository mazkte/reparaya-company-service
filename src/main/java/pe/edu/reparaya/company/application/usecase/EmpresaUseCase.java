package pe.edu.reparaya.company.application.usecase;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pe.edu.reparaya.company.application.dto.CompanyDtos.*;
import pe.edu.reparaya.company.application.mapper.EmpresaMapper;
import pe.edu.reparaya.company.domain.model.CategoriaEnum;
import pe.edu.reparaya.company.domain.model.EmpresaServicio;
import pe.edu.reparaya.company.domain.port.EmpresaRepository;
import pe.edu.reparaya.shared.exception.ReparaYaException;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmpresaUseCase {

 private final EmpresaRepository empresaRepository;
 private final EmpresaMapper empresaMapper;

 public EmpresaResponse crearEmpresa(CrearEmpresaRequest request) {
  if (empresaRepository.existePorRuc(request.ruc())) {
   throw new ReparaYaException.DuplicadoException(
     "Ya existe una empresa registrada con el RUC " + request.ruc());
  }

  EmpresaServicio empresa = EmpresaServicio.crear(
    request.nombre(), request.ruc(),
    request.emailCoordinador(), request.whatsappCoordinador(),
    request.especialidades(), request.capacidadDiariaMax(),
    request.vigenciaContrato()
  );

  EmpresaServicio guardada = empresaRepository.guardar(empresa);
  log.info("Empresa creada: {} ({})", guardada.getNombre(), guardada.getId());
  return empresaMapper.toResponse(guardada);
 }

 public EmpresaResponse obtenerPorId(UUID id) {
  return empresaRepository.buscarPorId(id)
    .map(empresaMapper::toResponse)
    .orElseThrow(() -> new ReparaYaException.RecursoNoEncontradoException("EmpresaServicio", id));
 }

 public List<EmpresaResponse> listarTodas() {
  return empresaMapper.toResponseList(empresaRepository.buscarTodas());
 }

 public List<DisponibilidadResponse> obtenerDisponiblesPorCategoria(CategoriaEnum categoria) {
  List<EmpresaServicio> disponibles = empresaRepository
    .buscarDisponiblesPorCategoria(categoria);
  log.debug("Empresas disponibles para {}: {}", categoria, disponibles.size());
  return empresaMapper.toDisponibilidadList(disponibles);
 }

 public EmpresaResponse actualizarCupo(UUID id, ActualizarCupoRequest request) {
  EmpresaServicio empresa = buscarOFallar(id);
  empresa.actualizarCupo(request.capacidadDiariaMax());
  EmpresaServicio actualizada = empresaRepository.guardar(empresa);
  log.info("Cupo actualizado para {}: {}", empresa.getNombre(), request.capacidadDiariaMax());
  return empresaMapper.toResponse(actualizada);
 }

 // ── Cambiar estado ────────────────────────────────────────

 public EmpresaResponse cambiarEstado(UUID id, CambiarEstadoRequest request) {
  EmpresaServicio empresa = buscarOFallar(id);
  switch (request.estado()) {
   case ACTIVA -> empresa.activar();
   case INACTIVA -> empresa.desactivar();
   case SUSPENDIDA -> empresa.suspender();
  }
  EmpresaServicio actualizada = empresaRepository.guardar(empresa);
  log.info("Estado de {} cambiado a {}", empresa.getNombre(), request.estado());
  return empresaMapper.toResponse(actualizada);
 }

 // ── Incrementar / decrementar carga (llamado por worker-service vía API) ──

 public void incrementarCarga(UUID id) {
  EmpresaServicio empresa = buscarOFallar(id);
  empresa.incrementarCarga();
  empresaRepository.guardar(empresa);
  log.debug("Carga incrementada para {}: {}/{}", empresa.getNombre(),
    empresa.getTrabajosHoy(), empresa.getCapacidadDiariaMax());
 }

 public void decrementarCarga(UUID id) {
  EmpresaServicio empresa = buscarOFallar(id);
  empresa.decrementarCarga();
  empresaRepository.guardar(empresa);
 }

 // ── Scheduler: reset de cupos a medianoche ────────────────
 @Scheduled(cron = "0 0 0 * * *", zone = "America/Lima")
 public void resetearCuposDiarios() {
  List<EmpresaServicio> todas = empresaRepository.buscarTodas();
  todas.forEach(EmpresaServicio::resetearCargaDiaria);
  empresaRepository.guardarTodas(todas);
  log.info("Cupos diarios reiniciados para {} empresas", todas.size());
 }

 private EmpresaServicio buscarOFallar(UUID id) {
  return empresaRepository.buscarPorId(id)
    .orElseThrow(() -> new ReparaYaException.RecursoNoEncontradoException("EmpresaServicio", id));
 }
}
