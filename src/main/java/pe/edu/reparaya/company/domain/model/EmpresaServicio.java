package pe.edu.reparaya.company.domain.model;

import pe.edu.reparaya.shared.exception.ReparaYaException;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Entidad de dominio EmpresaServicio.
 * Contiene la lógica de negocio pura — sin dependencias de frameworks.
 */
public class EmpresaServicio {

    private final UUID   id;
    private String       nombre;
    private String       ruc;
    private String       emailCoordinador;
    private String       whatsappCoordinador;
    private List<CategoriaEnum> especialidades;
    private int          capacidadDiariaMax;
    private int          trabajosHoy;
    private EmpresaEstadoEnum estado;
    private LocalDate    vigenciaContrato;

    private EmpresaServicio(UUID id, String nombre, String ruc,
                             String emailCoordinador, String whatsappCoordinador,
                             List<CategoriaEnum> especialidades, int capacidadDiariaMax,
                             int trabajosHoy, EmpresaEstadoEnum estado,
                             LocalDate vigenciaContrato) {
        this.id                  = id;
        this.nombre              = nombre;
        this.ruc                 = ruc;
        this.emailCoordinador    = emailCoordinador;
        this.whatsappCoordinador = whatsappCoordinador;
        this.especialidades      = especialidades;
        this.capacidadDiariaMax  = capacidadDiariaMax;
        this.trabajosHoy         = trabajosHoy;
        this.estado              = estado;
        this.vigenciaContrato    = vigenciaContrato;
    }

    public static EmpresaServicio crear(String nombre, String ruc,
                                         String emailCoordinador, String whatsappCoordinador,
                                         List<CategoriaEnum> especialidades,
                                         int capacidadDiariaMax, LocalDate vigenciaContrato) {
        return new EmpresaServicio(
                UUID.randomUUID(), nombre, ruc, emailCoordinador, whatsappCoordinador,
                especialidades, capacidadDiariaMax, 0, EmpresaEstadoEnum.ACTIVA, vigenciaContrato
        );
    }

    public static EmpresaServicio reconstituir(UUID id, String nombre, String ruc,
                                                String emailCoordinador, String whatsappCoordinador,
                                                List<CategoriaEnum> especialidades, int capacidadDiariaMax,
                                                int trabajosHoy, EmpresaEstadoEnum estado,
                                                LocalDate vigenciaContrato) {
        return new EmpresaServicio(id, nombre, ruc, emailCoordinador, whatsappCoordinador,
                especialidades, capacidadDiariaMax, trabajosHoy, estado, vigenciaContrato);
    }

    /**
     * Verifica si la empresa puede tomar un trabajo de la categoría dada.
     */
    public boolean esElegible(CategoriaEnum categoria) {
        return estado == EmpresaEstadoEnum.ACTIVA
                && especialidades.contains(categoria)
                && contratoVigente()
                && tieneCapacidad();
    }

    public boolean tieneCapacidad() {
        return trabajosHoy < capacidadDiariaMax;
    }

    public boolean contratoVigente() {
        if (vigenciaContrato == null) return false;
        return !vigenciaContrato.isBefore(LocalDate.now());
    }

    public boolean contratoVenceProximamente(int diasAlerta) {
        if (vigenciaContrato == null) return false;
        return !vigenciaContrato.isBefore(LocalDate.now())
                && vigenciaContrato.isBefore(LocalDate.now().plusDays(diasAlerta));
    }

    public int porcentajeCarga() {
        if (capacidadDiariaMax == 0) return 100;
        return (int) Math.round((double) trabajosHoy / capacidadDiariaMax * 100);
    }

    public void incrementarCarga() {
        if (!tieneCapacidad()) {
            throw new ReparaYaException.CapacidadExcedidaException(nombre);
        }
        this.trabajosHoy++;
    }

    public void decrementarCarga() {
        if (this.trabajosHoy > 0) this.trabajosHoy--;
    }

    public void resetearCargaDiaria() {
        this.trabajosHoy = 0;
    }

    public void activar() {
        this.estado = EmpresaEstadoEnum.ACTIVA;
    }

    public void desactivar() {
        this.estado = EmpresaEstadoEnum.INACTIVA;
    }

    public void suspender() {
        this.estado = EmpresaEstadoEnum.SUSPENDIDA;
    }

    public void actualizarCupo(int nuevoCupo) {
        if (nuevoCupo < 1) throw new IllegalArgumentException("El cupo debe ser mayor a 0");
        this.capacidadDiariaMax = nuevoCupo;
    }

    public UUID   getId()                  { return id; }
    public String getNombre()              { return nombre; }
    public String getRuc()                 { return ruc; }
    public String getEmailCoordinador()    { return emailCoordinador; }
    public String getWhatsappCoordinador() { return whatsappCoordinador; }
    public List<CategoriaEnum> getEspecialidades() { return especialidades; }
    public int    getCapacidadDiariaMax()  { return capacidadDiariaMax; }
    public int    getTrabajosHoy()         { return trabajosHoy; }
    public EmpresaEstadoEnum getEstado()   { return estado; }
    public LocalDate getVigenciaContrato() { return vigenciaContrato; }
}
