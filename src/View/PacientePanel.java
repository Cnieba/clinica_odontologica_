package View;

import Entity.Domicilio;
import Entity.Paciente;
import Excepciones.ClinicaException;
import LogicaDeNegocio.ServicioPaciente;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.util.List;

public class PacientePanel extends BaseFormPanel {

    private ServicioPaciente servicio;

    private JTextField txtNombre, txtApellido, txtDni, txtEmail, txtBuscar;
    private JTable tabla;
    private DefaultTableModel modelo;
    private String dniSeleccionado = null;

    public PacientePanel(ServicioPaciente servicio) {
        this.servicio = servicio;
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        JScrollPane scrollFormulario = enScrollPane(crearFormulario(), 300);
        add(scrollFormulario, BorderLayout.WEST);
        add(crearTablaPanel(), BorderLayout.CENTER);

        cargarTabla(servicio.listar());
    }

    private JPanel crearFormulario() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Datos del Paciente"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;

        txtNombre = new JTextField();
        txtApellido = new JTextField();
        txtDni = new JTextField();
        txtEmail = new JTextField();
        inicializarBorder(txtNombre);

        agregarCampo(panel, gbc, "Nombre:", txtNombre);
        agregarCampo(panel, gbc, "Apellido:", txtApellido);
        agregarCampo(panel, gbc, "DNI (7-8 dígitos):", txtDni);
        agregarCampo(panel, gbc, "Email:", txtEmail);

        JButton btnAgregar = new JButton("Agregar");
        JButton btnModificar = new JButton("Modificar");
        JButton btnEliminar = new JButton("Eliminar");
        JButton btnLimpiar = new JButton("Limpiar");

        btnAgregar.addActionListener(e -> agregarPaciente());
        btnModificar.addActionListener(e -> modificarPaciente());
        btnEliminar.addActionListener(e -> eliminarPaciente());
        btnLimpiar.addActionListener(e -> limpiarFormulario());

        gbc.gridy++; gbc.gridwidth = 1;
        panel.add(btnAgregar, gbc);
        gbc.gridx = 1;
        panel.add(btnModificar, gbc);

        gbc.gridy++; gbc.gridx = 0;
        panel.add(btnEliminar, gbc);
        gbc.gridx = 1;
        panel.add(btnLimpiar, gbc);

        gbc.gridy++; gbc.gridx = 0; gbc.gridwidth = 2;
        panel.add(new JSeparator(), gbc);

        gbc.gridy++;
        panel.add(new JLabel("Buscar por apellido:"), gbc);

        gbc.gridy++;
        txtBuscar = new JTextField();
        txtBuscar.addActionListener(e -> buscarPorApellido());
        panel.add(txtBuscar, gbc);

        gbc.gridy++;
        JButton btnBuscar = new JButton("Buscar");
        JButton btnMostrarTodos = new JButton("Mostrar todos");
        gbc.gridwidth = 1;
        panel.add(btnBuscar, gbc);
        gbc.gridx = 1;
        panel.add(btnMostrarTodos, gbc);

        btnBuscar.addActionListener(e -> buscarPorApellido());
        btnMostrarTodos.addActionListener(e -> {
            txtBuscar.setText("");
            cargarTabla(servicio.listar());
        });

