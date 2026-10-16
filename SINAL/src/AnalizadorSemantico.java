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

    /* Avanza al siguiente token */
    private void avanzar() {
        if (posicion < tokens.size() - 1) {
            posicion++;
            tokenActual = tokens.get(posicion);
        }
    }

    /* Analiza todo el programa */
    public void analizar() {

        if (tokens.isEmpty() == true) {
            return;
        }

        while (tokenActual.getTipo() != Constantes.TIPO_FIN_ARCHIVO) {
            analizarElemento();
        }
    }

    /* Decide qué elemento semántico se está analizando */
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

    /* Analiza: int A; o boolean B; */
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

    /* Verifica que una variable utilizada exista */
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

    /* Analiza: println A; */
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