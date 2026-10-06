package Entity;

import java.io.Serializable;

public class Ortodoncista extends Odontologo{
    public Ortodoncista(String nombre, String apellido, String matricula){
        super(nombre, apellido, matricula);
    }

    @Override
    public int calcularDuracionTurno() {
        return 50;
    }

    @Override
    public String getEspecialidad() {
        return "Ortodoncista";
    }
}
