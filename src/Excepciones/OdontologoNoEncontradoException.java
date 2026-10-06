package Excepciones;


public class OdontologoNoEncontradoException extends ClinicaException {

    public OdontologoNoEncontradoException(String matricula) {
        super("No se encontró ningún odontólogo con matrícula: " + matricula, "ODO-001");
    }
}
