package LogicaDeNegocio;

import Entity.Odontologo;
import Excepciones.DatoInvalidoException;
import Excepciones.OdontologoNoEncontradoException;
import Persistencia.IRepositorio;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ServicioOdonto {

    private IRepositorio<Odontologo, String> repo;

    public ServicioOdonto(IRepositorio<Odontologo, String> repo) {
        this.repo = repo;
    }

    public boolean registrarOdontologo(Odontologo o) {
        if (o.getMatricula() == null || o.getMatricula().isBlank())
            throw new DatoInvalidoException("Matrícula", "no puede estar vacía");
        if (!o.getMatricula().matches("\\d+"))
            throw new DatoInvalidoException("Matrícula", "solo puede contener números");
        if (o.getNombre() == null || o.getNombre().isBlank())
            throw new DatoInvalidoException("Nombre", "no puede estar vacío");
        if (o.getApellido() == null || o.getApellido().isBlank())
            throw new DatoInvalidoException("Apellido", "no puede estar vacío");

        if (repo.buscarPorId(o.getMatricula()) != null)
            throw new DatoInvalidoException("Matrícula", "ya existe un odontólogo con la matrícula " + o.getMatricula());
        repo.guardar(o);
        return true;
    }

    /**
     * Busca odontólogo por matrícula. Lanza OdontologoNoEncontradoException si no existe.
     */
    public Odontologo buscar(String matricula) {
        if (matricula == null || matricula.isBlank())
            throw new DatoInvalidoException("Matrícula", "no puede estar vacía");
        Odontologo o = repo.buscarPorId(matricula);
        if (o == null) throw new OdontologoNoEncontradoException(matricula);
        return o;
    }

    /**
     * Lista todos los odontólogos ordenados por apellido usando Comparator.
     */
    public List<Odontologo> listar() {
        return repo.listarTodos().stream()
                .sorted(Comparator.comparing(Odontologo::getApellido, String.CASE_INSENSITIVE_ORDER)
                        .thenComparing(Odontologo::getNombre, String.CASE_INSENSITIVE_ORDER))
                .collect(Collectors.toList());
    }

    public boolean actualizarOdontologo(String matricula, String nuevoNombre, String nuevoApellido) {
        Odontologo o = repo.buscarPorId(matricula);
        if (o == null) throw new OdontologoNoEncontradoException(matricula);
        if (nuevoNombre == null || nuevoNombre.isBlank())
            throw new DatoInvalidoException("Nombre", "no puede estar vacío");
        if (nuevoApellido == null || nuevoApellido.isBlank())
            throw new DatoInvalidoException("Apellido", "no puede estar vacío");
        o.setNombre(nuevoNombre);
        o.setApellido(nuevoApellido);
        repo.guardar(o);
        return true;
    }

    public void eliminar(String matricula) {
        if (repo.buscarPorId(matricula) == null)
            throw new OdontologoNoEncontradoException(matricula);
        repo.eliminar(matricula);
    }
}
