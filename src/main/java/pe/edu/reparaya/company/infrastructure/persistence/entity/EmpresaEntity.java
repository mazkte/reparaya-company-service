package pe.edu.reparaya.company.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import pe.edu.reparaya.company.domain.model.CategoriaEnum;
import pe.edu.reparaya.company.domain.model.EmpresaEstadoEnum;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(
    name = "empresa_servicio",
    schema = "companies",
    uniqueConstraints = @UniqueConstraint(name = "uk_empresa_ruc", columnNames = "ruc")
)
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmpresaEntity {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "nombre", nullable = false, length = 200)
    private String nombre;

    @Column(name = "ruc", nullable = false, length = 11, unique = true)
    private String ruc;

    @Column(name = "email_coordinador", nullable = false)
    private String emailCoordinador;

    @Column(name = "whatsapp_coordinador")
    private String whatsappCoordinador;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
        name = "empresa_especialidad",
        schema = "companies",
        joinColumns = @JoinColumn(name = "empresa_id")
    )
    @Column(name = "categoria")
    @Enumerated(EnumType.STRING)
    private List<CategoriaEnum> especialidades;

    @Column(name = "capacidad_diaria_max", nullable = false)
    private int capacidadDiariaMax;

    @Column(name = "trabajos_hoy", nullable = false)
    private int trabajosHoy;

    @Column(name = "estado", nullable = false)
    @Enumerated(EnumType.STRING)
    private EmpresaEstadoEnum estado;

    @Column(name = "vigencia_contrato")
    private LocalDate vigenciaContrato;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    @PrePersist
    void prePersist() {
        if (id == null) id = UUID.randomUUID();
        fechaCreacion     = LocalDateTime.now();
        fechaActualizacion = LocalDateTime.now();
        if (trabajosHoy < 0) trabajosHoy = 0;
    }

    @PreUpdate
    void preUpdate() {
        fechaActualizacion = LocalDateTime.now();
    }
}
