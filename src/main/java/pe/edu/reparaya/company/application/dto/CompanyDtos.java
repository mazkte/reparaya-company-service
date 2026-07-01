package pe.edu.reparaya.company.application.dto;

import jakarta.validation.constraints.*;
import pe.edu.reparaya.company.domain.model.CategoriaEnum;
import pe.edu.reparaya.company.domain.model.EmpresaEstadoEnum;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class CompanyDtos {

 public record CrearEmpresaRequest(

   @NotBlank(message = "El legalEntityName is required")
   @Size(max = 200)
   String nombre,

   @NotBlank(message = "El RUC is required")
   @Pattern(regexp = "^\\d{11}$", message = "RUC has debe tener 11 dígitos")
   String ruc,

   @NotBlank(message = "El email del coordinador es requerido")
   @Email(message = "Email inválido")
   String emailCoordinador,

   String whatsappCoordinador,

   @NotEmpty(message = "Debe indicar al menos una especialidad")
   List<CategoriaEnum> especialidades,

   @Min(value = 1, message = "La capacidad mínima es 1")
   @Max(value = 100, message = "La capacidad máxima es 100")
   int capacidadDiariaMax,

   LocalDate vigenciaContrato

 ) {
 }

 public record ActualizarCupoRequest(

   @Min(value = 1, message = "El cupo mínimo es 1")
   @Max(value = 100, message = "El cupo máximo es 100")
   int capacidadDiariaMax

 ) {
 }

 public record CambiarEstadoRequest(

   @NotNull(message = "El estado es requerido")
   EmpresaEstadoEnum estado

 ) {
 }

 // ─── RESPONSE DTOs ────────────────────────────────────────

 public record EmpresaResponse(
   UUID id,
   String nombre,
   String ruc,
   String emailCoordinador,
   String whatsappCoordinador,
   List<CategoriaEnum> especialidades,
   int capacidadDiariaMax,
   int trabajosHoy,
   int porcentajeCarga,
   EmpresaEstadoEnum estado,
   LocalDate vigenciaContrato,
   boolean contratoVigente,
   boolean contratoVenceProximamente
 ) {
 }

 public record DisponibilidadResponse(
   UUID empresaId,
   String nombre,
   String emailCoordinador,
   String whatsappCoordinador,
   int trabajosHoy,
   int capacidadDiariaMax,
   int porcentajeCarga
 ) {
 }
}
