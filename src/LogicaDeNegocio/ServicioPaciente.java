package LogicaDeNegocio;

import Entity.Domicilio;
import Entity.Paciente;
import Entity.Turno;
import Excepciones.DatoInvalidoException;
import Excepciones.PacienteNoEncontradoException;
import Excepciones.TieneTurnosAsociadosException;
import Persistencia.IRepositorio;

import java.util.List;
import java.util.stream.Collectors;

public class ServicioPaciente {

    private final IRepositorio<Paciente, String> repo;
    private final IRepositorio<Turno, Integer> repoTurno;   // para saber si el paciente tiene turnos

    public ServicioPaciente(IRepositorio<Paciente, String> repo, IRepositorio<Turno, Integer> repoTurno) {
        this.repo = repo;
        this.repoTurno = repoTurno;
    }

    public void registrarPaciente(Paciente p) {
        // Validaciones con DatoInvalidoException
        if (p.getDni() == null || p.getDni().isBlank())
            throw new DatoInvalidoException("DNI", "no puede estar vacío");
        if (!Validaciones.esDniValido(p.getDni()))
            throw new DatoInvalidoException("DNI", "debe contener entre 7 y 8 dígitos numéricos");
        validarDatosPersonales(p.getNombre(), p.getApellido(), p.getEmail());
        validarDomicilio(p.getDomicilio());

        if (repo.buscarPorId(p.getDni()) != null)
            throw new DatoInvalidoException("DNI", "ya existe un paciente con el DNI " + p.getDni());
        p.asignarId();
        repo.guardar(p);
    }

    public void eliminarPaciente(String dni) {
        if (repo.buscarPorId(dni) == null)
            throw new PacienteNoEncontradoException(dni);

        // No se permite la baja si el paciente tiene turnos (quedarían turnos sin paciente)
        int cantidadTurnos = 0;
        for (Turno t : repoTurno.listarTodos()) {
            if (t.getPaciente().getDni().equals(dni)) cantidadTurnos++;
        }
        if (cantidadTurnos > 0)
            throw new TieneTurnosAsociadosException("al paciente con DNI " + dni, cantidadTurnos);

        repo.eliminar(dni);
    }

    public void actualizarPaciente(String dni, String nuevoNombre, String nuevoApellido, String nuevoEmail,
                                   Domicilio nuevoDomicilio) {
        Paciente p = repo.buscarPorId(dni);
        if (p == null) throw new PacienteNoEncontradoException(dni);

        validarDatosPersonales(nuevoNombre, nuevoApellido, nuevoEmail);
        validarDomicilio(nuevoDomicilio);

        p.setNombre(nuevoNombre);
        p.setApellido(nuevoApellido);
        p.setEmail(nuevoEmail);
        p.setDomicilio(nuevoDomicilio);
        repo.guardar(p);
    }

    // ── Validaciones ──
    private void validarDatosPersonales(String nombre, String apellido, String email) {
        if (nombre == null || nombre.isBlank())
            throw new DatoInvalidoException("Nombre", "no puede estar vacío");
        if (!Validaciones.esNombreValido(nombre))
            throw new DatoInvalidoException("Nombre", "solo puede contener letras");
        if (apellido == null || apellido.isBlank())
            throw new DatoInvalidoException("Apellido", "no puede estar vacío");
        if (!Validaciones.esNombreValido(apellido))
            throw new DatoInvalidoException("Apellido", "solo puede contener letras");
        if (!Validaciones.esEmailValido(email))
            throw new DatoInvalidoException("Email", "debe tener un formato válido (ej. nombre@mail.com)");
    }

    private void validarDomicilio(Domicilio d) {
        if (d == null)
            throw new DatoInvalidoException("Domicilio", "es obligatorio");
        if (d.getCalle() == null || d.getCalle().isBlank())
            throw new DatoInvalidoException("Calle", "no puede estar vacía");
        if (!Validaciones.esNumeroCalleValido(d.getNumero()))
            throw new DatoInvalidoException("Número", "debe ser un número mayor a 0");
        if (d.getLocalidad() == null || d.getLocalidad().isBlank())
            throw new DatoInvalidoException("Localidad", "no puede estar vacía");
        if (d.getProvincia() == null || d.getProvincia().isBlank())
            throw new DatoInvalidoException("Provincia", "no puede estar vacía");
        if (!Validaciones.esCodigoPostalValido(d.getCodigoPostal()))
            throw new DatoInvalidoException("Código postal",
                    "debe tener 4 dígitos (ej. 1425) o formato CPA (ej. C1425ABC)");
    }

    /**
     * Busca paciente por DNI. Lanza PacienteNoEncontradoException si no existe.
     */
    public Paciente buscar(String dni) {
        if (dni == null || dni.isBlank())
            throw new DatoInvalidoException("DNI", "no puede estar vacío");
        Paciente p = repo.buscarPorId(dni);
        if (p == null) throw new PacienteNoEncontradoException(dni);
        return p;
    }

    /**
     * Lista todos los pacientes ordenados alfabéticamente por apellido (luego nombre).
     */
    public List<Paciente> listar() {
        return repo.listarTodos().stream()
                .sorted()
                .collect(Collectors.toList());
    }

    /**
     * Filtra pacientes cuyo apellido contenga el texto dado (búsqueda parcial, sin distinguir mayúsculas).
     */
    public List<Paciente> buscarPorApellido(String fragmento) {
        if (fragmento == null || fragmento.isBlank()) {
            throw new DatoInvalidoException(
                    "Apellido", "el texto de búsqueda no puede estar vacío");
        }

        String busqueda = fragmento.trim();

        return repo.listarTodos().stream()
                .filter(p -> p.getApellido() != null
                        && p.getApellido().toLowerCase(
                        java.util.Locale.ROOT).contains(
                        busqueda.toLowerCase(
                                java.util.Locale.ROOT)))
                .sorted()
                .collect(Collectors.toList());
    }
}