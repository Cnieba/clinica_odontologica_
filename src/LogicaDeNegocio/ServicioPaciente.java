package LogicaDeNegocio;

import Entity.Paciente;
import Excepciones.DatoInvalidoException;
import Excepciones.PacienteNoEncontradoException;
import Persistencia.IRepositorio;

import java.util.List;
import java.util.stream.Collectors;

public class ServicioPaciente {

    private IRepositorio<Paciente, String> repo;

    public ServicioPaciente(IRepositorio<Paciente, String> repo) {
        this.repo = repo;
    }

    public boolean registrarPaciente(Paciente p) {
        // Validaciones con DatoInvalidoException
        if (p.getDni() == null || p.getDni().isBlank())
            throw new DatoInvalidoException("DNI", "no puede estar vacío");
        if (!p.getDni().matches("\\d{7,8}"))
            throw new DatoInvalidoException("DNI", "debe contener entre 7 y 8 dígitos numéricos");
        if (p.getNombre() == null || p.getNombre().isBlank())
            throw new DatoInvalidoException("Nombre", "no puede estar vacío");
        if (p.getApellido() == null || p.getApellido().isBlank())
            throw new DatoInvalidoException("Apellido", "no puede estar vacío");
        if (p.getEmail() == null || !p.getEmail().contains("@"))
            throw new DatoInvalidoException("Email", "debe contener '@'");

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

    public boolean actualizarPaciente(String dni, String nuevoNombre, String nuevoApellido, String nuevoEmail) {
        Paciente p = repo.buscarPorId(dni);
        if (p == null) throw new PacienteNoEncontradoException(dni);

        if (nuevoNombre == null || nuevoNombre.isBlank())
            throw new DatoInvalidoException("Nombre", "no puede estar vacío");
        if (nuevoApellido == null || nuevoApellido.isBlank())
            throw new DatoInvalidoException("Apellido", "no puede estar vacío");
        if (nuevoEmail == null || !nuevoEmail.contains("@"))
            throw new DatoInvalidoException("Email", "debe contener '@'");

        p.setNombre(nuevoNombre);
        p.setApellido(nuevoApellido);
        p.setEmail(nuevoEmail);
        repo.guardar(p);
        return true;
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
        return repo.listarTodos().stream()
                .filter(p -> p.getApellido().toLowerCase().contains(fragmento.toLowerCase()))
                .sorted()
                .collect(Collectors.toList());
    }
}
