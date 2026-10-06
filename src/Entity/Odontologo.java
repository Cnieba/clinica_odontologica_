package Entity;

import java.io.Serializable;

public class Odontologo implements Serializable {

    private static final long serialVersionUID = 1L;
    private static int contador = 0;
    private Integer id;
    private String nombre, apellido, matricula;

    public Odontologo(String nombre, String apellido, String matricula) {
        this.id = ++contador;
        this.nombre = nombre;
        this.apellido = apellido;
        this.matricula = matricula;
    }

    public static void sincronizarContador(int max) {
        if (max > contador) contador = max;
    }

    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }
    public String getMatricula() { return matricula; }
    public void setMatricula(String matricula) { this.matricula = matricula; }

    public int calcularDuracionTurno() { return 30; }
    public String getEspecialidad() { return "General"; }

    @Override
    public String toString() {
        return "Odontologo: " +
                "ID: " + getId() +
                ", Nombre: " + getNombre() + '\'' +
                ", Apellido: " + getApellido() + '\'' +
                ", Matricula: " + getMatricula() +
                ", Especialidad: " + getEspecialidad();
    }
}
