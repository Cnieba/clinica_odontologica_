package View;

import Controller.PacienteController;
import Entity.Domicilio;
import Entity.Paciente;
import Excepciones.ClinicaException;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class PacientePanel extends BaseFormPanel {

    private PacienteController controller;

    private JTextField txtNombre, txtApellido, txtDni, txtEmail, txtBuscar;
    private JTextField txtCalle, txtNumero, txtLocalidad, txtProvincia, txtCodigoPostal;
    private JTable tabla;
    private DefaultTableModel modelo;
    private String dniSeleccionado = null;

    public PacientePanel(PacienteController controller) {
        this.controller = controller;
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        JScrollPane scrollFormulario = enScrollPane(crearFormulario(), 300);
        add(scrollFormulario, BorderLayout.WEST);
        add(crearTablaPanel(), BorderLayout.CENTER);

        cargarTabla(controller.listarPacientes());
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
        txtCalle = new JTextField();
        txtNumero = new JTextField();
        txtLocalidad = new JTextField();
        txtProvincia = new JTextField();
        txtCodigoPostal = new JTextField();
        inicializarBorder(txtNombre);

        agregarCampo(panel, gbc, "Nombre:", txtNombre);
        agregarCampo(panel, gbc, "Apellido:", txtApellido);
        agregarCampo(panel, gbc, "DNI (7-8 dígitos):", txtDni);
        agregarCampo(panel, gbc, "Email:", txtEmail);

        // Domicilio (antes se guardaba un valor fijo "Calle 123, Ciudad, Provincia")
        panel.add(new JSeparator(), gbc);
        gbc.gridy++;
        agregarCampo(panel, gbc, "Calle:", txtCalle);
        agregarCampo(panel, gbc, "Número:", txtNumero);
        agregarCampo(panel, gbc, "Localidad:", txtLocalidad);
        agregarCampo(panel, gbc, "Provincia:", txtProvincia);
        agregarCampo(panel, gbc, "Código postal (1425 o C1425ABC):", txtCodigoPostal);

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
            cargarTabla(controller.listarPacientes());
        });

        return panel;
    }

    private JPanel crearTablaPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Listado de Pacientes"));

        String[] columnas = {"ID", "Nombre", "Apellido", "DNI", "Email", "Domicilio", "Fecha Ingreso"};
        modelo = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        tabla = new JTable(modelo);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.getColumnModel().getColumn(0).setPreferredWidth(40);
        tabla.getColumnModel().getColumn(5).setPreferredWidth(160); // Domicilio

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
            String domicilio = (p.getDomicilio() != null) ? p.getDomicilio().getResumen() : "";
            modelo.addRow(new Object[]{
                    p.getId(), p.getNombre(), p.getApellido(),
                    p.getDni(), p.getEmail(), domicilio, p.getFechaIngreso()
            });
        }
    }

    private void cargarEnFormulario(int fila) {
        dniSeleccionado = (String) modelo.getValueAt(fila, 3);
        try {
            // El domicilio completo no está en la tabla: se pide el paciente al controller
            Paciente p = controller.buscarPaciente(dniSeleccionado);
            txtNombre.setText(p.getNombre());
            txtApellido.setText(p.getApellido());
            txtDni.setText(p.getDni());
            txtEmail.setText(p.getEmail());

            Domicilio d = p.getDomicilio();
            txtCalle.setText(d != null ? textoONada(d.getCalle()) : "");
            txtNumero.setText(d != null ? textoONada(d.getNumero()) : "");
            txtLocalidad.setText(d != null ? textoONada(d.getLocalidad()) : "");
            txtProvincia.setText(d != null ? textoONada(d.getProvincia()) : "");
            txtCodigoPostal.setText(d != null ? textoONada(d.getCodigoPostal()) : "");
        } catch (ClinicaException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        txtDni.setEditable(false);
        resetBordes(todosLosCampos());
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
            controller.registrarPaciente(
                    txtNombre.getText().trim(),
                    txtApellido.getText().trim(),
                    txtDni.getText().trim(),
                    txtEmail.getText().trim(),
                    txtCalle.getText().trim(),
                    txtNumero.getText().trim(),
                    txtLocalidad.getText().trim(),
                    txtProvincia.getText().trim(),
                    txtCodigoPostal.getText().trim()
            );
            cargarTabla(controller.listarPacientes());
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
        if (!validarCamposDomicilio()) return;
        try {
            controller.modificarPaciente(
                    dniSeleccionado,
                    txtNombre.getText().trim(),
                    txtApellido.getText().trim(),
                    txtEmail.getText().trim(),
                    txtCalle.getText().trim(),
                    txtNumero.getText().trim(),
                    txtLocalidad.getText().trim(),
                    txtProvincia.getText().trim(),
                    txtCodigoPostal.getText().trim()
            );
            cargarTabla(controller.listarPacientes());
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
                controller.eliminarPaciente(dniSeleccionado);
                cargarTabla(controller.listarPacientes());
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
            cargarTabla(controller.listarPacientes());
            return;
        }
        List<Paciente> resultado = controller.buscarPorApellido(texto);
        cargarTabla(resultado);
        if (resultado.isEmpty())
            JOptionPane.showMessageDialog(this, "No se encontraron pacientes con ese apellido.", "Sin resultados", JOptionPane.INFORMATION_MESSAGE);
    }

    private boolean validarCampos() {
        boolean ok = true;
        resetBordes(todosLosCampos());

        if (txtNombre.getText().trim().isEmpty()) { marcarError(txtNombre); ok = false; }
        if (txtApellido.getText().trim().isEmpty()) { marcarError(txtApellido); ok = false; }
        if (!txtDni.getText().trim().matches("\\d{7,8}")) { marcarError(txtDni); ok = false; }
        if (!txtEmail.getText().trim().contains("@")) { marcarError(txtEmail); ok = false; }
        if (!marcarErroresDomicilio()) ok = false;

        if (!ok)
            JOptionPane.showMessageDialog(this, "Complete correctamente los campos marcados en rojo.", "Campos inválidos", JOptionPane.WARNING_MESSAGE);
        return ok;
    }

    /** Se usa al modificar: solo revisa los campos de domicilio. */
    private boolean validarCamposDomicilio() {
        resetBordes(txtCalle, txtNumero, txtLocalidad, txtProvincia, txtCodigoPostal);
        boolean ok = marcarErroresDomicilio();
        if (!ok)
            JOptionPane.showMessageDialog(this, "Complete correctamente los campos marcados en rojo.", "Campos inválidos", JOptionPane.WARNING_MESSAGE);
        return ok;
    }

    /** Marca en rojo los campos de domicilio inválidos. Devuelve true si están todos bien. */
    private boolean marcarErroresDomicilio() {
        boolean ok = true;
        if (txtCalle.getText().trim().isEmpty()) { marcarError(txtCalle); ok = false; }
        if (!txtNumero.getText().trim().matches("\\d+")) { marcarError(txtNumero); ok = false; }
        if (txtLocalidad.getText().trim().isEmpty()) { marcarError(txtLocalidad); ok = false; }
        if (txtProvincia.getText().trim().isEmpty()) { marcarError(txtProvincia); ok = false; }
        if (!controller.esCodigoPostalValido(txtCodigoPostal.getText().trim())) { marcarError(txtCodigoPostal); ok = false; }
        return ok;
    }

    private JTextField[] todosLosCampos() {
        return new JTextField[]{txtNombre, txtApellido, txtDni, txtEmail,
                txtCalle, txtNumero, txtLocalidad, txtProvincia, txtCodigoPostal};
    }

    private String textoONada(String texto) {
        return texto != null ? texto : "";
    }

    private void limpiarFormulario() {
        for (JTextField campo : todosLosCampos()) campo.setText("");
        txtDni.setEditable(true);
        dniSeleccionado = null;
        resetBordes(todosLosCampos());
        tabla.clearSelection();
    }
}
