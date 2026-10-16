import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;

public class PanelCodigoIntermedio extends JPanel {

    private final DefaultTableModel modelo;
    private final JTable tabla;
    private final JButton botonCI;

    public PanelCodigoIntermedio() {
        setLayout(new BorderLayout(0, 6));
        setBackground(new Color(240, 242, 245));

        JPanel encabezado = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        encabezado.setOpaque(false);
        botonCI = new JButton("CI");
        botonCI.setFont(new Font("Segoe UI", Font.BOLD, 14));
        botonCI.setBackground(new Color(25, 135, 84));
        botonCI.setForeground(Color.WHITE);
        botonCI.setFocusPainted(false);
        botonCI.setOpaque(true);
        botonCI.setBorderPainted(false);
        botonCI.setCursor(new Cursor(Cursor.HAND_CURSOR));
        botonCI.setPreferredSize(new Dimension(58, 32));
        encabezado.add(botonCI);
        add(encabezado, BorderLayout.NORTH);

        JPanel recuadro = new JPanel(new BorderLayout());
        recuadro.setBackground(new Color(240, 242, 245));
        recuadro.setBorder(BorderFactory.createLineBorder(new Color(206, 212, 218)));

        String[] columnas = {"VARIABLE", "TIPO", "VALOR"};
        modelo = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabla = new JTable(modelo);
        tabla.setFont(new Font("Consolas", Font.PLAIN, 14));
        tabla.setRowHeight(28);
        tabla.setShowVerticalLines(false);
        tabla.setGridColor(new Color(222, 226, 230));
        tabla.setBackground(Color.WHITE);
        tabla.setSelectionBackground(new Color(204, 229, 255));
        tabla.setSelectionForeground(new Color(33, 37, 41));
        tabla.setFillsViewportHeight(true);

        DefaultTableCellRenderer centrado = new DefaultTableCellRenderer();
        centrado.setHorizontalAlignment(JLabel.CENTER);
        centrado.setBackground(Color.WHITE);
        tabla.getColumnModel().getColumn(1).setCellRenderer(centrado);
        tabla.getColumnModel().getColumn(2).setCellRenderer(centrado);

        JTableHeader header = tabla.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(new Color(241, 243, 245));
        header.setForeground(new Color(73, 80, 87));
        header.setPreferredSize(new Dimension(header.getWidth(), 34));
        header.setBorder(BorderFactory.createMatteBorder(
                0, 0, 1, 0, new Color(206, 212, 218)));

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setBackground(new Color(240, 242, 245));
        scroll.getViewport().setBackground(Color.WHITE);
        recuadro.add(scroll, BorderLayout.CENTER);
        add(recuadro, BorderLayout.CENTER);
    }

    public void setAccionCI(java.awt.event.ActionListener accion) {
        for (java.awt.event.ActionListener existente : botonCI.getActionListeners()) {
            botonCI.removeActionListener(existente);
        }
        botonCI.addActionListener(accion);
    }

    public void mostrar(List<CodigoIntermedio.DeclaracionIntermedia> declaraciones) {
        limpiar();
        if (declaraciones == null) return;

        for (CodigoIntermedio.DeclaracionIntermedia declaracion : declaraciones) {
            modelo.addRow(new Object[]{
                    declaracion.getNombre(),
                    declaracion.getTipo(),
                    declaracion.getValor()
            });
        }
    }

    public void limpiar() {
        modelo.setRowCount(0);
    }
}
