package View;

import LogicaDeNegocio.ServicioOdonto;
import LogicaDeNegocio.ServicioPaciente;
import LogicaDeNegocio.ServicioTurno;
import Persistencia.RepoOdonto;
import Persistencia.RepoPaciente;
import Persistencia.RepoTurno;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class MainFrame extends JFrame {

    private ServicioPaciente servPac;
    private ServicioOdonto servOdo;
    private ServicioTurno servTur;

    private JPanel contenidoPanel;
    private CardLayout cardLayout;

    private PacientePanel pacientePanel;
    private OdontologoPanel odontologoPanel;
    private TurnoPanel turnoPanel;

    public MainFrame() {
        RepoPaciente repoPac = new RepoPaciente();
        RepoOdonto repoOdo = new RepoOdonto();
        RepoTurno repoTur = new RepoTurno();

        servPac = new ServicioPaciente(repoPac);
        servOdo = new ServicioOdonto(repoOdo);
        servTur = new ServicioTurno(repoTur, repoPac, repoOdo);

        setTitle("Clínica Sonrisa Feliz");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(900, 650);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        add(crearNavegacion(), BorderLayout.NORTH);

        cardLayout = new CardLayout();
        contenidoPanel = new JPanel(cardLayout);

        pacientePanel = new PacientePanel(servPac);
        odontologoPanel = new OdontologoPanel(servOdo);
        turnoPanel = new TurnoPanel(servTur, servPac, servOdo);

        contenidoPanel.add(pacientePanel, "PACIENTES");
        contenidoPanel.add(odontologoPanel, "ODONTOLOGOS");
        contenidoPanel.add(turnoPanel, "TURNOS");

        add(contenidoPanel, BorderLayout.CENTER);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int op = JOptionPane.showConfirmDialog(
                        MainFrame.this,
                        "¿Desea salir? Los datos se guardarán automáticamente.",
                        "Confirmar salida",
                        JOptionPane.YES_NO_OPTION
                );
                if (op == JOptionPane.YES_OPTION) {
                    dispose();
                    System.exit(0);
                }
            }
        });
    }

    private JPanel crearNavegacion() {
        JPanel nav = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        nav.setBackground(new Color(41, 128, 185));

        JLabel titulo = new JLabel("  Clínica Sonrisa Feliz");
        titulo.setForeground(Color.WHITE);
        titulo.setFont(new Font("Arial", Font.BOLD, 16));
        nav.add(titulo);

        nav.add(Box.createHorizontalStrut(30));

        JButton btnPacientes = crearBotonNav("Pacientes");
        JButton btnOdontologos = crearBotonNav("Odontólogos");
        JButton btnTurnos = crearBotonNav("Turnos");

        btnPacientes.addActionListener(e -> cardLayout.show(contenidoPanel, "PACIENTES"));
        btnOdontologos.addActionListener(e -> cardLayout.show(contenidoPanel, "ODONTOLOGOS"));
        btnTurnos.addActionListener(e -> {
            turnoPanel.refrescarCombos();
            cardLayout.show(contenidoPanel, "TURNOS");
        });

        nav.add(btnPacientes);
        nav.add(btnOdontologos);
        nav.add(btnTurnos);

        return nav;
    }

    private JButton crearBotonNav(String texto) {
        JButton btn = new JButton(texto);
        btn.setBackground(new Color(52, 152, 219));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setFont(new Font("Arial", Font.PLAIN, 13));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }
}
