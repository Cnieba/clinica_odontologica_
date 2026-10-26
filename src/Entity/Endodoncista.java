package Entity;


public class Endodoncista extends Odontologo{
    public Endodoncista(String nombre, String apellido, String matricula){
        super(nombre, apellido,matricula);

    }

    @Override
    public int calcularDuracionTurno() {
        return 60;
    }

    @Override
    public String getEspecialidad() {
        return "Endodoncista";
    }
}
