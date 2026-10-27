package Persistencia;

import Entity.Odontologo;

public class RepoOdonto extends RepoArchivo<Odontologo, String> {

    public RepoOdonto() {
        super("datos_odontologos.dat", "odontólogos");
        for (Odontologo o : datos.values()) {
            Odontologo.sincronizarContador(o.getId());
        }
    }

    @Override
    protected String obtenerId(Odontologo o) {
        return o.getMatricula();
    }
}
