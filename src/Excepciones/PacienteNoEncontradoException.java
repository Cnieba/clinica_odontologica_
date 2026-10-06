package Excepciones;


public class PacienteNoEncontradoException extends ClinicaException {

    public PacienteNoEncontradoException(String dni) {
        super("No se encontró ningún paciente con DNI: " + dni, "PAC-001");
    }
}
