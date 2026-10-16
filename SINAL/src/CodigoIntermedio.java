import java.util.ArrayList;
import java.util.List;

public class CodigoIntermedio {

    public static class DeclaracionIntermedia {
        private String nombre;
        private String tipo;
        private String valor;

        public DeclaracionIntermedia(String nombre, String tipo, String valor) {
            this.nombre = nombre;
            this.tipo = tipo;
            this.valor = valor;
        }

        public String getNombre() {
            return nombre;
        }

        public String getTipo() {
            return tipo;
        }

        public String getValor() {
            return valor;
        }
    }

    public List<DeclaracionIntermedia> generar(List<Token> tokens) {

        List<DeclaracionIntermedia> resultado =
                new ArrayList<DeclaracionIntermedia>();

        if (tokens == null || tokens.isEmpty()) {
            return resultado;
        }

        for (int i = 0; i < tokens.size(); i++) {

            Token token = tokens.get(i);

            String tipoAsm = null;

            if (token.getTipo() == Constantes.TIPO_INT) {
                tipoAsm = "DW";
            } else if (token.getTipo() == Constantes.TIPO_BOOLEAN) {
                tipoAsm = "DB";
            }

            if (tipoAsm == null) {
                continue;
            }
            if (i + 1 < tokens.size()
                    && tokens.get(i + 1).getTipo()
                    == Constantes.TIPO_IDENTIFICADOR) {
                String nombre = tokens.get(i + 1).getValor();
                String valor = "?";

                resultado.add(
                        new DeclaracionIntermedia(
                                nombre,
                                tipoAsm,
                                valor
                        )
                );

                i++;
            }
        }

        return resultado;
    }
}
