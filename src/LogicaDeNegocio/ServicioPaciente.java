package LogicaDeNegocio;

import Entity.Domicilio;
import Entity.Paciente;
import Excepciones.DatoInvalidoException;
import Excepciones.PacienteNoEncontradoException;
import Persistencia.IRepositorio;

import java.util.List;
import java.util.stream.Collectors;

public class ServicioPaciente {

    private final IRepositorio<Paciente, String> repo;

    public ServicioPaciente(IRepositorio<Paciente, String> repo) {
        this.repo = repo;
    }

    public boolean registrarPaciente(Paciente p) {
        // Validaciones con DatoInvalidoException
        if (p.getDni() == null || p.getDni().isBlank())
            throw new DatoInvalidoException("DNI", "no puede estar vacío");
        if (!p.getDni().matches("\\d{7,8}"))
            throw new DatoInvalidoException("DNI", "debe contener entre 7 y 8 dígitos numéricos");
        validarDatosPersonales(p.getNombre(), p.getApellido(), p.getEmail());
        validarDomicilio(p.getDomicilio());

        if (repo.buscarPorId(p.getDni()) != null)
            throw new DatoInvalidoException("DNI", "ya existe un paciente con el DNI " + p.getDni());
        repo.guardar(p);
        return true;
    }

    public void eliminarPaciente(String dni) {
        if (repo.buscarPorId(dni) == null)
            throw new PacienteNoEncontradoException(dni);
        repo.eliminar(dni);
    }

    public boolean actualizarPaciente(String dni, String nuevoNombre, String nuevoApellido, String nuevoEmail,
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
        return true;
    }

    // ── Validaciones (antes estaban repetidas en registrar y actualizar) ──
    private void validarDatosPersonales(String nombre, String apellido, String email) {
        if (nombre == null || nombre.isBlank())
            throw new DatoInvalidoException("Nombre", "no puede estar vacío");
        if (apellido == null || apellido.isBlank())
            throw new DatoInvalidoException("Apellido", "no puede estar vacío");
        if (email == null || !email.contains("@"))
            throw new DatoInvalidoException("Email", "debe contener '@'");
    }

    private void validarDomicilio(Domicilio d) {
        if (d == null)
            throw new DatoInvalidoException("Domicilio", "es obligatorio");
        if (d.getCalle() == null || d.getCalle().isBlank())
            throw new DatoInvalidoException("Calle", "no puede estar vacía");
        if (d.getNumero() == null || !d.getNumero().matches("\\d+"))
            throw new DatoInvalidoException("Número", "solo puede contener números");
        if (d.getLocalidad() == null || d.getLocalidad().isBlank())
            throw new DatoInvalidoException("Localidad", "no puede estar vacía");
        if (d.getProvincia() == null || d.getProvincia().isBlank())
            throw new DatoInvalidoException("Provincia", "no puede estar vacía");
        if (!esCodigoPostalValido(d.getCodigoPostal()))
            throw new DatoInvalidoException("Código postal",
                    "debe tener 4 dígitos (ej. 1425) o formato CPA (ej. C1425ABC)");
    }

    /** CP viejo de 4 dígitos (1425) o CPA argentino: letra + 4 dígitos + 3 letras (C1425ABC). */
    public static boolean esCodigoPostalValido(String cp) {
        if (cp == null) return false;
        return cp.matches("\\d{4}") || cp.matches("[A-Za-z]\\d{4}[A-Za-z]{3}");
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
     * Usa Stream API + Comparable implementado en Paciente.
     */
    public List<Paciente> listar() {
        return repo.listarTodos().stream()
                .sorted()                    // usa Paciente.compareTo()
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
