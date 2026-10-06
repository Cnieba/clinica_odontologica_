package Entity;

import java.io.Serializable;

public class Periodoncista extends Odontologo{
    public Periodoncista(String nombre, String apellido, String matricula){
        super(nombre, apellido,matricula);

    }

    @Override
    public int calcularDuracionTurno() {
        return 40;
    }

    @Override
    public String getEspecialidad() {
        return "Periodoncista";
    }
}
