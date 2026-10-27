package Excepciones;


public class PacienteConTurnoException extends ClinicaException {

    public PacienteConTurnoException(String nombrePaciente, String fecha, String hora) {
        super("El paciente " + nombrePaciente + " ya tiene un turno el " + fecha + " a las " + hora, "TUR-002");
    }
}
