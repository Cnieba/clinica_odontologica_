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
    private String codigoPostal;

    public Domicilio(String calle, String numero, String localidad, String provincia, String codigoPostal) {
        this.id = ++contador;
        this.calle = calle;
        this.numero = numero;
        this.localidad = localidad;
        this.provincia = provincia;
        this.codigoPostal = codigoPostal;
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
    public String getCodigoPostal() { return codigoPostal; }
    public void setCodigoPostal(String codigoPostal) { this.codigoPostal = codigoPostal; }

    // Experto en información: el domicilio sabe cómo mostrarse resumido (para la tabla)
    public String getResumen() {
        return calle + " " + numero + ", " + localidad;
    }

    @Override
    public String toString() {
        return "Domicilio: " +
                "ID: " + getId() +
                ", Calle: " + getCalle() + '\'' +
                ", Número: " + getNumero() + '\'' +
                ", Localidad: " + getLocalidad() + '\'' +
                ", Provincia: " + getProvincia() + '\'' +
                ", Código postal: " + getCodigoPostal();
    }
}
