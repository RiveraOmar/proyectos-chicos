# Compilador del lenguaje SINAL

Front-end de compilador con interfaz gráfica en Java Swing. Implementa las tres
fases de análisis y muestra el árbol de derivación.

## Cómo ejecutarlo

**Desde IntelliJ:** abrir la carpeta `SINAL` como proyecto y ejecutar `Main.java`.

**Desde terminal:**
```
javac -d out src/*.java
java -cp out Main
```

## Gramática implementada

G = (N, T, S, P)

**N** = { PROGRAMA, LISTA_DECL, DECL, TIPO_DATO, EXPRESION, CONDICIONAL, SINO,
CADENA, IDENTIFICADOR, NUMERO, OPERADOR, LETRA, DIGITO }

**S** = PROGRAMA

**P** (13 producciones):
```
 1  PROGRAMA      -> INICIA LISTA_DECL TERMINA
 2  LISTA_DECL    -> [ DECL ; ]*
 3  DECL          -> TIPO_DATO IDENTIFICADOR |
                     IDENTIFICADOR = EXPRESION |
                     println IDENTIFICADOR |
                     println CADENA |
                     while EXPRESION { LISTA_DECL } |
                     CONDICIONAL
 4  TIPO_DATO     -> int | boolean
 5  EXPRESION     -> IDENTIFICADOR | NUMERO | ( EXPRESION OPERADOR EXPRESION )
 6  CONDICIONAL   -> si EXPRESION { LISTA_DECL } SINO
 7  SINO          -> sino { LISTA_DECL } | epsilon
 8  CADENA        -> " [[ LETRA | DIGITO ]]* "
 9  IDENTIFICADOR -> LETRA [[ LETRA ]]*
10  NUMERO        -> DIGITO [[ DIGITO ]]*
11  OPERADOR      -> @ | # | & | | | == | < | >
12  LETRA         -> A | B | C | ... | Z
13  DIGITO        -> 0 | 1 | ... | 9
```

Operadores: `@` suma, `#` resta, `&` multiplicación, `|` división,
`==` igualdad, `<` menor que, `>` mayor que.

## Archivos

| Archivo | Fase / responsabilidad |
|---|---|
| `Constantes.java` | Códigos de tipo de token |
| `Token.java` | Lexema + tipo + línea |
| `Scanner.java` | Análisis léxico (producciones 8 a 13) |
| `Parser.java` | Análisis sintáctico descendente recursivo (producciones 1 a 7) y análisis semántico |
| `TablaSimbolos.java` | Tabla de símbolos |
| `RegistroError.java` | Registro de errores léxicos, sintácticos y semánticos |
| `Main.java` | Interfaz gráfica |

## Qué detecta

**Léxicos:** carácter no permitido, identificador con minúsculas, número pegado a
letras, cadena sin cerrar, carácter inválido dentro de una cadena.

**Sintácticos:** falta `INICIA` o `TERMINA`, falta `;`, falta `{` `}` `(` `)`,
falta `=` en la asignación, operador ausente, expresión mal formada,
inicio de sentencia inválido. Recuperación en modo pánico.

**Semánticos:** variable no declarada, variable declarada dos veces, variable
usada sin inicializar, tipos incompatibles en la asignación, operandos
incorrectos para un operador, condición de `while` o `si` que no es boolean.

## Ejemplos incluidos

- `ejemplos/ejemplo1_suma.sinal` — programa correcto con while, si/sino y println
- `ejemplos/ejemplo2_tabla.sinal` — tabla de multiplicar, condición con variable boolean
- `ejemplos/ejemplo3_con_errores.sinal` — provoca los tres tipos de error
