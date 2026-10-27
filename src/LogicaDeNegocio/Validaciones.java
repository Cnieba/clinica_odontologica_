package LogicaDeNegocio;

/**
 * Reglas de formato de los datos (Fabricación Pura, GRASP).
 * Las usan los servicios para validar y las pantallas (a través de los controllers)
 * para marcar en rojo los campos, así la regla está escrita en un solo lugar.
 */
public class Validaciones {

    /** Nombre o apellido: letras (con acentos), espacios, apóstrofo o guion. */
    public static boolean esNombreValido(String texto) {
        if (texto == null || texto.isBlank()) return false;
        return texto.trim().matches("[\\p{L}][\\p{L} '\\-]*");
    }

    /** Email con formato usuario@dominio.algo */
    public static boolean esEmailValido(String email) {
        if (email == null) return false;
        return email.matches("[^@\\s]+@[^@\\s]+\\.[^@\\s]+");
    }

    /** DNI de 7 u 8 dígitos. */
    public static boolean esDniValido(String dni) {
        if (dni == null) return false;
        return dni.matches("\\d{7,8}");
    }

    /** Número de calle: solo dígitos y mayor a 0. */
    public static boolean esNumeroCalleValido(String numero) {
        if (numero == null) return false;
        return numero.matches("[1-9]\\d*");
    }

    /** Matrícula: solo dígitos y distinta de 0. */
    public static boolean esMatriculaValida(String matricula) {
        if (matricula == null) return false;
        return matricula.matches("\\d+") && !matricula.matches("0+");
    }

    /** CP viejo de 4 dígitos (1425) o CPA argentino: letra + 4 dígitos + 3 letras (C1425ABC). */
    public static boolean esCodigoPostalValido(String cp) {
        if (cp == null) return false;
        return cp.matches("\\d{4}") || cp.matches("[A-Za-z]\\d{4}[A-Za-z]{3}");
    }
}
