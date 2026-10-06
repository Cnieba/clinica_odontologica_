package Excepciones;


public class TurnoYaReservadoException extends ClinicaException {

    public TurnoYaReservadoException(String nombreOdontologo, String fecha, String hora) {
        super("El odontólogo " + nombreOdontologo + " ya tiene un turno reservado el " + fecha + " a las " + hora, "TUR-001");
    }
}
