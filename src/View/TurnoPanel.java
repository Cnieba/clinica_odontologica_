package View;

import Entity.*;
import Excepciones.ClinicaException;
import LogicaDeNegocio.ServicioOdonto;
import LogicaDeNegocio.ServicioPaciente;
import LogicaDeNegocio.ServicioTurno;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;

public class TurnoPanel extends JPanel {

    private ServicioTurno servTur;
    private ServicioPaciente servPac;
    private ServicioOdonto servOdo;

    private JComboBox<String> cmbPaciente, cmbOdontologo;
    private JComboBox<EstadoTurno> cmbEstado;
    private JTextField txtFecha, txtHora, txtFechaDesde, txtFechaHasta, txtBuscarDni, txtBuscarMatricula;
    private JTable tabla;
    private DefaultTableModel modelo;
    private Integer idSeleccionado = null;

    public TurnoPanel(ServicioTurno servTur, ServicioPaciente servPac, ServicioOdonto servOdo) {
        this.servTur = servTur;
        this.servPac = servPac;
        this.servOdo = servOdo;

        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        add(crearPanelIzquierdo(), BorderLayout.WEST);
        add(crearTablaPanel(), BorderLayout.CENTER);

        refrescarCombos();
        cargarTabla(servTur.listar());
    }

    private JPanel crearPanelIzquierdo() {
        JPanel contenedor = new JPanel();
        contenedor.setLayout(new BoxLayout(contenedor, BoxLayout.Y_AXIS));
        contenedor.setPreferredSize(new Dimension(300, 0));

        contenedor.add(crearFormularioTurno());
        contenedor.add(Box.createVerticalStrut(10));
        contenedor.add(crearPanelBusqueda());

        return contenedor;
    }

    private JPanel crearFormularioTurno() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Datos del Turno"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 5, 4, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;

        cmbPaciente = new JComboBox<>();
        cmbOdontologo = new JComboBox<>();
        cmbEstado = new JComboBox<>(EstadoTurno.values());
        txtFecha = new JTextField();
        txtHora = new JTextField();
        configurarPlaceholder(txtFecha, "YYYY-MM-DD");
        configurarPlaceholder(txtHora, "HH:MM");

        panel.add(new JLabel("Paciente (DNI - Nombre):"), gbc); gbc.gridy++;
        panel.add(cmbPaciente, gbc); gbc.gridy++;
        panel.add(new JLabel("Odontólogo (Matrícula - Nombre):"), gbc); gbc.gridy++;
        panel.add(cmbOdontologo, gbc); gbc.gridy++;
        panel.add(new JLabel("Fecha (YYYY-MM-DD):"), gbc); gbc.gridy++;
        panel.add(txtFecha, gbc); gbc.gridy++;
        panel.add(new JLabel("Hora (HH:MM):"), gbc); gbc.gridy++;
        panel.add(txtHora, gbc); gbc.gridy++;
        panel.add(new JLabel("Estado:"), gbc); gbc.gridy++;
        panel.add(cmbEstado, gbc); gbc.gridy++;

        JButton btnCrear = new JButton("Crear Turno");
        JButton btnModificar = new JButton("Modificar");
        JButton btnEliminar = new JButton("Eliminar");
        JButton btnLimpiar = new JButton("Limpiar");

        btnCrear.addActionListener(e -> crearTurno());
        btnModificar.addActionListener(e -> modificarTurno());
        btnEliminar.addActionListener(e -> eliminarTurno());
        btnLimpiar.addActionListener(e -> limpiarFormulario());

        gbc.gridwidth = 1;
        panel.add(btnCrear, gbc); gbc.gridx = 1;
        panel.add(btnModificar, gbc); gbc.gridy++;

        gbc.gridx = 0;
        panel.add(btnEliminar, gbc); gbc.gridx = 1;
        panel.add(btnLimpiar, gbc);

