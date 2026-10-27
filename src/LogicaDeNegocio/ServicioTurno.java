package LogicaDeNegocio;

import Entity.EstadoTurno;
import Entity.Odontologo;
import Entity.Paciente;
import Entity.Turno;
import Excepciones.*;
import Persistencia.IRepositorio;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ServicioTurno {

    private final IRepositorio<Turno, Integer> repoTurno;
    private final IRepositorio<Paciente, String> repoPaciente;
    private final IRepositorio<Odontologo, String> repoOdonto;

    public ServicioTurno(IRepositorio<Turno, Integer> repoTurno,
                         IRepositorio<Paciente, String> repoPaciente,
                         IRepositorio<Odontologo, String> repoOdonto) {
        this.repoTurno = repoTurno;
        this.repoPaciente = repoPaciente;
        this.repoOdonto = repoOdonto;
    }

    /**
     * Crea un turno. Lanza:
     * - DatoInvalidoException si el turno tiene campos nulos.
     * - PacienteNoEncontradoException si el paciente no está registrado.
     * - OdontologoNoEncontradoException si el odontólogo no está registrado.
     * - TurnoYaReservadoException si el odontólogo ya tiene turno en ese horario.
     */
    public boolean crearTurno(Turno t) {
        if (t == null)
            throw new DatoInvalidoException("Turno", "no puede ser nulo");
        if (t.getPaciente() == null)
            throw new DatoInvalidoException("Paciente", "el turno debe tener un paciente");
        if (t.getOdontologo() == null)
            throw new DatoInvalidoException("Odontólogo", "el turno debe tener un odontólogo");
        if (t.getFecha() == null)
            throw new DatoInvalidoException("Fecha", "no puede ser nula");
        if (t.getHora() == null)
            throw new DatoInvalidoException("Hora", "no puede ser nula");

        if (repoPaciente.buscarPorId(t.getPaciente().getDni()) == null)
            throw new PacienteNoEncontradoException(t.getPaciente().getDni());

        if (repoOdonto.buscarPorId(t.getOdontologo().getMatricula()) == null)
            throw new OdontologoNoEncontradoException(t.getOdontologo().getMatricula());

        // Validar que el odontólogo no tenga otro turno en ese horario
        verificarHorarioLibre(t.getOdontologo(), t.getFecha(), t.getHora(), null);

        repoTurno.guardar(t);
        return true;
    }

    public Turno buscar(Integer id) {
        Turno t = repoTurno.buscarPorId(id);
        if (t == null)
            throw new DatoInvalidoException("ID de turno", "no se encontró ningún turno con ID: " + id);
        return t;
    }

    /**
     * Lista todos los turnos ordenados por fecha y hora.
     */
    public List<Turno> listar() {
        return repoTurno.listarTodos().stream()
                .sorted(Comparator.comparing(Turno::getFecha).thenComparing(Turno::getHora))
                .collect(Collectors.toList());
    }

    /**
     * Filtra turnos dentro de un rango de fechas (inclusivo).
     * Usa Stream API con filter.
     */
    public List<Turno> buscarPorRangoFechas(LocalDate desde, LocalDate hasta) {
        if (desde == null || hasta == null)
            throw new DatoInvalidoException("Rango de fechas", "las fechas no pueden ser nulas");
        if (desde.isAfter(hasta))
            throw new DatoInvalidoException("Rango de fechas", "la fecha 'desde' no puede ser posterior a 'hasta'");

        return repoTurno.listarTodos().stream()
                .filter(t -> !t.getFecha().isBefore(desde) && !t.getFecha().isAfter(hasta))
                .sorted(Comparator.comparing(Turno::getFecha).thenComparing(Turno::getHora))
                .collect(Collectors.toList());
    }

    /**
     * Filtra turnos por matrícula de odontólogo.
     */
    public List<Turno> buscarPorOdontologo(String matricula) {
        if (matricula == null || matricula.isBlank())
            throw new DatoInvalidoException("Matrícula", "no puede estar vacía");
        return repoTurno.listarTodos().stream()
                .filter(t -> t.getOdontologo().getMatricula().equals(matricula))
                .sorted(Comparator.comparing(Turno::getFecha).thenComparing(Turno::getHora))
                .collect(Collectors.toList());
    }

    /**
     * Filtra turnos por DNI del paciente.
     */
    public List<Turno> buscarPorPaciente(String dni) {
        if (dni == null || dni.isBlank())
            throw new DatoInvalidoException("DNI", "no puede estar vacío");
        return repoTurno.listarTodos().stream()
                .filter(t -> t.getPaciente().getDni().equals(dni))
                .sorted(Comparator.comparing(Turno::getFecha).thenComparing(Turno::getHora))
                .collect(Collectors.toList());
    }

    public boolean actualizarTurno(Integer id, LocalDate nuevaFecha, LocalTime nuevaHora) {
        Turno t = buscar(id);

        // excluye el turno actual de la comparación
        verificarHorarioLibre(t.getOdontologo(), nuevaFecha, nuevaHora, id);

        t.setFecha(nuevaFecha);
        t.setHora(nuevaHora);
        repoTurno.guardar(t);
        return true;
    }

    public boolean cambiarEstadoTurno(Integer id, EstadoTurno nuevoEstado) {
        Turno t = buscar(id);
        if (nuevoEstado == null)
            throw new DatoInvalidoException("Estado", "no puede ser nulo");
        t.setEstadoTurno(nuevoEstado);
        repoTurno.guardar(t);
        return true;
    }

    public boolean eliminar(Integer id) {
        buscar(id); // lanza la excepción si no existe
        repoTurno.eliminar(id);
        return true;
    }

    /**
     * Lanza TurnoYaReservadoException si el odontólogo ya tiene un turno en esa fecha y hora.
     * idExcluido: turno que no se compara (el que se está modificando). En un alta es null.
     * Antes esta lógica estaba copiada en crearTurno y actualizarTurno.
     */
    private void verificarHorarioLibre(Odontologo odontologo, LocalDate fecha, LocalTime hora, Integer idExcluido) {
        boolean horarioOcupado = repoTurno.listarTodos().stream()
                .filter(existente -> idExcluido == null || !existente.getId().equals(idExcluido))
                .anyMatch(existente ->
                        existente.getFecha().equals(fecha) &&
                        existente.getHora().equals(hora) &&
                        existente.getOdontologo().getMatricula().equals(odontologo.getMatricula()));

        if (horarioOcupado) {
            String nombre = odontologo.getNombre() + " " + odontologo.getApellido();
            throw new TurnoYaReservadoException(nombre, fecha.toString(), hora.toString());
        }
    }
}
