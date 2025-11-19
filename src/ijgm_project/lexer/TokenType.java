package ijgm_project.lexer;

/**
 * Define o conjunto de todos os tokens válidos na linguagem (símbolos
 * terminais).
 * Esta enumeração funciona como o "dicionário" do compilador.
 */

public enum TokenType {
    // Palavras-reservadas de comandos
    WHILE,
    PRINT,
    IF,
    ELSE,

    // Palavras-reservadas de literais booleanos
    TRUE,
    FALSE,

    // Palavras-reservadas de tipos (Sistema de Tipagem Estática)
    INT,
    FLOAT,
    BOOL,
    STRING_TYPE, // Usado para declaração de variáveis string

    // Identificadores e literais
    IDENTIFIER, // Nomes de variáveis
    NUMBER, // Literais inteiros (ex: 10)
    FLOAT_LITERAL, // Literais de ponto flutuante (ex: 10.5)
    STRING, // Literais de string (ex: "texto")

    // Símbolos de um caractere
    SEMICOLON,
    ASSIGN, // =
    OPEN_PAREN, // (
    CLOSE_PAREN, // )
    OPEN_BRACE, // {
    CLOSE_BRACE, // }

    // Operadores aritméticos
    PLUS, // +
    MINUS, // -
    MULTIPLY, // *
    DIVIDE, // /
    
    // --- NOVOS TOKENS ---
    INCREMENT, // ++ 
    DECREMENT, // --

    // Operadores de comparação e lógicos
    GREATER_THAN, // >
    LESS_THAN, // <
    EQUAL_EQUAL, // ==
    NOT_EQUAL, // !=
    GREATER_EQUAL, // >=
    LESS_EQUAL, // <=
    AND, // &&
    OR, // ||

    // Fim do arquivo (EOF - End Of File)
    EOF
}