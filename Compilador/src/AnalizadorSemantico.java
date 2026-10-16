import java.util.List;

public class AnalizadorSemantico {

    private List<Token> tokens;
    private int posicion;
    private Token tokenActual;
    private List<RegistroError> errores;
    private TablaSimbolos tabla;

    public AnalizadorSemantico(
            List<Token> tokens,
            List<RegistroError> errores,
            TablaSimbolos tabla) {

        this.tokens = tokens;
        this.errores = errores;
        this.tabla = tabla;

        posicion = 0;

        if (tokens.isEmpty() == false) {
            tokenActual = tokens.get(0);
        }
    }

    private void avanzar() {
        if (posicion < tokens.size() - 1) {
            posicion++;
            tokenActual = tokens.get(posicion);
        }
    }

    public void analizar() {

        if (tokens.isEmpty() == true) {
            return;
        }

        while (tokenActual.getTipo() != Constantes.TIPO_FIN_ARCHIVO) {
            analizarElemento();
        }
    }

    private void analizarElemento() {

        if (tokenActual.getTipo() == Constantes.TIPO_INT) {
            analizarDeclaracion("int");
            return;
        }

        if (tokenActual.getTipo() == Constantes.TIPO_BOOLEAN) {
            analizarDeclaracion("boolean");
            return;
        }

        if (tokenActual.getTipo() == Constantes.TIPO_IDENTIFICADOR) {
            analizarUsoVariable();
            return;
        }

        if (tokenActual.getTipo() == Constantes.TIPO_PRINTLN) {
            analizarPrintln();
            return;
        }

        avanzar();
    }

    private void analizarDeclaracion(String tipo) {

        avanzar();

        if (tokenActual.getTipo() == Constantes.TIPO_IDENTIFICADOR) {

            String nombre = tokenActual.getValor();
            int linea = tokenActual.getLinea();

            boolean sePudoRegistrar =
                    tabla.declarar(nombre, tipo, linea);

            if (sePudoRegistrar == false) {

                errores.add(
                        new RegistroError(
                                "Semantico",
                                linea,
                                "La variable ya fue declarada",
                                nombre
                        )
                );
            }

            avanzar();
        }
    }

    private void analizarUsoVariable() {

        String nombre = tokenActual.getValor();
        int linea = tokenActual.getLinea();

        if (tabla.existe(nombre) == false) {

            errores.add(
                    new RegistroError(
                            "Semantico",
                            linea,
                            "La variable no ha sido declarada",
                            nombre
                    )
            );
        }

        avanzar();
    }

    private void analizarPrintln() {

        avanzar();

        if (tokenActual.getTipo() == Constantes.TIPO_IDENTIFICADOR) {

            String nombre = tokenActual.getValor();
            int linea = tokenActual.getLinea();

            if (tabla.existe(nombre) == false) {

                errores.add(
                        new RegistroError(
                                "Semantico",
                                linea,
                                "La variable no ha sido declarada",
                                nombre
                        )
                );
            }

            avanzar();
        }
    }
}