package Excepciones;


public class TieneTurnosAsociadosException extends ClinicaException {

    public TieneTurnosAsociadosException(String quien, int cantidadTurnos) {
        super("No se puede eliminar " + quien + " porque tiene " + cantidadTurnos
                + " turno(s) registrado(s). Elimine primero sus turnos.", "ELI-001");
    }
}
