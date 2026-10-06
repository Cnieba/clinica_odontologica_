package Entity;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

public class Turno implements Serializable {

    private static final long serialVersionUID = 1L;
    private static int contador = 0;
    private Integer id;
    private Paciente paciente;
    private Odontologo odontologo;
    private LocalDate fecha;
    private LocalTime hora;
    private EstadoTurno estadoTurno;

    public Turno(Paciente paciente, Odontologo odontologo, LocalDate fecha, LocalTime hora, EstadoTurno estadoTurno) {
        this.id = ++contador;
        this.paciente = paciente;
        this.odontologo = odontologo;
        this.fecha = fecha;
        this.hora = hora;
        this.estadoTurno = estadoTurno;
    }

    public static void sincronizarContador(int max) {
        if (max > contador) contador = max;
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
