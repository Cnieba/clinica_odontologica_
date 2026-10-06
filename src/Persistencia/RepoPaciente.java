package Persistencia;

import Entity.Paciente;

import java.io.*;
import java.net.URISyntaxException;
import java.util.*;

public class RepoPaciente implements IRepositorio<Paciente, String> {

    private static final String ARCHIVO = obtenerRuta("datos_pacientes.dat");

    private static String obtenerRuta(String nombre) {
        try {
            File src = new File(RepoPaciente.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            // Si es un .jar, guardar junto al jar; si son clases sueltas (IDE), usar user.dir
            File base = src.getName().endsWith(".jar") ? src.getParentFile() : new File(System.getProperty("user.dir"));
            return new File(base, nombre).getAbsolutePath();
        } catch (URISyntaxException e) {
            return nombre;
        }
    }
    private Map<String, Paciente> pacientes;

    public RepoPaciente() {
        this.pacientes = cargarDesdeDisco();
        pacientes.values().forEach(p -> {
            Paciente.sincronizarContador(p.getId());
            if (p.getDomicilio() != null)
                Entity.Domicilio.sincronizarContador(p.getDomicilio().getId());
        });
    }

    @Override
    public void guardar(Paciente p) {
        pacientes.put(p.getDni(), p);
        guardarEnDisco();
    }

    @Override
    public Paciente buscarPorId(String dni) {
        return pacientes.get(dni);
    }

    @Override
    public List<Paciente> listarTodos() {
        return new ArrayList<>(pacientes.values());
    }

    @Override
    public void eliminar(String dni) {
        pacientes.remove(dni);
        guardarEnDisco();
    }

    // ── Persistencia ────────────────────────────────────────────────
    @SuppressWarnings("unchecked")
    private Map<String, Paciente> cargarDesdeDisco() {
        File archivo = new File(ARCHIVO);
        if (!archivo.exists()) return new HashMap<>();

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(archivo))) {
            return (Map<String, Paciente>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Advertencia: no se pudo leer " + ARCHIVO + ". Se inicia con datos vacíos.");
            return new HashMap<>();
        }
    }

    private void guardarEnDisco() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ARCHIVO))) {
            oos.writeObject(pacientes);
        } catch (IOException e) {
            System.out.println("Error al guardar pacientes en disco: " + e.getMessage());
        }
    }
}
