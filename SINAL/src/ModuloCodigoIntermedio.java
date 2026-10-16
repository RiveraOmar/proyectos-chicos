import java.util.List;


public class ModuloCodigoIntermedio {

    private final PanelCodigoIntermedio panel;
    private final CodigoIntermedio generador;

    public ModuloCodigoIntermedio() {
        panel = new PanelCodigoIntermedio();
        generador = new CodigoIntermedio();
    }

    public PanelCodigoIntermedio getPanel() {
        return panel;
    }
    public void setAccionCI(java.awt.event.ActionListener accion) {
        panel.setAccionCI(accion);
    }

    public void generar(List<Token> tokens) {
        List<CodigoIntermedio.DeclaracionIntermedia> declaraciones =
                generador.generar(tokens);
        panel.mostrar(declaraciones);
    }

    public void limpiar() {
        panel.limpiar();
    }
}