        return panel;
    }

    private JPanel crearPanelBusqueda() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Búsquedas"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(3, 5, 3, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;

        txtFechaDesde = new JTextField();
        txtFechaHasta = new JTextField();
        txtBuscarDni = new JTextField();
        txtBuscarMatricula = new JTextField();
        configurarPlaceholder(txtFechaDesde, "YYYY-MM-DD");
        configurarPlaceholder(txtFechaHasta, "YYYY-MM-DD");
        txtBuscarDni.addActionListener(e -> buscarPorPaciente());
        txtBuscarMatricula.addActionListener(e -> buscarPorOdontologo());

        panel.add(new JLabel("Desde:"), gbc); gbc.gridy++;
        panel.add(txtFechaDesde, gbc); gbc.gridy++;
        panel.add(new JLabel("Hasta:"), gbc); gbc.gridy++;
        panel.add(txtFechaHasta, gbc); gbc.gridy++;

        JButton btnRango = new JButton("Buscar por rango");
        panel.add(btnRango, gbc); gbc.gridy++;

        panel.add(new JSeparator(), gbc); gbc.gridy++;
        panel.add(new JLabel("DNI paciente:"), gbc); gbc.gridy++;
        panel.add(txtBuscarDni, gbc); gbc.gridy++;

        JButton btnPorPac = new JButton("Buscar por paciente");
        panel.add(btnPorPac, gbc); gbc.gridy++;

        panel.add(new JSeparator(), gbc); gbc.gridy++;
        panel.add(new JLabel("Matrícula odontólogo:"), gbc); gbc.gridy++;
        panel.add(txtBuscarMatricula, gbc); gbc.gridy++;

        JButton btnPorOdo = new JButton("Buscar por odontólogo");
        panel.add(btnPorOdo, gbc); gbc.gridy++;

        panel.add(new JSeparator(), gbc); gbc.gridy++;
        JButton btnTodos = new JButton("Mostrar todos");
        panel.add(btnTodos, gbc);

        btnRango.addActionListener(e -> buscarPorRango());
        btnPorPac.addActionListener(e -> buscarPorPaciente());
        btnPorOdo.addActionListener(e -> buscarPorOdontologo());
        btnTodos.addActionListener(e -> cargarTabla(servTur.listar()));

        return panel;
    }

    private JPanel crearTablaPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Listado de Turnos"));

        String[] columnas = {"ID", "Paciente", "Odontólogo", "Especialidad", "Fecha", "Hora", "Duración", "Estado"};
        modelo = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        tabla = new JTable(modelo);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.getColumnModel().getColumn(0).setPreferredWidth(40);

        tabla.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int fila = tabla.getSelectedRow();
                if (fila >= 0) cargarEnFormulario(fila);
            }
        });

        panel.add(new JScrollPane(tabla), BorderLayout.CENTER);
        return panel;
    }

    public void refrescarCombos() {
        cmbPaciente.removeAllItems();
        for (Paciente p : servPac.listar())
            cmbPaciente.addItem(p.getDni() + " - " + p.getNombre() + " " + p.getApellido());

        cmbOdontologo.removeAllItems();
        for (Odontologo o : servOdo.listar())
            cmbOdontologo.addItem(o.getMatricula() + " - " + o.getNombre() + " " + o.getApellido());
    }

    private void cargarTabla(List<Turno> lista) {
        modelo.setRowCount(0);
        for (Turno t : lista) {
            modelo.addRow(new Object[]{
                    t.getId(),
                    t.getPaciente().getNombre() + " " + t.getPaciente().getApellido(),
                    t.getOdontologo().getNombre() + " " + t.getOdontologo().getApellido(),
                    t.getOdontologo().getEspecialidad(),
                    t.getFecha(),
                    t.getHora(),
                    t.getDuracion() + " min",
                    t.getEstadoTurno()
            });
        }
    }

    private void cargarEnFormulario(int fila) {
        idSeleccionado = (Integer) modelo.getValueAt(fila, 0);
        txtFecha.setForeground(Color.BLACK);
        txtFecha.setText(modelo.getValueAt(fila, 4).toString());
        txtHora.setForeground(Color.BLACK);
        txtHora.setText(modelo.getValueAt(fila, 5).toString());
        cmbEstado.setSelectedItem(modelo.getValueAt(fila, 7));
    }

    private boolean campoFechaHoraValido(JTextField campo, String placeholder) {
        String val = campo.getText().trim();
        return !val.isEmpty() && !val.equals(placeholder);
    }

    private void crearTurno() {
        if (cmbPaciente.getItemCount() == 0 || cmbOdontologo.getItemCount() == 0) {
            JOptionPane.showMessageDialog(this, "Debe haber al menos un paciente y un odontólogo registrados.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!campoFechaHoraValido(txtFecha, "YYYY-MM-DD") || !campoFechaHoraValido(txtHora, "HH:MM")) {
            JOptionPane.showMessageDialog(this, "Ingrese una fecha y hora válidas.", "Campos incompletos", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            String dniStr = ((String) cmbPaciente.getSelectedItem()).split(" - ", 2)[0];
            String matStr = ((String) cmbOdontologo.getSelectedItem()).split(" - ", 2)[0];
            Paciente p = servPac.buscar(dniStr);
            Odontologo o = servOdo.buscar(matStr);
            LocalDate fecha = LocalDate.parse(txtFecha.getText().trim());
            LocalTime hora = LocalTime.parse(txtHora.getText().trim());

            Turno t = new Turno(p, o, fecha, hora, EstadoTurno.PENDIENTE);
            servTur.crearTurno(t);
            cargarTabla(servTur.listar());
            limpiarFormulario();
            JOptionPane.showMessageDialog(this, "Turno creado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        } catch (ClinicaException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Formato de fecha u hora inválido. Use YYYY-MM-DD y HH:MM.", "Error de formato", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void modificarTurno() {
        if (idSeleccionado == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un turno de la tabla.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!campoFechaHoraValido(txtFecha, "YYYY-MM-DD") || !campoFechaHoraValido(txtHora, "HH:MM")) {
            JOptionPane.showMessageDialog(this, "Ingrese una fecha y hora válidas.", "Campos incompletos", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            LocalDate fecha = LocalDate.parse(txtFecha.getText().trim());
            LocalTime hora = LocalTime.parse(txtHora.getText().trim());
            servTur.actualizarTurno(idSeleccionado, fecha, hora);
            servTur.cambiarEstadoTurno(idSeleccionado, (EstadoTurno) cmbEstado.getSelectedItem());
            cargarTabla(servTur.listar());
            limpiarFormulario();
            JOptionPane.showMessageDialog(this, "Turno actualizado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        } catch (ClinicaException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Formato de fecha u hora inválido. Use YYYY-MM-DD y HH:MM.", "Error de formato", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminarTurno() {
        if (idSeleccionado == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un turno de la tabla.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int conf = JOptionPane.showConfirmDialog(this,
                "¿Desea eliminar el turno con ID " + idSeleccionado + "?",
                "Confirmar eliminación", JOptionPane.YES_NO_OPTION);
        if (conf == JOptionPane.YES_OPTION) {
            try {
                servTur.eliminar(idSeleccionado);
                cargarTabla(servTur.listar());
                limpiarFormulario();
                JOptionPane.showMessageDialog(this, "Turno eliminado.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } catch (ClinicaException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void buscarPorRango() {
        try {
            LocalDate desde = LocalDate.parse(txtFechaDesde.getText().trim());
            LocalDate hasta = LocalDate.parse(txtFechaHasta.getText().trim());
            List<Turno> res = servTur.buscarPorRangoFechas(desde, hasta);
            cargarTabla(res);
            if (res.isEmpty())
                JOptionPane.showMessageDialog(this, "No hay turnos en ese rango.", "Sin resultados", JOptionPane.INFORMATION_MESSAGE);
        } catch (ClinicaException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Formato de fecha inválido. Use YYYY-MM-DD.", "Error de formato", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void buscarPorPaciente() {
        String dni = txtBuscarDni.getText().trim();
        if (dni.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese un DNI.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            List<Turno> res = servTur.buscarPorPaciente(dni);
            cargarTabla(res);
            if (res.isEmpty())
                JOptionPane.showMessageDialog(this, "No hay turnos para ese paciente.", "Sin resultados", JOptionPane.INFORMATION_MESSAGE);
        } catch (ClinicaException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void buscarPorOdontologo() {
        String mat = txtBuscarMatricula.getText().trim();
        if (mat.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese una matrícula.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            List<Turno> res = servTur.buscarPorOdontologo(mat);
            cargarTabla(res);
            if (res.isEmpty())
                JOptionPane.showMessageDialog(this, "No hay turnos para ese odontólogo.", "Sin resultados", JOptionPane.INFORMATION_MESSAGE);
        } catch (ClinicaException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void configurarPlaceholder(JTextField campo, String placeholder) {
        campo.setForeground(Color.GRAY);
        campo.setText(placeholder);
        campo.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if (campo.getText().equals(placeholder)) {
                    campo.setText("");
                    campo.setForeground(Color.BLACK);
                }
            }
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (campo.getText().isEmpty()) {
                    campo.setForeground(Color.GRAY);
                    campo.setText(placeholder);
                }
            }
        });
    }

    private void limpiarFormulario() {
        txtFecha.setForeground(Color.GRAY);
        txtFecha.setText("YYYY-MM-DD");
        txtHora.setForeground(Color.GRAY);
        txtHora.setText("HH:MM");
        idSeleccionado = null;
        tabla.clearSelection();
    }
}
