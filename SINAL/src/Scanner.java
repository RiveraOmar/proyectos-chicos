import java.util.ArrayList;
import java.util.List;

public class Scanner {
    private String codigoFuente;
    private int posicionActual = 0;
    private int numeroLinea = 1;
    private List<RegistroError> listaDeErrores;

    public Scanner(String entrada, List<RegistroError> errores) {
        this.codigoFuente = entrada;
        this.listaDeErrores = errores;
    }

    public int getLinea() {
        return numeroLinea;
    }

    public List<Token> analizar() {
        List<Token> listaDeTokens = new ArrayList<Token>();
        Token tokenEncontrado;
        do {
            tokenEncontrado = leerSiguienteToken();
            if (!listaDeErrores.isEmpty()) {
                break;
            }
            if (tokenEncontrado != null) {
                listaDeTokens.add(tokenEncontrado);
            }
        } while (tokenEncontrado != null && tokenEncontrado.getTipo() != Constantes.TIPO_FIN_ARCHIVO);
        return listaDeTokens;
    }

    public Token leerSiguienteToken() {
        ignorarEspacios();
        if (posicionActual >= codigoFuente.length()) {
            return new Token("EOF", Constantes.TIPO_FIN_ARCHIVO, numeroLinea);
        }
        char caracterActual = codigoFuente.charAt(posicionActual);
        if (Character.isLetter(caracterActual)) return extraerPalabra();
        if (Character.isDigit(caracterActual)) return extraerNumero();
        if (caracterActual == '"') return extraerCadenaTexto();
        return extraerSimbolo(caracterActual);
    }

    private void ignorarEspacios() {
        while (posicionActual < codigoFuente.length()) {
            char caracterEvaluado = codigoFuente.charAt(posicionActual);
            if (caracterEvaluado == '\n') {
                numeroLinea++;
                posicionActual++;
            } else if (Character.isWhitespace(caracterEvaluado)) {
                posicionActual++;
            } else {
                break;
            }
        }
    }

    private Token extraerPalabra() {
        int lineaDelToken = numeroLinea;
        StringBuilder textoAcumulado = new StringBuilder();
        while (posicionActual < codigoFuente.length() && Character.isLetterOrDigit(codigoFuente.charAt(posicionActual))) {
            textoAcumulado.append(codigoFuente.charAt(posicionActual++));
        }
        String palabraExtraida = textoAcumulado.toString();
        switch (palabraExtraida) {
            case "INICIA":
                return new Token(palabraExtraida, Constantes.TIPO_INICIA, lineaDelToken);
            case "TERMINA":
                return new Token(palabraExtraida, Constantes.TIPO_TERMINA, lineaDelToken);
            case "int":
                return new Token(palabraExtraida, Constantes.TIPO_INT, lineaDelToken);
            case "boolean":
                return new Token(palabraExtraida, Constantes.TIPO_BOOLEAN, lineaDelToken);
            case "println":
                return new Token(palabraExtraida, Constantes.TIPO_PRINTLN, lineaDelToken);
            case "while":
                return new Token(palabraExtraida, Constantes.TIPO_WHILE, lineaDelToken);
            case "si":
                return new Token(palabraExtraida, Constantes.TIPO_SI, lineaDelToken);
            case "sino":
                return new Token(palabraExtraida, Constantes.TIPO_SINO, lineaDelToken);
        }
        if (esIdentificadorValido(palabraExtraida)) {
            return new Token(palabraExtraida, Constantes.TIPO_IDENTIFICADOR, lineaDelToken);
        }
        listaDeErrores.add(new RegistroError("Léxico", lineaDelToken,
                "Se encontró un identificador inválido en esta línea", palabraExtraida));
        return new Token(palabraExtraida, Constantes.TIPO_IDENTIFICADOR, lineaDelToken);
    }

    private boolean esIdentificadorValido(String textoAEvaluar) {
        for (int i = 0; i < textoAEvaluar.length(); i++) {
            char letra = textoAEvaluar.charAt(i);
            if (letra < 'A' || letra > 'Z') return false;
        }
        return textoAEvaluar.length() > 0;
    }

