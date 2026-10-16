public class Token {
    private String valor;
    private int tipo;
    private int linea;

    public Token(String valor, int tipo, int linea) {
        this.valor = valor;
        this.tipo = tipo;
        this.linea = linea;
    }

    public String getValor() { return valor; }
    public int getTipo()     { return tipo; }
    public int getLinea()    { return linea; }

    @Override
    public String toString() {
        return String.format("Linea %-3d | %-28s | %s",
                linea, Constantes.nombreTipo(tipo), valor);
    }
}
