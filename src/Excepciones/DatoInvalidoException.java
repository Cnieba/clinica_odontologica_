package Excepciones;


public class DatoInvalidoException extends ClinicaException {

    public DatoInvalidoException(String campo, String motivo) {
        super("Dato inválido en el campo '" + campo + "': " + motivo, "DAT-001");
    }
}
