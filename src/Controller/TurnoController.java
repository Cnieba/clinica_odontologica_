package Controller;

import Entity.*;
import LogicaDeNegocio.ServicioOdonto;
import LogicaDeNegocio.ServicioPaciente;
import LogicaDeNegocio.ServicioTurno;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Controlador (GRASP / MVC) de turnos.
 * Convierte los textos de la vista (fecha, hora) a objetos, crea el Turno (Creador)
 * y delega en los servicios. Si la fecha u hora tienen mal formato, se propaga
 * DateTimeParseException para que la vista muestre el mismo mensaje de antes.
 */
public class TurnoController {

    private final ServicioTurno servTur;
    private final ServicioPaciente servPac;
    private final ServicioOdonto servOdo;

    public TurnoController(ServicioTurno servTur, ServicioPaciente servPac, ServicioOdonto servOdo) {
        this.servTur = servTur;
        this.servPac = servPac;
        this.servOdo = servOdo;
    }

    public void crearTurno(String dniPaciente, String matriculaOdontologo, String fechaTexto, String horaTexto) {
        Paciente p = servPac.buscar(dniPaciente);
        Odontologo o = servOdo.buscar(matriculaOdontologo);
        LocalDate fecha = LocalDate.parse(fechaTexto);
        LocalTime hora = LocalTime.parse(horaTexto);

        Turno t = new Turno(p, o, fecha, hora, EstadoTurno.PENDIENTE);
        servTur.crearTurno(t);
    }

    public void modificarTurno(Integer id, String fechaTexto, String horaTexto, EstadoTurno estado) {
        LocalDate fecha = LocalDate.parse(fechaTexto);
        LocalTime hora = LocalTime.parse(horaTexto);
        servTur.actualizarTurno(id, fecha, hora);
        servTur.cambiarEstadoTurno(id, estado);
    }

    public void eliminarTurno(Integer id) {
        servTur.eliminar(id);
    }

    public List<Turno> listarTurnos() {
        return servTur.listar();
    }

    public List<Turno> buscarPorRango(String desdeTexto, String hastaTexto) {
        LocalDate desde = LocalDate.parse(desdeTexto);
        LocalDate hasta = LocalDate.parse(hastaTexto);
        return servTur.buscarPorRangoFechas(desde, hasta);
    }

    public List<Turno> buscarPorPaciente(String dni) {
        return servTur.buscarPorPaciente(dni);
    }

    public List<Turno> buscarPorOdontologo(String matricula) {
        return servTur.buscarPorOdontologo(matricula);
    }

    // Para llenar los combos de la vista
    public List<Paciente> listarPacientes() {
        return servPac.listar();
    }

    public List<Odontologo> listarOdontologos() {
        return servOdo.listar();
    }
}
