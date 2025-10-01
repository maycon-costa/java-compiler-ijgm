package ijgm_project.lexer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Analisador Léxico (Scanner).
 * Implementa um Autômato Finito Determinístico (AFD) manual para transformar
 * o código-fonte em uma sequência de tokens.
 */

public class Lexer {
    private final String sourceCode;
    private int position = 0;
    private int line = 1;
    private int column = 1;

    /**
     * Construtor que lê o código-fonte do arquivo.
     * 
     * @param filePath Caminho para o arquivo de código-fonte.
     */

    public Lexer(String filePath) throws IOException {
        this.sourceCode = new String(Files.readAllBytes(Paths.get(filePath)));
    }

    /**
     * Método principal para a análise léxica.
     * Percorre o código-fonte caractere por caractere e identifica os tokens.
     * 
     * @return Uma lista sequencial de objetos Token.
     */

    public List<Token> tokenize() {
        List<Token> tokens = new ArrayList<>();
        while (position < sourceCode.length()) {
            char currentChar = sourceCode.charAt(position);

            // 1. Tratamento de espaços e quebras de linha
            if (Character.isWhitespace(currentChar)) {
                if (currentChar == '\n') {
                    line++;
                    column = 1;
                } else {
                    column++;
                }
                position++;
                continue;
            }

            // 2. Tratamento de comentários (// e /* */)
            if (currentChar == '/' && peek() == '/') {
                while (position < sourceCode.length() && sourceCode.charAt(position) != '\n') {
                    position++;
                }
                continue;
            }
            if (currentChar == '/' && peek() == '*') {
                position += 2;
                while (position < sourceCode.length() && !(sourceCode.charAt(position) == '*' && peek() == '/')) {
                    if (sourceCode.charAt(position) == '\n') {
                        line++;
                        column = 1;
                    } else {
                        column++;
                    }
                    position++;
                }
                if (position < sourceCode.length()) {
                    position += 2;
                }
                continue;
            }

            // 3. Reconhecimento de strings
            if (currentChar == '"') {
                position++;
                int start = position;
                while (position < sourceCode.length() && sourceCode.charAt(position) != '"') {
                    if (sourceCode.charAt(position) == '\n') {
                        throw new RuntimeException("Erro Léxico: String literal não fechada na linha " + line);
                    }
                    position++;
                }
                if (position == sourceCode.length()) {
                    throw new RuntimeException("Erro Léxico: String literal não terminada");
                }
                String value = sourceCode.substring(start, position);
                tokens.add(new Token(TokenType.STRING, value, line, column));
                position++;
                column += value.length() + 2;
                continue;
            }

            // 4. Reconhecimento de números (inteiros e float)
            if (Character.isDigit(currentChar)) {
                StringBuilder numberBuilder = new StringBuilder();
                int startColumn = column;

                // Lê a parte inteira
                while (position < sourceCode.length() && Character.isDigit(sourceCode.charAt(position))) {
                    numberBuilder.append(sourceCode.charAt(position));
                    position++;
                    column++;
                }

                // Verifica e lê a parte decimal (Float)
                if (position < sourceCode.length() && sourceCode.charAt(position) == '.'
                        && position + 1 < sourceCode.length() && Character.isDigit(peek())) {
                    numberBuilder.append(sourceCode.charAt(position));
                    position++;
                    column++;

                    while (position < sourceCode.length() && Character.isDigit(sourceCode.charAt(position))) {
                        numberBuilder.append(sourceCode.charAt(position));
                        position++;
                        column++;
                    }
                    tokens.add(new Token(TokenType.FLOAT_LITERAL, numberBuilder.toString(), line, startColumn));
                } else {
                    // Se não for float, é um inteiro
                    tokens.add(new Token(TokenType.NUMBER, numberBuilder.toString(), line, startColumn));
                }
                continue;
            }

            // 5. Reconhecimento de identificadores e palavras-reservadas
            if (Character.isLetter(currentChar)) {
                StringBuilder idBuilder = new StringBuilder();
                int startColumn = column;
                while (position < sourceCode.length() && (Character.isLetterOrDigit(sourceCode.charAt(position)))) {
                    idBuilder.append(sourceCode.charAt(position));
                    position++;
                    column++;
                }
                String id = idBuilder.toString();
                // Mapeamento para Palavras-Reservadas e Tipos
                TokenType type = switch (id) {
                    case "while" -> TokenType.WHILE;
                    case "print" -> TokenType.PRINT;
                    case "if" -> TokenType.IF;
                    case "else" -> TokenType.ELSE;
                    case "int" -> TokenType.INT;
                    case "float" -> TokenType.FLOAT;
                    case "bool" -> TokenType.BOOL;
                    case "string" -> TokenType.STRING_TYPE;
                    case "true" -> TokenType.TRUE;
                    case "false" -> TokenType.FALSE;
                    default -> TokenType.IDENTIFIER;
                };
                tokens.add(new Token(type, id, line, startColumn));
                continue;
            }

            // 6. Reconhecimento de operadores e símbolos
            int startColumn = column;
            switch (currentChar) {
                // Operadores de dois caracteres e desambiguação
                case '=' -> {
                    if (peek() == '=') {
                        tokens.add(new Token(TokenType.EQUAL_EQUAL, "==", line, startColumn));
                        position += 2;
                        column += 2;
                    } else {
                        tokens.add(new Token(TokenType.ASSIGN, "=", line, startColumn));
                        position++;
                        column++;
                    }
                }
                case '!' -> {
                    if (peek() == '=') {
                        tokens.add(new Token(TokenType.NOT_EQUAL, "!=", line, startColumn));
                        position += 2;
                        column += 2;
                    } else {
                        throw new RuntimeException(
                                "Erro Léxico: Caractere inesperado '!' na linha " + line + ", coluna " + startColumn);
                    }
                }
                case '>' -> {
                    if (peek() == '=') {
                        tokens.add(new Token(TokenType.GREATER_EQUAL, ">=", line, startColumn));
                        position += 2;
                        column += 2;
                    } else {
                        tokens.add(new Token(TokenType.GREATER_THAN, ">", line, startColumn));
                        position++;
                        column++;
                    }
                }
                case '<' -> {
                    if (peek() == '=') {
                        tokens.add(new Token(TokenType.LESS_EQUAL, "<=", line, startColumn));
                        position += 2;
                        column += 2;
                    } else {
                        tokens.add(new Token(TokenType.LESS_THAN, "<", line, startColumn));
                        position++;
                        column++;
                    }
                }
                case '&' -> {
                    if (peek() == '&') {
                        tokens.add(new Token(TokenType.AND, "&&", line, startColumn));
                        position += 2;
                        column += 2;
                    } else {
                        throw new RuntimeException(
                                "Erro Léxico: Caractere inesperado '&' na linha " + line + ", coluna " + startColumn);
                    }
                }
                case '|' -> {
                    if (peek() == '|') {
                        tokens.add(new Token(TokenType.OR, "||", line, startColumn));
                        position += 2;
                        column += 2;
                    } else {
                        throw new RuntimeException(
                                "Erro Léxico: Caractere inesperado '|' na linha " + line + ", coluna " + startColumn);
                    }
                }
                // Símbolos de um único caractere
                case '+' -> {
                    tokens.add(new Token(TokenType.PLUS, "+", line, startColumn));
                    position++;
                    column++;
                }
                case '-' -> {
                    tokens.add(new Token(TokenType.MINUS, "-", line, startColumn));
                    position++;
                    column++;
                }
                case '*' -> {
                    tokens.add(new Token(TokenType.MULTIPLY, "*", line, startColumn));
                    position++;
                    column++;
                }
                case '/' -> {
                    tokens.add(new Token(TokenType.DIVIDE, "/", line, startColumn));
                    position++;
                    column++;
                }
                case ';' -> {
                    tokens.add(new Token(TokenType.SEMICOLON, ";", line, startColumn));
                    position++;
                    column++;
                }
                case '(' -> {
                    tokens.add(new Token(TokenType.OPEN_PAREN, "(", line, startColumn));
                    position++;
                    column++;
                }
                case ')' -> {
                    tokens.add(new Token(TokenType.CLOSE_PAREN, ")", line, startColumn));
                    position++;
                    column++;
                }
                case '{' -> {
                    tokens.add(new Token(TokenType.OPEN_BRACE, "{", line, startColumn));
                    position++;
                    column++;
                }
                case '}' -> {
                    tokens.add(new Token(TokenType.CLOSE_BRACE, "}", line, startColumn));
                    position++;
                    column++;
                }

                default -> throw new RuntimeException("Erro Léxico: Caractere inválido na linha " + line + ", coluna "
                        + column + ": '" + currentChar + "'");
            }
        }
        tokens.add(new Token(TokenType.EOF, "", line, column));
        return tokens;
    }

    /**
     * Olha o próximo caractere sem avançar a posição (Lookahead).
     * 
     * @return O próximo caractere ou '\0' se estiver no fim do arquivo.
     */

    private char peek() {
        if (position + 1 >= sourceCode.length()) {
            return '\0';
        }
        return sourceCode.charAt(position + 1);
    }
}