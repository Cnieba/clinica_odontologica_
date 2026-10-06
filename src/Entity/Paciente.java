package Entity;

import java.io.Serializable;
import java.time.LocalDate;

public class Paciente implements Serializable, Comparable<Paciente> {

    private static final long serialVersionUID = 1L;

    private static int contador = 0;
    private Integer id;
    private String nombre;
    private String apellido;
    private String dni;
    private String email;
    private LocalDate fechaIngreso;
    private Domicilio domicilio;

    public Paciente(String nombre, String apellido, String dni, String email, LocalDate fechaIngreso, Domicilio domicilio) {
        this.id = ++contador;
        this.nombre = nombre;
        this.apellido = apellido;
        this.dni = dni;
        this.email = email;
        this.fechaIngreso = fechaIngreso;
        this.domicilio = domicilio;
    }

    // Comparable: ordenar por apellido (luego nombre como desempate)
    @Override
    public int compareTo(Paciente otro) {
        int cmp = this.apellido.compareToIgnoreCase(otro.apellido);
        if (cmp != 0) return cmp;
        return this.nombre.compareToIgnoreCase(otro.nombre);
    }

    public static void sincronizarContador(int max) {
        if (max > contador) contador = max;
    }

    public Integer getId() { return id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }
    public String getDni() { return dni; }
    public void setDni(String dni) { this.dni = dni; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public LocalDate getFechaIngreso() { return fechaIngreso; }
    public void setFechaIngreso(LocalDate fechaIngreso) { this.fechaIngreso = fechaIngreso; }
    public Domicilio getDomicilio() { return domicilio; }
    public void setDomicilio(Domicilio domicilio) { this.domicilio = domicilio; }

    @Override
    public String toString() {
        return "Paciente: " +
                "ID: " + getId() +
                ", Nombre: " + getNombre() + '\'' +
                ", Apellido: " + getApellido() + '\'' +
                ", DNI: " + getDni() + '\'' +
                ", Email: " + getEmail() + '\'' +
                ", Fecha de ingreso: " + getFechaIngreso() +
                ", Domicilio: " + getDomicilio();
    }
}
