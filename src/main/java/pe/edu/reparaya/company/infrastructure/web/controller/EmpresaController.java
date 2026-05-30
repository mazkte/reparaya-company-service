package pe.edu.reparaya.company.infrastructure.web.controller;

 import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pe.edu.reparaya.company.application.dto.CompanyDtos.*;
import pe.edu.reparaya.company.application.usecase.EmpresaUseCase;
import pe.edu.reparaya.company.domain.model.CategoriaEnum;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/companies")
@RequiredArgsConstructor
@Tag(name = "Empresas", description = "Gestión de empresas contratadas de servicios")
public class EmpresaController {

    private final EmpresaUseCase empresaUseCase;

    // ── GET /api/companies ────────────────────────────────────

    @GetMapping
    @PreAuthorize("hasAnyRole('ROLE_AUTORIDAD', 'ROLE_ADMIN', 'ROLE_SUPERVISOR')")
    @Operation(summary = "Listar todas las empresas")
    public ResponseEntity<List<EmpresaResponse>> listarTodas() {
        return ResponseEntity.ok(empresaUseCase.listarTodas());
    }

    // ── GET /api/companies/{id} ───────────────────────────────

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_AUTORIDAD', 'ROLE_ADMIN', 'ROLE_SUPERVISOR', 'ROLE_EMPRESA')")
    @Operation(summary = "Obtener empresa por ID")
    public ResponseEntity<EmpresaResponse> obtenerPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(empresaUseCase.obtenerPorId(id));
    }

    // ── GET /api/companies/available/{categoria} ──────────────

    @GetMapping("/available/{categoria}")
    @PreAuthorize("hasAnyRole('ROLE_AUTORIDAD', 'ROLE_ADMIN')")
    @Operation(summary = "Obtener empresas disponibles por categoría",
               description = "Retorna empresas activas con capacidad y contrato vigente para la categoría dada")
    public ResponseEntity<List<DisponibilidadResponse>> disponibles(
            @PathVariable CategoriaEnum categoria) {
        return ResponseEntity.ok(empresaUseCase.obtenerDisponiblesPorCategoria(categoria));
    }

    // ── POST /api/companies ───────────────────────────────────

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Registrar nueva empresa contratada")
    public ResponseEntity<EmpresaResponse> crear(
            @Valid @RequestBody CrearEmpresaRequest request) {
        EmpresaResponse response = empresaUseCase.crearEmpresa(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ── PATCH /api/companies/{id}/quota ──────────────────────

    @PatchMapping("/{id}/quota")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Actualizar cupo diario de la empresa")
    public ResponseEntity<EmpresaResponse> actualizarCupo(
            @PathVariable UUID id,
            @Valid @RequestBody ActualizarCupoRequest request) {
        return ResponseEntity.ok(empresaUseCase.actualizarCupo(id, request));
    }

    // ── PATCH /api/companies/{id}/status ─────────────────────

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Cambiar estado de la empresa (ACTIVA | INACTIVA | SUSPENDIDA)")
    public ResponseEntity<EmpresaResponse> cambiarEstado(
            @PathVariable UUID id,
            @Valid @RequestBody CambiarEstadoRequest request) {
        return ResponseEntity.ok(empresaUseCase.cambiarEstado(id, request));
    }

    // ── POST /api/companies/{id}/increment-load ───────────────
    // Llamado internamente por worker-service cuando asigna un trabajo

    @PostMapping("/{id}/increment-load")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_AUTORIDAD')")
    @Operation(summary = "Incrementar carga diaria (uso interno de worker-service)")
    public ResponseEntity<Void> incrementarCarga(@PathVariable UUID id) {
        empresaUseCase.incrementarCarga(id);
        return ResponseEntity.noContent().build();
    }

    // ── POST /api/companies/{id}/decrement-load ───────────────

    @PostMapping("/{id}/decrement-load")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_AUTORIDAD')")
    @Operation(summary = "Decrementar carga diaria (uso interno)")
    public ResponseEntity<Void> decrementarCarga(@PathVariable UUID id) {
        empresaUseCase.decrementarCarga(id);
        return ResponseEntity.noContent().build();
    }
}
