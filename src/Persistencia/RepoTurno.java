package Persistencia;

import Entity.Turno;

public class RepoTurno extends RepoArchivo<Turno, Integer> {

    public RepoTurno() {
        super("datos_turnos.dat", "turnos");
        for (Turno t : datos.values()) {
            Turno.sincronizarContador(t.getId());
        }
    }

    @Override
    protected Integer obtenerId(Turno t) {
        return t.getId();
    }
}
