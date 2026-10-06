package Excepciones;

public class ClinicaException extends RuntimeException {

    private final String codigo;

    public ClinicaException(String mensaje, String codigo) {
        super(mensaje);
        this.codigo = codigo;
    }

    public String getCodigo() {
        return codigo;
    }

    @Override
    public String toString() {
        return "[" + codigo + "] " + getMessage();
    }
}
