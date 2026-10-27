package Controller;

import Entity.Domicilio;
import Entity.Paciente;
import LogicaDeNegocio.ServicioPaciente;
import LogicaDeNegocio.Validaciones;

import java.time.LocalDate;
import java.util.List;

/**
 * Controlador (GRASP / MVC) de pacientes.
 * Recibe los datos que carga la vista, crea los objetos del modelo (Creador)
 * y delega las reglas de negocio en ServicioPaciente.
 */
public class PacienteController {

    private final ServicioPaciente servicio;

    public PacienteController(ServicioPaciente servicio) {
        this.servicio = servicio;
    }

    public void registrarPaciente(String nombre, String apellido, String dni, String email,
                                  String calle, String numero, String localidad, String provincia,
                                  String codigoPostal) {
        Domicilio domicilio = new Domicilio(calle, numero, localidad, provincia, codigoPostal.toUpperCase());
        Paciente p = new Paciente(nombre, apellido, dni, email, LocalDate.now(), domicilio);
        servicio.registrarPaciente(p);
    }

    public void modificarPaciente(String dni, String nombre, String apellido, String email,
                                  String calle, String numero, String localidad, String provincia,
                                  String codigoPostal) {
        Domicilio domicilio = new Domicilio(calle, numero, localidad, provincia, codigoPostal.toUpperCase());
        servicio.actualizarPaciente(dni, nombre, apellido, email, domicilio);
    }

    public void eliminarPaciente(String dni) {
        servicio.eliminarPaciente(dni);
    }

    public Paciente buscarPaciente(String dni) {
        return servicio.buscar(dni);
    }

    public List<Paciente> listarPacientes() {
        return servicio.listar();
    }

    public List<Paciente> buscarPorApellido(String texto) {
        return servicio.buscarPorApellido(texto);
    }

    // Reglas de formato para que la vista marque en rojo los campos inválidos
    public boolean esNombreValido(String texto) {
        return Validaciones.esNombreValido(texto);
    }

    public boolean esEmailValido(String email) {
        return Validaciones.esEmailValido(email);
    }

    public boolean esDniValido(String dni) {
        return Validaciones.esDniValido(dni);
    }

    public boolean esNumeroCalleValido(String numero) {
        return Validaciones.esNumeroCalleValido(numero);
    }

    public boolean esCodigoPostalValido(String cp) {
        return Validaciones.esCodigoPostalValido(cp);
    }
}
