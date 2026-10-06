package Persistencia;

import Entity.Turno;

import java.io.*;
import java.net.URISyntaxException;
import java.util.*;

public class RepoTurno implements IRepositorio<Turno, Integer> {

    private static final String ARCHIVO = obtenerRuta("datos_turnos.dat");

    private static String obtenerRuta(String nombre) {
        try {
            File src = new File(RepoTurno.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            File base = src.getName().endsWith(".jar") ? src.getParentFile() : new File(System.getProperty("user.dir"));
            return new File(base, nombre).getAbsolutePath();
        } catch (URISyntaxException e) {
            return nombre;
        }
    }
    private Map<Integer, Turno> turnos;

    public RepoTurno() {
        this.turnos = cargarDesdeDisco();
        turnos.values().forEach(t -> Turno.sincronizarContador(t.getId()));
    }

    @Override
    public void guardar(Turno t) {
        turnos.put(t.getId(), t);
        guardarEnDisco();
    }

    @Override
    public Turno buscarPorId(Integer id) {
        return turnos.get(id);
    }

    @Override
    public List<Turno> listarTodos() {
        return new ArrayList<>(turnos.values());
    }

    @Override
    public void eliminar(Integer id) {
        turnos.remove(id);
        guardarEnDisco();
    }

    @SuppressWarnings("unchecked")
    private Map<Integer, Turno> cargarDesdeDisco() {
        File archivo = new File(ARCHIVO);
        if (!archivo.exists()) return new HashMap<>();

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(archivo))) {
            return (Map<Integer, Turno>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Advertencia: no se pudo leer " + ARCHIVO + ". Se inicia con datos vacíos.");
            return new HashMap<>();
        }
    }

    private void guardarEnDisco() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ARCHIVO))) {
            oos.writeObject(turnos);
        } catch (IOException e) {
            System.out.println("Error al guardar turnos en disco: " + e.getMessage());
        }
    }
}
