package Persistencia;

import Entity.Domicilio;
import Entity.Paciente;

public class RepoPaciente extends RepoArchivo<Paciente, String> {

    public RepoPaciente() {
        super("datos_pacientes.dat", "pacientes");
        for (Paciente p : datos.values()) {
            Paciente.sincronizarContador(p.getId());
            if (p.getDomicilio() != null)
                Domicilio.sincronizarContador(p.getDomicilio().getId());
        }
    }

    @Override
    protected String obtenerId(Paciente p) {
        return p.getDni();
    }
}
