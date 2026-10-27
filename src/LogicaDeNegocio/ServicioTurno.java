package LogicaDeNegocio;

import Entity.EstadoTurno;
import Entity.Odontologo;
import Entity.Paciente;
import Entity.Turno;
import Excepciones.*;
import Persistencia.IRepositorio;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ServicioTurno {

    // Horario de atención de la clínica: lunes a sábado de 08:00 a 20:00
    private static final LocalTime HORA_APERTURA = LocalTime.of(8, 0);
    private static final LocalTime HORA_CIERRE = LocalTime.of(20, 0);

    private final IRepositorio<Turno, Integer> repoTurno;
    private final IRepositorio<Paciente, String> repoPaciente;
    private final IRepositorio<Odontologo, String> repoOdonto;

    public ServicioTurno(IRepositorio<Turno, Integer> repoTurno,
                         IRepositorio<Paciente, String> repoPaciente,
                         IRepositorio<Odontologo, String> repoOdonto) {
        this.repoTurno = repoTurno;
        this.repoPaciente = repoPaciente;
        this.repoOdonto = repoOdonto;
        enlazarTurnosConPacientesYOdontologos();
    }

    /**
     * Cada archivo .dat guarda su propia copia de los objetos. Al iniciar, cada turno se vuelve a
     * enlazar con el paciente y el odontólogo "reales" de sus repositorios, para que los cambios
     * que se hagan después (por ejemplo, el nombre de un paciente) se vean también en sus turnos.
     */
    private void enlazarTurnosConPacientesYOdontologos() {
        for (Turno t : repoTurno.listarTodos()) {
            Paciente paciente = repoPaciente.buscarPorId(t.getPaciente().getDni());
            if (paciente != null) t.setPaciente(paciente);

            Odontologo odontologo = repoOdonto.buscarPorId(t.getOdontologo().getMatricula());
            if (odontologo != null) t.setOdontologo(odontologo);
        }
    }

    /**
     * Crea un turno. Lanza:
     * - DatoInvalidoException si el turno tiene campos nulos, es en el pasado o fuera del horario de atención.
     * - PacienteNoEncontradoException si el paciente no está registrado.
     * - OdontologoNoEncontradoException si el odontólogo no está registrado.
     * - TurnoYaReservadoException si el odontólogo ya tiene un turno que se superpone.
     * - PacienteConTurnoException si el paciente ya tiene un turno que se superpone.
     */
    public void crearTurno(Turno t) {
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

        validarFechaYHorario(t.getFecha(), t.getHora(), t.getDuracion());
        verificarHorarioLibre(t.getPaciente(), t.getOdontologo(), t.getFecha(), t.getHora(), null);

        t.asignarId();
        repoTurno.guardar(t);
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
     * Filtra turnos por matrícula de odontólogo. Avisa si el odontólogo no existe.
     */
    public List<Turno> buscarPorOdontologo(String matricula) {
        if (matricula == null || matricula.isBlank())
            throw new DatoInvalidoException("Matrícula", "no puede estar vacía");
        if (repoOdonto.buscarPorId(matricula) == null)
            throw new OdontologoNoEncontradoException(matricula);
        return repoTurno.listarTodos().stream()
                .filter(t -> t.getOdontologo().getMatricula().equals(matricula))
                .sorted(Comparator.comparing(Turno::getFecha).thenComparing(Turno::getHora))
                .collect(Collectors.toList());
    }

    /**
     * Filtra turnos por DNI del paciente. Avisa si el paciente no existe.
     */
    public List<Turno> buscarPorPaciente(String dni) {
        if (dni == null || dni.isBlank())
            throw new DatoInvalidoException("DNI", "no puede estar vacío");
        if (repoPaciente.buscarPorId(dni) == null)
            throw new PacienteNoEncontradoException(dni);
        return repoTurno.listarTodos().stream()
                .filter(t -> t.getPaciente().getDni().equals(dni))
                .sorted(Comparator.comparing(Turno::getFecha).thenComparing(Turno::getHora))
                .collect(Collectors.toList());
    }

    public void actualizarTurno(Integer id, LocalDate nuevaFecha, LocalTime nuevaHora) {
        Turno t = buscar(id);

        // Si la fecha y la hora no cambian no hay nada que validar
        // (así se puede, por ejemplo, marcar como COMPLETADO un turno que ya pasó)
        if (nuevaFecha.equals(t.getFecha()) && nuevaHora.equals(t.getHora())) return;

        validarFechaYHorario(nuevaFecha, nuevaHora, t.getDuracion());
        // excluye el turno actual de la comparación
        verificarHorarioLibre(t.getPaciente(), t.getOdontologo(), nuevaFecha, nuevaHora, id);

        t.setFecha(nuevaFecha);
        t.setHora(nuevaHora);
        repoTurno.guardar(t);
    }

    public void cambiarEstadoTurno(Integer id, EstadoTurno nuevoEstado) {
        Turno t = buscar(id);
        if (nuevoEstado == null)
            throw new DatoInvalidoException("Estado", "no puede ser nulo");

        // Un turno cancelado no ocupa su horario: si se reactiva, ese horario tiene que seguir libre
        if (t.estaCancelado() && nuevoEstado != EstadoTurno.CANCELADO)
            verificarHorarioLibre(t.getPaciente(), t.getOdontologo(), t.getFecha(), t.getHora(), id);

        t.setEstadoTurno(nuevoEstado);
        repoTurno.guardar(t);
    }

    public void eliminar(Integer id) {
        buscar(id); // lanza la excepción si no existe
        repoTurno.eliminar(id);
    }

    /**
     * El turno no puede ser en el pasado y debe estar dentro del horario de atención
     * (lunes a sábado, empezando desde las 08:00 y terminando, según su duración, hasta las 20:00).
     */
    private void validarFechaYHorario(LocalDate fecha, LocalTime hora, int duracionMinutos) {
        if (LocalDateTime.of(fecha, hora).isBefore(LocalDateTime.now()))
            throw new DatoInvalidoException("Fecha", "no se pueden reservar turnos en el pasado");
        if (fecha.getDayOfWeek() == DayOfWeek.SUNDAY)
            throw new DatoInvalidoException("Fecha", "la clínica atiende de lunes a sábado");
        if (hora.isBefore(HORA_APERTURA) || hora.isAfter(HORA_CIERRE.minusMinutes(duracionMinutos)))
            throw new DatoInvalidoException("Hora", "el turno debe ser entre las " + HORA_APERTURA + " y las "
                    + HORA_CIERRE + " (esta consulta dura " + duracionMinutos + " min)");
    }

    /**
     * Revisa que ni el odontólogo ni el paciente tengan otro turno que se superponga con el nuevo,
     * teniendo en cuenta la duración de cada consulta. Los turnos CANCELADOS no ocupan horario.
     * idExcluido: turno que no se compara (el que se está modificando). En un alta es null.
     */
    private void verificarHorarioLibre(Paciente paciente, Odontologo odontologo, LocalDate fecha,
                                       LocalTime inicio, Integer idExcluido) {
        LocalTime fin = inicio.plusMinutes(odontologo.calcularDuracionTurno());

        for (Turno existente : repoTurno.listarTodos()) {
            if (idExcluido != null && existente.getId().equals(idExcluido)) continue;
            if (existente.estaCancelado()) continue;
            if (!existente.seSuperponeCon(fecha, inicio, fin)) continue;

            String rango = existente.getHora() + " (hasta las " + existente.getHoraFin() + ")";
            if (existente.getOdontologo().getMatricula().equals(odontologo.getMatricula())) {
                String nombre = odontologo.getNombre() + " " + odontologo.getApellido();
                throw new TurnoYaReservadoException(nombre, fecha.toString(), rango);
            }
            if (existente.getPaciente().getDni().equals(paciente.getDni())) {
                String nombre = paciente.getNombre() + " " + paciente.getApellido();
                throw new PacienteConTurnoException(nombre, fecha.toString(), rango);
            }
        }
    }
}
