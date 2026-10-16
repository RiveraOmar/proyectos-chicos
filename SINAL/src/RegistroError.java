public class RegistroError {
    private String tipo;
    private int linea;
    private String mensaje;
    private String encontrado;

    public RegistroError(String tipo, int linea, String mensaje, String encontrado) {
        this.tipo = tipo;
        this.linea = linea;
        this.mensaje = mensaje;
        this.encontrado = encontrado;
    }

    public String getTipo() { return tipo; }
    public int getLinea()   { return linea; }

    @Override
    public String toString() {
        return String.format("[%s] Linea %d: %s. Se encontro: '%s'",
                tipo.toUpperCase(), linea, mensaje, encontrado);
    }
}
