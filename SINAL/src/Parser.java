import java.util.List;

public class Parser {
    private List<Token> listaTokens;
    private int pos;
    private Token tokenActual;
    private List<RegistroError> errores;

    public Parser(List<Token> tokens, List<RegistroError> errores) {

        this.listaTokens = tokens;
        this.errores = errores;

        pos = 0;

        if (!tokens.isEmpty()) {
            tokenActual = tokens.get(0);
        }
    }

    private void avanzar() {

        if (pos < listaTokens.size() - 1) {
            pos++;
            tokenActual = listaTokens.get(pos);
        }
    }

    private boolean eat(int tipo, String esperado) {

        if (tokenActual.getTipo() == tipo) {
            avanzar();
            return true;
        }
        errorSintactico("Se esperaba " + esperado, tokenActual.getValor());
        return false;
    }


    private void errorSintactico(String mensaje, String encontrado) {
        errores.add(new RegistroError("Sintactico", tokenActual.getLinea(), mensaje, encontrado));
    }


    private void sincronizar() {
        while (tokenActual.getTipo() != Constantes.TIPO_PUNTO_Y_COMA && tokenActual.getTipo() != Constantes.TIPO_LLAVE_CIERRA && tokenActual.getTipo() != Constantes.TIPO_TERMINA && tokenActual.getTipo() != Constantes.TIPO_FIN_ARCHIVO) {
            avanzar();
        }
    }


    private boolean esInicioDecl() {
        int tipo = tokenActual.getTipo();
        return tipo == Constantes.TIPO_INT || tipo == Constantes.TIPO_BOOLEAN || tipo == Constantes.TIPO_IDENTIFICADOR || tipo == Constantes.TIPO_PRINTLN || tipo == Constantes.TIPO_WHILE || tipo == Constantes.TIPO_SI;
    }


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


    private void reglaListaDecl() {

        while (esInicioDecl()) {

            reglaDecl();

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


    private void reglaDecl() {
       

        if (tokenActual.getTipo() == Constantes.TIPO_INT || tokenActual.getTipo() == Constantes.TIPO_BOOLEAN) {

            avanzar();

            if (tokenActual.getTipo() == Constantes.TIPO_IDENTIFICADOR) {

                avanzar();

            } else {

                errorSintactico("Se esperaba un identificador", tokenActual.getValor());
            }

            return;
        }


                

        if (tokenActual.getTipo() == Constantes.TIPO_IDENTIFICADOR) {

            avanzar();

            if (!eat(Constantes.TIPO_ASIGNACION, "=")) {

                return;
            }

            reglaExpresion();

            return;
        }


                

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


               

        if (tokenActual.getTipo() == Constantes.TIPO_WHILE) {

            avanzar();

            reglaExpresion();

            if (eat(Constantes.TIPO_LLAVE_ABRE, "{")) {

                reglaListaDecl();

                eat(Constantes.TIPO_LLAVE_CIERRA, "}");
            }

            return;
        }


               

        if (tokenActual.getTipo() == Constantes.TIPO_SI) {

            reglaCondicional();

            return;
        }


                

        errorSintactico("Declaracion no reconocida", tokenActual.getValor());

        sincronizar();
    }


    

    private void reglaCondicional() {
        eat(Constantes.TIPO_SI, "si");
        reglaExpresion();
        if (eat(Constantes.TIPO_LLAVE_ABRE, "{")) {
            reglaListaDecl();
            eat(Constantes.TIPO_LLAVE_CIERRA, "}");
        }
        reglaSino();
    }


    private void reglaSino() {

        if (tokenActual.getTipo() == Constantes.TIPO_SINO) {

            avanzar();

            if (eat(Constantes.TIPO_LLAVE_ABRE, "{")) {

                reglaListaDecl();

                eat(Constantes.TIPO_LLAVE_CIERRA, "}");
            }
        }
    }


    private void reglaExpresion() {
        if (tokenActual.getTipo() == Constantes.TIPO_NUMERO) {
            avanzar();
            return;
        }
        if (tokenActual.getTipo() == Constantes.TIPO_IDENTIFICADOR) {
            avanzar();
            return;
        }
        if (tokenActual.getTipo() == Constantes.TIPO_PAREN_ABRE) {
            avanzar();
            reglaExpresion();
            if (!Constantes.esOperador(tokenActual.getTipo())) {

                errorSintactico("Se esperaba un operador", tokenActual.getValor());

                return;
            }
            avanzar();
            reglaExpresion();
            eat(Constantes.TIPO_PAREN_CIERRA, ")");

            return;
        }
        errorSintactico("Se esperaba una expresion", tokenActual.getValor());
    }
}