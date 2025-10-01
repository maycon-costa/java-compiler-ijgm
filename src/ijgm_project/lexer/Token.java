package ijgm_project.lexer;

/**
 * Representa uma unidade atômica reconhecida pelo Analisador Léxico.
 * É a estrutura de dados central que armazena o token.
 */

public class Token {
    private final TokenType type;
    private final String value;
    private final int line;
    private final int column;

    /**
     * Construtor para um Token.
     * 
     * @param type   O tipo de token reconhecido (ex: IDENTIFIER).
     * @param value  O lexema, ou seja, a string exata lida (ex: "x" ou "10").
     * @param line   A linha onde o token começa (para relatório de erros).
     * @param column A coluna onde o token começa (para relatório de erros).
     */

    public Token(TokenType type, String value, int line, int column) {
        this.type = type;
        this.value = value;
        this.line = line;
        this.column = column;
    }

    // Métodos Getters
    public TokenType getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }

    @Override
    public String toString() {
        return "Token{" + "type=" + type + ", value='" + value + '\'' + ", line=" + line + ", column=" + column + '}';
    }
}