package View;

import Entity.*;
import Excepciones.ClinicaException;
import LogicaDeNegocio.ServicioOdonto;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class OdontologoPanel extends BaseFormPanel {

    private ServicioOdonto servicio;

    private JTextField txtNombre, txtApellido, txtMatricula;
    private JComboBox<String> cmbEspecialidad;
    private JTable tabla;
    private DefaultTableModel modelo;
    private String matriculaSeleccionada = null;

    public OdontologoPanel(ServicioOdonto servicio) {
        this.servicio = servicio;
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        add(enScrollPane(crearFormulario(), 300), BorderLayout.WEST);
        add(crearTablaPanel(), BorderLayout.CENTER);

        cargarTabla(servicio.listar());
    }

    private JPanel crearFormulario() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Datos del Odontólogo"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;

        txtNombre = new JTextField();
        txtApellido = new JTextField();
        txtMatricula = new JTextField();
        inicializarBorder(txtNombre);
        cmbEspecialidad = new JComboBox<>(new String[]{
                "Endodoncista", "Ortodoncista", "Periodoncista", "Odontopediatra"
        });

        agregarCampo(panel, gbc, "Nombre:", txtNombre);
        agregarCampo(panel, gbc, "Apellido:", txtApellido);
        agregarCampo(panel, gbc, "Matrícula:", txtMatricula);

        panel.add(new JLabel("Especialidad:"), gbc);
        gbc.gridy++;
        panel.add(cmbEspecialidad, gbc);
        gbc.gridy++;

        JButton btnAgregar = new JButton("Agregar");
        JButton btnModificar = new JButton("Modificar");
        JButton btnEliminar = new JButton("Eliminar");
        JButton btnLimpiar = new JButton("Limpiar");

        btnAgregar.addActionListener(e -> agregarOdontologo());
        btnModificar.addActionListener(e -> modificarOdontologo());
        btnEliminar.addActionListener(e -> eliminarOdontologo());
        btnLimpiar.addActionListener(e -> limpiarFormulario());

        gbc.gridwidth = 1;
        panel.add(btnAgregar, gbc);
        gbc.gridx = 1;
        panel.add(btnModificar, gbc);

        gbc.gridy++; gbc.gridx = 0;
        panel.add(btnEliminar, gbc);
        gbc.gridx = 1;
        panel.add(btnLimpiar, gbc);

        return panel;
    }

    private JPanel crearTablaPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Listado de Odontólogos"));

        String[] columnas = {"ID", "Nombre", "Apellido", "Matrícula", "Especialidad", "Duración Turno"};
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

    private void cargarTabla(List<Odontologo> lista) {
        modelo.setRowCount(0);
        for (Odontologo o : lista) {
            modelo.addRow(new Object[]{
                    o.getId(), o.getNombre(), o.getApellido(),
                    o.getMatricula(), o.getEspecialidad(), o.calcularDuracionTurno() + " min"
            });
        }
    }

    private void cargarEnFormulario(int fila) {
        matriculaSeleccionada = (String) modelo.getValueAt(fila, 3);
        txtNombre.setText((String) modelo.getValueAt(fila, 1));
        txtApellido.setText((String) modelo.getValueAt(fila, 2));
        txtMatricula.setText(matriculaSeleccionada);
        txtMatricula.setEditable(false);
        String esp = (String) modelo.getValueAt(fila, 4);
        for (int i = 0; i < cmbEspecialidad.getItemCount(); i++) {
            if (cmbEspecialidad.getItemAt(i).equals(esp)) {
                cmbEspecialidad.setSelectedIndex(i);
                break;
            }
        }
        resetBordes(txtNombre, txtApellido, txtMatricula);
    }

    private void agregarOdontologo() {
        if (matriculaSeleccionada != null) {
            JOptionPane.showMessageDialog(this,
                    "Hay un odontólogo seleccionado. Use 'Limpiar' para registrar uno nuevo.",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!validarCampos()) return;
        try {
            String nombre = txtNombre.getText().trim();
            String apellido = txtApellido.getText().trim();
            String mat = txtMatricula.getText().trim();
            String esp = (String) cmbEspecialidad.getSelectedItem();

            Odontologo o;
            switch (esp) {
                case "Endodoncista":   o = new Endodoncista(nombre, apellido, mat);   break;
                case "Ortodoncista":   o = new Ortodoncista(nombre, apellido, mat);   break;
                case "Periodoncista":  o = new Periodoncista(nombre, apellido, mat);  break;
                case "Odontopediatra": o = new Odontopediatra(nombre, apellido, mat); break;
                default:               o = new Odontologo(nombre, apellido, mat);
            }

            servicio.registrarOdontologo(o);
            cargarTabla(servicio.listar());
            limpiarFormulario();
            JOptionPane.showMessageDialog(this, "Odontólogo registrado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        } catch (ClinicaException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void modificarOdontologo() {
        if (matriculaSeleccionada == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un odontólogo de la tabla.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (txtNombre.getText().trim().isEmpty() || txtApellido.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nombre y apellido no pueden estar vacíos.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            servicio.actualizarOdontologo(matriculaSeleccionada, txtNombre.getText().trim(), txtApellido.getText().trim());
            cargarTabla(servicio.listar());
            limpiarFormulario();
            JOptionPane.showMessageDialog(this, "Odontólogo actualizado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        } catch (ClinicaException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminarOdontologo() {
        if (matriculaSeleccionada == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un odontólogo de la tabla.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int conf = JOptionPane.showConfirmDialog(this,
                "¿Desea eliminar al odontólogo con matrícula " + matriculaSeleccionada + "?",
                "Confirmar eliminación", JOptionPane.YES_NO_OPTION);
        if (conf == JOptionPane.YES_OPTION) {
            try {
                servicio.eliminar(matriculaSeleccionada);
                cargarTabla(servicio.listar());
                limpiarFormulario();
                JOptionPane.showMessageDialog(this, "Odontólogo eliminado.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } catch (ClinicaException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private boolean validarCampos() {
        boolean ok = true;
        resetBordes(txtNombre, txtApellido, txtMatricula);

        if (txtNombre.getText().trim().isEmpty())           { marcarError(txtNombre);    ok = false; }
        if (txtApellido.getText().trim().isEmpty())         { marcarError(txtApellido);  ok = false; }
        if (!txtMatricula.getText().trim().matches("\\d+")) { marcarError(txtMatricula); ok = false; }

        if (!ok)
            JOptionPane.showMessageDialog(this, "Complete correctamente los campos marcados en rojo.", "Campos inválidos", JOptionPane.WARNING_MESSAGE);
        return ok;
    }

    private void limpiarFormulario() {
        txtNombre.setText(""); txtApellido.setText(""); txtMatricula.setText("");
        txtMatricula.setEditable(true);
        cmbEspecialidad.setSelectedIndex(0);
        matriculaSeleccionada = null;
        resetBordes(txtNombre, txtApellido, txtMatricula);
        tabla.clearSelection();
    }
}
