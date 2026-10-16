import java.util.List;

public class Parser {
    // Lista de tokens que entrega el Scanner
    private List<Token> listaTokens;
    // Posicion actual dentro de la lista
    private int pos;
    // Token que estamos analizando actualmente
    private Token tokenActual;
    // Lista donde se guardan los errores
    private List<RegistroError> errores;

    // CONSTRUCTOR
    public Parser(List<Token> tokens, List<RegistroError> errores) {

        this.listaTokens = tokens;
        this.errores = errores;

        pos = 0;

        if (!tokens.isEmpty()) {
            tokenActual = tokens.get(0);
        }
    }

    // AVANZAR
    private void avanzar() {

        if (pos < listaTokens.size() - 1) {
            pos++;
            tokenActual = listaTokens.get(pos);
        }
    }

    // EAT
    private boolean eat(int tipo, String esperado) {

        if (tokenActual.getTipo() == tipo) {
            avanzar();
            return true;
        }
        errorSintactico("Se esperaba " + esperado, tokenActual.getValor());
        return false;
    }


    // ERROR SINTACTICO
    private void errorSintactico(String mensaje, String encontrado) {
        errores.add(new RegistroError("Sintactico", tokenActual.getLinea(), mensaje, encontrado));
    }


    // SINCRONIZACION
    private void sincronizar() {
        while (tokenActual.getTipo() != Constantes.TIPO_PUNTO_Y_COMA && tokenActual.getTipo() != Constantes.TIPO_LLAVE_CIERRA && tokenActual.getTipo() != Constantes.TIPO_TERMINA && tokenActual.getTipo() != Constantes.TIPO_FIN_ARCHIVO) {
            avanzar();
        }
    }


    // INICIO DE UNA DECLARACION
    private boolean esInicioDecl() {
        int tipo = tokenActual.getTipo();
        return tipo == Constantes.TIPO_INT || tipo == Constantes.TIPO_BOOLEAN || tipo == Constantes.TIPO_IDENTIFICADOR || tipo == Constantes.TIPO_PRINTLN || tipo == Constantes.TIPO_WHILE || tipo == Constantes.TIPO_SI;
    }


    // PROGRAMA
    //
    // PROGRAMA -> INICIA LISTA_DECL TERMINA
    public void analizar() {

        if (listaTokens.isEmpty()) {
            return;
        }
        eat(Constantes.TIPO_INICIA, "INICIA");

        reglaListaDecl();

        eat(Constantes.TIPO_TERMINA, "TERMINA");

        if (tokenActual.getTipo() != Constantes.TIPO_FIN_ARCHIVO) {
            errorSintactico("Se esperaba el final del archivo", tokenActual.getValor());
        }
    }


    // LISTA_DECL
    //
    // LISTA_DECL -> [ DECL ; ]*

    private void reglaListaDecl() {

        while (esInicioDecl()) {

            reglaDecl();

            // Cada declaración termina con ;
            if (tokenActual.getTipo() == Constantes.TIPO_PUNTO_Y_COMA) {

                avanzar();

            } else {

                errorSintactico("Se esperaba ';' al final de la declaracion", tokenActual.getValor());

                sincronizar();

                if (tokenActual.getTipo() == Constantes.TIPO_PUNTO_Y_COMA) {

                    avanzar();
                }
            }
        }
    }


    // DECL
    private void reglaDecl() {
        // DECLARACION
        // int IDENTIFICADOR
        // boolean IDENTIFICADOR

        if (tokenActual.getTipo() == Constantes.TIPO_INT || tokenActual.getTipo() == Constantes.TIPO_BOOLEAN) {

            avanzar();

            if (tokenActual.getTipo() == Constantes.TIPO_IDENTIFICADOR) {

                avanzar();

            } else {

                errorSintactico("Se esperaba un identificador", tokenActual.getValor());
            }

            return;
        }


                // ASIGNACION
        //
        // IDENTIFICADOR = EXPRESION

        if (tokenActual.getTipo() == Constantes.TIPO_IDENTIFICADOR) {

            avanzar();

            if (!eat(Constantes.TIPO_ASIGNACION, "=")) {

                return;
            }

            reglaExpresion();

            return;
        }


                // PRINTLN
        //
        // println IDENTIFICADOR
        // println CADENA

        if (tokenActual.getTipo() == Constantes.TIPO_PRINTLN) {

            avanzar();

            if (tokenActual.getTipo() == Constantes.TIPO_IDENTIFICADOR) {

                avanzar();

            } else if (tokenActual.getTipo() == Constantes.TIPO_CADENA) {

                avanzar();

            } else {

                errorSintactico("Se esperaba un identificador o una cadena despues de println", tokenActual.getValor());
            }

            return;
        }


                // WHILE
        //
        // while EXPRESION { LISTA_DECL }

        if (tokenActual.getTipo() == Constantes.TIPO_WHILE) {

            avanzar();

            reglaExpresion();

            if (eat(Constantes.TIPO_LLAVE_ABRE, "{")) {

                reglaListaDecl();

                eat(Constantes.TIPO_LLAVE_CIERRA, "}");
            }

            return;
        }


                // SI

        if (tokenActual.getTipo() == Constantes.TIPO_SI) {

            reglaCondicional();

            return;
        }


                // DECLARACION NO RECONOCIDA

        errorSintactico("Declaracion no reconocida", tokenActual.getValor());

        sincronizar();
    }


    // CONDICIONAL
    //
    // si EXPRESION { LISTA_DECL } SINO

    private void reglaCondicional() {
        eat(Constantes.TIPO_SI, "si");
        // EXPRESION
        reglaExpresion();
        // {
        if (eat(Constantes.TIPO_LLAVE_ABRE, "{")) {
            // LISTA_DECL
            reglaListaDecl();
            // }
            eat(Constantes.TIPO_LLAVE_CIERRA, "}");
        }
        // SINO
        reglaSino();
    }


    // SINO
    // sino { LISTA_DECL } |  vacio
    private void reglaSino() {

        if (tokenActual.getTipo() == Constantes.TIPO_SINO) {

            avanzar();

            if (eat(Constantes.TIPO_LLAVE_ABRE, "{")) {

                reglaListaDecl();

                eat(Constantes.TIPO_LLAVE_CIERRA, "}");
            }
        }
    }


    // EXPRESION -> IDENTIFICADOR  |   NUMERO   | ( EXPRESION OPERADOR EXPRESION )
    private void reglaExpresion() {
                // NUMERO
        if (tokenActual.getTipo() == Constantes.TIPO_NUMERO) {
            avanzar();
            return;
        }
                // IDENTIFICADOR
        if (tokenActual.getTipo() == Constantes.TIPO_IDENTIFICADOR) {
            avanzar();
            return;
        }
                // EXPRESION ENTRE PARENTESIS
        if (tokenActual.getTipo() == Constantes.TIPO_PAREN_ABRE) {
            avanzar();
            // Expresión izquierda
            reglaExpresion();
            // Operador
            if (!Constantes.esOperador(tokenActual.getTipo())) {

                errorSintactico("Se esperaba un operador", tokenActual.getValor());

                return;
            }
            avanzar();
            // Expresión derecha
            reglaExpresion();
            // )
            eat(Constantes.TIPO_PAREN_CIERRA, ")");

            return;
        }
                // ERROR
        errorSintactico("Se esperaba una expresion", tokenActual.getValor());
    }
}