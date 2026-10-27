package Entity;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

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
        this.nombre = Objects.requireNonNull(nombre, "El nombre es obligatorio");
        this.apellido = Objects.requireNonNull(apellido, "El apellido es obligatorio");
        this.dni = Objects.requireNonNull(dni, "El DNI es obligatorio");
        this.email = email;
        this.fechaIngreso = Objects.requireNonNull(fechaIngreso, "La fecha de ingreso es obligatoria");
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
        return "Paciente{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", apellido='" + apellido + '\'' +
                ", dni='" + dni + '\'' +
                ", email='" + email + '\'' +
                ", fechaIngreso=" + fechaIngreso +
                ", domicilio=" + domicilio +
                '}';
    }
}
