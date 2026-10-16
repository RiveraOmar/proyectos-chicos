public class Constantes {
    public static final int
            // Palabras reservadas
            TIPO_INICIA        = 1,
            TIPO_TERMINA       = 2,
            TIPO_INT           = 3,
            TIPO_BOOLEAN       = 4,
            TIPO_PRINTLN       = 5,
            TIPO_WHILE         = 6,
            TIPO_SI            = 7,
            TIPO_SINO          = 8,

    // Unidades léxicas
    TIPO_IDENTIFICADOR = 9,
            TIPO_NUMERO        = 10,
            TIPO_CADENA        = 11,

    // Simbolos y delimitadores
    TIPO_PUNTO_Y_COMA  = 12,
            TIPO_LLAVE_ABRE    = 13,
            TIPO_LLAVE_CIERRA  = 14,
            TIPO_PAREN_ABRE    = 15,
            TIPO_PAREN_CIERRA  = 16,
            TIPO_ASIGNACION    = 17,

    // Operadores específicos
    TIPO_SUMA          = 18,
            TIPO_RESTA         = 19,
            TIPO_MULTIPLICACION = 20,
            TIPO_DIVISION      = 21,
            TIPO_MENOR         = 22,
            TIPO_MAYOR         = 23,
            TIPO_IGUALDAD      = 24,

    TIPO_FIN_ARCHIVO   = 25;

    public static String nombreTipo(int tipo) {
        switch (tipo) {
            case TIPO_INICIA:
            case TIPO_TERMINA:
            case TIPO_PRINTLN:
            case TIPO_WHILE:
            case TIPO_SI:
            case TIPO_SINO:          return "PR";

            case TIPO_INT:
            case TIPO_BOOLEAN:       return "TIPO_DATO";

            case TIPO_IDENTIFICADOR: return "ID";
            case TIPO_NUMERO:        return "NUM";
            case TIPO_CADENA:        return "CADENA";

            case TIPO_PUNTO_Y_COMA:  return "PYC";
            case TIPO_LLAVE_ABRE:    return "LLAVE_ABRE";
            case TIPO_LLAVE_CIERRA:  return "LLAVE_CIERRA";
            case TIPO_PAREN_ABRE:    return "PAREN_ABRE";
            case TIPO_PAREN_CIERRA:  return "PAREN_CIERRA";

            case TIPO_ASIGNACION:    return "ASIGNACION";

            // Nombres característicos para la tabla e interfaz
            case TIPO_SUMA:          return "SUMA";
            case TIPO_RESTA:         return "RESTA";
            case TIPO_MULTIPLICACION:return "MULTIPLICACION";
            case TIPO_DIVISION:      return "DIVISION";
            case TIPO_MENOR:         return "MENOR_QUE";
            case TIPO_MAYOR:         return "MAYOR_QUE";
            case TIPO_IGUALDAD:      return "IGUALDAD";

            case TIPO_FIN_ARCHIVO:   return "EOF";
            default:                 return "DESCONOCIDO";
        }
    }
    public static boolean esOperador(int tipo) {
        return tipo >= TIPO_SUMA && tipo <= TIPO_IGUALDAD;
    }
}