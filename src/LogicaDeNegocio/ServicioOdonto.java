package LogicaDeNegocio;

import Entity.Odontologo;
import Entity.Turno;
import Excepciones.DatoInvalidoException;
import Excepciones.OdontologoNoEncontradoException;
import Excepciones.TieneTurnosAsociadosException;
import Persistencia.IRepositorio;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ServicioOdonto {

    private final IRepositorio<Odontologo, String> repo;
    private final IRepositorio<Turno, Integer> repoTurno;   // para saber si el odontólogo tiene turnos

    public ServicioOdonto(IRepositorio<Odontologo, String> repo, IRepositorio<Turno, Integer> repoTurno) {
        this.repo = repo;
        this.repoTurno = repoTurno;
    }

    public void registrarOdontologo(Odontologo o) {
        if (o.getMatricula() == null || o.getMatricula().isBlank())
            throw new DatoInvalidoException("Matrícula", "no puede estar vacía");
        if (!o.getMatricula().matches("\\d+"))
            throw new DatoInvalidoException("Matrícula", "solo puede contener números");
        if (!Validaciones.esMatriculaValida(o.getMatricula()))
            throw new DatoInvalidoException("Matrícula", "no puede ser 0");
        validarNombreYApellido(o.getNombre(), o.getApellido());

        if (repo.buscarPorId(o.getMatricula()) != null)
            throw new DatoInvalidoException("Matrícula", "ya existe un odontólogo con la matrícula " + o.getMatricula());

        o.asignarId();
        repo.guardar(o);
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

    public void actualizarOdontologo(String matricula, String nuevoNombre, String nuevoApellido) {
        Odontologo o = repo.buscarPorId(matricula);
        if (o == null) throw new OdontologoNoEncontradoException(matricula);
        validarNombreYApellido(nuevoNombre, nuevoApellido);

        o.setNombre(nuevoNombre);
        o.setApellido(nuevoApellido);
        repo.guardar(o);
    }

    public void eliminar(String matricula) {
        if (repo.buscarPorId(matricula) == null)
            throw new OdontologoNoEncontradoException(matricula);

        // No se permite la baja si el odontólogo tiene turnos (quedarían turnos sin odontólogo)
        int cantidadTurnos = 0;
        for (Turno t : repoTurno.listarTodos()) {
            if (t.getOdontologo().getMatricula().equals(matricula)) cantidadTurnos++;
        }
        if (cantidadTurnos > 0)
            throw new TieneTurnosAsociadosException("al odontólogo con matrícula " + matricula, cantidadTurnos);

        repo.eliminar(matricula);
    }

    private void validarNombreYApellido(String nombre, String apellido) {
        if (nombre == null || nombre.isBlank())
            throw new DatoInvalidoException("Nombre", "no puede estar vacío");
        if (!Validaciones.esNombreValido(nombre))
            throw new DatoInvalidoException("Nombre", "solo puede contener letras");
        if (apellido == null || apellido.isBlank())
            throw new DatoInvalidoException("Apellido", "no puede estar vacío");
        if (!Validaciones.esNombreValido(apellido))
            throw new DatoInvalidoException("Apellido", "solo puede contener letras");
    }
}