    private Token extraerNumero() {
        int lineaDelToken = numeroLinea;
        StringBuilder digitosAcumulados = new StringBuilder();
        while (posicionActual < codigoFuente.length() && Character.isDigit(codigoFuente.charAt(posicionActual))) {
            digitosAcumulados.append(codigoFuente.charAt(posicionActual++));
        }
        if (posicionActual < codigoFuente.length() && Character.isLetter(codigoFuente.charAt(posicionActual))) {
            while (posicionActual < codigoFuente.length() && Character.isLetterOrDigit(codigoFuente.charAt(posicionActual))) {
                digitosAcumulados.append(codigoFuente.charAt(posicionActual++));
            }
            listaDeErrores.add(new RegistroError("Léxico", lineaDelToken,
                    "Se encontró un número inválido o mal formado en esta línea", digitosAcumulados.toString()));
            return new Token(digitosAcumulados.toString(), Constantes.TIPO_NUMERO, lineaDelToken);
        }
        return new Token(digitosAcumulados.toString(), Constantes.TIPO_NUMERO, lineaDelToken);
    }

    private Token extraerCadenaTexto() {
        int lineaDelToken = numeroLinea;
        posicionActual++; 
        StringBuilder contenidoDeCadena = new StringBuilder();
        boolean contieneComillaDeCierre = false;
        while (posicionActual < codigoFuente.length()) {
            char caracterEvaluado = codigoFuente.charAt(posicionActual);
            if (caracterEvaluado == '"') {
                posicionActual++;
                contieneComillaDeCierre = true;
                break;
            }
            if (caracterEvaluado == '\n') {
                break;
            }
            contenidoDeCadena.append(caracterEvaluado);
            posicionActual++;
        }
        if (!contieneComillaDeCierre) {
            listaDeErrores.add(new RegistroError("Léxico", lineaDelToken,
                    "Se encontró una cadena sin comilla de cierre en esta línea", "\"" + contenidoDeCadena.toString()));
            return new Token("\"" + contenidoDeCadena.toString(), Constantes.TIPO_CADENA, lineaDelToken);
        }
        return new Token("\"" + contenidoDeCadena.toString() + "\"", Constantes.TIPO_CADENA, lineaDelToken);
    }

    private Token extraerSimbolo(char simboloEncontrado) {
        int lineaDelToken = numeroLinea;
        posicionActual++;
        switch (simboloEncontrado) {
            case ';':
                return new Token(";", Constantes.TIPO_PUNTO_Y_COMA, lineaDelToken);
            case '{':
                return new Token("{", Constantes.TIPO_LLAVE_ABRE, lineaDelToken);
            case '}':
                return new Token("}", Constantes.TIPO_LLAVE_CIERRA, lineaDelToken);
            case '(':
                return new Token("(", Constantes.TIPO_PAREN_ABRE, lineaDelToken);
            case ')':
                return new Token(")", Constantes.TIPO_PAREN_CIERRA, lineaDelToken);
            case '@':
                return new Token("@", Constantes.TIPO_SUMA, lineaDelToken);
            case '#':
                return new Token("#", Constantes.TIPO_RESTA, lineaDelToken);
            case '&':
                return new Token("&", Constantes.TIPO_MULTIPLICACION, lineaDelToken);
            case '|':
                return new Token("|", Constantes.TIPO_DIVISION, lineaDelToken);
            case '<':
                return new Token("<", Constantes.TIPO_MENOR, lineaDelToken);
            case '>':
                return new Token(">", Constantes.TIPO_MAYOR, lineaDelToken);
            case '=':
                if (posicionActual < codigoFuente.length() && codigoFuente.charAt(posicionActual) == '=') {
                    posicionActual++;
                    return new Token("==", Constantes.TIPO_IGUALDAD, lineaDelToken);
                }
                return new Token("=", Constantes.TIPO_ASIGNACION, lineaDelToken);
        }
        listaDeErrores.add(new RegistroError("Léxico", lineaDelToken,
                "Se encontró un carácter no permitido en esta línea", String.valueOf(simboloEncontrado)));
        return new Token(String.valueOf(simboloEncontrado), Constantes.TIPO_FIN_ARCHIVO, lineaDelToken);
    }
}