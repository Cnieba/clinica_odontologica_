package Persistencia;

import java.io.*;
import java.net.URISyntaxException;
import java.util.*;

/**
 * Clase base para los repositorios que guardan sus datos en un archivo .dat.
 * Junta el código que antes estaba repetido en RepoPaciente, RepoOdonto y RepoTurno
 * (ruta del archivo, carga y guardado en disco).
 * Cada subclase solo indica cuál es la clave (ID) de su entidad.
 */
public abstract class RepoArchivo<T, ID> implements IRepositorio<T, ID> {

    protected Map<ID, T> datos;
    private final String archivo;
    private final String descripcion;

    protected RepoArchivo(String nombreArchivo, String descripcion) {
        this.archivo = obtenerRuta(nombreArchivo);
        this.descripcion = descripcion;
        this.datos = cargarDesdeDisco();
    }

    /** Cada repositorio indica qué dato de la entidad se usa como clave. */
    protected abstract ID obtenerId(T entidad);

    @Override
    public void guardar(T entidad) {
        datos.put(obtenerId(entidad), entidad);
        guardarEnDisco();
    }

    @Override
    public T buscarPorId(ID id) {
        return datos.get(id);
    }

    @Override
    public List<T> listarTodos() {
        return new ArrayList<>(datos.values());
    }

    @Override
    public void eliminar(ID id) {
        datos.remove(id);
        guardarEnDisco();
    }

    // ── Persistencia ────────────────────────────────────────────────
    private static String obtenerRuta(String nombre) {
        try {
            File src = new File(RepoArchivo.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            // Si es un .jar, guardar junto al jar; si son clases sueltas (IDE), usar user.dir
            File base = src.getName().endsWith(".jar") ? src.getParentFile() : new File(System.getProperty("user.dir"));
            return new File(base, nombre).getAbsolutePath();
        } catch (URISyntaxException e) {
            return nombre;
        }
    }

    @SuppressWarnings("unchecked")
    private Map<ID, T> cargarDesdeDisco() {
        File f = new File(archivo);
        if (!f.exists()) return new HashMap<>();

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
            return (Map<ID, T>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Advertencia: no se pudo leer " + archivo + ". Se inicia con datos vacíos.");
            return new HashMap<>();
        }
    }

    private void guardarEnDisco() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(archivo))) {
            oos.writeObject(datos);
        } catch (IOException e) {
            System.out.println("Error al guardar " + descripcion + " en disco: " + e.getMessage());
        }
    }
}
