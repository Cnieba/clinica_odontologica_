package View;

import Controller.OdontologoController;
import Controller.PacienteController;
import Controller.TurnoController;
import LogicaDeNegocio.ServicioOdonto;
import LogicaDeNegocio.ServicioPaciente;
import LogicaDeNegocio.ServicioTurno;
import Persistencia.RepoOdonto;
import Persistencia.RepoPaciente;
import Persistencia.RepoTurno;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // Armado de las capas (antes lo hacía MainFrame): Repo -> Servicio -> Controller -> Vista
        RepoPaciente repoPac = new RepoPaciente();
        RepoOdonto repoOdo = new RepoOdonto();
        RepoTurno repoTur = new RepoTurno();

        ServicioPaciente servPac = new ServicioPaciente(repoPac, repoTur);
        ServicioOdonto servOdo = new ServicioOdonto(repoOdo, repoTur);
        ServicioTurno servTur = new ServicioTurno(repoTur, repoPac, repoOdo);

        PacienteController pacienteController = new PacienteController(servPac);
        OdontologoController odontologoController = new OdontologoController(servOdo);
        TurnoController turnoController = new TurnoController(servTur, servPac, servOdo);

        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame(pacienteController, odontologoController, turnoController);
            frame.setVisible(true);
        });
    }
}
