package Controller;

import Entity.*;
import LogicaDeNegocio.ServicioOdonto;

import java.util.List;

/**
 * Controlador (GRASP / MVC) de odontólogos.
 * Es el Creador de los odontólogos según la especialidad elegida en la vista.
 * (Próxima entrega: este switch se reemplaza por un Factory.)
 */
public class OdontologoController {

    private final ServicioOdonto servicio;

    public OdontologoController(ServicioOdonto servicio) {
        this.servicio = servicio;
    }

    public void registrarOdontologo(String nombre, String apellido, String matricula, String especialidad) {
        Odontologo o;
        switch (especialidad) {
            case "Endodoncista":   o = new Endodoncista(nombre, apellido, matricula);   break;
            case "Ortodoncista":   o = new Ortodoncista(nombre, apellido, matricula);   break;
            case "Periodoncista":  o = new Periodoncista(nombre, apellido, matricula);  break;
            case "Odontopediatra": o = new Odontopediatra(nombre, apellido, matricula); break;
            default:               o = new Odontologo(nombre, apellido, matricula);
        }
        servicio.registrarOdontologo(o);
    }

    public void modificarOdontologo(String matricula, String nombre, String apellido) {
        servicio.actualizarOdontologo(matricula, nombre, apellido);
    }

    public void eliminarOdontologo(String matricula) {
        servicio.eliminar(matricula);
    }

    public List<Odontologo> listarOdontologos() {
        return servicio.listar();
    }
}
