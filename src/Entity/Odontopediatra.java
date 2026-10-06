package Entity;

import java.io.Serializable;

public class Odontopediatra extends Odontologo{
    public Odontopediatra(String nombre, String apellido, String matricula){
        super(nombre, apellido, matricula);

    }

    @Override
    public int calcularDuracionTurno() {
        return 45;
    }

    @Override
    public String getEspecialidad() {
        return "OdontoPediatra";
    }
}
