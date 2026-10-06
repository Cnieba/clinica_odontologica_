package Entity;

import java.io.Serializable;

public class Domicilio implements Serializable {

    private static final long serialVersionUID = 1L;
    private static int contador = 0;
    private int id;
    private String calle;
    private String numero;
    private String localidad;
    private String provincia;

    public Domicilio(String calle, String numero, String localidad, String provincia) {
        this.id = ++contador;
        this.calle = calle;
        this.numero = numero;
        this.localidad = localidad;
        this.provincia = provincia;
    }

    public static void sincronizarContador(int max) {
        if (max > contador) contador = max;
    }

    public int getId() { return id; }
    public String getCalle() { return calle; }
    public void setCalle(String calle) { this.calle = calle; }
    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }
    public String getLocalidad() { return localidad; }
    public void setLocalidad(String localidad) { this.localidad = localidad; }
    public String getProvincia() { return provincia; }
    public void setProvincia(String provincia) { this.provincia = provincia; }

    @Override
    public String toString() {
        return "Domicilio: " +
                "ID: " + getId() +
                ", Calle: " + getCalle() + '\'' +
                ", Número: " + getNumero() + '\'' +
                ", Localidad: " + getLocalidad() + '\'' +
                ", Provincia: " + getProvincia();
    }
}