        return panel;
    }

    private JPanel crearTablaPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Listado de Pacientes"));

        String[] columnas = {"ID", "Nombre", "Apellido", "DNI", "Email", "Fecha Ingreso"};
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

    private void cargarTabla(List<Paciente> lista) {
        modelo.setRowCount(0);
        for (Paciente p : lista) {
            modelo.addRow(new Object[]{
                    p.getId(), p.getNombre(), p.getApellido(),
                    p.getDni(), p.getEmail(), p.getFechaIngreso()
            });
        }
    }

    private void cargarEnFormulario(int fila) {
        dniSeleccionado = (String) modelo.getValueAt(fila, 3);
        txtNombre.setText((String) modelo.getValueAt(fila, 1));
        txtApellido.setText((String) modelo.getValueAt(fila, 2));
        txtDni.setText(dniSeleccionado);
        txtEmail.setText((String) modelo.getValueAt(fila, 4));
        txtDni.setEditable(false);
        resetBordes(txtNombre, txtApellido, txtDni, txtEmail);
    }

    private void agregarPaciente() {
        if (dniSeleccionado != null) {
            JOptionPane.showMessageDialog(this,
                    "Hay un paciente seleccionado. Use 'Limpiar' para registrar uno nuevo.",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!validarCampos()) return;
        try {
            Paciente p = new Paciente(
                    txtNombre.getText().trim(),
                    txtApellido.getText().trim(),
                    txtDni.getText().trim(),
                    txtEmail.getText().trim(),
                    LocalDate.now(),
                    new Domicilio("Calle", "123", "Ciudad", "Provincia")
            );
            servicio.registrarPaciente(p);
            cargarTabla(servicio.listar());
            limpiarFormulario();
            JOptionPane.showMessageDialog(this, "Paciente registrado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        } catch (ClinicaException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void modificarPaciente() {
        if (dniSeleccionado == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un paciente de la tabla.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (txtNombre.getText().trim().isEmpty() || txtApellido.getText().trim().isEmpty() || txtEmail.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nombre, apellido y email no pueden estar vacíos.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            servicio.actualizarPaciente(dniSeleccionado, txtNombre.getText().trim(), txtApellido.getText().trim(), txtEmail.getText().trim());
            cargarTabla(servicio.listar());
            limpiarFormulario();
            JOptionPane.showMessageDialog(this, "Paciente actualizado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        } catch (ClinicaException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminarPaciente() {
        if (dniSeleccionado == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un paciente de la tabla.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int conf = JOptionPane.showConfirmDialog(this,
                "¿Desea eliminar al paciente con DNI " + dniSeleccionado + "?",
                "Confirmar eliminación", JOptionPane.YES_NO_OPTION);
        if (conf == JOptionPane.YES_OPTION) {
            try {
                servicio.eliminarPaciente(dniSeleccionado);
                cargarTabla(servicio.listar());
                limpiarFormulario();
                JOptionPane.showMessageDialog(this, "Paciente eliminado.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } catch (ClinicaException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void buscarPorApellido() {
        String texto = txtBuscar.getText().trim();
        if (texto.isEmpty()) {
            cargarTabla(servicio.listar());
            return;
        }
        List<Paciente> resultado = servicio.buscarPorApellido(texto);
        cargarTabla(resultado);
        if (resultado.isEmpty())
            JOptionPane.showMessageDialog(this, "No se encontraron pacientes con ese apellido.", "Sin resultados", JOptionPane.INFORMATION_MESSAGE);
    }

    private boolean validarCampos() {
        boolean ok = true;
        resetBordes(txtNombre, txtApellido, txtDni, txtEmail);

        if (txtNombre.getText().trim().isEmpty()) { marcarError(txtNombre); ok = false; }
        if (txtApellido.getText().trim().isEmpty()) { marcarError(txtApellido); ok = false; }
        if (!txtDni.getText().trim().matches("\\d{7,8}")) { marcarError(txtDni); ok = false; }
        if (!txtEmail.getText().trim().contains("@")) { marcarError(txtEmail); ok = false; }

        if (!ok)
            JOptionPane.showMessageDialog(this, "Complete correctamente los campos marcados en rojo.", "Campos inválidos", JOptionPane.WARNING_MESSAGE);
        return ok;
    }

    private void limpiarFormulario() {
        txtNombre.setText(""); txtApellido.setText("");
        txtDni.setText(""); txtEmail.setText("");
        txtDni.setEditable(true);
        dniSeleccionado = null;
        resetBordes(txtNombre, txtApellido, txtDni, txtEmail);
        tabla.clearSelection();
    }
}
