import java.util.LinkedHashMap;
import java.util.Map;

public class TablaSimbolos {

    public static class Simbolo {

        public String nombre;
        public String tipo;
        public int lineaDecl;
        public boolean inicializada = false;
        public int usos = 0;

        public Simbolo(String nombre, String tipo, int lineaDecl) {
            this.nombre = nombre;
            this.tipo = tipo;
            this.lineaDecl = lineaDecl;
        }
    }

    private Map<String, Simbolo> tabla =
            new LinkedHashMap<String, Simbolo>();

    public boolean existe(String nombre) {
        return tabla.containsKey(nombre);
    }

    public boolean declarar(String nombre, String tipo, int linea) {
        if (tabla.containsKey(nombre)) {
            return false;
        }
        tabla.put(
                nombre,
                new Simbolo(nombre, tipo, linea)
        );
        return true;
    }

    public Simbolo obtener(String nombre) {
        return tabla.get(nombre);
    }

    public String tipoDe(String nombre) {
        Simbolo simbolo = tabla.get(nombre);
        return (simbolo == null)
                ? "error"
                : simbolo.tipo;
    }

    public void limpiar() {
        tabla.clear();
    }

    public Map<String, Simbolo> getTabla() {
        return tabla;
    }

    public String reporte() {

        if (tabla.isEmpty()) {
            return "  (sin variables declaradas)";
        }

        StringBuilder sb =
                new StringBuilder();

        sb.append(
                String.format(
                        "  %-18s %-10s %-8s %-14s %s%n",
                        "NOMBRE",
                        "TIPO",
                        "LINEA",
                        "INICIALIZADA",
                        "USOS"
                )
        );

        sb.append(
                "  ------------------------------------------------------------\n"
        );

        for (Simbolo simbolo : tabla.values()) {

            sb.append(
                    String.format(
                            "  %-18s %-10s %-8d %-14s %d%n",
                            simbolo.nombre,
                            simbolo.tipo,
                            simbolo.lineaDecl,
                            (simbolo.inicializada
                                    ? "si"
                                    : "no"),
                            simbolo.usos
                    )
            );
        }

        return sb.toString();
    }
}