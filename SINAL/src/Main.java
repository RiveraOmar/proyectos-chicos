import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main extends JFrame {

    private JTextPane editorCodigo;
    private JTable tablaTokens;
    private DefaultTableModel modeloTablaTokens;
    private ModuloCodigoIntermedio moduloCodigoIntermedio;
    private JPanel panelEstado;
    private JLabel etiquetaEstadoError;
    private JLabel etiquetaEstadoExito;
    private JTextArea consolaErrores;
    private JScrollPane scrollErrores;

    private static final String EJEMPLO = "INICIA\n"
            + "    int CONT;\n"
            + "    int SUMA;\n"
            + "    int TOPE;\n"
            + "    boolean BAN;\n"
            + "\n"
            + "    CONT = 1;\n"
            + "    SUMA = 0;\n"
            + "    TOPE = 5;\n"
            + "\n"
            + "    while ( CONT < TOPE ) {\n"
            + "        SUMA = ( SUMA @ CONT );\n"
            + "        CONT = ( CONT @ 1 );\n"
            + "    };\n"
            + "\n"
            + "    BAN = ( SUMA == 10 );\n"
            + "\n"
            + "    si ( SUMA > 8 ) {\n"
            + "        println \"TOTAL\";\n"
            + "        println SUMA;\n"
            + "    } sino {\n"
            + "        println CONT;\n"
            + "    };\n"
            + "TERMINA\n";

    public Main() {
        setTitle("Scanner - LENGUAJE ROCKO");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        configurarAparienciaGlobal();

        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        toolBar.setBackground(new Color(248, 249, 250));
        toolBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0,
                new Color(222, 226, 230)));
        toolBar.setMargin(new Insets(6, 10, 6, 10));

        JButton btnCargar = crearBotonToolbar("Abrir Archivo");
        JButton btnEjemplo = crearBotonToolbar("Cargar Ejemplo");
        JButton btnLimpiar = crearBotonToolbar("Limpiar Editor");
        toolBar.add(btnCargar);
        toolBar.addSeparator(new Dimension(10, 0));
        toolBar.add(btnEjemplo);
        toolBar.addSeparator(new Dimension(10, 0));
        toolBar.add(btnLimpiar);
        add(toolBar, BorderLayout.NORTH);

        editorCodigo = new JTextPane();
        editorCodigo.setText(EJEMPLO);
        editorCodigo.setFont(new Font("Consolas", Font.PLAIN, 15));
        editorCodigo.setMargin(new Insets(12, 12, 12, 12));
        editorCodigo.setBackground(new Color(253, 254, 255));
        editorCodigo.setForeground(new Color(33, 37, 41));
        JScrollPane scrollEditor = crearScrollConTitulo(editorCodigo,
                " Código Fuente (Lenguaje ROCKO) ");

        JButton btnTokens = crearBotonPrincipal("TOKENS", new Color(13, 110, 253));
        JButton btnParser = crearBotonPrincipal("PARSER", new Color(25, 135, 84));
        JButton btnSemantico = crearBotonPrincipal("ANALISIS SEMANTICO", new Color(111, 66, 193));

        JPanel panelTokens = new JPanel(new BorderLayout(0, 6));
        panelTokens.setBackground(new Color(240, 242, 245));

        JPanel panelBotones = new JPanel(new GridLayout(1, 3, 8, 0));
        panelBotones.setBackground(new Color(240, 242, 245));
        panelBotones.add(btnTokens);
        panelBotones.add(btnParser);
        panelBotones.add(btnSemantico);
        panelTokens.add(panelBotones, BorderLayout.NORTH);

        String[] columnas = {"Tipo de Token", "Token (Valor)", "Línea"};
        modeloTablaTokens = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tablaTokens = new JTable(modeloTablaTokens);
        tablaTokens.setFont(new Font("Consolas", Font.PLAIN, 14));
        tablaTokens.setRowHeight(28);
        tablaTokens.setShowVerticalLines(false);
        tablaTokens.setGridColor(new Color(222, 226, 230));
        tablaTokens.setBackground(Color.WHITE);
        tablaTokens.setSelectionBackground(new Color(204, 229, 255));
        tablaTokens.setSelectionForeground(new Color(33, 37, 41));

        DefaultTableCellRenderer centrado = new DefaultTableCellRenderer();
        centrado.setHorizontalAlignment(JLabel.CENTER);
        centrado.setBackground(Color.WHITE);
        tablaTokens.getColumnModel().getColumn(2).setCellRenderer(centrado);
        tablaTokens.getColumnModel().getColumn(2).setMaxWidth(80);

        JTableHeader header = tablaTokens.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(new Color(241, 243, 245));
        header.setForeground(new Color(73, 80, 87));
        header.setPreferredSize(new Dimension(header.getWidth(), 38));

        JScrollPane scrollTokens = new JScrollPane(tablaTokens);
        scrollTokens.setBackground(new Color(240, 242, 245));
        scrollTokens.getViewport().setBackground(Color.WHITE);
        scrollTokens.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(206, 212, 218)),
                " Tabla de Símbolos / Tokens Encontrados ",
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION,
                new Font("Segoe UI", Font.BOLD, 12),
                new Color(73, 80, 87)));
        panelTokens.add(scrollTokens, BorderLayout.CENTER);

        // SECTOR 4: Código Intermedio, encapsulado en su propia clase.
        moduloCodigoIntermedio = new ModuloCodigoIntermedio();

        // SECTOR 3: Estado + Registro de Errores en el mismo cuadrante.
        panelEstado = new JPanel(new BorderLayout(0, 8));
        panelEstado.setBackground(new Color(240, 242, 245));
        panelEstado.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(206, 212, 218)),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)));

        JLabel tituloEstado = new JLabel("Estado del Análisis");
        tituloEstado.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tituloEstado.setForeground(new Color(73, 80, 87));
        tituloEstado.setBorder(BorderFactory.createEmptyBorder(0, 2, 4, 2));
        panelEstado.add(tituloEstado, BorderLayout.NORTH);

        JPanel panelMensajes = new JPanel(new GridLayout(2, 1, 0, 5));
        panelMensajes.setOpaque(false);
        etiquetaEstadoError = new JLabel("", SwingConstants.CENTER);
        etiquetaEstadoError.setFont(new Font("Segoe UI", Font.BOLD, 13));
        etiquetaEstadoError.setOpaque(true);
        etiquetaEstadoError.setBackground(new Color(248, 215, 218));
        etiquetaEstadoError.setForeground(new Color(114, 28, 36));
        etiquetaEstadoError.setVisible(false);

        etiquetaEstadoExito = new JLabel("Análisis Exitoso", SwingConstants.CENTER);
        etiquetaEstadoExito.setFont(new Font("Segoe UI", Font.BOLD, 13));
        etiquetaEstadoExito.setOpaque(true);
        etiquetaEstadoExito.setBackground(new Color(209, 231, 221));
        etiquetaEstadoExito.setForeground(new Color(15, 81, 50));
        etiquetaEstadoExito.setVisible(false);
        panelMensajes.add(etiquetaEstadoError);
        panelMensajes.add(etiquetaEstadoExito);

        JPanel contenidoEstado = new JPanel(new BorderLayout(0, 8));
        contenidoEstado.setOpaque(false);
        contenidoEstado.add(panelMensajes, BorderLayout.NORTH);

        consolaErrores = new JTextArea();
        consolaErrores.setEditable(false);
        consolaErrores.setFont(new Font("Consolas", Font.PLAIN, 13));
        consolaErrores.setLineWrap(true);
        consolaErrores.setWrapStyleWord(true);
        consolaErrores.setMargin(new Insets(8, 8, 8, 8));
        consolaErrores.setBackground(new Color(253, 242, 242));
        consolaErrores.setForeground(new Color(139, 0, 0));
        scrollErrores = new JScrollPane(consolaErrores);
        scrollErrores.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(235, 180, 180)),
                " Registro de Errores ",
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION,
                new Font("Segoe UI", Font.BOLD, 11),
                new Color(165, 42, 42)));
        contenidoEstado.add(scrollErrores, BorderLayout.CENTER);
        panelEstado.add(contenidoEstado, BorderLayout.CENTER);

        /*
         * DISTRIBUCIÓN DE LOS 5 SECTORES:
         *
         * 1 Programa       | 2 Tokens       | 3 Estado + Errores
         * 4 Código inter.  | 4 Código inter. | 5 Próximo módulo (vacío)
         *
         * El cuadrante inferior derecho queda reservado para la siguiente
         * etapa del compilador.
         */
        JPanel areaTrabajo = new JPanel(new GridBagLayout());
        areaTrabajo.setBackground(new Color(240, 242, 245));
        areaTrabajo.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(6, 6, 6, 6);

        // 1. Programa
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 1; gbc.gridheight = 1;
        gbc.weightx = 1.05; gbc.weighty = 0.58;
        areaTrabajo.add(scrollEditor, gbc);

        // 2. Tokens
        gbc.gridx = 1; gbc.gridy = 0; gbc.gridwidth = 1;
        gbc.weightx = 1.35; gbc.weighty = 0.58;
        areaTrabajo.add(panelTokens, gbc);

        // 3. Estado + Registro de Errores en el mismo cuadrante.
        gbc.gridx = 2; gbc.gridy = 0; gbc.gridwidth = 1;
        gbc.weightx = 0.90; gbc.weighty = 0.58;
        areaTrabajo.add(panelEstado, gbc);

        // 4. Código Intermedio abajo a la izquierda.
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2;
        gbc.weightx = 2.40; gbc.weighty = 0.42;
        areaTrabajo.add(moduloCodigoIntermedio.getPanel(), gbc);

        // 5. Espacio reservado para la siguiente parte del compilador.
        JPanel panelFuturo = new JPanel(new BorderLayout());
        panelFuturo.setBackground(new Color(240, 242, 245));
        panelFuturo.setBorder(BorderFactory.createLineBorder(new Color(222, 226, 230)));
        gbc.gridx = 2; gbc.gridy = 1; gbc.gridwidth = 1;
        gbc.weightx = 0.90; gbc.weighty = 0.42;
        areaTrabajo.add(panelFuturo, gbc);

        add(areaTrabajo, BorderLayout.CENTER);

        btnCargar.addActionListener(e -> abrirArchivo());
        btnEjemplo.addActionListener(e -> {
            editorCodigo.setText(EJEMPLO);
            limpiarResultados();
        });
        btnLimpiar.addActionListener(e -> {
            editorCodigo.setText("");
            limpiarResultados();
        });
        btnTokens.addActionListener(e -> ejecutarTokens());
        btnParser.addActionListener(e -> ejecutarParser());
        btnSemantico.addActionListener(e -> ejecutarSemantico());
        moduloCodigoIntermedio.setAccionCI(e -> ejecutarCodigoIntermedio());
    }

    private JButton crearBotonPrincipal(String texto, Color fondo) {
        JButton boton = new JButton(texto);
        boton.setFont(new Font("Segoe UI", Font.BOLD, 15));
        boton.setBackground(fondo);
        boton.setForeground(Color.WHITE);
        boton.setFocusPainted(false);
        boton.setOpaque(true);
        boton.setBorderPainted(false);
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return boton;
    }

    private JButton crearBotonToolbar(String texto) {
        JButton boton = new JButton(texto);
        boton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        boton.setBackground(Color.WHITE);
        boton.setForeground(new Color(73, 80, 87));
        boton.setFocusPainted(false);
        boton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(206, 212, 218)),
                BorderFactory.createEmptyBorder(5, 12, 5, 12)));
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return boton;
    }

    private JScrollPane crearScrollConTitulo(JComponent componente, String titulo) {
        JScrollPane scroll = new JScrollPane(componente);
        scroll.setBackground(new Color(240, 242, 245));
        scroll.getViewport().setBackground(new Color(253, 254, 255));
        scroll.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(206, 212, 218)),
                titulo,
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION,
                new Font("Segoe UI", Font.BOLD, 12),
                new Color(73, 80, 87)));
        return scroll;
    }

    private void configurarAparienciaGlobal() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) { }
    }

    private void limpiarResultados() {
        modeloTablaTokens.setRowCount(0);
        moduloCodigoIntermedio.limpiar();
        consolaErrores.setText("");
        etiquetaEstadoError.setVisible(false);
        etiquetaEstadoExito.setVisible(false);
        panelEstado.revalidate();
        panelEstado.repaint();
    }

    private void abrirArchivo() {
        JFileChooser chooser = new JFileChooser(new File(System.getProperty("user.dir")));
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try (BufferedReader br = new BufferedReader(new FileReader(chooser.getSelectedFile()))) {
                editorCodigo.read(br, null);
                limpiarResultados();
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error al leer el archivo", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void ejecutarTokens() {
        limpiarResultados();
        String codigoFuente = editorCodigo.getText();
        if (codigoFuente.trim().isEmpty()) return;

        List<RegistroError> errores = new ArrayList<>();
        Scanner scanner = new Scanner(codigoFuente, errores);
        List<Token> tokens = scanner.analizar();
        mostrarTokens(tokens);

        if (errores.isEmpty()) {
            etiquetaEstadoExito.setText("Análisis Léxico Correcto");
            etiquetaEstadoExito.setVisible(true);
        } else {
            mostrarErrores(errores, "Error léxico en línea ");
        }
    }

    private void ejecutarParser() {
        limpiarResultados();
        String codigoFuente = editorCodigo.getText();
        if (codigoFuente.trim().isEmpty()) return;

        List<RegistroError> errores = new ArrayList<>();
        Scanner scanner = new Scanner(codigoFuente, errores);
        List<Token> tokens = scanner.analizar();
        mostrarTokens(tokens);

        if (errores.isEmpty()) {
            Parser parser = new Parser(tokens, errores);
            parser.analizar();
        }

        if (errores.isEmpty()) {
            etiquetaEstadoExito.setText("Programa Sintácticamente Correcto");
            etiquetaEstadoExito.setVisible(true);
        } else {
            mostrarErrores(errores, "Error sintáctico en línea ");
        }
    }

    private void ejecutarSemantico() {
        limpiarResultados();
        String codigoFuente = editorCodigo.getText();
        if (codigoFuente.trim().isEmpty()) return;

        List<RegistroError> errores = new ArrayList<>();
        Scanner scanner = new Scanner(codigoFuente, errores);
        List<Token> tokens = scanner.analizar();
        mostrarTokens(tokens);

        if (errores.isEmpty()) {
            Parser parser = new Parser(tokens, errores);
            parser.analizar();
        }

        if (errores.isEmpty()) {
            TablaSimbolos tabla = new TablaSimbolos();
            AnalizadorSemantico semantico = new AnalizadorSemantico(tokens, errores, tabla);
            semantico.analizar();
        }

        // El Código Intermedio se genera desde el botón CI, no automáticamente.
        if (errores.isEmpty()) {
            moduloCodigoIntermedio.limpiar();
            etiquetaEstadoExito.setText("Análisis Semántico Correcto");
            etiquetaEstadoExito.setVisible(true);
        } else {
            moduloCodigoIntermedio.limpiar();
            mostrarErrores(errores, "Error semántico en línea ");
        }
    }

    /**
     * Ejecuta las fases necesarias y, si no existen errores, llama al módulo
     * independiente CodigoIntermedio para generar la tabla del sector 4.
     */
    private void ejecutarCodigoIntermedio() {
        limpiarResultados();
        String codigoFuente = editorCodigo.getText();
        if (codigoFuente.trim().isEmpty()) return;

        List<RegistroError> errores = new ArrayList<>();
        Scanner scanner = new Scanner(codigoFuente, errores);
        List<Token> tokens = scanner.analizar();
        mostrarTokens(tokens);

        if (errores.isEmpty()) {
            Parser parser = new Parser(tokens, errores);
            parser.analizar();
        }

        if (errores.isEmpty()) {
            TablaSimbolos tabla = new TablaSimbolos();
            AnalizadorSemantico semantico = new AnalizadorSemantico(tokens, errores, tabla);
            semantico.analizar();
        }

        if (errores.isEmpty()) {
            moduloCodigoIntermedio.generar(tokens);

            etiquetaEstadoExito.setText("Código Intermedio Generado Correctamente");
            etiquetaEstadoExito.setVisible(true);
        } else {
            moduloCodigoIntermedio.limpiar();
            mostrarErrores(errores, "Error al generar Código Intermedio en línea ");
        }
    }

    private void mostrarTokens(List<Token> tokens) {
        for (Token t : tokens) {
            if (t.getTipo() != Constantes.TIPO_FIN_ARCHIVO) {
                modeloTablaTokens.addRow(new Object[]{
                        Constantes.nombreTipo(t.getTipo()), t.getValor(), t.getLinea()
                });
            }
        }
    }

    private void mostrarErrores(List<RegistroError> errores, String mensaje) {
        etiquetaEstadoExito.setVisible(false);
        if (!errores.isEmpty()) {
            etiquetaEstadoError.setText(mensaje + errores.get(0).getLinea());
            etiquetaEstadoError.setVisible(true);
        }
        consolaErrores.setText("");
        for (RegistroError error : errores) {
            consolaErrores.append(error.toString() + "\n\n");
        }
        scrollErrores.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Main().setVisible(true));
    }


}
