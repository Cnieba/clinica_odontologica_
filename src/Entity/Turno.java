package Entity;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

public class Turno implements Serializable {

    private static final long serialVersionUID = 1L;
    private static int contador = 0;
    private Integer id;
    private Paciente paciente;
    private Odontologo odontologo;
    private LocalDate fecha;
    private LocalTime hora;
    private EstadoTurno estadoTurno;

    // El ID se asigna recién al guardar el turno (asignarId), así un turno rechazado no consume un número
    public Turno(Paciente paciente, Odontologo odontologo, LocalDate fecha, LocalTime hora, EstadoTurno estadoTurno) {
        this.paciente = Objects.requireNonNull(paciente, "El paciente es obligatorio");
        this.odontologo = Objects.requireNonNull(odontologo, "El odontólogo es obligatorio");
        this.fecha = Objects.requireNonNull(fecha, "La fecha es obligatoria");
        this.hora = Objects.requireNonNull(hora, "La hora es obligatoria");
        this.estadoTurno = Objects.requireNonNull(estadoTurno, "El estado del turno es obligatorio");
    }

    public static void sincronizarContador(int max) {
        if (max > contador) contador = max;
    }

    public void asignarId() {
        if (id == null) id = ++contador;
    }

    public Integer getId() { return id; }
    public Paciente getPaciente() { return paciente; }
    public void setPaciente(Paciente paciente) { this.paciente = paciente; }
    public Odontologo getOdontologo() { return odontologo; }
    public void setOdontologo(Odontologo odontologo) { this.odontologo = odontologo; }
    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    public LocalTime getHora() { return hora; }
    public void setHora(LocalTime hora) { this.hora = hora; }
    public EstadoTurno getEstadoTurno() { return estadoTurno; }
    public void setEstadoTurno(EstadoTurno estadoTurno) { this.estadoTurno = estadoTurno; }
    public int getDuracion() { return odontologo.calcularDuracionTurno(); }

    // Experto en información: el turno sabe a qué hora termina y si choca con otro horario
    public LocalTime getHoraFin() { return hora.plusMinutes(getDuracion()); }

    public boolean estaCancelado() { return estadoTurno == EstadoTurno.CANCELADO; }

    /** true si este turno ocupa algún minuto del rango [otroInicio, otroFin) de esa fecha. */
    public boolean seSuperponeCon(LocalDate otraFecha, LocalTime otroInicio, LocalTime otroFin) {
        if (!fecha.equals(otraFecha)) return false;
        return hora.isBefore(otroFin) && otroInicio.isBefore(getHoraFin());
    }

    @Override
    public String toString() {
        return "Turno: " +
                "ID: " + id +
                ", Paciente: " + paciente.getNombre() + " " + paciente.getApellido() +
                ", Odontologo: " + odontologo.getNombre() + " " + odontologo.getApellido() + "(" + odontologo.getEspecialidad() + ")" +
                ", Fecha: " + fecha +
                ", Hora: " + hora +
                ", Duración: " + getDuracion() + " min" +
                ", Estado: " + estadoTurno;
    }
}
