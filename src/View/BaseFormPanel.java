package View;

import javax.swing.*;
import java.awt.*;

public abstract class BaseFormPanel extends JPanel {

    protected javax.swing.border.Border borderOriginal;

    protected void inicializarBorder(JTextField campo) {
        borderOriginal = campo.getBorder();
    }

    protected void agregarCampo(JPanel panel, GridBagConstraints gbc, String label, JTextField campo) {
        gbc.gridwidth = 2; gbc.gridx = 0;
        panel.add(new JLabel(label), gbc);
        gbc.gridy++;
        panel.add(campo, gbc);
        gbc.gridy++;
    }

    protected void marcarError(JTextField campo) {
        campo.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
    }

    protected void resetBordes(JTextField... campos) {
        for (JTextField f : campos) f.setBorder(borderOriginal);
    }

    protected JScrollPane enScrollPane(JPanel formulario, int ancho) {
        JScrollPane scroll = new JScrollPane(formulario);
        scroll.setPreferredSize(new Dimension(ancho, 0));
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setBorder(null);
        return scroll;
    }
}
