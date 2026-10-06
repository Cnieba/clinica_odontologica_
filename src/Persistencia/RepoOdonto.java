package Persistencia;

import Entity.Odontologo;

import java.io.*;
import java.net.URISyntaxException;
import java.util.*;

public class RepoOdonto implements IRepositorio<Odontologo, String> {

    private static final String ARCHIVO = obtenerRuta("datos_odontologos.dat");

    private static String obtenerRuta(String nombre) {
        try {
            File src = new File(RepoOdonto.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            File base = src.getName().endsWith(".jar") ? src.getParentFile() : new File(System.getProperty("user.dir"));
            return new File(base, nombre).getAbsolutePath();
        } catch (URISyntaxException e) {
            return nombre;
        }
    }
    private Map<String, Odontologo> odontologos;

    public RepoOdonto() {
        this.odontologos = cargarDesdeDisco();
        odontologos.values().forEach(o -> Odontologo.sincronizarContador(o.getId()));
    }

    @Override
    public void guardar(Odontologo o) {
        odontologos.put(o.getMatricula(), o);
        guardarEnDisco();
    }

    @Override
    public Odontologo buscarPorId(String matricula) {
        return odontologos.get(matricula);
    }

    @Override
    public List<Odontologo> listarTodos() {
        return new ArrayList<>(odontologos.values());
    }

    @Override
    public void eliminar(String matricula) {
        odontologos.remove(matricula);
        guardarEnDisco();
    }

    @SuppressWarnings("unchecked")
    private Map<String, Odontologo> cargarDesdeDisco() {
        File archivo = new File(ARCHIVO);
        if (!archivo.exists()) return new HashMap<>();

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(archivo))) {
            return (Map<String, Odontologo>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Advertencia: no se pudo leer " + ARCHIVO + ". Se inicia con datos vacíos.");
            return new HashMap<>();
        }
    }

    private void guardarEnDisco() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ARCHIVO))) {
            oos.writeObject(odontologos);
        } catch (IOException e) {
            System.out.println("Error al guardar odontólogos en disco: " + e.getMessage());
        }
    }
}
